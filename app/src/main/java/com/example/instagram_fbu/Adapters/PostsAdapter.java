package com.example.instagram_fbu.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagram_fbu.Post;
import com.example.instagram_fbu.R;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.json.JSONException;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    private Context context;
    private List<Post> posts;
    OnClickListener clickListenerItemView;
    OnClickListener clickListenerProfile;
    OnClickListener clickListenerComment;

    public PostsAdapter(Context context, List<Post> posts, OnClickListener clickListenerItemView, OnClickListener clickListenerProfile, OnClickListener clickListenerComment) {
        this.context = context;
        this.posts = posts;
        this.clickListenerItemView = clickListenerItemView;
        this.clickListenerProfile = clickListenerProfile;
        this.clickListenerComment = clickListenerComment;
    }

    public interface OnClickListener {
        void onItemClicked(int position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post, position);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvUsername;
        private TextView tvUsernameBelow;
        private ImageView ivImage;
        private ImageView ivProfile;
        private TextView tvDescription;
        private TextView tvCreatedAt;
        private TextView tvLikes;
        private View itemView;
        private Button btnHeart;
        private Button btnComment;
        private Button btnSave;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvUsernameBelow = itemView.findViewById(R.id.tvUsernameBelow);
            ivImage = itemView.findViewById(R.id.ivImage);
            ivProfile = itemView.findViewById(R.id.ivProfile);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvCreatedAt = itemView.findViewById(R.id.tvCreatedAt);
            btnHeart = itemView.findViewById(R.id.btnHeart);
            btnComment = itemView.findViewById(R.id.btnComment);
            btnSave = itemView.findViewById(R.id.btnSave);
            tvLikes = itemView.findViewById(R.id.tvLikes);
        }

        public void bind(Post post, int position) {
            // Bind the post data to the view elements
            tvDescription.setText(post.getDescription());
            tvUsername.setText(post.getUser().getUsername());
            tvUsernameBelow.setText(post.getUser().getUsername());
            tvCreatedAt.setText(post.getTime());

            if (post.getNumLikes() == 0) {
                tvLikes.setVisibility(View.GONE);
            } else {
                if (post.getNumLikes() <= 1)
                    tvLikes.setText(String.format("%s like", post.getNumLikes()));
                else
                    tvLikes.setText(String.format("%s likes", post.getNumLikes()));
            }

            if (post.isLikedBy(ParseUser.getCurrentUser())){
                btnHeart.setBackgroundResource(R.drawable.ufi_heart_active);
            }

            ParseFile image = post.getImage();
            if (image != null) {
                Glide.with(context).load(image.getUrl()).into(ivImage);
            }

            ParseFile imagePofile = post.getUser().getParseFile("image");
            if(imagePofile != null) {
                Glide.with(context).load(imagePofile.getUrl()).into(ivProfile);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListenerItemView.onItemClicked(getAdapterPosition());
                }
            });

            ivProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListenerProfile.onItemClicked(getAdapterPosition());
                }
            });
            btnHeart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isLikedByCurrentUser = post.isLikedBy(ParseUser.getCurrentUser());
                    int likeCount = 0;

                    if(post.getLikes() != null) {
                        likeCount = post.getLikes().length();
                    }

                    if(isLikedByCurrentUser) {
                        likeCount--;
                        btnHeart.setBackgroundResource(R.drawable.ufi_heart);
                        try {
                            post.unLike(ParseUser.getCurrentUser());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        post.saveInBackground();
                    } else {
                        likeCount++;
                        btnHeart.setBackgroundResource(R.drawable.ufi_heart_active);
                        post.like(ParseUser.getCurrentUser());

                        post.saveInBackground();
                    }
                    tvLikes.setText(String.format("%s likes", likeCount));
                }
            });
            btnComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListenerComment.onItemClicked(getAdapterPosition());
                }
            });
            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btnSave.setBackgroundResource(R.drawable.ufi_save_active);
                    Toast.makeText(context, "Post Saved", Toast.LENGTH_SHORT).show();
                }
            });




        }
    }

    // Clean all elements of the recycler
    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Post> post) {
        posts.addAll(post);
        notifyDataSetChanged();
    }

}