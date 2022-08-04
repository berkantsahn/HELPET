package com.helpetapplicationgmail.helpet.Models;

/**
 * Created by acer on 3.05.2018.
 */

public class CommentNormal {

    private String comment;
    private String user_id;
    private String date_created;

    public CommentNormal(String comment, String user_id, String date_created) {
        this.comment = comment;
        this.user_id = user_id;
        this.date_created = date_created;
    }

    public CommentNormal() {

    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    @Override
    public String toString() {
        return "CommentNormal{" +
                "comment='" + comment + '\'' +
                ", user_id='" + user_id + '\'' +
                ", date_created='" + date_created + '\'' +
                '}';
    }
}
