package com.helpetapplicationgmail.helpet.Share;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.helpetapplicationgmail.helpet.R;
import com.helpetapplicationgmail.helpet.Utils.BottomNavigationViewHelper;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

/**
 * Created by acer on 15.04.2018.
 */

public class SelectionFragment extends AppCompatActivity {

    private static final String TAG = "SelectionFragment";
    private static final int ACTIVITY_NUM = 2;
    private Context mContext = SelectionFragment.this;

    private TextView normalFoto, yardimFoto;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        Log.d(TAG, "onCreate: started.");

        setupBottomNavigationView();
        selectMenuAdapter();




    }

    private void selectMenuAdapter(){


        TextView normalFoto = (TextView) findViewById(R.id.normalFotoKamera);
        TextView yardimFoto = (TextView) findViewById(R.id.yardimFotoKamera);
        TextView yazi = (TextView) findViewById(R.id.durumKamera);

        normalFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SelectionFragment.this,ShareActivity.class);
                startActivity(intent);

            }
        });

        yardimFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(SelectionFragment.this, ShareHelpActivity.class);
                startActivity(intent2);
            }
        });

        yazi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent3 = new Intent(SelectionFragment.this, SharePostActivity.class);
                startActivity(intent3);
            }
        });


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
