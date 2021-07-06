package com.example.instagram_fbu.models;

import android.util.Log;

import com.example.instagram_fbu.Post;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;

import java.util.Date;

@Parcel(analyze = Comment.class)
@ParseClassName("Comment")
public class Comment extends ParseObject {

    public Comment() {}

    public static final String TAG = "Comment.java";

    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_USER = "user";
    public static final String KEY_POSTOBJECTID = "PostObjectId";
    public static final String KEY_POSTID = "PostID";
    public static final String KEY_TIMESTAMP = "createdAt";

    // Getters
    public String getDescription() { return getString(KEY_DESCRIPTION); }
    public ParseUser getUser() { return getParseUser(KEY_USER); }
    public String getPostobjectid() { return getString(KEY_POSTOBJECTID); }
    public ParseObject getPostID() { return getParseObject(KEY_POSTID); }

    // Setters
    public void setDescription(String description) { put(KEY_DESCRIPTION, description); }
    public void setUser(ParseUser user) {  put(KEY_USER, user); }
    public void setPostobjectid (String postobjectid) { put(KEY_POSTOBJECTID, postobjectid);}
    public void setPostid (ParseObject postid) { put(KEY_POSTID, postid);}

    public String getTime() {
        Date date = this.getCreatedAt();

        int SECOND_MILLIS = 1000;
        int MINUTE_MILLIS = 60 * SECOND_MILLIS;
        int HOUR_MILLIS = 60 * MINUTE_MILLIS;
        int DAY_MILLIS = 24 * HOUR_MILLIS;

        try {
            date.getTime();
            long time = date.getTime();
            long now = System.currentTimeMillis();

            final long diff = now - time;
            if (diff < MINUTE_MILLIS) {
                return "just now";
            } else if (diff < 2 * MINUTE_MILLIS) {
                return "a minute ago";
            } else if (diff < 50 * MINUTE_MILLIS) {
                return diff / MINUTE_MILLIS + " m";
            } else if (diff < 90 * MINUTE_MILLIS) {
                return "an hour ago";
            } else if (diff < 24 * HOUR_MILLIS) {
                return diff / HOUR_MILLIS + " h";
            } else if (diff < 48 * HOUR_MILLIS) {
                return "yesterday";
            } else {
                return diff / DAY_MILLIS + " d";
            }
        } catch (Exception e) {
            Log.i(TAG, "getRelativeTimeAgo failed", e);
            e.printStackTrace();
        }

        return "";
    }

}
