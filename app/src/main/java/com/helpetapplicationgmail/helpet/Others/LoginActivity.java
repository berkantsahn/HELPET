package com.helpetapplicationgmail.helpet.Others;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.helpetapplicationgmail.helpet.Home.HomeActivity;
import com.helpetapplicationgmail.helpet.R;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    //nesneler
    private ProgressBar myProgressBar;
    private EditText myEmail, myPassword;
    private Button btn_fb;
    //currentcontext
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**arka plan fotoğrafını çağırdık!!*/
        getWindow().setBackgroundDrawableResource(R.drawable.login_background);
        setContentView(R.layout.activity_login);
        myProgressBar = (ProgressBar) findViewById(R.id.loginRequestLoadingProgressbar);
        myEmail = (EditText) findViewById(R.id.input_email_login);
        myPassword = (EditText) findViewById(R.id.input_password_login);
        btn_fb=(Button) findViewById(R.id.btn_facebook);
        mContext = LoginActivity.this;
        Log.d(TAG, "Başlatıldı: Login");
        myProgressBar.setVisibility(View.GONE);

        //Facebook İle Giriş Yap
        setBtn_fb();
        //firebase
        setupFirebaseAuth();
        //Giriş Yap
        initLogin();
    }
    private void setBtn_fb(){
        btn_fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,"Yapım aşamasındadır..",Toast.LENGTH_SHORT).show();
            }
        });
    }
    private boolean isStringNull(String string){
        Log.d(TAG,"isStringNull: Stringlerin boş olup olmadığı kontrol ediliyor.");
        if(string.equals("")){
            return true;
        }
        else{
            return false;
        }
    }
/*
---------------------------------------------- Firebase başlangıç -----------------------------------------------------------
*/
    private void initLogin(){
        Button btnLogin = (Button) findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                    Log.d(TAG,"onClickLogin: Giriş Yap butonuna basıldı.");
                    String email = myEmail.getText().toString();
                    String password = myPassword.getText().toString();
                    if(isStringNull(email) && isStringNull(password)) {
                        Toast.makeText(mContext, "E-Posta veya Şifre bölümünü boş bıraktınız.", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        myProgressBar.setVisibility(View.VISIBLE);
                        mAuth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        Log.d(TAG, "signInWithEmail: Tamamlandı." + task.isSuccessful());
                                        FirebaseUser user=mAuth.getCurrentUser();
                                        if(!task.isSuccessful()){
                                            //Alanlar hatalı/eksik girilmiş
                                            Log.w(TAG,"signInWithEmail:Hata", task.getException());
                                            Toast.makeText(LoginActivity.this, getString(R.string.auth_failed),
                                            Toast.LENGTH_SHORT).show();
                                            myProgressBar.setVisibility(View.GONE);
                                        }
                                        else{
                                            //Verification tamamlanmadan giriş yapılamasın
                                            try { if (user.isEmailVerified()){
                                                Log.d(TAG,"Email doğrulanmıştır");
                                                Intent intent= new Intent(LoginActivity.this,HomeActivity.class);
                                                startActivity(intent);
                                            }else{
                                                Toast.makeText(mContext,"Email adresinizi doğrulamadan giriş yapamazsınız",Toast.LENGTH_SHORT).show();
                                                myProgressBar.setVisibility(View.GONE);
                                                mAuth.signOut();
                                            }
                                            }catch(NullPointerException e){
                                                Log.e(TAG,"NullPointerException:DOĞRULAMA"+e.getMessage());
                                            }
                                        }
                                    }
                                });
                    }
            }
        });
        //Kayıt ol
        TextView linkSignUp = (TextView) findViewById(R.id.linkSignUp);
        linkSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "onClick: Kaydol ekranına yönlendiriliyor.");
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);

            }
        });
        //Şifremi Unuttum
        TextView forgotPassword = (TextView) findViewById(R.id.forgotPassword);
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Şifremi unuttum ekranına yönlendiriliyor.");
                Intent intents = new Intent(LoginActivity.this,ForgotPassword.class);
                startActivity(intents);

            }
        });
        //kullanıcı giriş yaptıysa anasayfaya yönlendiriliyor.
        if(mAuth.getCurrentUser()!=null){
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }

    }
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
