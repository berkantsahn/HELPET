package com.helpetapplicationgmail.helpet.Utils;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

/**
 * Created by acer on 1.05.2018.
 */

public class Like {
    private static final String TAG = "Like";

    private static final DecelerateInterpolator DECELERATE_INTERPOLATOR = new DecelerateInterpolator();
    private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();

    public ImageView likeWhite, likeRed;

    public Like(ImageView likeWhite, ImageView likeRed) {
        this.likeWhite = likeWhite;
        this.likeRed = likeRed;
    }

    public void toggleLike(){
        Log.d(TAG, "toggleLike: Toggling like button.");

        AnimatorSet animatorSet = new AnimatorSet();

        if(likeRed.getVisibility() == View.VISIBLE){
            Log.d(TAG, "toggleLike: toggling red like button off.");
            likeRed.setScaleX(0.1f);
            likeRed.setScaleY(0.1f);

            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(likeRed, "scaleY", 1f,0f);
            scaleDownY.setDuration(300);
            scaleDownY.setInterpolator(ACCELERATE_INTERPOLATOR);

            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(likeRed, "scaleX", 1f,0f);
            scaleDownX.setDuration(300);
            scaleDownX.setInterpolator(ACCELERATE_INTERPOLATOR);

            likeRed.setVisibility(View.GONE);
            likeWhite.setVisibility(View.VISIBLE);

            animatorSet.playTogether(scaleDownY, scaleDownX);

        } else if(likeRed.getVisibility() == View.GONE){
            Log.d(TAG, "toggleLike: toggling red like button on.");
            likeRed.setScaleX(0.1f);
            likeRed.setScaleY(0.1f);

            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(likeRed, "scaleY", 0.1f,1f);
            scaleDownY.setDuration(300);
            scaleDownY.setInterpolator(DECELERATE_INTERPOLATOR);

            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(likeRed, "scaleX", 0.1f,1f);
            scaleDownX.setDuration(300);
            scaleDownX.setInterpolator(DECELERATE_INTERPOLATOR);

            likeRed.setVisibility(View.VISIBLE);
            likeWhite.setVisibility(View.GONE);

            animatorSet.playTogether(scaleDownY, scaleDownX);
        }

        animatorSet.start();

    }
}
