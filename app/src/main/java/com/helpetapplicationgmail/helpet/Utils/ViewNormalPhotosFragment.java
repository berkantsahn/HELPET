package com.helpetapplicationgmail.helpet.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

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
import com.helpetapplicationgmail.helpet.Models.UserSettings;
import com.helpetapplicationgmail.helpet.Models.Users;
import com.helpetapplicationgmail.helpet.R;
import com.helpetapplicationgmail.helpet.Utils.Like;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by acer on 5.03.2018.
 */

public class ViewNormalPhotosFragment extends Fragment {

    private static final String TAG = "ViewNormalPhotosFragment";
    private static final int ACTIVITY_NUM = 4;


    public interface OnGridImageSelectedListener{

        void onGridImageSelected(PhotoNormal photo, int activityNumber);

    }

    private static final int NUM_GRID_COLUMNS = 4;
    OnGridImageSelectedListener mOnGridImageSelectedListener;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase myFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;
    private GridView gridViewNormal;
    private Context mContext;
    private String id;
    private Users mUser;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_normal_photos_profile_view, container, false);
        mFirebaseMethods = new FirebaseMethods(getActivity());
        gridViewNormal = (GridView) view.findViewById(R.id.gridViewNormalProfile);
        Log.d(TAG,"onCreateView: başladı");
        mContext=getActivity();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        id=preferences.getString("myString","boşluk");
         Log.d(TAG,"ALINAN ID=" +id);
        try{
            mUser = getUserFromBundle();
            init();
        } catch (NullPointerException e){
            Log.e(TAG, "onCreateView: NullPointerException:"+e.getMessage());
            Toast.makeText(mContext, "Birşeyler yanlış gitti.", Toast.LENGTH_SHORT).show();
            getActivity().getSupportFragmentManager().popBackStack();
        }
        setupFirebaseAuth();
        //setupGridView();
        return view;
    }
    private void init(){
        //get the users profile photos;
        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference();
        Query query2 = reference2
                .child(getString(R.string.dbname_user_photos_normal))
                .child(id);
        query2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ArrayList<PhotoNormal> photos = new ArrayList<PhotoNormal>();
                for(DataSnapshot singleSnapshot:dataSnapshot.getChildren()){

                    PhotoNormal photo = new PhotoNormal();
                    Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();

                    photo.setCaption(objectMap.get(getString(R.string.field_caption)).toString());
                    photo.setTags(objectMap.get(getString(R.string.field_tags)).toString());
                    photo.setPhoto_id(objectMap.get(getString(R.string.field_photo_id)).toString());
                    photo.setUser_id(objectMap.get(getString(R.string.field_user_id)).toString());
                    photo.setDate_created(objectMap.get(getString(R.string.field_date_created)).toString());
                    photo.setImage_path(objectMap.get(getString(R.string.field_image_path)).toString());

                    ArrayList<CommentNormal> comments  = new ArrayList<>();
                    for (DataSnapshot dSnapshot:singleSnapshot
                            .child(getString(R.string.field_comments)).getChildren()){
                        CommentNormal comment = new CommentNormal();
                        comment.setUser_id(dSnapshot.getValue(CommentNormal.class).getUser_id());
                        comment.setComment(dSnapshot.getValue(CommentNormal.class).getComment());
                        comment.setDate_created(dSnapshot.getValue(CommentNormal.class).getDate_created());
                        comments.add(comment);
                    }

                    photo.setComments(comments);

                    photo.setComments(comments);
                    List<LikeNormal> likesList = new ArrayList<>();
                    for(DataSnapshot dSnapshot : singleSnapshot
                            .child(getString(R.string.field_likes)).getChildren()){
                        LikeNormal like = new LikeNormal();
                        like.setUser_id(dSnapshot.getValue(LikeNormal.class).getUser_id());
                        likesList.add(like);
                    }
                    photo.setLikes(likesList);
                    photos.add(photo);
                }
                setupImageGrid(photos);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: query cancelled.");
            }
        });
    }

    private void setupImageGrid(final ArrayList<PhotoNormal> photos){
        //setup the imagegrid
        int gridWidth = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = gridWidth/NUM_GRID_COLUMNS;
        gridViewNormal.setColumnWidth(imageWidth);

        ArrayList<String> imgUrls = new ArrayList<String>();
        for(int i = 0; i<photos.size();i++){
            imgUrls.add(photos.get(i).getImage_path());
        }

        GridImageAdapter adapter = new GridImageAdapter(getActivity(), R.layout.layout_grid_imageview, "", imgUrls);
        gridViewNormal.setAdapter(adapter);

        gridViewNormal.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mOnGridImageSelectedListener.onGridImageSelected(photos.get(position), ACTIVITY_NUM);
            }
        });

    }

    private Users getUserFromBundle(){
        Log.d(TAG, "getUserFromBundle: arguments:" + getArguments());

        Bundle bundle = this.getArguments();
        if(bundle != null) {
            return bundle.getParcelable(getString(R.string.intent_user));
        } else {
            return null;
        }
    }

    @Override
    public void onAttach(Context context) {
        try{
            mOnGridImageSelectedListener = (OnGridImageSelectedListener) getActivity();
        }catch (ClassCastException e){
            Log.e(TAG, "onAttach: ClassCastException: "+ e.getMessage());
        }
        super.onAttach(context);
    }

    private void setupGridView(){
        Log.d(TAG, "setupGridView: Setting up image grid.");
        final ArrayList<PhotoNormal> photos = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_user_photos_normal))
                .child(id);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){

                    PhotoNormal photo = new PhotoNormal();
                    Map<String, Object> objectMap = (HashMap<String ,Object>) singleSnapshot.getValue();

                    photo.setCaption(objectMap.get(getString(R.string.field_caption)).toString());
                    photo.setDate_created(objectMap.get(getString(R.string.field_date_created)).toString());
                    photo.setImage_path(objectMap.get(getString(R.string.field_image_path)).toString());
                    photo.setPhoto_id(objectMap.get(getString(R.string.field_photo_id)).toString());
                    photo.setUser_id(objectMap.get(getString(R.string.field_user_id)).toString());
                    photo.setTags(objectMap.get(getString(R.string.field_tags)).toString());

                    ArrayList<CommentNormal> comments = new ArrayList<CommentNormal>();
                    for(DataSnapshot dataSnapshot1 : singleSnapshot
                            .child(getString(R.string.field_comments)).getChildren()){
                        CommentNormal commentNormal = new CommentNormal();
                        commentNormal.setUser_id(dataSnapshot1.getValue(CommentNormal.class).getUser_id());
                        commentNormal.setComment(dataSnapshot1.getValue(CommentNormal.class).getComment());
                        commentNormal.setDate_created(dataSnapshot1.getValue(CommentNormal.class).getDate_created());
                        comments.add(commentNormal);
                    }

                    photo.setComments(comments);

                    List<LikeNormal> likesList = new ArrayList<LikeNormal>();
                    for(DataSnapshot dataSnapshot1 : singleSnapshot
                            .child(getString(R.string.field_likes)).getChildren()){
                        LikeNormal like = new LikeNormal();
                        like.setUser_id(dataSnapshot1.getValue(LikeNormal.class).getUser_id());
                        likesList.add(like);
                    }
                    photo.setLikes(likesList);
                    photos.add(photo);
                }
                //setup the imagegrid
                int gridWidth = getResources().getDisplayMetrics().widthPixels;
                int imageWidth = gridWidth/NUM_GRID_COLUMNS;
                gridViewNormal.setColumnWidth(imageWidth);

                ArrayList<String> imgUrls = new ArrayList<String>();
                for(int i = 0; i<photos.size(); i++){
                    imgUrls.add(photos.get(i).getImage_path());
                }
                GridImageAdapter myAdapter = new GridImageAdapter(getActivity(), R.layout.layout_grid_imageview, "", imgUrls);
                gridViewNormal.setAdapter(myAdapter);

                gridViewNormal.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        mOnGridImageSelectedListener.onGridImageSelected(photos.get(position), ACTIVITY_NUM);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: query cancelled");
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
