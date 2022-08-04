package com.helpetapplicationgmail.helpet.Search;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.helpetapplicationgmail.helpet.Profile.HelpetPhotosFragment;
import com.helpetapplicationgmail.helpet.R;
import com.helpetapplicationgmail.helpet.Utils.BottomNavigationViewHelper;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Berkant Şahin on 5.03.2018.
 */

public class SearchActivity extends AppCompatActivity {

    private static final String TAG = "SearchActivity";
    private static final int ACTIVITY_NUM = 1;
    private Context mContext = SearchActivity.this;

    /** TABLAYOUT**/
    private TabLayout tabLayout;
    private AppBarLayout appBarLayout;
    private ViewPager viewPager;
    /****/




    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search);
        Log.d(TAG, "onCreate: started.");
        setupViewPager();
        setupBottomNavigationView();
    }



    private void setupViewPager(){

        tabLayout = (TabLayout) findViewById(R.id.search_tab);
        viewPager = (ViewPager) findViewById(R.id.search_viewpager);
        ViewPagerAdapter spAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        spAdapter.addFragment(new SearchNormalFragment());
        spAdapter.addFragment(new SearchHelpFragment());
        spAdapter.addFragment(new SearchPostFragment());
        spAdapter.addFragment(new SearchPersonFragment());

        viewPager.setAdapter(spAdapter);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_normal_photos_white);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_helpphotos_white);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_post_white);
        tabLayout.getTabAt(3).setIcon(R.drawable.ic_person_white);

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
