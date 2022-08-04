package com.helpetapplicationgmail.helpet.Utils;

import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.helpetapplicationgmail.helpet.Home.HomeActivity;
import com.helpetapplicationgmail.helpet.Models.CommentNormal;
import com.helpetapplicationgmail.helpet.Models.LikeNormal;
import com.helpetapplicationgmail.helpet.Models.PhotoNormal;
import com.helpetapplicationgmail.helpet.Models.UserBilgileri;
import com.helpetapplicationgmail.helpet.Models.Users;
import com.helpetapplicationgmail.helpet.R;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import org.w3c.dom.Comment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by acer on 29.04.2018.
 */

public class ViewCommentsFragment extends Fragment {

    private static final String TAG = "ViewCommentsFragment";

    public ViewCommentsFragment(){
        super();
        setArguments(new Bundle());
    }
    //widgets
    private ImageView mBackArrow, mCheckMark;
    private EditText mComment;
    private ListView mListView;
    //vars
    private PhotoNormal mPhoto;
    private ArrayList<CommentNormal> mComments;
    private Context mContext;
    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase myFirebaseDatabase;
    private DatabaseReference myRef;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_comments, container, false);
        mBackArrow = (ImageView) view.findViewById(R.id.backArrowComment);
        mCheckMark = (ImageView) view.findViewById(R.id.ivPostComment);
        mComment = (EditText) view.findViewById(R.id.comment);

        mListView = (ListView) view.findViewById(R.id.listView);
        mComments = new ArrayList<>();
        mContext = getActivity();



        try{

            mPhoto = getPhotoFromBundle();
        }catch (NullPointerException e){
            Log.e(TAG, "onCreateView: NullPointerException: " + e.getMessage());
        }

        setupFirebaseAuth();

        return view;
    }
    private void setupWidgets(){
        CommentListAdapter mAdapter = new CommentListAdapter(mContext, R.layout.layout_comment, mComments);
        mListView.setAdapter(mAdapter);

        mCheckMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mComment.getText().toString().equals("")){
                    Log.d(TAG, "onClick: attempting to submit new comment.");
                    addNewComment(mComment.getText().toString());

                    mComment.setText("");
                    closeKeyboard();
                } else {
                    Toast.makeText(mContext, "Boş bir yorum ekleyemezsiniz.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back.");
                if(getCallingActivityFromBundle().equals(getString(R.string.home_activity))){
                    getActivity().getSupportFragmentManager().popBackStack();
                    ((HomeActivity)getActivity()).showLayout();
                } else {
                    getActivity().getSupportFragmentManager().popBackStack();
                }

            }
        });
    }
    private void closeKeyboard(){
        View view = getActivity().getCurrentFocus();
        if(view != null){
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void addNewComment(String newComment){
        Log.d(TAG, "addNewComment: "+ newComment);

        String commentID = myRef.push().getKey();

        CommentNormal comment = new CommentNormal();
        comment.setComment(newComment);
        comment.setDate_created(getTimestamp());
        comment.setUser_id(FirebaseAuth.getInstance().getUid());

        //insert into photos_normal node
        myRef.child(mContext.getString(R.string.dbname_photos_normal))
                .child(mPhoto.getPhoto_id())
                .child(mContext.getString(R.string.field_comments))
                .child(commentID)
                .setValue(comment);

        //insert into user_photos_normal node
        myRef.child(mContext.getString(R.string.dbname_user_photos_normal))
                .child(mPhoto.getUser_id())
                .child(mPhoto.getPhoto_id())
                .child(mContext.getString(R.string.field_comments))
                .child(commentID)
                .setValue(comment);
    }

    private String getTimestamp(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'H:mm:ss'Z'",Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("Turkey"));
        return sdf.format(new Date());
    }


    /**
     * retrieve the photo from the incoming bundle from profileActivity interface
     * @return
     */
    private String getCallingActivityFromBundle() {
        Log.d(TAG, "getPhotoFromBundle: arguments:"+ getArguments());

        Bundle bundle = this.getArguments();
        if(bundle != null){
            return bundle.getString(mContext.getString(R.string.home_activity));
        } else {
            return null;
        }
    }


    /**
     * retrieve the photo from the incoming bundle from profileActivity interface
     * @return
     */
    private PhotoNormal getPhotoFromBundle() {
        Log.d(TAG, "getPhotoFromBundle: arguments:"+ getArguments());

        Bundle bundle = this.getArguments();
        if(bundle != null){
            return bundle.getParcelable(mContext.getString(R.string.photo));
        } else {
            return null;
        }
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

        if(mPhoto.getComments().size() == 0){

            mComments.clear();
            CommentNormal firstComment = new CommentNormal();
            firstComment.setComment(mPhoto.getCaption());
            firstComment.setUser_id(mPhoto.getUser_id());
            firstComment.setDate_created(mPhoto.getDate_created());

            mComments.add(firstComment);
            mPhoto.setComments(mComments);
            setupWidgets();

        }
        myRef.child(mContext.getString(R.string.dbname_photos_normal))
                .child(mPhoto.getPhoto_id())
                .child(mContext.getString(R.string.field_comments))
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        Query query = myRef.child(mContext.getString(R.string.dbname_photos_normal))
                                .orderByChild(mContext.getString(R.string.field_photo_id))
                                .equalTo(mPhoto.getPhoto_id());
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){

                                    PhotoNormal photo = new PhotoNormal();
                                    Map<String, Object> objectMap = (HashMap<String ,Object>) singleSnapshot.getValue();

                                    photo.setCaption(objectMap.get(mContext.getString(R.string.field_caption)).toString());
                                    photo.setDate_created(objectMap.get(mContext.getString(R.string.field_date_created)).toString());
                                    photo.setImage_path(objectMap.get(mContext.getString(R.string.field_image_path)).toString());
                                    photo.setPhoto_id(objectMap.get(mContext.getString(R.string.field_photo_id)).toString());
                                    photo.setUser_id(objectMap.get(mContext.getString(R.string.field_user_id)).toString());
                                    photo.setTags(objectMap.get(mContext.getString(R.string.field_tags)).toString());


                                    mComments.clear();

                                    CommentNormal firstComment = new CommentNormal();
                                    firstComment.setComment(mPhoto.getCaption());
                                    firstComment.setUser_id(mPhoto.getUser_id());
                                    firstComment.setDate_created(mPhoto.getDate_created());

                                    mComments.add(firstComment);

                                    for(DataSnapshot dataSnapshot1 : singleSnapshot
                                            .child(mContext.getString(R.string.field_comments)).getChildren()){
                                        CommentNormal commentNormal = new CommentNormal();
                                        commentNormal.setUser_id(dataSnapshot1.getValue(CommentNormal.class).getUser_id());
                                        commentNormal.setComment(dataSnapshot1.getValue(CommentNormal.class).getComment());
                                        commentNormal.setDate_created(dataSnapshot1.getValue(CommentNormal.class).getDate_created());
                                        mComments.add(commentNormal);
                                    }

                                    photo.setComments(mComments);
                                    mPhoto = photo;

                                    setupWidgets();

                                    /** List<LikeNormal> likesList = new ArrayList<LikeNormal>();
                                     for(DataSnapshot dataSnapshot1 : singleSnapshot
                                     .child(getString(R.string.field_likes)).getChildren()){
                                     LikeNormal like = new LikeNormal();
                                     like.setUser_id(dataSnapshot1.getValue(LikeNormal.class).getUser_id());
                                     likesList.add(like);
                                     }**/

                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.d(TAG, "onCancelled: query cancelled");
                            }
                        });


                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

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
