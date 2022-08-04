package com.helpetapplicationgmail.helpet.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by acer on 9.04.2018.
 */

public class UserBilgileri implements Parcelable {

    private String name;
    private String cinsiyet;
    private String biyografi;
    private String username;
    private String profil_foto;
    private String  yasadigiyer;
    private long gonderiSay;
    private long takipSay;
    private long takipciSay;
    private String user_id;


    public UserBilgileri(String name, String cinsiyet, String biyografi, String username, String profil_foto, String yasadigiyer, long gonderiSay, long takipSay, long takipciSay, String user_id) {
        this.name = name;
        this.cinsiyet = cinsiyet;
        this.biyografi = biyografi;
        this.username = username;
        this.profil_foto = profil_foto;
        this.yasadigiyer = yasadigiyer;
        this.gonderiSay = gonderiSay;
        this.takipSay = takipSay;
        this.takipciSay = takipciSay;
        this.user_id = user_id;
    }

    public UserBilgileri() {

    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCinsiyet() {
        return cinsiyet;
    }

    public void setCinsiyet(String cinsiyet) {
        this.cinsiyet = cinsiyet;
    }

    public String getBiyografi() {
        return biyografi;
    }

    public void setBiyografi(String biyografi) {
        this.biyografi = biyografi;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfil_foto() {
        return profil_foto;
    }

    public void setProfil_foto(String profil_foto) {
        this.profil_foto = profil_foto;
    }

    public String getYasadigiyer() {
        return yasadigiyer;
    }

    public void setYasadigiyer(String yasadigiyer) {
        this.yasadigiyer = yasadigiyer;
    }

    public long getGonderiSay() {
        return gonderiSay;
    }

    public void setGonderiSay(long gonderiSay) {
        this.gonderiSay = gonderiSay;
    }

    public long getTakipSay() {
        return takipSay;
    }

    public void setTakipSay(long takipSay) {
        this.takipSay = takipSay;
    }

    public long getTakipciSay() {
        return takipciSay;
    }

    public void setTakipciSay(long takipciSay) {
        this.takipciSay = takipciSay;
    }

    @Override
    public String toString() {
        return "UserBilgileri{" +
                "name='" + name + '\'' +
                ", cinsiyet='" + cinsiyet + '\'' +
                ", biyografi='" + biyografi + '\'' +
                ", username='" + username + '\'' +
                ", profil_foto='" + profil_foto + '\'' +
                ", yasadigiyer='" + yasadigiyer + '\'' +
                ", gonderiSay=" + gonderiSay +
                ", takipSay=" + takipSay +
                ", takipciSay=" + takipciSay +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
