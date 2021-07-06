package com.example.instagram_fbu;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Parcel(analyze = Post.class)
@ParseClassName("Post")
public class Post extends ParseObject {

    public Post() {}

    public static final String TAG = "PostModel";


    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_USER = "user";
    public static final String KEY_TIMESTAMP = "createdAt";
    public static final String KEY_LIKES = "like";

    // Getters
    public String getDescription() { return getString(KEY_DESCRIPTION); }
    public ParseFile getImage() { return getParseFile(KEY_IMAGE); }
    public ParseUser getUser() { return getParseUser(KEY_USER); }
    public JSONArray getLikes() { return getJSONArray(KEY_LIKES); }
    public int getNumLikes() {
        if(getLikes() != null) {
            JSONArray usersLiked = getLikes();
            return usersLiked.length();
        } else {
            return 0;
        }

    }


    // Setters
    public void setDescription(String description) { put(KEY_DESCRIPTION, description); }
    public void setImage(ParseFile parseFile) { put(KEY_IMAGE, parseFile); }
    public void setUser(ParseUser user) {  put(KEY_USER, user); }

    public boolean isLikedBy(ParseUser user) {
        if(getLikes() != null) {
            JSONArray usersLiked = getLikes();

            for (int i = 0; i < usersLiked.length(); i++) {
                JSONObject userPointer = null;
                try {
                    userPointer = usersLiked.getJSONObject(i);
                    if(userPointer.getString("objectId").equals(user.getObjectId())) {
                        return true;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        return false;
    }




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

    public void unLike(ParseUser currentUser) throws JSONException {
        JSONArray usersLiked = getLikes();

        if(usersLiked == null) {
            usersLiked = new JSONArray();
        }

        for (int i = 0; i < usersLiked.length(); i++) {
            JSONObject userPointer = usersLiked.getJSONObject(i);
            if (userPointer.getString("objectId").equals(currentUser.getObjectId())) {
                usersLiked.remove(i);
            }
        }
        put(KEY_LIKES, usersLiked);

    }


    public void like(ParseUser currentUser) {
        add(KEY_LIKES, currentUser);
    }
}
