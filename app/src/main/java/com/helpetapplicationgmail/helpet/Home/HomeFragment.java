package com.helpetapplicationgmail.helpet.Home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.helpetapplicationgmail.helpet.Models.CommentNormal;
import com.helpetapplicationgmail.helpet.Models.LikeNormal;
import com.helpetapplicationgmail.helpet.Models.PhotoNormal;
import com.helpetapplicationgmail.helpet.Models.UserBilgileri;
import com.helpetapplicationgmail.helpet.Others.LoginActivity;
import com.helpetapplicationgmail.helpet.R;
import com.helpetapplicationgmail.helpet.Utils.MainfeedListAdapter;

import org.w3c.dom.Comment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by acer on 5.03.2018.
 */

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    //vars
    private ArrayList<PhotoNormal> mPhotos;
    private ArrayList<PhotoNormal> mPaginatedPhotos;
    private ArrayList<String> mFollowing;
    private ListView mListView;
    private MainfeedListAdapter mAdapter;
    private int mResults;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_home, container, false);

            mListView = (ListView) view.findViewById(R.id.listViewMainFeed);
            mFollowing = new ArrayList<>();
            mPhotos = new ArrayList<>();
            setupFirebaseAuth();
            return view;
    }

    private void setupFirebaseAuth() {
        Log.d(TAG,"setupFirebaseAuth: firebase auth yükleniyor");
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        //userID = mAuth.getCurrentUser().getUid();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                //kullanıcının giriş yapıp yapılmadığı kontrol ediliyor.
                if(user != null){
                    Log.d(TAG, "onAuthStateChanged:signed_in"+user.getUid());
                    getFollowing();
                }
                else {
                    Log.d(TAG,"onAuthStateChanged:signed_out");
                    Intent intent=new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
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

    private void getFollowing(){
        Log.d(TAG, "getFollowing: searching for following");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_following))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: found User: " + singleSnapshot.child(getString(R.string.field_user_id)).getValue());

                    mFollowing.add(singleSnapshot.child(getString(R.string.field_user_id)).getValue().toString());
                }

                //mFollowing.add(FirebaseAuth.getInstance().getCurrentUser().getUid());
                //get the photo
                getPhotos();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getPhotos(){
        Log.d(TAG, "getPhotos: getting photos.");

        for(int i = 0 ; i < mFollowing.size(); i++){
            final int count = i;
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            Query query = reference
                    .child(getString(R.string.dbname_user_photos_normal))
                    .child(mFollowing.get(i))
                    .orderByChild(getString(R.string.field_user_id))
                    .equalTo(mFollowing.get(i));
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){

                        PhotoNormal photo = new PhotoNormal();
                        Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();

                        photo.setCaption(objectMap.get(getString(R.string.field_caption)).toString());
                        photo.setTags(objectMap.get(getString(R.string.field_tags)).toString());
                        photo.setPhoto_id(objectMap.get(getString(R.string.field_photo_id)).toString());
                        photo.setUser_id(objectMap.get(getString(R.string.field_user_id)).toString());
                        photo.setDate_created(objectMap.get(getString(R.string.field_date_created)).toString());
                        photo.setImage_path(objectMap.get(getString(R.string.field_image_path)).toString());

                        ArrayList<CommentNormal> comments  = new ArrayList<CommentNormal>();
                        for (DataSnapshot dSnapshot:singleSnapshot
                                .child(getString(R.string.field_comments)).getChildren()){
                            CommentNormal comment = new CommentNormal();
                            comment.setUser_id(dSnapshot.getValue(CommentNormal.class).getUser_id());
                            comment.setComment(dSnapshot.getValue(CommentNormal.class).getComment());
                            comment.setDate_created(dSnapshot.getValue(CommentNormal.class).getDate_created());
                            comments.add(comment);
                        }

                        photo.setComments(comments);

                        mPhotos.add(photo);
                    }

                    if(count >= mFollowing.size() - 1){
                        //display our photos
                        displayPhotos();
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(TAG, "onCancelled: query cancelled.");

                }
            });
        }
    }

    private void displayPhotos(){
        mPaginatedPhotos = new ArrayList<>();

        if(mPhotos != null){

            try{
                Collections.sort(mPhotos, new Comparator<PhotoNormal>() {
                @Override
                public int compare(PhotoNormal o1, PhotoNormal o2) {
                    return o2.getDate_created().compareTo(o1.getDate_created());
                    }
                });

                int iteration = mPhotos.size();

                if(iteration > 10) {
                    iteration = 10;
                }

                mResults = 10;

                for(int i =0; i<iteration;i++){
                    mPaginatedPhotos.add(mPhotos.get(i));
                }

                mAdapter = new MainfeedListAdapter(getActivity(), R.layout.layout_mainfeed_listitem, mPhotos);
                mListView.setAdapter(mAdapter);
            }catch (NullPointerException e){
                Log.e(TAG, "displayPhotos: NullPointerException" + e.getMessage());
            }catch (IndexOutOfBoundsException e){
                Log.e(TAG, "displayPhotos: IndexOutOfBoundsException" + e.getMessage());
            }
        }
    }

    public void displayMorePhotos() {
        Log.d(TAG, "displayMorePhotos: displaying more more photos");
        try{
            if(mPhotos.size() > mResults && mPhotos.size()>0){
                int iteration;
                if(mPhotos.size() > (mResults + 10)){
                    Log.d(TAG, "displayMorePhotos: there are bigger than 10 photos");
                    iteration = 10;
                } else {
                    Log.d(TAG, "displayMorePhotos: there is less than 10 photos");
                    iteration = mPhotos.size() - mResults;
                }

                //add the new photos to paginated results
                for(int i = mResults; i < mResults + iteration;i++){
                    mPaginatedPhotos.add(mPhotos.get(i));
                }
                mResults = mResults + iteration;
                mAdapter.notifyDataSetChanged();
            }

        }catch (NullPointerException e){
        Log.e(TAG, "displayPhotos: NullPointerException" + e.getMessage());
        }catch (IndexOutOfBoundsException e){
        Log.e(TAG, "displayPhotos: IndexOutOfBoundsException" + e.getMessage());
        }
    }

}
