package com.helpetapplicationgmail.helpet.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.media.Image;
import android.preference.PreferenceManager;
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
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.ImageView;

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
import com.helpetapplicationgmail.helpet.R;
import com.helpetapplicationgmail.helpet.Utils.BottomNavigationViewHelper;
import com.helpetapplicationgmail.helpet.Utils.FirebaseMethods;
import com.helpetapplicationgmail.helpet.Utils.GridImageAdapter;
import com.helpetapplicationgmail.helpet.Utils.SquareImageView;
import com.helpetapplicationgmail.helpet.Utils.UniversalImageLoader;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.helpetapplicationgmail.helpet.Models.Users;

import org.w3c.dom.Text;

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

public class ViewPostFragment extends Fragment {

    private static final String TAG = "ViewPostFragment";


    public interface OnCommentThreadSelectedListener{
        void onCommentThreadSelectedListener(PhotoNormal photo);
    }

    OnCommentThreadSelectedListener mOnCommentThreadSelectedListener;

    public ViewPostFragment(){
        super();
        setArguments(new Bundle());
    }

    //widgets
    private SquareImageView myPostImage;
    private BottomNavigationViewEx bottomNavigationViewEx;
    private TextView mBackLabel, mCaption, mUsername, mTimestamp, mLikes, mCaptionUsername, mComments;
    private ImageView mBackArrow, mSettings, mLikeRed, mLikeWhite, mProfileImage, mComment;

    //vars
    private PhotoNormal mPhoto;
    private int mActivityNumber = 0;
    private String photoUsername ="";
    private String photoUrl = "";
    private UserBilgileri mUserBilgileri;
    private Like mLike;
    private GestureDetector mGestureDetector;
    private Boolean mLikedByCurrentUser;
    private StringBuilder mUsers;
    private String mLikesString;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase myFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;
    private Users mCurrentUser;
    private String id;
    private Context mContext;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_post, container, false);
        myPostImage = (SquareImageView) view.findViewById(R.id.post_image);
        bottomNavigationViewEx = (BottomNavigationViewEx) view.findViewById(R.id.bottomNavViewBar);

        mBackArrow = (ImageView) view.findViewById(R.id.postBackArrow);
        mBackLabel = (TextView) view.findViewById(R.id.tvBackLabel);
        mCaption = (TextView) view.findViewById(R.id.image_caption);
        mUsername = (TextView) view.findViewById(R.id.username_viewpost);
        mTimestamp = (TextView) view.findViewById(R.id.image_time_posted);
        mSettings = (ImageView) view.findViewById(R.id.ivEllipses);
        mLikeRed = (ImageView) view.findViewById(R.id.image_like_red);
        mLikeWhite = (ImageView) view.findViewById(R.id.image_like);
        mProfileImage = (ImageView) view.findViewById(R.id.profil_foto_view_post);
        mLikes = (TextView) view.findViewById(R.id.image_likes);
        mComment = (ImageView) view.findViewById(R.id.speech_bubble);
        mCaptionUsername = (TextView) view.findViewById(R.id.image_caption_username);
        mComments = (TextView) view.findViewById(R.id.image_comments_link);
        mContext=getActivity();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        id=preferences.getString("myString","boşluk");

        mLike = new Like(mLikeWhite, mLikeRed);
        mGestureDetector = new GestureDetector(getActivity(), new GestureListener());
        try{

            //mPhoto = getPhotoFromBundle();
            UniversalImageLoader.setImage(getPhotoFromBundle().getImage_path(), myPostImage, null, "");
            mActivityNumber = getActivityNoFromBundle();

            String photo_id = getPhotoFromBundle().getPhoto_id();

            Query query = FirebaseDatabase.getInstance().getReference()
                    .child(getString(R.string.dbname_photos_normal))
                    .orderByChild(getString(R.string.field_photo_id))
                    .equalTo(photo_id);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for(DataSnapshot singleSnapshot: dataSnapshot.getChildren()){
                        PhotoNormal newPhoto = new PhotoNormal();
                        Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();

                        newPhoto.setCaption(objectMap.get(getString(R.string.field_caption)).toString());
                        newPhoto.setTags(objectMap.get(getString(R.string.field_tags)).toString());
                        newPhoto.setPhoto_id(objectMap.get(getString(R.string.field_photo_id)).toString());
                        newPhoto.setUser_id(objectMap.get(getString(R.string.field_user_id)).toString());
                        newPhoto.setDate_created(objectMap.get(getString(R.string.field_date_created)).toString());
                        newPhoto.setImage_path(objectMap.get(getString(R.string.field_image_path)).toString());

                        List<CommentNormal> commentsList = new ArrayList<CommentNormal>();
                        for(DataSnapshot dSnapshot:singleSnapshot.child(getString(R.string.field_comments)).getChildren()){
                            CommentNormal comment = new CommentNormal();
                            comment.setUser_id(dSnapshot.getValue(CommentNormal.class).getUser_id());
                            comment.setComment(dSnapshot.getValue(CommentNormal.class).getComment());
                            comment.setDate_created(dSnapshot.getValue(CommentNormal.class).getComment());
                            commentsList.add(comment);
                        }
                        newPhoto.setComments(commentsList);
                        mPhoto = newPhoto;
                        getCurrentUser();
                        getPhotoDetails();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }catch (NullPointerException e){
            Log.e(TAG, "onCreateView: NullPointerException: " + e.getMessage());
        }
        setupFirebaseAuth();
        setupBottomNavigationView();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            mOnCommentThreadSelectedListener = (OnCommentThreadSelectedListener) getActivity();
        }catch (ClassCastException e){
            Log.e(TAG, "onAttach: ClassCastException:" + e.getMessage());
        }
    }

    private void getLikesString(){
        Log.d(TAG, "getLikesString: getting likes string");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_photos_normal))
                .child(mPhoto.getPhoto_id())
                .child(getString(R.string.field_likes));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUsers = new StringBuilder();
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                    Query query = reference
                            .child(getString(R.string.dbname_users))
                            .orderByChild(getString(R.string.field_userID))
                            .equalTo(singleSnapshot.getValue(LikeNormal.class).getUser_id());

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){

                                Log.d(TAG, "onDataChange: found like: "+ singleSnapshot.getValue(Users.class).getUsername());

                                mUsers.append(singleSnapshot.getValue(Users.class).getUsername());
                                mUsers.append(",");
                            }

                            String[] splitUsers = mUsers.toString().split(",");

                            if(mUsers.toString().contains(mCurrentUser.getUsername() + ",")){
                                mLikedByCurrentUser = true;
                            } else {
                                mLikedByCurrentUser = false;
                            }

                            int length = splitUsers.length;
                            if(length == 1) {
                                mLikesString = splitUsers[0]+" tarafından beğenildi";
                            } else if (length == 2){
                                mLikesString = splitUsers[0]+" ve "+splitUsers[1]+ " tarafından beğenildi";
                            } else if (length == 3){
                                mLikesString = splitUsers[0]+", "+splitUsers[1]+" ve "+splitUsers[2]+" tarafından beğenildi";

                            } else if (length > 3){
                                mLikesString = splitUsers[0]+ ", "+splitUsers[1]+", "+splitUsers[2]+ "ve diğer" + (splitUsers.length - 3)
                                        + " kişi tarafından beğenildi";
                            }
                            Log.d(TAG, "onDataChange: likes string:" + mLikesString);
                            setupWidgets();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
                if(!dataSnapshot.exists()){
                    mLikesString = "";
                    mLikedByCurrentUser = false;
                    setupWidgets();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void getCurrentUser(){

        Log.d(TAG, "getPhotoDetails: retrieving photo details.");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_users))
                .orderByChild(getString(R.string.field_userID))
                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    mCurrentUser = singleSnapshot.getValue(Users.class);
                }
                getLikesString();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: query cancelled");
            }
        });

    }
    public class GestureListener extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.d(TAG, "onDoubleTap: DOUBLE TAP DETECTED.");
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            Query query = reference
                    .child(getString(R.string.dbname_photos_normal))
                    .child(mPhoto.getPhoto_id())
                    .child(getString(R.string.field_likes));
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){

                        String keyID = singleSnapshot.getKey();
                        //case1: Then user already liked the photo
                        if(mLikedByCurrentUser && singleSnapshot.getValue(LikeNormal.class).getUser_id()
                                .equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){

                            myRef.child(getString(R.string.dbname_photos_normal))
                                    .child(mPhoto.getPhoto_id())
                                    .child(getString(R.string.field_likes))
                                    .child(keyID)
                                    .removeValue();

                            myRef.child(getString(R.string.dbname_user_photos_normal))
                                    .child(id) //girilen kişinin profil idsi alınacak.
                                    .child(mPhoto.getPhoto_id())
                                    .child(getString(R.string.field_likes))
                                    .child(keyID)
                                    .removeValue();

                            mLike.toggleLike();
                            getLikesString();
                        }
                        //case2: User has not liked the photo
                        else if(!mLikedByCurrentUser){
                            //add new like
                            addNewLike();
                            break;
                        }
                    }

                    if(!dataSnapshot.exists()){
                        //add new like
                        addNewLike();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            return true;
        }
    }
    private void addNewLike(){
        String newLikeID = myRef.push().getKey();
        LikeNormal like = new LikeNormal();
        like.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());

        myRef.child(getString(R.string.dbname_photos_normal))
                .child(mPhoto.getPhoto_id())
                .child(getString(R.string.field_likes))
                .child(newLikeID)
                .setValue(like);

        myRef.child(getString(R.string.dbname_user_photos_normal))
                .child(mPhoto.getUser_id())
                .child(mPhoto.getPhoto_id())
                .child(getString(R.string.field_likes))
                .child(newLikeID)
                .setValue(like);

        mLike.toggleLike();
        getLikesString();
    }

    private void setupWidgets(){
        String timestampDiff = getTimestampDifference();
        if(!timestampDiff.equals("0")){
            mTimestamp.setText(timestampDiff + " gün önce paylaşıldı");
        } else {
            mTimestamp.setText(R.string.posted_today);
        }

        UniversalImageLoader.setImage(mUserBilgileri.getProfil_foto(), mProfileImage, null, "");
        mUsername.setText(mUserBilgileri.getUsername());
        mLikes.setText(mLikesString);
        mCaptionUsername.setText(mUserBilgileri.getUsername());
        mCaption.setText(mPhoto.getCaption());

        if(mPhoto.getComments().size()>0){
            mComments.setText(mPhoto.getComments().size()+" yorumu göster");
        } else {
            mComments.setText("");
        }

        mComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigationg to comments thread");
                mOnCommentThreadSelectedListener.onCommentThreadSelectedListener(mPhoto);
            }
        });

        mBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back.");
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        mComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to comments fragment.");
                mOnCommentThreadSelectedListener.onCommentThreadSelectedListener(mPhoto);
            }
        });

        if(mLikedByCurrentUser){
            mLikeWhite.setVisibility(View.GONE);
            mLikeRed.setVisibility(View.VISIBLE);
            mLikeRed.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Log.d(TAG, "onTouch: Like Red Touch Detected");
                    return mGestureDetector.onTouchEvent(event);
                }
            });

        } else {
            mLikeWhite.setVisibility(View.VISIBLE);
            mLikeRed.setVisibility(View.GONE);
            mLikeWhite.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Log.d(TAG, "onTouch: Like White Touch Detected");
                    return mGestureDetector.onTouchEvent(event);
                }
            });
        }
    }

    private void getPhotoDetails(){

        Log.d(TAG, "getPhotoDetails: retrieving photo details.");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_user_bilgileri))
                .orderByChild(getString(R.string.field_user_id))
                .equalTo(mPhoto.getUser_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    mUserBilgileri = singleSnapshot.getValue(UserBilgileri.class);

                }
                //setupWidgets();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: query cancelled");
            }
        });
    }
    /**
     * Gönderinin kaç gün önce paylaşıldığını String türünde döndüren metod.
     * @return
     */
    private String getTimestampDifference(){
        Log.d(TAG, "getTimestampDifference: getting Timestamp difference");

        String difference = "";
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("Turkey")); //google > android list of timezones.

        Date today = cal.getTime();
        sdf.format(today);

        Date timestamp;
        final String photoTimestamp = mPhoto.getDate_created();

        try{
            timestamp = sdf.parse(photoTimestamp);
            difference = String.valueOf(Math.round(((today.getTime() - timestamp.getTime()) / 1000 / 60 / 60 / 24)));
        }catch(ParseException e){
            Log.e(TAG, "getTimestampDifference: ParseException" + e.getMessage());
            difference = "0";
        }

        return difference;
    }

    /**
     * retrieve the activity number from the incoming bundle from profileActivity interface
     * @return
     */

    private int getActivityNoFromBundle() {
        Log.d(TAG, "getActivityNoFromBundle: arguments:"+ getArguments());

        Bundle bundle = this.getArguments();
        if(bundle != null){
            return bundle.getInt(getString(R.string.activity_number));
        } else {
            return 0;
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
            return bundle.getParcelable(getString(R.string.photo));
        } else {
            return null;
        }
    }

    /**
     * BottomNavigationView(Alt menu) SETUP
     */
    private void setupBottomNavigationView(){

        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);

        /** Menu Sayfa Değişimi */
        BottomNavigationViewHelper.enableNavigation(getActivity(), getActivity(),bottomNavigationViewEx);


        /**İkon Değişimi switch case*/
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(mActivityNumber);
        menuItem.setChecked(true);
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
