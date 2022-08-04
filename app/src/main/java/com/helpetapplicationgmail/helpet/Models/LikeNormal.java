package com.helpetapplicationgmail.helpet.Models;

/**
 * Created by acer on 2.05.2018.
 */

public class LikeNormal {
    private String user_id;

    public LikeNormal(String user_id){
        this.user_id = user_id;
    }

    public LikeNormal(){}

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    @Override
    public String toString() {
        return "LikeNormal{" +
                "user_id='" + user_id + '\'' +
                '}';
    }
}
