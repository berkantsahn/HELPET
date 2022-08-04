package com.helpetapplicationgmail.helpet.Profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.helpetapplicationgmail.helpet.Models.PhotoNormal;
import com.helpetapplicationgmail.helpet.Models.UserBilgileri;
import com.helpetapplicationgmail.helpet.Models.UserSettings;
import com.helpetapplicationgmail.helpet.Utils.ViewCommentsFragment;
import com.helpetapplicationgmail.helpet.Utils.ViewNormalPhotosFragment;
import com.helpetapplicationgmail.helpet.Utils.ViewPostFragment;
import com.helpetapplicationgmail.helpet.R;
import com.helpetapplicationgmail.helpet.Utils.BottomNavigationViewHelper;
import com.helpetapplicationgmail.helpet.Utils.FirebaseMethods;
import com.helpetapplicationgmail.helpet.Utils.UniversalImageLoader;
import com.helpetapplicationgmail.helpet.Utils.ViewProfileActivity;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Berkant Şahin on 5.03.2018.
 */

public class ProfileActivity extends AppCompatActivity implements NormalPhotosFragment.OnGridImageSelectedListener,
        ViewPostFragment.OnCommentThreadSelectedListener{
    /** TABLAYOUT**/
    private TabLayout tabLayout;
    private AppBarLayout appBarLayout;
    private ViewPager viewPager;
    /****/
    private static final String TAG = "ProfileActivity";

    @Override
    public void onCommentThreadSelectedListener(PhotoNormal photo) {
        Log.d(TAG, "onCommentThreadSelectedListener: selected a comment thread");

        ViewCommentsFragment fragment = new ViewCommentsFragment();
        Bundle args = new Bundle();
        args.putParcelable(getString(R.string.photo), photo);
        fragment.setArguments(args);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.mainActivityProfile, fragment);
        transaction.addToBackStack(getString(R.string.view_comments_fragment));
        transaction.commit();
    }

    @Override
    public void onGridImageSelected(PhotoNormal photo, int activityNumber) {
        Log.d(TAG, "onGridImageSelected: selected an image: "+ photo.toString());

        ViewPostFragment fragment = new ViewPostFragment();
        Bundle args = new Bundle();
        args.putParcelable(getString(R.string.photo), photo);
        args.putInt(getString(R.string.activity_number), activityNumber);
        fragment.setArguments(args);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.mainActivityProfile, fragment);
        fragmentTransaction.addToBackStack(getString(R.string.view_post_fragment));
        fragmentTransaction.commit();
    }

    private static final int ACTIVITY_NUM = 4;



    private Context mContext = ProfileActivity.this;
    private ProgressBar myProgressBar;
    private ImageView myProfilePhoto;
    private static final int NUM_GRID_COLUMNS = 4;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase myFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;


    private TextView myPosts, myFollowers, myFollowing, myName, myUsername, myBiyografi;
    private CircleImageView mProfilePhoto;
    private Toolbar toolbar;
    private ImageView profileMenu;
    private BottomNavigationViewEx bottomNavigationView;
    private Button editProfileButton;

    private int followersCount=0;
    private int followingCount=0;
    private int postsCount=0;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        //init();
        mFirebaseMethods = new FirebaseMethods(getApplication());
        //biyografi eklenecek.
        myName = (TextView) findViewById(R.id.name);
        myUsername = (TextView) findViewById(R.id.username);
        myBiyografi = (TextView) findViewById(R.id.biyografi_profile);
        myPosts = (TextView) findViewById(R.id.tvPosts);
        myFollowers = (TextView) findViewById(R.id.tvFollowers);
        myFollowing = (TextView) findViewById(R.id.tvFollowing);
        mProfilePhoto = (CircleImageView) findViewById(R.id.profile_photo);
        toolbar = (Toolbar) findViewById(R.id.profileToolBar);
        profileMenu = (ImageView) findViewById(R.id.profileMenu);
        bottomNavigationView = (BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);
        Log.d(TAG, "onCreate: started.");
        myProgressBar = (ProgressBar) findViewById(R.id.profileProgressBar);
        myProgressBar.setVisibility(View.GONE);
        editProfileButton = (Button) findViewById(R.id.viewButton);
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), AccountSettingsActivity.class);
                intent.putExtra(getString(R.string.calling_activity), getString(R.string.profile_activity));
                startActivity(intent);
            }
        });

        setupBottomNavigationView();
        setupViewPager();
        setupToolBar();
        setupFirebaseAuth();
        setupActivityWidgets();
        getFollowersCount();
        getFollowingCount();
        getPostCount();
    }
    private void setProfileWidgets(UserSettings userSettings){
        //Log.d(TAG, "setProfileWidgets: Veritabanından çekilen bilgileri yükler." + userSettings.toString());

        //Users users = userSettings.getUsers();
        UserBilgileri settings = userSettings.getSettings();

        UniversalImageLoader.setImage(settings.getProfil_foto(), mProfilePhoto, null, "");
        myName.setText(settings.getName());
        myUsername.setText(settings.getUsername());
        //myFollowers.setText(String.valueOf(settings.getTakipciSay()));
        //myFollowing.setText(String.valueOf(settings.getTakipSay()));
        //myPosts.setText(String.valueOf(settings.getGonderiSay()));
        myBiyografi.setText(settings.getBiyografi());
    }
    private void getFollowersCount(){

        followersCount=0;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_followers))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot:dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: TAKİPÇİ BULUNDU:" + singleSnapshot.getValue());
                    followersCount++;
                }
                myFollowers.setText(String.valueOf(followersCount));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void getFollowingCount(){
        followingCount=0;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_following))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot:dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: TAKİP EDİLENLER BULUNDU:" + singleSnapshot.getValue());
                    followingCount++;
                }
                myFollowing.setText(String.valueOf(followingCount));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void getPostCount(){
        postsCount=0;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_user_photos_normal))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot:dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: GÖNDERİ BULUNDU:" + singleSnapshot.getValue());
                    postsCount++;
                }
                myPosts.setText(String.valueOf(postsCount));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

   /**private void init(){
        Log.d(TAG, "init: inflating:" + getString(R.string.profile_fragment));
        Intent intent = getIntent();
        if(intent.hasExtra(getString(R.string.calling_activity))){
            Log.d(TAG, "init: searching for user object attached as intent extra");


            ViewProfileActivity fragment = new ViewProfileActivity();
            Bundle args = new Bundle();
            args.putParcelable(getString(R.string.intent_user), intent.getParcelableExtra(getString(R.string.intent_user)));
            fragment.setArguments(args);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.mainActivityProfile, fragment);
            transaction.addToBackStack(getString(R.string.field_comments));
            transaction.commit();
        }

    }**/
    private void setupViewPager(){

        tabLayout = (TabLayout) findViewById(R.id.profile_tab);
        viewPager = (ViewPager) findViewById(R.id.container);
        ViewPagerAdapter spAdapter = new ViewPagerAdapter(getSupportFragmentManager());


        spAdapter.addFragment(new NormalPhotosFragment());
        spAdapter.addFragment(new PostsFragment());
        spAdapter.addFragment(new HelpetPhotosFragment());
        viewPager.setAdapter(spAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_normal_photos_white);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_post_white);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_helpphotos_white);

    }

    private void setupActivityWidgets(){

        myProgressBar = (ProgressBar) findViewById(R.id.profileProgressBar);
        myProgressBar.setVisibility(View.GONE);
        myProfilePhoto = (ImageView) findViewById(R.id.profile_photo);

    }
    /** profile toolbar **/
    private void setupToolBar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.profileToolBar);
        setSupportActionBar(toolbar);

        ImageView profileMenu = (ImageView) findViewById(R.id.profileMenu);
        profileMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Hesap ayarlarına yönlendirme.");
                Intent intent = new Intent(mContext, AccountSettingsActivity.class);
                startActivity(intent);
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
        /*
---------------------------------------------- Firebase başlangıç -----------------------------------------------------------
*/
    /**
     * setup firebase auth objesi
     * **/

   private void setupFirebaseAuth() {
        Log.d(TAG,"setupFirebaseAuth: firebase auth yükleniyor");
        mAuth = FirebaseAuth.getInstance();
        myFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = myFirebaseDatabase.getReference();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user != null){
                    Log.d(TAG, "onAuthStateChanged:signed_in"+user.getUid());
                }
                else {
                    Log.d(TAG,"onAuthStateChanged:signed_out");
                }
            }
        };
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //kullanıcı bilgilerini databaseden çekiyor.
                setProfileWidgets(mFirebaseMethods.getUserSettings(dataSnapshot));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    @Override
    public void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
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
