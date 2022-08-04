package com.helpetapplicationgmail.helpet.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.helpetapplicationgmail.helpet.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

/**
 * Created by acer on 15.03.2018.
 */

public class GridImageAdapter extends ArrayAdapter {
    private Context mContext;
    private LayoutInflater myInflater;
    private int layoutResource;
    private String myAppend;
    private ArrayList<String> imgURLs;

    public GridImageAdapter(Context context, int layoutResource, String append, ArrayList<String> imgURLs) {

        super(context, layoutResource, imgURLs);

        this.mContext = context;
        myInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layoutResource = layoutResource;
        this.myAppend = append;
        this.imgURLs = imgURLs;
    }

    private static class ViewHolder{
        SquareImageView image;
        ProgressBar myProgressBar;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){

        final ViewHolder viewHolder;
        if (convertView == null){
            convertView = myInflater.inflate(layoutResource, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.myProgressBar = (ProgressBar) convertView.findViewById(R.id.gridImageProgressBar);
            viewHolder.image = (SquareImageView) convertView.findViewById(R.id.gridImageView);

            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Object imgURL = getItem(position);
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(myAppend + imgURL, viewHolder.image, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                if (viewHolder.myProgressBar != null){
                    viewHolder.myProgressBar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                if (viewHolder.myProgressBar != null){
                    viewHolder.myProgressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if (viewHolder.myProgressBar != null){
                    viewHolder.myProgressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                if (viewHolder.myProgressBar != null){
                    viewHolder.myProgressBar.setVisibility(View.GONE);
                }
            }
        });

        return convertView;
    }
}
