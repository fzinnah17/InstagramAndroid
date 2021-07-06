package com.example.instagram_fbu.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagram_fbu.Post;
import com.example.instagram_fbu.R;
import com.parse.ParseFile;

import java.util.List;

public class ProfileAdapter extends  RecyclerView.Adapter<ProfileAdapter.ViewHolder>{

    private List<Post> posts;
    private Context context;
    OnClickListener clickListener;

    public static final String TAG = "ProfileAdapter";

    public ProfileAdapter(Context context, List<Post> posts, OnClickListener clickListener) {
        this.context = context;
        this.posts = posts;
        this.clickListener = clickListener;
    }

    public interface OnClickListener {
        void onItemClicked(int position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post_profile_grid, parent, false);
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

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivImage);


        }

        public void bind(Post post, int position) {
            ParseFile image = post.getImage();
            if (image != null) {
                Glide.with(context).load(image.getUrl()).into(ivImage);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onItemClicked(getAdapterPosition());
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
