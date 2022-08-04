package com.helpetapplicationgmail.helpet.Profile;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.helpetapplicationgmail.helpet.Others.LoginActivity;
import com.helpetapplicationgmail.helpet.R;
import com.helpetapplicationgmail.helpet.Utils.FirebaseMethods;
import com.helpetapplicationgmail.helpet.Utils.UniversalImageLoader;
import com.helpetapplicationgmail.helpet.dialogs.ConfirmPasswordDialog;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by acer on 12.03.2018.
 */
public class EditPasswordFragment extends Fragment implements ConfirmPasswordDialog.OnConfirmPasswordListener{
    public void onConfirmPassword(final String password){
        Log.d(TAG,"Şifre alındı");
        mAuth= FirebaseAuth.getInstance();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential credential = EmailAuthProvider
                .getCredential(user.getEmail(), password);
        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Reauthenticate BAŞARILI");
                            mAuth.sendPasswordResetEmail(user.getEmail())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "Email gönderildi.");
                                                Toast.makeText(getActivity(), "Şifrenizi değiştirmek üzere, e-posta adresinize mail gönderilmiştir.", Toast.LENGTH_SHORT).show();
                                            }else{
                                                Log.d(TAG, "Şifre sıfırlamak için email gönderilemedi");
                                                Toast.makeText(getActivity(), "Lütfen daha sonra tekrar deneyiniz", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }else{
                            Log.d(TAG,"Reauthenticate BAŞARISIZ!!");
                            Toast.makeText(getActivity(),"İşlem başarısız,lütfen şifrenizi kontrol ediniz.",Toast.LENGTH_SHORT).show();
                        }
                    }});}
    private static final String TAG = "EditPasswordFragment";
    private EditText changePassword;
    private ImageView saveChangesPassword;
    private FirebaseAuth mAuth;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_password, container, false);

        changePassword=(EditText) view.findViewById(R.id.current_email_editpassword);
        saveChangesPassword=(ImageView) view.findViewById(R.id.saveChangesPassword);
        mAuth=FirebaseAuth.getInstance();
        changePassword.setText(mAuth.getCurrentUser().getEmail().toString());

        saveChangesPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfirmPasswordDialog dialog=new ConfirmPasswordDialog();
                dialog.show(getFragmentManager(),getString(R.string.confirm_password_dialog));
                dialog.setTargetFragment(EditPasswordFragment.this,1);
            }
        });
        //geridön butonu -> ProfileActivity
        ImageView backArrow = (ImageView) view.findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: ProfileActivity'e geri yönlendiriliyor.");
                getActivity().finish();
            }
        });
        return view;
    }

}
