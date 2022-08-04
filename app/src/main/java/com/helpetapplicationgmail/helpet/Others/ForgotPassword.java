package com.helpetapplicationgmail.helpet.Others;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.helpetapplicationgmail.helpet.R;

/**
 * Created by acer on 12.04.2018.
 */

public class ForgotPassword extends AppCompatActivity {


    private static final String TAG = "ForgotPassword";

    private Context mContext;
    private EditText forgotpassword;
    private Button sifreyenile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**arka plan fotoğrafını çağırdık!!*/
        getWindow().setBackgroundDrawableResource(R.drawable.login_background);
        setContentView(R.layout.activity_forgot_password);
        mContext = ForgotPassword.this;
        forgotpassword=(EditText) findViewById(R.id.input_email_forgotpassword);
        sifreyenile=(Button) findViewById(R.id.btn_sifreyenile);

        //Buton'a tıklandığında:
        sifreyenile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "LOGIN-->FORGOTPASSWORD:Şifre yenilemek için TIKLANDI");
                //Durum1:Email girişi yapıldıysa email parametresini methoda aktar.
                if(!forgotpassword.getText().toString().equals("")){
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    String emailAddress = forgotpassword.getText().toString();
                    auth.sendPasswordResetEmail(emailAddress)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        //Durum1.1:İşlem başarılı
                                        Log.d(TAG, "LOGIN-->FORGOTPASSWORD:Şifre yenilemek için email gönderildi");
                                        Toast.makeText(mContext,"Şifrenizi yenilemek için mail gönderilmiştir.",Toast.LENGTH_SHORT).show();
                                    }else{
                                        //Durum1.2:İşlem başarısız ise,email hatalıdır.
                                        Toast.makeText(mContext,"Hatalı veya eksik giriş yaptınız",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }else{
                    //Durum2:
                    Toast.makeText(mContext,"Email alanını boş bırakamazsınız",Toast.LENGTH_SHORT).show();
                }
            }});
    }}
