package com.helpetapplicationgmail.helpet.Share;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.helpetapplicationgmail.helpet.Profile.AccountSettingsActivity;
import com.helpetapplicationgmail.helpet.R;
import com.helpetapplicationgmail.helpet.Utils.Permissions;

/**
 * Created by acer on 5.03.2018.
 */

public class PhotoFragmentHelp extends Fragment {
    private static final String TAG = "PhotoFragmentHelp";
    //constant
    private static final int PHOTO_FRAGMENT_NUM=1;
    private static final int GALLERY_FRAGMENT_NUM=2;
    private static final int CAMERA_REQUEST_CODE=5;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_help, container, false);

        Log.d(TAG, "onCreateView: started.");

        Button btnLaunchCamera = (Button) view.findViewById(R.id.btnLaunchCameraHelp);

        btnLaunchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: launching camera.");

                if(((ShareHelpActivity)getActivity()).getCurrentTabNumber() == PHOTO_FRAGMENT_NUM){
                    if(((ShareHelpActivity)getActivity()).checkPermissions(Permissions.CAMERA_PERMISSION[0])){
                            Log.d(TAG, "onClick: starting camera");
                            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                            Log.e(TAG,"GERİ TUŞUNA BASTI VERİ DÖNMEDİ");
                    }else{
                        Intent intent = new Intent(getActivity(), ShareHelpActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                }
            }
        });
        return view;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode == CAMERA_REQUEST_CODE){
            Log.d(TAG, "onActivityResult: done taking a photo.");
            Log.d(TAG, "onActivityResult: attempting to navigate to final share screen.");
            Bitmap bitmap;
            bitmap = (Bitmap) data.getExtras().get("data");

                try{
                    Log.d(TAG, "onActivityResult: Received new bitmap from camera:" + bitmap);
                    Intent intent=new Intent(getActivity(),NextHelpActivity.class);
                    intent.putExtra(getString(R.string.selected_bitmap),bitmap);
                    startActivity(intent);
                }catch(NullPointerException e){
                    Log.d(TAG,"onActivityResult: NullPointerException:" + e.getMessage());
                }

            }
     }

}


