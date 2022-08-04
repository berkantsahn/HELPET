package com.helpetapplicationgmail.helpet.Share;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.helpetapplicationgmail.helpet.R;
import com.helpetapplicationgmail.helpet.Utils.BottomNavigationViewHelper;
import com.helpetapplicationgmail.helpet.Utils.Permissions;
import com.helpetapplicationgmail.helpet.Utils.SectionsPagerAdapter;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

/**
 * Created by Berkant Şahin on 5.03.2018.
 */

public class ShareHelpActivity extends AppCompatActivity {

    private static final String TAG = "ShareHelpActivity";
    //constants
    private static final int VERIFY_PERMISSIONS_REQUEST=1;
    private ViewPager mViewPager;
    private Context mContext = ShareHelpActivity.this;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_help);
        Log.d(TAG, "onCreate: started.");


        if(checkPermissionsArray(Permissions.PERMISSIONS)){
            setupViewPager();
        }
        else
        {
            verifyPermissions(Permissions.PERMISSIONS);
        }

    }

    /**
     * 0=GalleryFragment
     * 1=PhotoFragment
     * @return
     */

    public int getCurrentTabNumber(){
        return mViewPager.getCurrentItem();
    }

    /**
     * setup viewpager for manager the tabs
     */


    private void setupViewPager(){
        SectionsPagerAdapter adapter=new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new GalleryFragmentHelp());
        adapter.addFragment(new PhotoFragmentHelp());
        mViewPager=(ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(adapter);
        TabLayout tabLayout=(TabLayout) findViewById(R.id.tabsBottomHelp);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.getTabAt(0).setText(getString(R.string.gallery));
        tabLayout.getTabAt(1).setText(getString(R.string.photo));

    }

    public int getTask(){
        Log.d(TAG, "getTask: TASK:" + getIntent().getFlags());
        return getIntent().getFlags();
    }

    /**
     * verify all the permissions passed to the array
     * @param permissions
     */

    public void verifyPermissions(String[] permissions){
        Log.d(TAG, "VerifyPermissions:verifying permissions");

        ActivityCompat.requestPermissions(ShareHelpActivity.this,permissions,VERIFY_PERMISSIONS_REQUEST);
    }



    /**
     * Check an array of permissions
     * @param permissions
     * @return
     */


    public boolean checkPermissionsArray(String[] permissions) {
        Log.d(TAG, "checkPermissionsArray checking permissions array.");

        for (int i = 0; i < permissions.length; i++) {
            String check = permissions[i];
            if (!checkPermissions(check)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Chech a single permission is it has been verified
     * @param permission
     * @return
     */


    public boolean checkPermissions(String permission){
        Log.d(TAG,"checkPermissions: checking permissions :" +permission);

        int permisionRequest= ActivityCompat.checkSelfPermission(ShareHelpActivity.this,permission);

        if(permisionRequest != PackageManager.PERMISSION_GRANTED){
            Log.d(TAG,"checkPermission:\n Permission was not granted for :" +permission);
            return false;
        }
        else {
            Log.d(TAG,"checkPermission:\n Permission was granted for :" +permission);
            return  true;
        }
    }





}
