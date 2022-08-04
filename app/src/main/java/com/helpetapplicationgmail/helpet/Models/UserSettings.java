package com.helpetapplicationgmail.helpet.Models;

/**
 * Created by acer on 10.04.2018.
 */

public class UserSettings {

    private Users users;
    private UserBilgileri settings;


    public UserSettings(Users users, UserBilgileri settings) {
        this.users = users;
        this.settings = settings;
    }

    public UserSettings() {

    }

    public Users getUsers() {
        return users;
    }

    public void setUsers(Users users) {
        this.users = users;
    }

    public UserBilgileri getSettings() {
        return settings;
    }

    public void setSettings(UserBilgileri settings) {
        this.settings = settings;
    }

    @Override
    public String toString() {
        return "UserSettings{" +
                "users=" + users +
                ", settings=" + settings +
                '}';
    }
}
