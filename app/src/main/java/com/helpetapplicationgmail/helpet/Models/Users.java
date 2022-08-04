package com.helpetapplicationgmail.helpet.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by acer on 9.04.2018.
 */

public class Users implements Parcelable {

    private String userID;
    private String email;
    private String username;
    private long ceptel;

    public Users(String userID, String email, String username, long ceptel) {
        this.userID = userID;
        this.email = email;
        this.username = username;
        this.ceptel = ceptel;
    }
    public Users() {

    }

    protected Users(Parcel in) {
        userID = in.readString();
        email = in.readString();
        username = in.readString();
        ceptel = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userID);
        dest.writeString(email);
        dest.writeString(username);
        dest.writeLong(ceptel);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Users> CREATOR = new Creator<Users>() {
        @Override
        public Users createFromParcel(Parcel in) {
            return new Users(in);
        }

        @Override
        public Users[] newArray(int size) {
            return new Users[size];
        }
    };

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getCeptel() {
        return ceptel;
    }

    public void setCeptel(long ceptel) {
        this.ceptel = ceptel;
    }

    @Override
    public String toString() {
        return "Users{" +
                "userID='" + userID + '\'' +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", ceptel='" + ceptel + '\'' +
                '}';
    }

}
