package com.helpetapplicationgmail.helpet.Share;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.helpetapplicationgmail.helpet.Profile.AccountSettingsActivity;
import com.helpetapplicationgmail.helpet.Profile.ProfileActivity;
import com.helpetapplicationgmail.helpet.R;
import com.helpetapplicationgmail.helpet.Utils.FirebaseMethods;

import java.util.ArrayList;

/**
 * Created by acer on 4.05.2018.
 */

public class SharePostActivity extends AppCompatActivity {
    private static final String TAG = "SharePostFragment";

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase myFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;

    //vars

    //widgets
    private TextView share;
    private EditText mPost;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_status);
        mFirebaseMethods= new FirebaseMethods(SharePostActivity.this);
        mPost = (EditText) findViewById(R.id.etSharePost);
        setupFirebaseAuth();
        share = (TextView) findViewById(R.id.tvShare);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String post = mPost.getText().toString();
                Toast.makeText(SharePostActivity.this, "Gönderiniz paylaşılıyor..", Toast.LENGTH_SHORT).show();
                mFirebaseMethods.addNewPostToDatabase(post);
                Toast.makeText(SharePostActivity.this, "Gönderiniz paylaşıldı.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SharePostActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });
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
