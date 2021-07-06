package com.example.instagram_fbu.models;

import android.util.Log;

import com.example.instagram_fbu.Post;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;

@Parcel(analyze = User.class)
public class User extends ParseUser {

    public static final String TAG = "User";

    public User() { }

    public static final String KEY_FULLNAME = "fullname";
    public static final String KEY_PROFILE_IMAGE = "image";
    public static final String KEY_FOLLOWERS = "followers";
    public static final String KEY_FOLLOWING = "following";
    public static final String KEY_DESCRIPTION = "description";

    public String getImage() { return getParseFile(KEY_PROFILE_IMAGE).getUrl(); }
    public String getFullName() { return getString(KEY_FULLNAME); }
    public String getFollowers() { return getString(KEY_FOLLOWERS); }
    public String getFollowing() { return getString(KEY_FOLLOWING); }
    public String getDescription() { return getString(KEY_DESCRIPTION); }

    public void setImage(ParseFile parseFile) { put(KEY_PROFILE_IMAGE, parseFile); }
    public void setFullName(String fullName) {  put(KEY_FULLNAME, fullName); }
    public void setFollowers(String followers) {  put(KEY_FOLLOWERS, followers); }
    public void setFollowing(String following) {  put(KEY_FOLLOWING, following); }
    public void setDescription(String description) {  put(KEY_DESCRIPTION, description); }

    @Override
    public String toString() {
        return "User Name: " + this.getUsername() + ", user fullname: " + this.getFullName();
    }
}
