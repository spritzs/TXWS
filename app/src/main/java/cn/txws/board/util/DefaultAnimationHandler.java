/*
 *   Copyright 2014 Oguz Bilgener
 */
package cn.txws.board.util;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.PopupWindow;


import java.util.List;

/**
 * An example animation handler
 * Animates translation, rotation, scale and alpha at the same time using Property Animation APIs.
 */
public class DefaultAnimationHandler {

    /** duration of animations, in milliseconds */
    protected static final int DURATION = 800;
    /** duration to wait between each of  */
    protected static final int LAG_BETWEEN_ITEMS = 20;
    /** holds the current state of animation */
    private boolean animating;

    List<View> mListView;

    public DefaultAnimationHandler(List<View> listView) {
        setAnimating(false);
        mListView=listView;
    }
    int[] transNum={0,150,200};

    public void animateMenuOpening() {
        setAnimating(true);
        Animator lastAnimation = null;
        for (int i = 0; i < mListView.size(); i++) {
            View v=mListView.get(i);

            PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, transNum[i],0);
            PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, transNum[mListView.size()-i-1],0);
            PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 360);
            PropertyValuesHolder pvhsX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1);
            PropertyValuesHolder pvhsY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1);
            PropertyValuesHolder pvhA = PropertyValuesHolder.ofFloat(View.ALPHA, 1);

            final ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(v, pvhX, pvhY, pvhR, pvhsX, pvhsY, pvhA);
            animation.setDuration(DURATION);
            animation.setInterpolator(new OvershootInterpolator(0.9f));
//            animation.addListener();

            if(i == 0) {
                lastAnimation = animation;
            }

            // Put a slight lag between each of the menu items to make it asymmetric
            animation.setStartDelay((mListView.size() - i) * LAG_BETWEEN_ITEMS);
            animation.start();
        }
        if(lastAnimation != null) {
//            lastAnimation.addListener(new LastAnimationListener());
        }

    }

    public void animateMenuClosing(Animator.AnimatorListener listener) {
        setAnimating(true);

        Animator lastAnimation = null;
        for (int i = 0; i < mListView.size(); i++) {
            View v=mListView.get(i);
            PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, 0,transNum[i]);
            PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, 0,transNum[mListView.size()-i-1]);
            PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, -360);
            PropertyValuesHolder pvhsX = PropertyValuesHolder.ofFloat(View.SCALE_X, 0);
            PropertyValuesHolder pvhsY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0);
            PropertyValuesHolder pvhA = PropertyValuesHolder.ofFloat(View.ALPHA, 0);

            final ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(v, pvhX, pvhY, pvhR, pvhsX, pvhsY, pvhA);
            animation.setDuration(DURATION);
            animation.setInterpolator(new AccelerateDecelerateInterpolator());
            animation.addListener(listener);

            if(i == 0) {
                lastAnimation = animation;
            }

            animation.setStartDelay((mListView.size() - i) * LAG_BETWEEN_ITEMS);
            animation.start();
        }
        if(lastAnimation != null) {
//            lastAnimation.addListener(new LastAnimationListener());
        }
    }

    public boolean isAnimating() {
        return animating;
    }


    protected void setAnimating(boolean animating) {
        this.animating = animating;
    }

}
