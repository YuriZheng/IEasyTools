package com.zyj.ieasytools.library.views;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;

/**
 * Created by yuri.zheng on 2016/5/16.
 */
public class MenuRevealView extends LinearLayout {

    private final int ANIMATION_DURATION = 200;

    private boolean isAnimation = false;

    public MenuRevealView(Context context) {
        this(context, null);
    }

    public MenuRevealView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public MenuRevealView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setOrientation(LinearLayout.VERTICAL);
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                for (int i = 0; i < getChildCount(); i++) {
                    getChildAt(i).setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    public void showMenu() {
        if (isAnimation) {
            return;
        }
        for (int i = 0; i < getChildCount(); i++) {
            final int index = i;
            final View view = getChildAt(index);
            Animation rotation = getAnimation(view, true);
            rotation.setStartOffset(i * 50);
            rotation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    if (index == 0) {
                        isAnimation = true;
                    }
                    view.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (index == getChildCount() - 1) {
                        isAnimation = false;
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            view.startAnimation(rotation);
        }
    }

    public void hideMenu() {
        if (isAnimation) {
            return;
        }
        for (int i = 0; i < getChildCount(); i++) {
            final int index = i;
            final View view = getChildAt(index);
            Animation rotation = getAnimation(view, false);
            rotation.setStartOffset(i * 50);
            rotation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    if (index == 0) {
                        isAnimation = true;
                    }
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (index == getChildCount() - 1) {
                        setAllChildViewGone();
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            view.startAnimation(rotation);
        }
    }

    public boolean isShowMenu() {
        if (getChildCount() < 1) {
            return false;
        } else {
            return getChildAt(0).getVisibility() == View.VISIBLE;
        }
    }

    private Animation getAnimation(View view, boolean show) {
        FlipAnimation rotation = new FlipAnimation(show ? -90 : 0, show ? 0 : -90, view.getWidth(), view.getHeight() / 2.0f);
        rotation.setDuration(ANIMATION_DURATION);
        rotation.setFillAfter(true);
        rotation.setInterpolator(new AccelerateInterpolator());
        return rotation;
    }

    private void setAllChildViewGone() {
        isAnimation = false;
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).setVisibility(View.GONE);
        }
    }

    /**
     * Created by Konstantin on 22.12.2014.
     */
    private class FlipAnimation extends Animation {
        private final float mFromDegrees;
        private final float mToDegrees;
        private final float mCenterX;
        private final float mCenterY;
        private Camera mCamera;

        public FlipAnimation(float fromDegrees, float toDegrees,
                             float centerX, float centerY) {
            mFromDegrees = fromDegrees;
            mToDegrees = toDegrees;
            mCenterX = centerX;
            mCenterY = centerY;
        }

        @Override
        public void initialize(int width, int height, int parentWidth, int parentHeight) {
            super.initialize(width, height, parentWidth, parentHeight);
            mCamera = new Camera();
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            final float fromDegrees = mFromDegrees;
            float degrees = fromDegrees + ((mToDegrees - fromDegrees) * interpolatedTime);

            final float centerX = mCenterX;
            final float centerY = mCenterY;
            final Camera camera = mCamera;

            final Matrix matrix = t.getMatrix();

            camera.save();

            camera.rotateY(degrees);

            camera.getMatrix(matrix);
            camera.restore();

            matrix.preTranslate(-centerX, -centerY);
            matrix.postTranslate(centerX, centerY);

        }

    }
}
