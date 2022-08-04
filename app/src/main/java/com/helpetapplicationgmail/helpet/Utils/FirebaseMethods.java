package com.helpetapplicationgmail.helpet.Utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.helpetapplicationgmail.helpet.Home.HomeActivity;
import com.helpetapplicationgmail.helpet.Models.PhotoHelp;
import com.helpetapplicationgmail.helpet.Models.PhotoNormal;
import com.helpetapplicationgmail.helpet.Models.Posts;
import com.helpetapplicationgmail.helpet.Models.UserBilgileri;
import com.helpetapplicationgmail.helpet.Models.UserSettings;
import com.helpetapplicationgmail.helpet.Models.Users;
import com.helpetapplicationgmail.helpet.Profile.AccountSettingsActivity;
import com.helpetapplicationgmail.helpet.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by acer on 9.04.2018.
 */

public class FirebaseMethods {

    private static final String TAG = "FirebaseMethods";

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private String userID;
    private StorageReference mStorageReference;

    //vars
    private Context mContext;
    private double mPhotoUploadProgress = 0;

    public FirebaseMethods(Context context){

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mContext = context;

        if(mAuth.getCurrentUser() != null){
            userID = mAuth.getCurrentUser().getUid();
        }
    }

    //Yazı İşleri Baş

    public void addNewPostToDatabase(final String post){
        Log.d(TAG, "uploadNewPost: Attempting to upload new post.");

        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String tags = StringManipulation.getTags(post);

        String newPhotoKey = myRef.child(mContext.getString(R.string.dbname_posts)).push().getKey();
        Posts posts = new Posts();

        posts.setPost(post);
        posts.setDate_created(getTimestamp());
        posts.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());
        posts.setPost_id(newPhotoKey);
        posts.setTags(tags);


        //insert into database
        myRef.child(mContext.getString(R.string.dbname_user_posts))
                .child(FirebaseAuth.getInstance()
                        .getCurrentUser().getUid()).child(newPhotoKey).setValue(posts);
        myRef.child(mContext.getString(R.string.dbname_posts)).child(newPhotoKey).setValue(posts);

    }

    //Yazı İşlero Son

    //Fotoğraf işleri baş -- PhotoHelp

    public void uploadNewPhotoHelp(String photoType, final String caption, final String live, final int count, String imgUrl, Bitmap bm){
        Log.d(TAG, "uploadNewPhotoHelp: Attempting to upload new photo help.");

        FilePaths filePaths = new FilePaths();
        //case1: new_photo_help
        if(photoType.equals(mContext.getString(R.string.new_photo_help))) {
            Log.d(TAG, "uploadNewPhotoHelp: uploading new photo.");

            String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            StorageReference storageReference = mStorageReference
                    .child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/photohelp" + (count+1));

            //convert imageUrl to Bitmap
            if(bm==null){
                bm = ImageManager.getBitmap(imgUrl);
            }

            byte[] bytes = ImageManager.getBytesFromBitmap(bm, 100);

            UploadTask uploadTask = null;
            uploadTask = storageReference.putBytes(bytes);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Uri firebaseUrl = taskSnapshot.getDownloadUrl();
                    Toast.makeText(mContext, "Fotoğraf başarıyla yüklendi.", Toast.LENGTH_SHORT).show();

                    //add new photo to 'photos_help' node and 'user_photos_help' node
                    addPhotoToDatabaseHelp(caption, live,firebaseUrl.toString());
                    //navigate to the main feed so the user can see their photo
                    Intent intent = new Intent(mContext, HomeActivity.class);
                    mContext.startActivity(intent);



                }
            });

        }

    }

    //Fotoğraf işleri son -- PhotoHelp


    //Fotoğraf işleri baş -- PhotoNormal

    public void uploadNewPhoto(String photoType, final String caption, final int count, String imgUrl, Bitmap bm){
        Log.d(TAG, "uploadNewPhoto: Attempting to upload new photo.");

        FilePaths filePaths = new FilePaths();
        //case1: new_photo_normal
        if(photoType.equals(mContext.getString(R.string.new_photo_normal))) {
            Log.d(TAG, "uploadNewPhoto: uploading new photo.");

            String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            StorageReference storageReference = mStorageReference
                    .child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/photonormal" + (count+1));

            //convert image url to bitmap
            if(bm==null){
                bm = ImageManager.getBitmap(imgUrl);
            }

            byte[] bytes = ImageManager.getBytesFromBitmap(bm, 100);

            UploadTask uploadTask = null;
            uploadTask = storageReference.putBytes(bytes);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Uri firebaseUrl = taskSnapshot.getDownloadUrl();
                    Toast.makeText(mContext, "Fotoğraf yükleme işlemi başarıyla gerçekleştirildi.", Toast.LENGTH_SHORT).show();

                    //add new photo to 'photos_normal' node and 'user_photos_normal' node
                    addPhotoToDatabase(caption, firebaseUrl.toString());
                    //navigate to the main feed so the user can see their photo
                    Intent intent = new Intent(mContext, HomeActivity.class);
                    mContext.startActivity(intent);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: Photo upload failed.");
                    Toast.makeText(mContext, "Fotoğraf yükleme işlemi başarısız oldu.", Toast.LENGTH_SHORT).show();

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100*taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                    if(progress - 15 > mPhotoUploadProgress){
                        Toast.makeText(mContext, "Fotoğraf yükleniyor: " + String.format("%.0f", progress)+"%", Toast.LENGTH_SHORT).show();
                        mPhotoUploadProgress = progress;
                    }

                    Log.d(TAG, "onProgress: upload progress:" + progress + "% done");
                }
            });

        }
        else if(photoType.equals(mContext.getString(R.string.profil_foto))){
            Log.d(TAG, "uploadNewPhoto: uploading new profile photo.");


            String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            StorageReference storageReference = mStorageReference
                    .child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/profile_photo");

            //convert image url to bitmap
            if(bm==null){
                bm = ImageManager.getBitmap(imgUrl);
            }
            byte[] bytes = ImageManager.getBytesFromBitmap(bm, 100);

            UploadTask uploadTask = null;
            uploadTask = storageReference.putBytes(bytes);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Uri firebaseUrl = taskSnapshot.getDownloadUrl();
                    Toast.makeText(mContext, "Fotoğraf başarıyla yüklendi.", Toast.LENGTH_SHORT).show();

                    //insert into 'user_bilgileri' node
                    setProfilePhoto(firebaseUrl.toString());

                    ((AccountSettingsActivity)mContext).setViewPager(
                            ((AccountSettingsActivity)mContext).myPagerAdapter
                                    .getFragmentNumber(mContext.getString(R.string.profili_duzenle))
                    );
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: Photo upload failed.");
                    Toast.makeText(mContext, "Fotoğraf yükleme başarısız oldu.", Toast.LENGTH_SHORT).show();

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100*taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                    if(progress - 15 > mPhotoUploadProgress){
                        Toast.makeText(mContext, "Fotoğraf yükleniyor: " + String.format("%.0f", progress)+"%", Toast.LENGTH_SHORT).show();
                        mPhotoUploadProgress = progress;
                    }

                    Log.d(TAG, "onProgress: upload progress:" + progress + "% done");
                }
            });
        }
        //case profile_photo
    }

    private void setProfilePhoto(String url){
        Log.d(TAG, "setProfilePhoto: setting new profile photo:" +url);
        myRef.child(mContext.getString(R.string.dbname_user_bilgileri))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(mContext.getString(R.string.profil_foto))
                .setValue(url);
    }

    private String getTimestamp(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("Turkey"));
        return sdf.format(new Date());
    }

    private void addPhotoToDatabaseHelp(String caption, String live, String url){
        Log.d(TAG, "addPhotoToDatabaseHelp: uploading photohelp to database");
        String tags = StringManipulation.getTags(caption);
        String newPhotoKey = myRef.child(mContext.getString(R.string.dbname_photos_help)).push().getKey();
        PhotoHelp photo = new PhotoHelp();
        photo.setCaption(caption);
        photo.setSendNot(live);
        photo.setDate_created(getTimestamp());
        photo.setImage_path(url);
        photo.setTags(tags);
        photo.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());
        photo.setPhoto_id(newPhotoKey);

        //insert into user_photos_help node
        myRef.child(mContext.getString(R.string.dbname_user_photos_help))
                .child(FirebaseAuth.getInstance()
                        .getCurrentUser().getUid()).child(newPhotoKey).setValue(photo);

        //insert into photos_help node
        myRef.child(mContext.getString(R.string.dbname_photos_help)).child(newPhotoKey).setValue(photo);

    }

    private void addPhotoToDatabase(String caption, String url){
        Log.d(TAG, "uploadNewPhoto: uploading photonormal to database");
        String tags = StringManipulation.getTags(caption);
        String newPhotoKey = myRef.child(mContext.getString(R.string.dbname_photos_normal)).push().getKey();
        PhotoNormal photo = new PhotoNormal();
        photo.setCaption(caption);
        photo.setDate_created(getTimestamp());
        photo.setImage_path(url);
        photo.setTags(tags);
        photo.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());
        photo.setPhoto_id(newPhotoKey);

        //insert into database
        myRef.child(mContext.getString(R.string.dbname_user_photos_normal))
                .child(FirebaseAuth.getInstance()
                        .getCurrentUser().getUid()).child(newPhotoKey).setValue(photo);
        myRef.child(mContext.getString(R.string.dbname_photos_normal)).child(newPhotoKey).setValue(photo);
    }



    public int getImageCountHelp(DataSnapshot dataSnapshot){
        int count = 0;
        for(DataSnapshot ds: dataSnapshot
                .child(mContext.getString(R.string.dbname_user_photos_help))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .getChildren()){
            count++;
        }
        return count;
    }



    public int getImageCount(DataSnapshot dataSnapshot){
        int count = 0;
        for(DataSnapshot ds: dataSnapshot
                .child(mContext.getString(R.string.dbname_user_photos_normal))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .getChildren()){
            count++;
        }
        return count;
    }

    //Fotoğraf işleri son -- PhotoNormal
    public void updateUsername(String username) {
        Log.d(TAG, "updateUsername: yeni kullanıcı adı" + username);

        myRef.child(mContext.getString(R.string.dbname_users))
                .child(userID)
                .child(mContext.getString(R.string.field_username))
                .setValue(username);

        myRef.child(mContext.getString(R.string.dbname_user_bilgileri))
                .child(userID)
                .child(mContext.getString(R.string.field_username))
                .setValue(username);
    }

    //name,bio,yasadigiyer,cinsiyet
    public void updateUserAccountSettings(String name,String biyografi,String yasadigiyer,String cinsiyet) {
        Log.d(TAG, "updateUsername: yeni kullanıcı bilgileri,İSİM,BİO useridsi=" + userID);

        myRef.child(mContext.getString(R.string.dbname_user_bilgileri))
                .child(userID)
                .child(mContext.getString(R.string.field_name))
                .setValue(name);

        myRef.child(mContext.getString(R.string.dbname_user_bilgileri))
                .child(userID)
                .child(mContext.getString(R.string.field_biyografi))
                .setValue(biyografi);

        myRef.child(mContext.getString(R.string.dbname_user_bilgileri))
                .child(userID)
                .child(mContext.getString(R.string.field_yasadigiyer))
                .setValue(yasadigiyer);

        myRef.child(mContext.getString(R.string.dbname_user_bilgileri))
                .child(userID)
                .child(mContext.getString(R.string.field_cinsiyet))
                .setValue(cinsiyet);
    }

    public void updateEmail(String email)
    {
        myRef.child(mContext.getString(R.string.dbname_users))
                .child(userID)
                .child(mContext.getString(R.string.field_email))
                .setValue(email);
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
    public void registerNewEmail(final String name, final String email, final String username, final String password){

        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>(){
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmailAndPassword: onComplete"+task.isSuccessful());
                        if(!task.isSuccessful()) {
                            Toast.makeText(mContext, R.string.kayit_basarisiz, Toast.LENGTH_SHORT).show();
                        }
                        else if(task.isSuccessful()){
                            sendVerificationEmail();
                            userID = mAuth.getCurrentUser().getUid();
                            Log.d(TAG, "onComplete: Authstate changed:" +userID);
                            Toast.makeText(mContext, R.string.kayit_basarili, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    public void sendVerificationEmail(){
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();

        if (user!=null){
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                    }else{
                        Toast.makeText(mContext,"Doğrulama maili gönderilemedi!",Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
    }
    public void yeniKullaniciEkleUsers(String email, String username, String name,
                                       String cinsiyet, String biyografi,
                                       String profil_foto,String yasadigiyer, long gonderiSay, long takipSay, long takipciSay){
        Users users = new Users(userID, email, username, 0);
        myRef.child(mContext.getString(R.string.dbname_users))
                .child(userID).setValue(users);

        UserBilgileri userBilgileri = new UserBilgileri(name, cinsiyet, biyografi, StringManipulation.condenseUsername(username),
                profil_foto, yasadigiyer, 0, 0, 0,userID);

        myRef.child(mContext.getString(R.string.dbname_user_bilgileri))
                .child(userID).setValue(userBilgileri);

        /**String user_id = "";
        myRef.child(mContext.getString(R.string.dbname_following))
                .child(userID).setValue(user_id);**/
    }
    /**
     * Kullanıcı giriş yaptığında kullanıcı bilgilerini çeker.*/
    public UserSettings getUserSettings(DataSnapshot dataSnapshot){
        Log.d(TAG,"getUserAccountSettings: kullanıcı bilgileri veritabanından çekildi");

        UserBilgileri settings = new UserBilgileri();
        Users users = new Users();
        for(DataSnapshot ds: dataSnapshot.getChildren()){
            if(ds.getKey().equals(mContext.getString(R.string.dbname_user_bilgileri))){
                Log.d(TAG, "getUserAccountSettings: Datasnapshot:" + ds);
                //user_bilgilerinden veriyi çekiyor.
                try{
                settings.setName(
                        ds.child(userID)
                        .getValue(UserBilgileri.class)
                                .getName());
                settings.setUsername(
                        ds.child(userID)
                                .getValue(UserBilgileri.class)
                                .getUsername());
                settings.setBiyografi(
                        ds.child(userID)
                                .getValue(UserBilgileri.class)
                                .getBiyografi());
                settings.setProfil_foto(
                        ds.child(userID)
                                .getValue(UserBilgileri.class)
                                .getProfil_foto());
                settings.setGonderiSay(
                        ds.child(userID)
                                .getValue(UserBilgileri.class)
                                .getGonderiSay());
                settings.setTakipciSay(
                        ds.child(userID)
                                .getValue(UserBilgileri.class)
                                .getTakipciSay());
                settings.setTakipSay(
                        ds.child(userID)
                                .getValue(UserBilgileri.class)
                                .getTakipSay());
                settings.setCinsiyet(
                        ds.child(userID)
                                .getValue(UserBilgileri.class)
                                .getCinsiyet());
                settings.setYasadigiyer(
                            ds.child(userID)
                                    .getValue(UserBilgileri.class)
                                    .getYasadigiyer());
                    Log.d(TAG,"getUserAccountSettings: user_bilgileri veri çekme işlemi tamamlandı." + settings.toString());

                }catch (NullPointerException e){
                    Log.e(TAG,"getUserAccountSettings: NullPointerException"+ e.getMessage());
                }
            }
            //user_bilgileri veri çekme son --------------------------
            //users'den veri çekme başlangıç
            if(ds.getKey().equals(mContext.getString(R.string.dbname_users))){
                Log.d(TAG, "getUserAccountSettings: Datasnapshot:" + ds);

                users.setUsername(
                        ds.child(userID)
                                .getValue(Users.class)
                                .getUsername());
                users.setEmail(
                        ds.child(userID)
                                .getValue(Users.class)
                                .getEmail());
                users.setCeptel(
                        ds.child(userID)
                                .getValue(Users.class)
                                .getCeptel());
                users.setUserID(
                        ds.child(userID)
                                .getValue(Users.class)
                                .getUserID());

                Log.d(TAG,"getUserAccountSettings: users veri çekme işlemi tamamlandı." + users.toString());
            }
            //users veri çekme son
        } //for döngüsü sonu
        return new UserSettings(users,settings);
    }
}
