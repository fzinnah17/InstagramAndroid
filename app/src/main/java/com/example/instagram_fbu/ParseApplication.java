package com.example.instagram_fbu;

import android.app.Application;

import com.example.instagram_fbu.models.Comment;
import com.example.instagram_fbu.models.User;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Post.class);
        ParseObject.registerSubclass(User.class);
        ParseObject.registerSubclass(Comment.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("1sxpdmTlsLyf2FQWCwIwxE1ayikoWPpAy2bBMHos")
                .clientKey("JiLndTbylDzmHTcxSnOnrOInPpN8hnYIiET5KleM")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
