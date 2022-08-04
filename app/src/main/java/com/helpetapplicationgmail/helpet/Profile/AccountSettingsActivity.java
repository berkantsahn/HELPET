package com.helpetapplicationgmail.helpet.Profile;

import android.accounts.Account;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.ImageViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.helpetapplicationgmail.helpet.R;

import com.helpetapplicationgmail.helpet.Utils.BottomNavigationViewHelper;
import com.helpetapplicationgmail.helpet.Utils.FirebaseMethods;
import com.helpetapplicationgmail.helpet.Utils.SectionsStatePagerAdapter;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;

import static com.helpetapplicationgmail.helpet.Utils.BottomNavigationViewHelper.setupBottomNavigationView;

/**
 * Created by acer on 10.03.2018.
 */

public class AccountSettingsActivity extends AppCompatActivity {

    private static final String TAG = "AccountSettingsActivity";
    private Context mContext;
    private static final int ACTIVITY_NUM = 4;
    ListView textSettings;
    String[] titles;

    //setupFragments -- setViewPager değişkenleri
    public SectionsStatePagerAdapter myPagerAdapter;
    private ViewPager myViewPager;
    private RelativeLayout myRelativeLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);
        mContext = AccountSettingsActivity.this;
        Log.d(TAG, "onCreate: started.");
        //layout_center_viewpager.xml içerisindeki container çekildi.
        myViewPager = (ViewPager) findViewById(R.id.container);
        //activity_account_settings.xml içerisindeki relLayout1 çekildi.
        myRelativeLayout = (RelativeLayout) findViewById(R.id.relLayout1);
        //metodları çağırma
        setupBottomNavigationView();
        setupSettingsList();
        setupFragments();
        //yeni
        getIncomingIntent();
        //yeni
        // -- son metodları çağırma
        //geri dön butonu
        ImageView backArrow = (ImageView) findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: ProfileActivity'e yönlendirildi");
                finish();
            }
        });

    }

    private void getIncomingIntent(){
        Intent intent = getIntent();

        if(intent.hasExtra(getString(R.string.selected_image))
                || intent.hasExtra(getString(R.string.selected_bitmap)) ){

            //if there is an imageUrl attached as an extra, then it was chosen from the gallery/photo fragment
        Log.d(TAG," getIncomingIntent: new incoming imgUrl");
        if(intent.getStringExtra(getString(R.string.return_to_fragment)).equals(getString(R.string.profili_duzenle))){

            if(intent.hasExtra(getString(R.string.selected_image))){
                //set the new profile picture
                FirebaseMethods firebaseMethods = new FirebaseMethods(AccountSettingsActivity.this);
                firebaseMethods.uploadNewPhoto(getString(R.string.profil_foto), null, 0,
                        intent.getStringExtra(getString(R.string.selected_image)), null);
            }
            else if(intent.hasExtra(getString(R.string.selected_bitmap))){
                //set the new profile picture
                FirebaseMethods firebaseMethods = new FirebaseMethods(AccountSettingsActivity.this);
                firebaseMethods.uploadNewPhoto(getString(R.string.profil_foto), null, 0,
                        null,(Bitmap) intent.getParcelableExtra(getString(R.string.selected_bitmap)));
            }}}

            if(intent.hasExtra(getString(R.string.calling_activity))){
            setViewPager(myPagerAdapter.getFragmentNumber(getString(R.string.profili_duzenle)));
        }
    }

    @SuppressLint("WrongViewCast")
    private void setupFragments() {

        myPagerAdapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
        myPagerAdapter.addFragment(new EditProfileFragment(), getString(R.string.profili_duzenle)); //fragmanNo 0
        myPagerAdapter.addFragment(new EditEmailFragment(), getString(R.string.epostami_degistir)); //fragmanNo 1
        myPagerAdapter.addFragment(new EditPasswordFragment(), getString(R.string.sifremi_degistir)); //fragmanNo 2
        myPagerAdapter.addFragment(new ReportProblemsFragment(), getString(R.string.sorun_bildir)); //fragmanNo 3
        myPagerAdapter.addFragment(new PrivacyPolicyFragment(), getString(R.string.gizlilik_ilkesi)); //fragmanNo 4
        myPagerAdapter.addFragment(new WhatsHelpetFragment(), getString(R.string.helpet_nedir)); // fragmanNo 5
        myPagerAdapter.addFragment(new SignOutFragment(), getString(R.string.cikis_yap)); // fragmanNo 6

    }


    /**
     * BottomNavigationView(Alt menu) SETUP
     */
    private void setupBottomNavigationView() {

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

    /** **/
    private void setupSettingsList() {
        Log.d(TAG, "setupSettingsList: Hesap ayarları listesini başlat.");
        ListView listView = (ListView) findViewById(R.id.lvAccountSetting);
        ArrayList<String> ayarlar = new ArrayList<>();

        /** values dosyası > strings.xml'den çekiliyor.**/
        ayarlar.add(getString(R.string.profili_duzenle));
        ayarlar.add(getString(R.string.epostami_degistir));
        ayarlar.add(getString(R.string.sifremi_degistir));
        ayarlar.add(getString(R.string.sorun_bildir));
        ayarlar.add(getString(R.string.gizlilik_ilkesi));
        ayarlar.add(getString(R.string.helpet_nedir));
        ayarlar.add(getString(R.string.cikis_yap));

        ArrayAdapter myAdapter = new ArrayAdapter(mContext, android.R.layout.simple_list_item_1, ayarlar);
        listView.setAdapter(myAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: Yönlendirildi -> Fragman No:" + position);
                setViewPager(position);
            }
        });
    }
    public void setViewPager(int fragmentNumber) {
        myRelativeLayout.setVisibility(View.GONE);
        Log.d(TAG, "setViewPager: Yönlendiriliyor -> Fragman No:" + fragmentNumber);
        myViewPager.setAdapter(myPagerAdapter);
        myViewPager.setCurrentItem(fragmentNumber);
    }
}

