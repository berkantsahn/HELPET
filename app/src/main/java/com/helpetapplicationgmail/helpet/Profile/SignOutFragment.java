package com.helpetapplicationgmail.helpet.Profile;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ThrowOnExtraProperties;
import com.helpetapplicationgmail.helpet.Others.LoginActivity;
import com.helpetapplicationgmail.helpet.R;

import org.w3c.dom.Text;

/**
 * Created by acer on 12.03.2018.
 */
public class SignOutFragment extends Fragment {

    private static final String TAG = "SignOutFragment";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_out, container, false);
        Button tvYes = (Button) view.findViewById(R.id.tvYesSignOut);
        setupFirebaseAuth();

        tvYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAuth.signOut();
                goToLogin();
            }
        });

        Button tvNo = (Button) view.findViewById(R.id.tvNoSignOut);

        tvNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToProfile();
            }
        });

        return view;
    }

    public void goToLogin(){
        Intent startIntent = new Intent(getActivity(), LoginActivity.class);
        startIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(startIntent);
    }

    public void goToProfile(){
        Intent profileIntent = new Intent(getActivity(), ProfileActivity.class);
        startActivity(profileIntent);
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