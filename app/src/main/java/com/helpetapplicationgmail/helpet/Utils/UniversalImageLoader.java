package com.helpetapplicationgmail.helpet.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.helpetapplicationgmail.helpet.R;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * Created by acer on 14.03.2018.
 */

public class UniversalImageLoader {

    private static final int defaultImage = R.drawable.default_profile_photo;
    private Context myContext;


    public UniversalImageLoader(Context context) {

        myContext = context;

    }

    public ImageLoaderConfiguration getConfig(){

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(null)
                .showImageForEmptyUri(defaultImage)
                .showImageOnFail(defaultImage)
                .considerExifParams(true)
                .cacheOnDisk(true).cacheInMemory(true)
                .cacheOnDisk(true).resetViewBeforeLoading(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300)).build();
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(myContext)
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .diskCacheSize(100 * 1024 * 1024).build();
        return configuration;

    }


    /**
     * BU METOD STATİK RESİMLERİ AYARLAMAYA YARAR.
     * BİR LİST VEYA GRİDDE AYARLANMIŞLARSA FRAGMAN VE ACTIVITY'DEN DEĞİŞEN RESİMLER İÇİN KULLANILAMAZ.
     * */

    public static void setImage(String imgURL, ImageView image, final ProgressBar myProgressBar, String append){

        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(append + imgURL, image, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                if (myProgressBar != null){
                    myProgressBar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                if (myProgressBar != null){
                    myProgressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if (myProgressBar != null){
                    myProgressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                if (myProgressBar != null){
                    myProgressBar.setVisibility(View.GONE);
                }
            }
        });

    }



}
