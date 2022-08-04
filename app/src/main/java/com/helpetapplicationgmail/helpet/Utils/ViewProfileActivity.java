package com.helpetapplicationgmail.helpet.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.helpetapplicationgmail.helpet.Models.Users;
import com.helpetapplicationgmail.helpet.Profile.AccountSettingsActivity;
import com.helpetapplicationgmail.helpet.Profile.HelpetPhotosFragment;
import com.helpetapplicationgmail.helpet.Profile.NormalPhotosFragment;
import com.helpetapplicationgmail.helpet.Profile.PostsFragment;
import com.helpetapplicationgmail.helpet.Profile.ViewPagerAdapter;
import com.helpetapplicationgmail.helpet.Utils.Like;
import com.helpetapplicationgmail.helpet.R;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Berkant Şahin on 5.03.2018.
 */

public class ViewProfileActivity extends AppCompatActivity implements ViewNormalPhotosFragment.OnGridImageSelectedListener,
        ViewPostFragment.OnCommentThreadSelectedListener{
    /** TABLAYOUT**/
    private TabLayout tabLayout;
    private AppBarLayout appBarLayout;
    private ViewPager viewPager;
    /****/
    private static final String TAG = "ViewProfileActivity";

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



    private Context mContext = ViewProfileActivity.this;
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
    private Button editProfile,follow,unfollow;
    private Users mUser;

    private int followersCount=0;
    private int followingCount=0;
    private int postsCount=0;
    private int normalPhotosPostCount=0;
    private int helpPhotosPostCount=0;
    private int textPostCount=0;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);
        mFirebaseMethods = new FirebaseMethods(getApplication());
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


        editProfile=(Button) findViewById(R.id.editProfile);
        follow=(Button) findViewById(R.id.follow);
        unfollow=(Button) findViewById(R.id.unfollow);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();

        try {
            Intent intent = getIntent();
            mUser = intent.getParcelableExtra(getString(R.string.intent_user));
            editor.putString("myString", mUser.getUserID());
            editor.commit();
            init();
        }catch (NullPointerException e){
            Log.d(TAG,"SEARCH ACTIVITY NULL");
        }

        setupBottomNavigationView();
        setupViewPager();
       // setupToolBar();
        setupFirebaseAuth();
        setupActivityWidgets();

        isFollowing();

        getFollowingCount();
        getFollowersCount();
        getPostCount();

        follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"FOLLOW ON CLICK LISTENER:Takip edilen kişi"+mUser.getUsername());
                FirebaseDatabase.getInstance().getReference()
                        .child(getString(R.string.dbname_following))
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(mUser.getUserID())
                        .child(getString(R.string.field_user_id))
                        .setValue(mUser.getUserID());

                FirebaseDatabase.getInstance().getReference()
                        .child(getString(R.string.dbname_followers))
                        .child(mUser.getUserID())
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(getString(R.string.field_user_id))
                        .setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                setFollowing();
            }
        });

        unfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"UNFOLLOW ON CLICK LISTENER:Takipten çıkarılan kişi"+mUser.getUsername());

                FirebaseDatabase.getInstance().getReference()
                        .child(getString(R.string.dbname_following))
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(mUser.getUserID())
                        .removeValue();

                FirebaseDatabase.getInstance().getReference()
                        .child(getString(R.string.dbname_followers))
                        .child(mUser.getUserID())
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .removeValue();

                setUnfollowing();
            }
        });
    }
    private void init(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_user_bilgileri))
                .orderByChild(getString(R.string.field_user_id)).equalTo(mUser.getUserID());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot:dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: Found user:" + singleSnapshot.getValue(UserBilgileri.class).toString());
                    UserSettings settings=new UserSettings();
                    settings.setUsers(mUser);
                    settings.setSettings(singleSnapshot.getValue(UserBilgileri.class));
                    setProfileWidgets(settings);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void getFollowersCount(){

        followersCount=0;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_followers))
                .child(mUser.getUserID());
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
                .child(mUser.getUserID());
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
        normalPhotosPostCount=0;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_user_photos_normal))
                .child(mUser.getUserID());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot:dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: GÖNDERİ BULUNDU:" + singleSnapshot.getValue());
                    normalPhotosPostCount++;
                }
                myPosts.setText(String.valueOf(normalPhotosPostCount));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        postsCount=normalPhotosPostCount;

        helpPhotosPostCount=0;
        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference();
        Query query2 = reference2
                .child(getString(R.string.dbname_user_photos_help))
                .child(mUser.getUserID());
        query2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot1) {
                for(DataSnapshot singleSnapshot1:dataSnapshot1.getChildren()){
                    Log.d(TAG, "onDataChange: GÖNDERİ BULUNDU:" + singleSnapshot1.getValue());
                    helpPhotosPostCount++;
                }
                postsCount=postsCount+helpPhotosPostCount;
                myPosts.setText(String.valueOf(postsCount));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void isFollowing(){
        Log.d(TAG,"isFollowing: Kullanıcının takip durumu kontrolü ");
        setUnfollowing();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_following))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .orderByChild(getString(R.string.field_user_id)).equalTo(mUser.getUserID());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot:dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: KULLANICI BULUNDU:" + singleSnapshot.getValue());
                    setFollowing();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void setFollowing()
    {
        Log.d(TAG,"Takip EDİLDİ UI ayarlanıyor");
        follow.setVisibility(View.GONE);
        unfollow.setVisibility(View.VISIBLE);
        editProfile.setVisibility(View.GONE);
    }

    private void setUnfollowing()
    {
        Log.d(TAG,"Takipten ÇIKARILDI UI ayarlanıyor");
        follow.setVisibility(View.VISIBLE);
        unfollow.setVisibility(View.GONE);
        editProfile.setVisibility(View.GONE);
    }

    private void setupCurrentUser(){
        Log.d(TAG,"Kendi profili UI ayarlanıyor");
        follow.setVisibility(View.GONE);
        unfollow.setVisibility(View.GONE);
        editProfile.setVisibility(View.VISIBLE);
    }


    private void setProfileWidgets(UserSettings userSettings){
        //Log.d(TAG, "setProfileWidgets: Veritabanından çekilen bilgileri yükler." + userSettings.toString());

        //Users users = userSettings.getUsers();
        UserBilgileri settings = userSettings.getSettings();

        UniversalImageLoader.setImage(settings.getProfil_foto(), mProfilePhoto, null, "");
        myName.setText(settings.getName());
        myUsername.setText(settings.getUsername());
        myFollowers.setText(String.valueOf(settings.getTakipciSay()));
        myFollowing.setText(String.valueOf(settings.getTakipSay()));
        myPosts.setText(String.valueOf(settings.getGonderiSay()));
        myBiyografi.setText(settings.getBiyografi());
    }

    private void setupViewPager(){

        tabLayout = (TabLayout) findViewById(R.id.profile_tab);
        viewPager = (ViewPager) findViewById(R.id.container);
        ViewPagerAdapter spAdapter = new ViewPagerAdapter(getSupportFragmentManager());


        spAdapter.addFragment(new ViewNormalPhotosFragment());
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
