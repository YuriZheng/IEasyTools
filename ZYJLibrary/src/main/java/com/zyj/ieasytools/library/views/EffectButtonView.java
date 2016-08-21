package com.zyj.ieasytools.library.views;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

/**
 * Author: Yuri.zheng<br>
 * Date: 8/21/16<br>
 * Email: 497393102@qq.com<br>
 */
public class EffectButtonView extends View {

    private final Class TAG = getClass();

    /**
     * Duration time
     */
    private final int D_TIME = 250;

    /**
     * Normale color
     */
    private int mNormalColor = Color.parseColor("#ffffffff");
    /**
     * Press color
     */
    private int mPressColor = Color.parseColor("#ff000000");
    /**
     * The rim color
     */
    private int mRimColor = Color.parseColor("#aaffffff");
    /**
     * The rim paint
     */
    private Paint mRimPaint;
    /**
     * The circle paint
     */
    private Paint mCirclePaint;
    /**
     * The text paint
     */
    private Paint mTextPaint;
    /**
     * The animation when press the button
     */
    private ValueAnimator mPressAnimation;
    /**
     * The animation when release the button
     */
    private ValueAnimator mNormalAnimation;
    /**
     * Circle or Rectangle
     */
    private boolean isCircle = false;
    /**
     * Rim visible
     */
    private boolean isRimVisible = true;
    /**
     * The flag when move touch
     */
    private boolean isMoveOutBound = false;
    /**
     * Click listener
     */
    private OnClickListener mListener;
    /**
     * The text
     */
    private String mText;

    public EffectButtonView(Context context) {
        this(context, null);
    }

    public EffectButtonView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public EffectButtonView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, -1);
    }

    public EffectButtonView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        mRimPaint = new Paint();
        mRimPaint.setAntiAlias(true);
        mRimPaint.setDither(true);
        mRimPaint.setColor(mRimColor);
        mRimPaint.setStyle(Paint.Style.STROKE);
        mRimPaint.setStrokeWidth(10);

        mCirclePaint = new Paint();
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setDither(true);
        mCirclePaint.setColor(mNormalColor);
        mCirclePaint.setStyle(Paint.Style.FILL);

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setDither(true);
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextSize(30.0f);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float rimWidth = mRimPaint.getStrokeWidth();
        if (isCircle) {
            if (isRimVisible) {
                canvas.drawCircle(getWidth() / 2, getHeight() / 2, (getWidth() - rimWidth) / 2, mRimPaint);
            } else {
                rimWidth = 0;
            }
            canvas.drawCircle(getWidth() / 2, getHeight() / 2, (getWidth() - rimWidth * 2) / 2, mCirclePaint);
        } else {
            if (isRimVisible) {
                canvas.drawRect(rimWidth / 2, rimWidth / 2, getWidth() - rimWidth / 2, getHeight() - rimWidth / 2, mRimPaint);
            } else {
                rimWidth = 0;
            }
            canvas.drawRect(rimWidth, rimWidth, getWidth() - rimWidth, getHeight() - rimWidth, mCirclePaint);
        }

        if (!TextUtils.isEmpty(mText)) {
            float width = mTextPaint.measureText(mText, 0, mText.length());
            if (width >= getWidth()) {
                canvas.drawText(mText, 0, getHeight() / 2 + mTextPaint.getTextSize() / 2, mTextPaint);
            } else {
                canvas.drawText(mText, (getWidth() - width) / 2, getHeight() / 2 + mTextPaint.getTextSize() / 2, mTextPaint);
            }
        }
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        mListener = l;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                isMoveOutBound = false;
                startPressAnimator();
                break;
            case MotionEvent.ACTION_MOVE:
                if (isMoveOutBound) {
                    return true;
                }
                if (!checkInRound(x, y)) {
                    startNormalAnimator();
                    isMoveOutBound = true;
                    if (mListener != null) {
                        mListener.onClick(this);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (!isMoveOutBound) {
                    startNormalAnimator();
                    if (mListener != null) {
                        mListener.onClick(this);
                    }
                }
                break;
        }
        return true;
    }

    private void startPressAnimator() {
        if (mPressAnimation != null) {
            if (mPressAnimation.isRunning()) {
                mPressAnimation.cancel();
            }
        }
        mPressAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), mCirclePaint.getColor(), mPressColor).setDuration(D_TIME);
        mPressAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int color = (Integer) animation.getAnimatedValue();
                mCirclePaint.setColor(color);
                invalidate();
            }
        });
        mPressAnimation.setInterpolator(new DecelerateInterpolator());
        mPressAnimation.start();
    }

    private void startNormalAnimator() {
        if (mNormalAnimation != null) {
            if (mNormalAnimation.isRunning()) {
                mNormalAnimation.cancel();
            }
        }
        mNormalAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), mCirclePaint.getColor(), mNormalColor).setDuration(D_TIME);
        mNormalAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int color = (Integer) animation.getAnimatedValue();
                mCirclePaint.setColor(color);
                invalidate();
            }
        });
        mNormalAnimation.setInterpolator(new DecelerateInterpolator());
        mNormalAnimation.start();
    }

    public int getNormalColor() {
        return mNormalColor;
    }

    public int getPressColor() {
        return mPressColor;
    }

    public int getRimColor() {
        return mRimColor;
    }

    public String getText() {
        return mText;
    }

    public boolean isCircle() {
        return isCircle;
    }

    public boolean isRimVisible() {
        return isRimVisible;
    }

    /**
     * Whether the rim line visible
     */
    public void setRimVisible(boolean visible) {
        isRimVisible = visible;
        invalidate();
    }

    /**
     * Set the rim line width
     */
    public void setRimStrokeWidth(float width) {
        mRimPaint.setStrokeWidth(width);
        invalidate();
    }

    /**
     * Whether is a circle
     */
    public void setCircle(boolean circle) {
        isCircle = circle;
        invalidate();
    }

    /**
     * Set text
     */
    public void setText(String string) {
        mText = string;
        invalidate();
    }

    /**
     * Set text size
     */
    public void setTextSize(float size) {
        mTextPaint.setTextSize(size);
        invalidate();
    }

    /**
     * Set text color
     */
    public void setTextColor(int color) {
        mTextPaint.setColor(color);
        invalidate();
    }

    private boolean checkInRound(float x, float y) {
        if (isCircle) {
            int cX = getWidth() / 2;
            int cY = getHeight() / 2;
            int r = getWidth() / 2;
            return Math.sqrt(Math.pow((cX - x), 2) + Math.pow((cY - y), 2)) < r;
        } else {
            if (x < 0 || y < 0) {
                return false;
            }
            if (x > getWidth() || y > getHeight()) {
                return false;
            }
            return true;
        }
    }
}
