package com.example.instagram_fbu.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.instagram_fbu.Adapters.CommentsAdapter;
import com.example.instagram_fbu.Post;
import com.example.instagram_fbu.R;
import com.example.instagram_fbu.models.Comment;
import com.example.instagram_fbu.models.User;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    public static final String TAG = "DetailActivity";

    ImageView ivProfile;
    ImageView ivImage;
    ImageView ivProfileBelow;
    TextView tvUsername;
    TextView tvUsernameBelow;
    TextView tvDescription;
    TextView tvCreatedAt;
    TextView tvPost;
    EditText etComment;

    Post post;

    RecyclerView rvComment;
    List<Comment> AllComments;
    CommentsAdapter adapter;

    String username;
    String description;
    String image;
    String timeStamp;

    @Override
    protected void onResume() {
        super.onResume();

       if( getIntent().getExtras().getBoolean("autoFocus") ) {
           etComment.requestFocus();
           InputMethodManager imm = (InputMethodManager) getSystemService(DetailActivity.this.INPUT_METHOD_SERVICE);
           imm.showSoftInput(etComment, InputMethodManager.SHOW_IMPLICIT);
       }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ivProfile = findViewById(R.id.ivProfile);
        ivProfileBelow = findViewById(R.id.ivProfileBelow);
        ivImage = findViewById(R.id.ivImage);
        tvUsername = findViewById(R.id.tvUsername);
        tvUsernameBelow = findViewById(R.id.tvUsernameBelow);
        tvDescription = findViewById(R.id.tvDescription);
        tvCreatedAt = findViewById(R.id.tvCreatedAt);
        tvPost = findViewById(R.id.tvPost);
        etComment = findViewById(R.id.etComment);
        rvComment = findViewById(R.id.rvComment);

        post = (Post) Parcels.unwrap(getIntent().getParcelableExtra("post"));

        AllComments = new ArrayList<>();
        adapter = new CommentsAdapter(DetailActivity.this, AllComments);
        rvComment.setAdapter(adapter);
        rvComment.setLayoutManager(new LinearLayoutManager(this));


        String image = post.getImage().getUrl();

        description = post.getDescription();
        image = post.getImage().getUrl();
        timeStamp =post.getTime();


        tvDescription.setText(description);
        tvCreatedAt.setText(timeStamp);
        if (image != null) {
            Glide.with(DetailActivity.this).load(image).into(ivImage);
        }
        post.getUser().fetchIfNeededInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser user, ParseException e) {
                username = user.getUsername();
                tvUsername.setText(username);
                tvUsernameBelow.setText(username);
                ParseFile imagePofile = user.getParseFile(User.KEY_PROFILE_IMAGE);
                if(imagePofile != null) {
                    Glide.with(DetailActivity.this).load(imagePofile.getUrl()).into(ivProfile);
                    Glide.with(DetailActivity.this).load(imagePofile.getUrl()).into(ivProfileBelow);
                }
            }
        });


        tvPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String com = etComment.getText().toString();
                addCommentToPost(com);
            }
        });

        queryComments();

    }

    private void addCommentToPost(String com) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        Comment comment = new Comment();
        comment.setUser(currentUser);
        comment.setDescription(com);
        comment.setPostobjectid(post.getObjectId());
        comment.setPostid(post);
        comment.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error While Posting Comment", e);
                    Toast.makeText(DetailActivity.this, "Could not post comment", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.i(TAG, "Comment Post Successfully");
                Toast.makeText(DetailActivity.this, "Comment Posted", Toast.LENGTH_SHORT).show();
                etComment.setText("");
                queryComments();
            }
        });

    }

    private void queryComments() {
        ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class);
        query.include(Comment.KEY_USER);
        query.include(Comment.KEY_POSTID);
        query.whereEqualTo("PostObjectId", post.getObjectId());
        query.setLimit(20);
        query.addDescendingOrder(Comment.KEY_TIMESTAMP);
        query.findInBackground(new FindCallback<Comment>() {
            @Override
            public void done(List<Comment> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting Comments", e);
                    return;
                }
                for (Comment comment : objects) {
                    Log.i(TAG, "Comment: " + comment.getTime() );
                }
                AllComments.clear();
                AllComments.addAll(objects);
                adapter.notifyDataSetChanged();

            }
        });


    }

    public void findUsers() {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("email", "email@example.com");
        query.findInBackground((users, e) -> {
            if (e == null) {
                // The query was successful, returns the users that matches
                // the criteria.
                for(ParseUser user1 : users) {
                    Log.d("User List ",(user1.getUsername()));
                }
            } else {
                // Something went wrong.
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}