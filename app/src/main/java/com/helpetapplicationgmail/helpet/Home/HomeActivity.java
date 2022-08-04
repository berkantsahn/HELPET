package com.helpetapplicationgmail.helpet.Home;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.helpetapplicationgmail.helpet.Models.PhotoNormal;
import com.helpetapplicationgmail.helpet.Others.LoginActivity;
import com.helpetapplicationgmail.helpet.R;
import com.helpetapplicationgmail.helpet.Utils.BottomNavigationViewHelper;
import com.helpetapplicationgmail.helpet.Utils.MainfeedListAdapter;
import com.helpetapplicationgmail.helpet.Utils.SectionsPagerAdapter;
import com.helpetapplicationgmail.helpet.Utils.UniversalImageLoader;
import com.helpetapplicationgmail.helpet.Utils.ViewCommentsFragment;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.nostra13.universalimageloader.core.ImageLoader;

public class HomeActivity extends AppCompatActivity implements MainfeedListAdapter.OnLoadMoreItemsListener{

    @Override
    public void onLoadMoreItems() {
        Log.d(TAG, "onLoadMoreItems: displaying more photos.");
        HomeFragment fragment = (HomeFragment)getSupportFragmentManager()
                .findFragmentByTag("android:switcher" + R.id.container + ":" + mViewPager.getCurrentItem());

        if(fragment != null){
            fragment.displayMorePhotos();
        }

    }
    private static final String TAG = "HomeActivity";
    private static final int ACTIVITY_NUM = 0;
    private static final int HOME_FRAGMENT = 1;
    private Context mContext = HomeActivity.this;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;

    //widgets
    private ViewPager mViewPager;
    private FrameLayout mFrameLayout;
    private RelativeLayout mRelativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Log.d(TAG, "onCreate: starting.");

        mViewPager = (ViewPager) findViewById(R.id.container);
        mFrameLayout = (FrameLayout) findViewById(R.id.frame_container);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.relLayoutParent);

        setupFirebaseAuth();
        initImageLoader();
        setupBottomNavigationView();
        setupViewPager();
    }

    public void onCommentThreadSelected(PhotoNormal photo, String callingActivity){
        Log.d(TAG, "onCommentThreadSelected: selected a comment thread");

        ViewCommentsFragment fragment = new ViewCommentsFragment();
        Bundle args = new Bundle();
        args.putParcelable(getString(R.string.photo), photo);
        args.putString(getString(R.string.home_activity), getString(R.string.home_activity));
        fragment.setArguments(args);

        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(getString(R.string.view_comments_fragment));
        transaction.commit();
    }

    public void hideLayout(){
        Log.d(TAG, "hideLayout: hiding layouts");
        mRelativeLayout.setVisibility(View.GONE);
        mFrameLayout.setVisibility(View.VISIBLE);
    }

    public void showLayout(){
        Log.d(TAG, "hideLayout: showing layouts");
        mRelativeLayout.setVisibility(View.VISIBLE);
        mFrameLayout.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(mFrameLayout.getVisibility() == View.VISIBLE) {
            showLayout();
        }
    }

    /**
     * Anasayfanın yukarısındaki 3 sekmeyi eklemek için kullandık:
    */
    private void setupViewPager(){
        SectionsPagerAdapter spAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        /**
         * HomeActivity'nin fragmanlarını ekledik.*/
        spAdapter.addFragment(new StatusFragment()); //index 0
        spAdapter.addFragment(new HomeFragment()); // index: 1
        spAdapter.addFragment(new MessagesFragment()); //index: 2
        mViewPager.setAdapter(spAdapter);
        /**
         * Sekmeleri oluşturduk.*/
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        /**Sekme ikonlarını ekledik */
        //tabLayout.getTabAt(0).setIcon(R.drawable.ic_helpet_hand);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_post_white);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_normal_photos_white);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_messages_white);
    }

    private void initImageLoader() {
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(mContext);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
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
        /*
    ---------------------------------------------- Firebase başlangıç -----------------------------------------------------------
    */
        //parametre setupFirebaseAuth içerisinden çekiliyor.
        private void checkCurrentUser(FirebaseUser user){
            Log.d(TAG, "checkCurrentUser: Kullanıcının giriş yapıp yapmadığı kontrol ediliyor.");
            if(user == null){
                Intent intent = new Intent(mContext, LoginActivity.class);
                startActivity(intent);
            }
        }
    /**
     * setup firebase auth objesi
     * **/
    private void setupFirebaseAuth() {
        Log.d(TAG,"setupFirebaseAuth: firebase auth yükleniyor");
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        //userID = mAuth.getCurrentUser().getUid();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                //kullanıcının giriş yapıp yapılmadığı kontrol ediliyor.
                checkCurrentUser(user);
                if(user != null){
                    Log.d(TAG, "onAuthStateChanged:signed_in"+user.getUid());
                    /**myRef.child(mContext.getString(R.string.dbname_users))
                            .child(userID)
                            .child(mContext.getString(R.string.field_email))
                            .setValue(user.getEmail());**/
                }
                else {
                    Log.d(TAG,"onAuthStateChanged:signed_out");
                }
            }
        };
    }
    @Override
    public void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        mViewPager.setCurrentItem(HOME_FRAGMENT);
        checkCurrentUser(mAuth.getCurrentUser());
    }
    @Override
    public void onStop(){
        super.onStop();
        if(mAuthListener!=null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

   /*
   * ------------------------------------------------- Firebase son ------------------------------------------------
   * */
}
