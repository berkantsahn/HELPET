package com.helpetapplicationgmail.helpet.Others;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.helpetapplicationgmail.helpet.Models.Users;
import com.helpetapplicationgmail.helpet.R;
import com.helpetapplicationgmail.helpet.Utils.FirebaseMethods;

/**
 * Created by acer on 3.04.2018.
 */

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    private Context mContext;
    private String name,email,username,password, biyografi, ceptel, cinsiyet,yasadigiyer;
    private int takipciSay, takipSay, gonderiSay;
    private EditText myName, myEmail, myUsername, myPassword;
    private Button btnKayit;
    private CheckBox sozlesme;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseMethods firebaseMethods;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;

    private String ekle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        /**arka plan fotoğrafını çağırdık!!*/
        getWindow().setBackgroundDrawableResource(R.drawable.login_background);

        setContentView(R.layout.activity_register);
        mContext = RegisterActivity.this;
        firebaseMethods = new FirebaseMethods(mContext);
        Log.d(TAG,"Başlatıldı: Register");

        initWidgets();
        setupFirebaseAuth();
        initKayit();
    }
    /**
     * Firebase Auth. yeni Kayıt işlemi
     * parametreler sırası ile:
     * name : ad soyad
     * email : eposta
     * username : kullanıcı adı
     * password: şifre
     * biyografi : biyografi
     * ceptel : cep telefonu
     * cinsiyet : cinsiyet
     * takipciSay : takipci sayısı
     * takipSay : takip edilen sayısı
     * gonderiSay: gonderi sayısı
     * yasadigiYer: yaşadığı yer -- EKLENECEK -- EKLENMEDİ!!!
     * **/
    private void initKayit(){
        btnKayit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = myName.getText().toString();
                email = myEmail.getText().toString();
                username = myUsername.getText().toString();
                password = myPassword.getText().toString();
                biyografi = "-";
                ceptel = "-";
                cinsiyet = "Belirtilmedi";
                takipciSay = 0;
                takipSay = 0;
                gonderiSay = 0;
                yasadigiyer="Belirtilmedi";
                if (!sozlesme.isChecked()){
                    Toast.makeText(mContext,"KULLANICI SÖZLEŞMESİ KABUL EDİLMEDİ",Toast.LENGTH_SHORT).show();
                }else{
                    if(checkInputs(name,email,username,password)){
                        firebaseMethods.registerNewEmail(name, email, username, password);
                    }
                }
            }
        });
    }

    private boolean checkInputs(String name, String email, String username, String password){
        Log.d(TAG,"checkInputs: Boş değerleri kontrol ediyor.");
        if(name.equals("") || email.equals("") || username.equals("") || password.equals("")){
            Toast.makeText(mContext, "Bilgilerinizi eksik girdiniz. Lütfen kontrol edin.",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void initWidgets(){
        Log.d(TAG,"initWidgets: Widget başlatılıyor.");
        myName = (EditText) findViewById(R.id.input_name_register);
        myEmail = (EditText) findViewById(R.id.input_email_register);
        myUsername = (EditText) findViewById(R.id.input_username_register);
        myPassword = (EditText) findViewById(R.id.input_password_register);
        btnKayit = (Button) findViewById(R.id.btn_kayit);
        sozlesme=(CheckBox) findViewById(R.id.sozlesme);
        mContext = RegisterActivity.this;
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
    // ---------------------------------Firebase başlangıç ------------------------------//
    /**
     * setup firebase auth objesi
     * **/
    //@param username veritabanında kayıtlımı
    private void checkIfUsernameExists(final String username) {
        Log.d(TAG,"checkIfUsernameExists: " +username+ " kayıtlıdır");
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference();
        Query query=reference.
                child(getString(R.string.dbname_users)).
                orderByChild(getString(R.string.field_username))
                .equalTo(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    if (singleSnapshot.exists()) {
                        Log.d(TAG, "checkIfUsernameExists: EŞLEŞTİRME BULUNDU!!" + singleSnapshot.getValue(Users.class).getUsername());
                        ekle = myRef.push().getKey().substring(3, 10);
                        Log.d(TAG, "onDataChange: Kullanıcı adı daha önceden alındı. Rastgele kullanıcı adı oluşturuluyor" + ekle);
                        Toast.makeText(mContext, "Kullanıcı adı zaten alınmış", Toast.LENGTH_LONG).show();
                    }
                }
                String mUsername = "";
                mUsername = username + ekle;
                // Databaseye yeni üye ekleniyor > users kısmına.
                firebaseMethods.yeniKullaniciEkleUsers(email,mUsername,name,cinsiyet,biyografi,"-",yasadigiyer,0,0,0);
                Toast.makeText(mContext, "Kayıt başarılı. Doğrulama E-Postanız Gönderiliyor.", Toast.LENGTH_SHORT).show();
                //Databaseye yeni üye ekleniyor > user_bilgileri kısmına.
                mAuth.signOut();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void setupFirebaseAuth() {

        Log.d(TAG,"setupFirebaseAuth: firebase auth yükleniyor");
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user != null){
                    Log.d(TAG, "onAuthStateChanged:signed_in"+user.getUid());
                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        //işlem başarılı ise üstteki değil ise alttaki metod çalışır!
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            checkIfUsernameExists(username);
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                    finish();
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

    // ----------- Firebase Son ------------------------
}
