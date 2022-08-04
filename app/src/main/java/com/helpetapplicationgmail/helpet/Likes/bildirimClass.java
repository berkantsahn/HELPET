package com.helpetapplicationgmail.helpet.Likes;

/**
 * Created by acer on 10.04.2018.
 */

public class bildirimClass {


    String kullanici_ad;
    int kullanici_resim;


    public bildirimClass(String kullanici_ad, int kullanici_resim) {
        this.kullanici_ad = kullanici_ad;
        this.kullanici_resim = kullanici_resim;

    }

    public String getKullanici_ad() {
        return kullanici_ad;
    }

    public void setKullanici_ad(String kullanici_ad) {
        this.kullanici_ad = kullanici_ad;
    }

    public int getKullanici_resim() {
        return kullanici_resim;
    }

    public void setKullanici_resim(int kullanici_resim) {
        this.kullanici_resim = kullanici_resim;
    }


}
