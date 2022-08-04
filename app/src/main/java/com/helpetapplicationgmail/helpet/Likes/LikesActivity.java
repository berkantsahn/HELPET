package com.helpetapplicationgmail.helpet.Likes;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.helpetapplicationgmail.helpet.R;
import com.helpetapplicationgmail.helpet.Utils.BottomNavigationViewHelper;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

/**
 * Created by Berkant Şahin on 5.03.2018.
 */

public class LikesActivity extends AppCompatActivity {

    private static final String TAG = "LikesActivity";
    private static final int ACTIVITY_NUM = 3;
    private Context mContext = LikesActivity.this;

    ListView bildirim_goster;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_notifications);

        bildirim_goster=(ListView)findViewById(R.id.listview);


        bildirimClass[] bildirimler=new bildirimClass[]{

                new bildirimClass("ESRA",R.drawable.logo),
                new bildirimClass("BERKANT",R.drawable.logo),
                new bildirimClass("GONCA",R.drawable.logo),
                new bildirimClass("BURAK",R.drawable.logo),
                new bildirimClass("ALPER",R.drawable.logo),
                new bildirimClass("ESRA",R.drawable.logo),
                new bildirimClass("ERDİ",R.drawable.logo),
                new bildirimClass("GONCA",R.drawable.logo),
                new bildirimClass("BURAK",R.drawable.logo),
                new bildirimClass("ALPER",R.drawable.logo),
        };

        bildirimAdapter adapter=new bildirimAdapter(this,R.layout.customlist,bildirimler);
        bildirim_goster.setAdapter(adapter);


        Log.d(TAG, "onCreate: started.");



        setupBottomNavigationView();
    }

    /**
     * BottomNavigationView(Alt menu) SETUP
     */
    private void setupBottomNavigationView(){

        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);

        /** Menu Sayfa Değişimi */
        BottomNavigationViewHelper.enableNavigation(mContext, this,bottomNavigationViewEx);


        /**İkon Değişimi switch case*/
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }
}
