package com.example.instagram_fbu.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.instagram_fbu.Activity.DetailActivity;
import com.example.instagram_fbu.Adapters.PostsAdapter;
import com.example.instagram_fbu.Adapters.ProfileAdapter;
import com.example.instagram_fbu.EndlessRecyclerViewScrollListener;
import com.example.instagram_fbu.Post;
import com.example.instagram_fbu.R;
import com.example.instagram_fbu.models.User;
import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.example.instagram_fbu.fragments.ComposeFragment.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE;

public class ProfileFragment extends Fragment {

    public static final String TAG = "PostsFragment";

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

    private File photoFile;
    private String photoFileName = "photo.jpg";

    public ProfileFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }



    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        profile_image = view.findViewById(R.id.profile_image);
        tvScreenName = view.findViewById(R.id.tvScreenName);
        tvPosts = view.findViewById(R.id.tvPosts);
        tvFollowers = view.findViewById(R.id.tvFollowers);
        tvFollowing = view.findViewById(R.id.tvFollowing);
        profileDescription = view.findViewById(R.id.profileDescription);
        rvProfile = view.findViewById(R.id.rvProfilePosts);
        swipeContainer = view.findViewById(R.id.swipeContainer);

        allPosts = new ArrayList<>();

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
                Intent intent = new Intent(getContext(), DetailActivity.class);
                intent.putExtra("post", Parcels.wrap(allPosts.get(position)));
                startActivity(intent);
            }
        };

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);

        adapter = new ProfileAdapter(getContext(), allPosts, clickListener);
        rvProfile.setAdapter(adapter);
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
        queryPosts();


        User currentUser = (User) User.getCurrentUser();

        Glide.with(this).load(currentUser.getImage()).into(profile_image);

        tvScreenName.setText(currentUser.getFullName());
        tvFollowers.setText(currentUser.getFollowers());
        tvFollowing.setText(currentUser.getFollowing());
        profileDescription.setText(currentUser.getDescription());

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LaunchCamera();
            }
        });


    }

    private void LaunchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

        }


    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }

    // Saving Posts to server
    private void updateProfileImage(File photoFile) {
        Log.e("test", "updateProfileImage called");
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            // Other attributes than "email" will remain unchanged!
            currentUser.put("image", new ParseFile(photoFile));
            // Saves the object.
            currentUser.saveInBackground(e -> {
                if(e==null){
                    //Save successfull
                    Toast.makeText(getContext(), "Save Successful", Toast.LENGTH_SHORT).show();
                }else{
                    // Something went wrong while saving
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                profile_image.setImageBitmap(takenImage);
                updateProfileImage(photoFile);
                // save to Parse!
            } else { // Result was a failure
                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
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
                    Log.i(TAG, "Post: " + post.getTime() );
                }

                // save received posts to list and notify adapter of new data
                allPosts.addAll(posts);
                adapter.notifyDataSetChanged();
            }
        });
    }

    protected void queryPosts() {
        // specify what type of data we want to query - Post.class
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        // include data referred by user key

        // do not limit query to latest 20 items yet
        query.countInBackground(new CountCallback() {
            @Override
            public void done(int count, ParseException e) {
                tvPosts.setText(String.valueOf(count));
            }
        });

        query.include(Post.KEY_USER); // "user" column is a pointer, so get its contents also

        // limit query to latest 20 items
        query.setLimit(20);
        query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());
        // order posts by creation date (newest first)
        query.addDescendingOrder(Post.KEY_TIMESTAMP);
        // start an asynchronous call for posts3
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
                }

                // save received posts to list and notify adapter of new data
                allPosts.clear();
                allPosts.addAll(posts);
                adapter.notifyDataSetChanged();
                swipeContainer.setRefreshing(false);
                tvPosts.setText(String.valueOf(allPosts.size()));
            }
        });
    }
}