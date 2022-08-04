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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.helpetapplicationgmail.helpet.Models.UserBilgileri;
import com.helpetapplicationgmail.helpet.Models.UserSettings;
import com.helpetapplicationgmail.helpet.Models.Users;
import com.helpetapplicationgmail.helpet.R;
import com.helpetapplicationgmail.helpet.Share.ShareActivity;
import com.helpetapplicationgmail.helpet.Utils.FirebaseMethods;
import com.helpetapplicationgmail.helpet.Utils.UniversalImageLoader;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileFragment extends Fragment {
    private static final String TAG = "EditProfileFragment";
    private ImageView myProfilePhoto;
    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase myFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;
    private String userID;
    //nesneler
    private EditText mUsername,mName,mDescription,mEmail;
    private Spinner mGenderitem,yasadigiyeritem;
    private TextView mChangeProfilePhoto;
    private CircleImageView mProfilePhoto;
    //variables
    private UserSettings mUserSettings;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        mProfilePhoto = (CircleImageView) view.findViewById(R.id.profile_photo);
        mName=(EditText) view.findViewById(R.id.display_name);
        mUsername=(EditText) view.findViewById(R.id.username);
        mEmail=(EditText) view.findViewById(R.id.email);
        mDescription=(EditText) view.findViewById(R.id.biography);
        mGenderitem=(Spinner) view.findViewById(R.id.genderSpinner);
        yasadigiyeritem=(Spinner) view.findViewById(R.id.citiesSpinner);
        mChangeProfilePhoto=(TextView) view.findViewById(R.id.change_photo);
        mFirebaseMethods=new FirebaseMethods(getActivity());
       // setProfileImage();
        setupFirebaseAuth();
        //geridön butonu -> ProfileActivity
        ImageView backArrow = (ImageView) view.findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: ProfileActivity'e geri yönlendiriliyor.");
                getActivity().finish();
            }
        });
        ImageView checkmark= (ImageView) view.findViewById(R.id.saveChangesProfile);
        checkmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"onClick: Profil ayarları düzenlenmek için tıklandı");
                saveProfileSettings();
            }
        });
        Log.d(TAG,"EditProfileFragment: başlatıldı. ");
        return view;

    }
    public boolean nameisWrong(String name) {
        //Ad Soyad REGEX
        if (name.matches("^\\p{L}+[\\p{L}\\p{Z}\\p{P}]{0,}")){
            return true;
        }else
        return false;
    }
    public boolean usernameisWrong(String username) {
        //Kullanıcı Adı REGEX
        if (username.matches("[a-zA-Z_0-9]+")){
            return true;
        }else
            return false;
    }
    private void saveProfileSettings() {
        final String name = mName.getText().toString();
        final String username = mUsername.getText().toString();
        final String desciption = mDescription.getText().toString();
        final String yasadigiyer = yasadigiyeritem.getSelectedItem().toString();
        final String cinsiyet = mGenderitem.getSelectedItem().toString();
        //Kullanıcıya ilgili hatayı göster
        if(nameisWrong(name) || usernameisWrong(username)) {
            if (usernameisWrong(username)) {
                if (nameisWrong(name)) {
                    if (!mUserSettings.getUsers().getUsername().equals(username)) {
                        checkIfUsernameExists(username, name, desciption, yasadigiyer, cinsiyet);
                    } else {
                        mFirebaseMethods.updateUserAccountSettings(name, desciption, yasadigiyer, cinsiyet);
                        Toast.makeText(getActivity(), "Profil bilgileriniz güncellendi.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Ad Soyad alanını kontrol ediniz.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), "Kullanıcı Adınızı kontrol ediniz.", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(getActivity(), "Kullanıcı Adı ve Ad Soyad alanlarınızı kontrol ediniz.", Toast.LENGTH_SHORT).show();
        }
    }
    //@param username veritabanında kayıtlımı
    private void checkIfUsernameExists(final String username,final String name,final String description,final String yasadigiyer,final String cinsiyet) {
        Log.d(TAG,"EditProfile: checkIfUsernameExists: " +username);
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference();
        Query query=reference.
                child(getString(R.string.dbname_users)).
                orderByChild(getString(R.string.field_username))
                .equalTo(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    //kullanıcı adı kullanımda değilse güncelle
                        mFirebaseMethods.updateUsername(username);
                        mFirebaseMethods.updateUserAccountSettings(name,description,yasadigiyer,cinsiyet);
                        Toast.makeText(getActivity(), "Bu kullanıcı GÜNCELLENDİ", Toast.LENGTH_SHORT).show();
                }
                for(DataSnapshot singleSnapshot:dataSnapshot.getChildren()){
                    if(singleSnapshot.exists()){
                        //Aynı kullanıcı adı var ise güncelleme
                        Log.d(TAG,"checkIfUsernameExists: EŞLEŞTİRME BULUNDU!!"+singleSnapshot.getValue(Users.class).getUsername());
                        Toast.makeText(getActivity(),"Bu kullanıcı adı var",Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    private void setProfileWidgets(UserSettings userSettings){
        Log.d(TAG,"EditProfileFragment:setProfileWidgets: Veritabanından alınan verilerle:"+userSettings.toString());
        Log.d(TAG,"EditProfileFragment:setProfileWidgets: Veritabanından alınan verilerle:"+userSettings.getUsers().getEmail());
        mUserSettings=userSettings;
        UserBilgileri settings = userSettings.getSettings();
        UniversalImageLoader.setImage(settings.getProfil_foto(), mProfilePhoto, null, "");
        mName.setText(settings.getName());
        mUsername.setText(settings.getUsername());
        mDescription.setText(settings.getBiyografi());
        mEmail.setText(userSettings.getUsers().getEmail());
        for(int x=0;x<82;x++){
            if (yasadigiyeritem.getItemAtPosition(x).equals(settings.getYasadigiyer())){
                yasadigiyeritem.setSelection(x);
                break;
            }
         for(int y=0;y<3;y++){
             if (mGenderitem.getItemAtPosition(y).equals(settings.getCinsiyet())) {
                 mGenderitem.setSelection(y);
                 break;
             }
                }
        }
        //Fotoğrafı Değiştir
        mChangeProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: changin profile photo");
                Intent intent = new Intent(getActivity(), ShareActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //268435456
                getActivity().startActivity(intent);
                getActivity().finish();
            }
        });
    }
   private void setupFirebaseAuth() {
        Log.d(TAG,"setupFirebaseAuth: firebase auth yükleniyor");
        mAuth = FirebaseAuth.getInstance();
        myFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = myFirebaseDatabase.getReference();
        userID=mAuth.getCurrentUser().getUid();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    Log.d(TAG, "EditProfile:onAuthStateChanged:signed_in"+user.getUid());
                }
                else {
                    Log.d(TAG,"EditProfile:onAuthStateChanged:signed_out");
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
}
