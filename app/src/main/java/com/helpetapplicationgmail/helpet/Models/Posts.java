package com.helpetapplicationgmail.helpet.Models;

/**
 * Created by acer on 4.05.2018.
 */

public class Posts {
    private static final String TAG = "Posts";

    private String post;
    private String user_id;
    private String post_id;
    private String date_created;
    private String tags;

    public Posts() {
    }

    public Posts(String post, String user_id, String post_id, String date_created, String tags) {
        this.post = post;
        this.user_id = user_id;
        this.post_id = post_id;
        this.date_created = date_created;
        this.tags = tags;
    }

    public static String getTAG() {
        return TAG;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "Posts{" +
                "post='" + post + '\'' +
                ", user_id='" + user_id + '\'' +
                ", post_id='" + post_id + '\'' +
                ", date_created='" + date_created + '\'' +
                ", tags='" + tags + '\'' +
                '}';
    }
}
