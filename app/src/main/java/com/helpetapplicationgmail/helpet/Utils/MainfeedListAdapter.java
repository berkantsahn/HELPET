package com.helpetapplicationgmail.helpet.Utils;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
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
import com.helpetapplicationgmail.helpet.Profile.ProfileActivity;
import com.helpetapplicationgmail.helpet.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by acer on 5.05.2018.
 */

public class MainfeedListAdapter extends ArrayAdapter<PhotoNormal> {

    public interface OnLoadMoreItemsListener {
        void onLoadMoreItems();
    }

    OnLoadMoreItemsListener mOnLoadMoreItemsListener;

    private static final String TAG = "MainfeedListAdapter";

    private LayoutInflater mInflater;
    private int mLayoutResource;
    private Context mContext;
    private DatabaseReference mReference;
    private String currentUsername = "";

    public MainfeedListAdapter(@NonNull Context context, int resource, @NonNull List<PhotoNormal> objects) {
        super(context, resource, objects);

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLayoutResource = resource;
        this.mContext = context;
        mReference = FirebaseDatabase.getInstance().getReference();

    }

    static class ViewHolder{
        CircleImageView mProfileImage;
        String likesString;
        TextView username, timeDelta, caption, likes, comments, id;
        SquareImageView image;
        ImageView likeRed, likeWhite, comment;

        UserBilgileri settings = new UserBilgileri();
        Users user = new Users();
        StringBuilder users;
        String mLikesString;
        boolean likedByCurrentUser;
        Like like;
        GestureDetector detector;
        PhotoNormal photo;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final ViewHolder holder;
        if(convertView == null){
            convertView = mInflater.inflate(mLayoutResource, parent, false);
            holder = new ViewHolder();

            holder.username = (TextView) convertView.findViewById(R.id.username_mainfeed);
            holder.image = (SquareImageView) convertView.findViewById(R.id.post_image_mainfeed);
            holder.likeRed = (ImageView) convertView.findViewById(R.id.image_like_red_mainfeed);
            holder.likeWhite = (ImageView) convertView.findViewById(R.id.image_like_mainfeed);
            holder.comment = (ImageView) convertView.findViewById(R.id.speech_bubble_mainfeed);
            holder.likes = (TextView) convertView.findViewById(R.id.image_likes_mainfeed);
            holder.comments = (TextView) convertView.findViewById(R.id.image_comments_link_mainfeed);
            holder.caption = (TextView) convertView.findViewById(R.id.image_caption_mainfeed);
            holder.timeDelta = (TextView) convertView.findViewById(R.id.image_time_posted_mainfeed);
            holder.mProfileImage = (CircleImageView) convertView.findViewById(R.id.profil_foto_mainfeed);
            holder.like = new Like(holder.likeWhite, holder.likeRed);
            holder.photo = getItem(position);
            holder.detector = new GestureDetector(mContext, new GestureListener(holder));
            holder.users = new StringBuilder();

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //getting current users username -> We need this for checking likes string
        getCurrentUsername();

        //get likes string
        getLikesString(holder);

        //set the caption
        holder.caption.setText(getItem(position).getCaption());

        //set the comment
        List<CommentNormal> comments = getItem(position).getComments();
        holder.comments.setText(comments.size()+" yorumun tümünü gör");
        holder.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: loading comment thread for:"+getItem(position).getPhoto_id());
                ((HomeActivity)mContext).onCommentThreadSelected(getItem(position),
                        mContext.getString(R.string.home_activity));

                //going to need to do a something else?
                ((HomeActivity)mContext).hideLayout();
            }
        });

        //set time it was posted
        String timestampDifference = getTimestampDifference(getItem(position));
        if(!timestampDifference.equals("0")) {
            holder.timeDelta.setText(timestampDifference+" gün önce paylaşıldı");
        } else {
            holder.timeDelta.setText(mContext.getString(R.string.posted_today));
        }

        //set the profile image
        final ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(getItem(position).getImage_path(), holder.image);

        //get the profile image and username
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(mContext.getString(R.string.dbname_user_bilgileri))
                .orderByChild(mContext.getString(R.string.field_user_id))
                .equalTo(getItem(position).getUser_id());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(final DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    //currentUsername = singleSnapshot.getValue(UserBilgileri.class).getUsername();
                    Log.d(TAG, "onDataChange: Found user:" + singleSnapshot.getValue(UserBilgileri.class).getUsername());


                    holder.username.setText(singleSnapshot.getValue(UserBilgileri.class).getUsername());
                    holder.username.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d(TAG, "onClick: Navigating to profile: "+ holder.user.getUsername());
                            Intent intent = new Intent(mContext, ViewProfileActivity.class);
                            intent.putExtra(mContext.getString(R.string.calling_activity), mContext.getString(R.string.home_activity));
                            intent.putExtra(mContext.getString(R.string.intent_user), holder.user);
                            mContext.startActivity(intent);
                        }
                    });

                    imageLoader.displayImage(singleSnapshot.getValue(UserBilgileri.class).getProfil_foto(),
                            holder.mProfileImage);

                    holder.mProfileImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d(TAG, "onClick: Navigating to profile: "+ holder.user.getUsername());
                            Intent intent = new Intent(mContext, ViewProfileActivity.class);
                            intent.putExtra(mContext.getString(R.string.calling_activity), mContext.getString(R.string.home_activity));
                            intent.putExtra(mContext.getString(R.string.intent_user), holder.user);
                            mContext.startActivity(intent);
                        }
                    });

                    holder.settings = singleSnapshot.getValue(UserBilgileri.class);

                    holder.comment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ((HomeActivity)mContext).onCommentThreadSelected(getItem(position), mContext.getString(R.string.home_activity));

                            //another thing?
                            ((HomeActivity)mContext).hideLayout();

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //get the user object

        Query userQuery = mReference
                .child(mContext.getString(R.string.dbname_users))
                .orderByChild(mContext.getString(R.string.field_userID))
                .equalTo(getItem(position).getUser_id());

        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: found user: "+singleSnapshot.getValue(Users.class).getUsername());
                    holder.user = singleSnapshot.getValue(Users.class);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if(reachedEndOfList(position)){
            loadMoreData();
        }


        return convertView;

    }

    private boolean reachedEndOfList(int position){
        return position == getCount() - 1;
    }

    private void loadMoreData(){

        try{
            mOnLoadMoreItemsListener = (OnLoadMoreItemsListener) getContext();
        }catch (ClassCastException e){
            Log.e(TAG, "loadMoreData: ClassCastException:" + e.getMessage());
        }

        try{
            mOnLoadMoreItemsListener.onLoadMoreItems();
        }catch (NullPointerException e){
            Log.e(TAG, "loadMoreData: NullPointerException:" + e.getMessage());
        }
    }

    public class GestureListener extends GestureDetector.SimpleOnGestureListener{


        ViewHolder mHolder;
        public GestureListener(ViewHolder holder) {
            mHolder = holder;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.d(TAG, "onDoubleTap: DOUBLE TAP DETECTED.");

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            Query query = reference
                    .child(mContext.getString(R.string.dbname_photos_normal))
                    .child(mHolder.photo.getPhoto_id())
                    .child(mContext.getString(R.string.field_likes));

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){

                        String keyID = singleSnapshot.getKey();
                        //case1: Then user already liked the photo
                        if(mHolder.likedByCurrentUser && singleSnapshot.getValue(LikeNormal.class).getUser_id()
                                .equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){

                            mReference.child(mContext.getString(R.string.dbname_photos_normal))
                                    .child(mHolder.photo.getPhoto_id())
                                    .child(mContext.getString(R.string.field_likes))
                                    .child(keyID)
                                    .removeValue();

                            mReference.child(mContext.getString(R.string.dbname_user_photos_normal))
                                    .child(mHolder.user.getUserID())
                                    .child(mHolder.photo.getPhoto_id())
                                    .child(mContext.getString(R.string.field_likes))
                                    .child(keyID)
                                    .removeValue();

                            mHolder.like.toggleLike();
                            getLikesString(mHolder);
                        }
                        //case2: User has not liked the photo
                        else if(!mHolder.likedByCurrentUser){
                            //add new like
                            addNewLike(mHolder);
                            break;
                        }
                    }

                    if(!dataSnapshot.exists()){
                        //add new like
                        addNewLike(mHolder);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            return true;
        }
    }

    private void addNewLike(final ViewHolder holder){
        String newLikeID = mReference.push().getKey();
        LikeNormal like = new LikeNormal();
        like.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());

        mReference.child(mContext.getString(R.string.dbname_photos_normal))
                .child(holder.photo.getPhoto_id())
                .child(mContext.getString(R.string.field_likes))
                .child(newLikeID)
                .setValue(like);

        mReference.child(mContext.getString(R.string.dbname_user_photos_normal))
                .child(holder.photo.getUser_id())
                .child(holder.photo.getPhoto_id())
                .child(mContext.getString(R.string.field_likes))
                .child(newLikeID)
                .setValue(like);

        holder.like.toggleLike();
        getLikesString(holder);
    }

    private void getCurrentUsername(){
        Log.d(TAG, "getCurrentUsername: retrieving user account settings");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(mContext.getString(R.string.dbname_users))
                .orderByChild(mContext.getString(R.string.field_userID))
                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    currentUsername = singleSnapshot.getValue(UserBilgileri.class).getUsername();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void getLikesString(final ViewHolder holder){
        Log.d(TAG, "getLikesString: getting likes string");

        try{


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(mContext.getString(R.string.dbname_photos_normal))
                .child(holder.photo.getPhoto_id())
                .child(mContext.getString(R.string.field_likes));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                holder.users = new StringBuilder();
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                    Query query = reference
                            .child(mContext.getString(R.string.dbname_users))
                            .orderByChild(mContext.getString(R.string.field_userID))
                            .equalTo(singleSnapshot.getValue(LikeNormal.class).getUser_id());

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){

                                Log.d(TAG, "onDataChange: found like: "+ singleSnapshot.getValue(Users.class).getUsername());

                                holder.users.append(singleSnapshot.getValue(Users.class).getUsername());
                                holder.users.append(",");
                            }

                            String[] splitUsers = holder.users.toString().split(",");

                            if(holder.users.toString().contains(currentUsername + ",")){
                                holder.likedByCurrentUser = true;
                            } else {
                                holder.likedByCurrentUser = false;
                            }

                            int length = splitUsers.length;
                            if(length == 1) {
                                holder.likesString = splitUsers[0]+" tarafından beğenildi";
                            } else if (length == 2){
                                holder.likesString = splitUsers[0]+" ve "+splitUsers[1]+ " tarafından beğenildi";
                            } else if (length == 3){
                                holder.likesString = splitUsers[0]+", "+splitUsers[1]+" ve "+splitUsers[2]+" tarafından beğenildi";

                            } else if (length > 3){
                                holder.likesString = splitUsers[0]+ ", "+splitUsers[1]+", "+splitUsers[2]+ "ve diğer" + (splitUsers.length - 3)
                                        + " kişi tarafından beğenildi";
                            }
                            Log.d(TAG, "onDataChange: likes string:" + holder.likesString);
                            //setup likes string
                            setupLikesString(holder, holder.likesString);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
                if(!dataSnapshot.exists()){
                    holder.likesString = "";
                    holder.likedByCurrentUser = false;
                    //setup likes string
                    setupLikesString(holder, holder.likesString);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        }catch (NullPointerException e){
            Log.e(TAG, "getLikesString: NullPointerException" + e.getMessage());
            holder.likesString = "";
            holder.likedByCurrentUser= false;
            //setup likes string
            setupLikesString(holder, holder.likesString);
        }
    }

    private void setupLikesString(final ViewHolder holder, String likesString){
        Log.d(TAG, "setupLikesString: likes string: "+holder.likesString);

        if(holder.likedByCurrentUser){
            Log.d(TAG, "setupLikesString: photo is liked by current user");
            holder.likeWhite.setVisibility(View.GONE);
            holder.likeRed.setVisibility(View.VISIBLE);
            holder.likeRed.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return holder.detector.onTouchEvent(event);
                }
            });
        } else {
            Log.d(TAG, "setupLikesString: photo is not liked by current user");
            holder.likeWhite.setVisibility(View.VISIBLE);
            holder.likeRed.setVisibility(View.GONE);
            holder.likeWhite.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return holder.detector.onTouchEvent(event);
                }
            });

        }

        holder.likes.setText(likesString);
    }

    /**
     * Gönderinin kaç gün önce paylaşıldığını String türünde döndüren metod.
     * @return
     */
    private String getTimestampDifference(PhotoNormal photo){
        Log.d(TAG, "getTimestampDifference: getting Timestamp difference");

        String difference = "";
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("Turkey")); //google > android list of timezones.

        Date today = cal.getTime();
        sdf.format(today);

        Date timestamp;
        final String photoTimestamp = photo.getDate_created();

        try{
            timestamp = sdf.parse(photoTimestamp);
            difference = String.valueOf(Math.round(((today.getTime() - timestamp.getTime()) / 1000 / 60 / 60 / 24)));
        }catch(ParseException e){
            Log.e(TAG, "getTimestampDifference: ParseException" + e.getMessage());
            difference = "0";
        }

        return difference;
    }
}
