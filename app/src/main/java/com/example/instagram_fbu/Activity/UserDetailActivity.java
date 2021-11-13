package com.example.instagram_fbu.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.instagram_fbu.Adapters.ProfileAdapter;
import com.example.instagram_fbu.EndlessRecyclerViewScrollListener;
import com.example.instagram_fbu.Post;
import com.example.instagram_fbu.R;
import com.example.instagram_fbu.models.User;
import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class UserDetailActivity extends AppCompatActivity {

    public static final String TAG = "UserDetailActivity";
    String objId;
    User user;

    List<Post> allPosts;
    SwipeRefreshLayout swipeContainer;
    EndlessRecyclerViewScrollListener scrollListener;

    ImageView profile_image;
    TextView tvScreenName;
    TextView tvPosts;
    TextView tvFollowers;
    TextView tvFollowing;
    TextView profileDescription;

    RecyclerView rvProfile;
    ProfileAdapter adapter;

    GridLayoutManager gridLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        profile_image = findViewById(R.id.profile_image);
        tvScreenName = findViewById(R.id.tvScreenName);
        tvPosts = findViewById(R.id.tvPosts);
        tvFollowers = findViewById(R.id.tvFollowers);
        tvFollowing = findViewById(R.id.tvFollowing);
        profileDescription = findViewById(R.id.profileDescription);
        rvProfile = findViewById(R.id.rvPosts);
        swipeContainer = findViewById(R.id.swipeContainer);

        allPosts = new ArrayList<>();

        objId = getIntent().getStringExtra("user");

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                queryPosts();
            }
        });

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        ProfileAdapter.OnClickListener clickListener = new ProfileAdapter.OnClickListener() {
            @Override
            public void onItemClicked(int position) {
                Intent intent = new Intent(UserDetailActivity.this, DetailActivity.class);
                intent.putExtra("post", Parcels.wrap(allPosts.get(position)));
                startActivity(intent);
            }
        };

        adapter = new ProfileAdapter(UserDetailActivity.this, allPosts, clickListener);
        rvProfile.setAdapter(adapter);
        gridLayoutManager = new GridLayoutManager(UserDetailActivity.this, 3);
        rvProfile.setLayoutManager(gridLayoutManager);

        scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadMore();
            }
        };

        rvProfile.addOnScrollListener(scrollListener);

        queryUser();
        queryPosts();
    }

    public void loadMore() {
        // specify what type of data we want to query - Post.class
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        // include data referred by user key
        query.include(Post.KEY_USER);
        // limit query to latest 20 items
        query.setLimit(20);
        query.setSkip(allPosts.size());
        // order posts by creation date (newest first)
        query.addDescendingOrder(Post.KEY_TIMESTAMP);
        // start an asynchronous call for posts
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                // check for errors
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                // for debugging purposes let's print every post description to logcat
                for (Post post : posts) {
                    Log.i(TAG, "Post: " + post.getTime());
                }

                // save received posts to list and notify adapter of new data
                allPosts.addAll(posts);
                adapter.notifyDataSetChanged();
            }
        });
    }

    // get user's details/bio/photo/etc
    protected void queryUser() {
        ParseQuery<User> query = ParseQuery.getQuery(User.class);
        query.setLimit(1);
        Log.i("test", objId);
        query.whereEqualTo("objectId", objId);
        query.findInBackground(new FindCallback<User>() {
            @Override
            public void done(List<User> objects, ParseException e) {
                User user = objects.get(0);
                Glide.with(UserDetailActivity.this).load(user.getImage()).into(profile_image);
                tvScreenName.setText(user.getUsername());
                tvFollowers.setText(user.getFollowers());
                tvFollowing.setText(user.getFollowing());
                profileDescription.setText(user.getDescription());
            }
        });
    }

    protected void queryPosts() {
        // specify what type of data we want to query - Post.class
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        // include data referred by user key
        query.include(Post.KEY_USER);

        // do not limit query to latest 20 items yet

        query.countInBackground(new CountCallback() {
            @Override
            public void done(int count, ParseException e) {
                tvPosts.setText(String.valueOf(count));
            }
        });

        // NOW limit query to latest 20 items

        query.setLimit(20);
        // order posts by creation date (newest first)
        query.addDescendingOrder(Post.KEY_TIMESTAMP);
        // start an asynchronous call for posts

        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                // check for errors
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                // for debugging purposes let's print every post description to logcat
                for (Post post : posts) {
                    Log.i(TAG, "Post: " + post.getTime());
                }

                // save received posts to list and notify adapter of new data
                allPosts.clear();
                allPosts.addAll(posts);
                adapter.notifyDataSetChanged();
                swipeContainer.setRefreshing(false);
            }
        });
    }
}