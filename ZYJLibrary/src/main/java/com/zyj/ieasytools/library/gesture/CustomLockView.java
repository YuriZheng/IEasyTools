package com.zyj.ieasytools.library.gesture;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.zyj.ieasytools.library.utils.PreferencesUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuri.zheng on 2016/5/27.
 */
public class CustomLockView extends View {

    private final Class TAG = getClass();

    private final String KEY = TAG.getSimpleName();

    private Context mContext;
    /**
     * Original state image
     */
    private Bitmap mLocusRoundOriginal;
    /**
     * Click image
     */
    private Bitmap mLocusRoundClick;
    /**
     * Erroe image
     */
    private Bitmap mLocusRoundError;
    /**
     * Original arrow image
     */
    private Bitmap mLocusArrow;
    /**
     * Error  arrow image
     */
    private Bitmap mLocusArrowError;
    /**
     * View width
     */
    private float mViewWidth = 0;
    /**
     * View height
     */
    private float mViewHeight = 0;
    /**
     * Init the cache flag
     */
    private boolean isInitCacheFlag = false;
    /**
     * All gesture point
     */
    private GesturePoint[][] mPoints = new GesturePoint[3][3];
    /**
     * The Select point list
     */
    private List<GesturePoint> mSelectPoints = new ArrayList<GesturePoint>();
    /**
     * Circle radius
     */
    private float mPaintRadius = 0;
    /**
     * Moving gestue point
     */
    private boolean mMovingNoPoint = false;
    /**
     * Moving x
     */
    private float mMoveingX;
    /**
     * Moving y
     */
    private float mMoveingY;
    /**
     * All-around padding
     */
    private float mCirclePadd = 0;
    /**
     * The paint stroke width
     */
    private int mStrokeWidth = 5;
    /**
     * The touch circle min count
     */
    private int mPasswordMinLength = 3;
    /**
     * The clear view delayed time
     */
    private int mDelayedClearView = 1500;
    /**
     * The select point less than {@link #mPasswordMinLength} and clear trace
     */
    private int mClearDelayedTime = 100;
    /**
     * Touching the screen
     */
    private boolean isTouching = false;
    /**
     * Touch listener
     */
    private OnCompleteListener mCompleteListener;
    /**
     * Encrypt method implements by user
     */
    private EncryptPassword mEncryptMethod;
    /**
     * Error limits times
     */
    private int mErrorTimes = 4;
    /**
     * Record error times
     */
    private int mRecordErrorTimes = 0;
    /**
     * Up to 4 times the number of errors, then reset setting delayed time
     */
    private long mErrorDelayedTime = 1000 * 30;
    /**
     * Record last time indexs or answer
     */
    private int[] mIndexs;
    /**
     * Current select point whether is right
     */
    private boolean isCorrect = true;
    /**
     * Whether to display the default display sliding direction
     */
    private boolean isDirectionImage = true;
    /**
     * This lock view statue
     */
    private LOCK_STATUS mLockState = null;


    private PreferencesUtils mSaveUtils;
    private Handler mHandler = new Handler();
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    /**
     * Lock view status
     */
    public enum LOCK_STATUS {
        /**
         * Setting
         */
        LOCK_SETTING,
        /**
         * Verify
         */
        LOCK_VERIFY
    }

    /**
     * Point overlapping
     */
    public enum OVERLAPPING {
        /**
         * New point
         */
        OVERLAPPING_NEW_POINT,
        /**
         * Overlapping
         */
        OVERLAPPING_OVERLAPPING,
        /**
         * Last one no overlapping
         */
        OVERLAPPING_NO_LAST_ONE
    }

    /**
     * The Circle state
     */
    public enum CircleState {
        /**
         * Normal state
         */
        STATE_NORMAL,
        /**
         * Check state
         */
        STATE_CHECK,
        /**
         * Error state
         */
        STATE_CHECK_ERROR
    }

    /**
     * Clear all point in Handler
     */
    private Runnable mClearRunnable = new Runnable() {
        @Override
        public void run() {
            mHandler.removeCallbacks(mClearRunnable);
            resetPoint();
            postInvalidate();
        }
    };

    public CustomLockView(Context context) {
        this(context, null);
    }

    public CustomLockView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public CustomLockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mSaveUtils = new PreferencesUtils(TAG.getSimpleName());
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                if (!isInitCacheFlag) {
                    init();
                }
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!checkLocusBitmapValid()) {
            return;
        }
        if (!isInitCacheFlag) {
            return;
        }
        drawToCanvas(canvas);
    }

    private void init() {
        mViewWidth = this.getWidth();
        mViewHeight = this.getHeight();
        //Forcibly square
        if (mViewWidth != mViewHeight) {
            int min = (int) Math.min(mViewHeight, mViewWidth);
            ViewGroup.LayoutParams lp = getLayoutParams();
            lp.width = min;
            lp.height = min;
            setLayoutParams(lp);
            mViewWidth = mViewHeight = min;
        }
        float y = 0;
        float x = 0;

        float roundMinW = mViewWidth / 8.0f * 2;
        float roundW = roundMinW / 2.f + mCirclePadd;
        if (mLocusRoundOriginal != null) {
            mPoints[0][0] = new GesturePoint(roundW, y + 0 + roundW);
            mPoints[0][0].index = 0;
            mPoints[0][1] = new GesturePoint(x + mViewWidth / 2, y + 0 + roundW);
            mPoints[0][1].index = 1;
            mPoints[0][2] = new GesturePoint(x + mViewWidth - roundW, y + 0 + roundW);
            mPoints[0][2].index = 2;
            mPoints[1][0] = new GesturePoint(roundW, y + mViewHeight / 2);
            mPoints[1][0].index = 3;
            mPoints[1][1] = new GesturePoint(x + mViewWidth / 2, y + mViewHeight / 2);
            mPoints[1][1].index = 4;
            mPoints[1][2] = new GesturePoint(x + mViewWidth - roundW, y + mViewHeight / 2);
            mPoints[1][2].index = 5;
            mPoints[2][0] = new GesturePoint(roundW, y + mViewHeight - roundW);
            mPoints[2][0].index = 6;
            mPoints[2][1] = new GesturePoint(x + mViewWidth / 2, y + mViewHeight - roundW);
            mPoints[2][1].index = 7;
            mPoints[2][2] = new GesturePoint(x + mViewWidth - roundW, y + mViewHeight - roundW);
            mPoints[2][2].index = 8;

            mPaintRadius = mLocusRoundOriginal.getHeight() / 2;
        }

        initLockStyle();
        isInitCacheFlag = true;
    }

    private void initLockStyle() {
        if (mLockState != null) {
            return;
        }
        String string = mSaveUtils.getString(mContext, KEY);
        if (TextUtils.isEmpty(string)) {
            // no password, so it's setting
            mLockState = LOCK_STATUS.LOCK_SETTING;
            mIndexs = null;
        } else {
            mIndexs = stringToArray(string);
            if (mIndexs != null) {
                // it's verify
                mLockState = LOCK_STATUS.LOCK_VERIFY;
            } else {
                mSaveUtils.putString(mContext, KEY, "");
                mLockState = LOCK_STATUS.LOCK_SETTING;
                mIndexs = null;
            }
        }
    }

    private boolean checkLocusBitmapValid() {
        return mLocusRoundOriginal != null && mLocusRoundClick != null && mLocusArrow != null
                && mLocusRoundError != null && mLocusArrowError != null;
    }

    /**
     * Draw all points and lines
     */
    private void drawToCanvas(Canvas canvas) {
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
        // Draw line
        if (mSelectPoints.size() > 0) {
            GesturePoint tp = mSelectPoints.get(0);
            for (int i = 1; i < mSelectPoints.size(); i++) {
                //Draw line by direction
                GesturePoint p = mSelectPoints.get(i);
                if (isCorrect) {
                    drawLine(canvas, tp, p);
                } else {
                    drawErrorLine(canvas, tp, p);
                }
                tp = p;
            }
            if (this.mMovingNoPoint) {
                // arrive next point and stop draw regular line by direction
                drawLine(canvas, tp, new GesturePoint((int) mMoveingX + 20, (int) mMoveingY));
            }
        }
        // Draw all point
        for (int i = 0; i < mPoints.length; i++) {
            for (int j = 0; j < mPoints[i].length; j++) {
                GesturePoint p = mPoints[i][j];
                if (p != null) {
                    if (p.state == CircleState.STATE_CHECK) {
                        canvas.drawBitmap(mLocusRoundClick, p.x - mPaintRadius, p.y - mPaintRadius, mPaint);
                    } else if (p.state == CircleState.STATE_CHECK_ERROR) {
                        canvas.drawBitmap(mLocusRoundError, p.x - mPaintRadius, p.y - mPaintRadius, mPaint);
                    } else {
                        canvas.drawBitmap(mLocusRoundOriginal, p.x - mPaintRadius, p.y - mPaintRadius, mPaint);
                    }
                }
            }
        }
        if (isDirectionImage) {
            // draw the direction image
            if (mSelectPoints.size() > 0) {
                GesturePoint tp = mSelectPoints.get(0);
                for (int i = 1; i < mSelectPoints.size(); i++) {
                    GesturePoint p = mSelectPoints.get(i);
                    if (isCorrect) {
                        drawDirection(canvas, tp, p);
                    } else {
                        drawErrorDirection(canvas, tp, p);
                    }
                    tp = p;
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mRecordErrorTimes >= mErrorTimes) {
            if (mCompleteListener != null) {
                mCompleteListener.onError(OnCompleteListener.ERROR_PASSWORD_ERROR_DELAYED);
            }
            return true;
        }
        isCorrect = true;
        mHandler.removeCallbacks(mClearRunnable);
        mMovingNoPoint = false;
        float ex = event.getX();
        float ey = event.getY();
        boolean finish = false;
        GesturePoint p = null;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                resetPoint();
                p = checkSelectPoint(ex, ey);
                if (p != null) {
                    isTouching = true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (isTouching) {
                    p = checkSelectPoint(ex, ey);
                    if (p == null) {
                        mMovingNoPoint = true;
                        mMoveingX = ex;
                        mMoveingY = ey;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                p = checkSelectPoint(ex, ey);
                isTouching = false;
                finish = true;
                break;
            default:
                mMovingNoPoint = true;
                break;
        }
        if (!finish && isTouching && p != null) {
            OVERLAPPING rk = crossPoint(p);
            if (rk == OVERLAPPING.OVERLAPPING_NO_LAST_ONE) {
                mMovingNoPoint = true;
                mMoveingX = ex;
                mMoveingY = ey;
            } else if (rk == OVERLAPPING.OVERLAPPING_NEW_POINT) {
                p.state = CircleState.STATE_CHECK;
                mSelectPoints.add(p);
                if (mCompleteListener != null) {
                    int size = mSelectPoints.size();
                    int[] points = new int[size];
                    for (int i = 0; i < size; i++) {
                        points[i] = mSelectPoints.get(i).index;
                    }
                    mCompleteListener.onSelecting(points);
                }
            }
        }
        if (finish) {
            mHandler.postDelayed(mClearRunnable, mDelayedClearView);
            int selectSize = mSelectPoints.size();
            if (selectSize == 1) {
                resetPoint();
            } else if (selectSize < mPasswordMinLength) {
                mHandler.postDelayed(mClearRunnable, mClearDelayedTime);
                if (mCompleteListener != null) {
                    mCompleteListener.onError(OnCompleteListener.ERROR_PASSWORD_SHORT);
                }
            } else {
                int[] indexs = new int[mSelectPoints.size()];
                for (int i = 0; i < mSelectPoints.size(); i++) {
                    indexs[i] = mSelectPoints.get(i).index;
                }
                if (mLockState == LOCK_STATUS.LOCK_SETTING) {
                    settingPass(indexs);
                } else if (mLockState == LOCK_STATUS.LOCK_VERIFY) {
                    verifyOldPassword(indexs);
                }
            }
        }
        postInvalidate();
        return true;
    }

    /**
     * Check the point whether select
     */
    private GesturePoint checkSelectPoint(float x, float y) {
        for (int i = 0; i < mPoints.length; i++) {
            for (int j = 0; j < mPoints[i].length; j++) {
                GesturePoint p = mPoints[i][j];
                if (checkInRound(p.x, p.y, mPaintRadius, (int) x, (int) y)) {
                    return p;
                }
            }
        }
        return null;
    }

    /**
     * Whether cross: {@link OVERLAPPING}
     */
    private OVERLAPPING crossPoint(GesturePoint p) {
        if (mSelectPoints.contains(p)) {
            if (mSelectPoints.size() > 2) {
                if (mSelectPoints.get(mSelectPoints.size() - 1).index != p.index) {
                    return OVERLAPPING.OVERLAPPING_NO_LAST_ONE;
                }
            }
            return OVERLAPPING.OVERLAPPING_OVERLAPPING;
        } else {
            return OVERLAPPING.OVERLAPPING_NEW_POINT;
        }
    }

    /**
     * Reset select point
     */
    private void resetPoint() {
        for (GesturePoint p : mSelectPoints) {
            p.state = CircleState.STATE_NORMAL;
        }
        mSelectPoints.clear();
    }

    /**
     * Connecting two points
     */
    private void drawLine(Canvas canvas, GesturePoint a, GesturePoint b) {
        mPaint.setColor(Color.YELLOW);
        mPaint.setStrokeWidth(mStrokeWidth);
        canvas.drawLine(a.x, a.y, b.x, b.y, mPaint);
    }

    /**
     * Error line
     */
    private void drawErrorLine(Canvas canvas, GesturePoint a, GesturePoint b) {
        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(mStrokeWidth);
        canvas.drawLine(a.x, a.y, b.x, b.y, mPaint);
    }

    /**
     * Draw direction icon
     */
    private void drawDirection(Canvas canvas, GesturePoint a, GesturePoint b) {
        float degrees = getDegrees(a, b);
        canvas.rotate(degrees, a.x, a.y);
        float x = a.x + mPaintRadius / 2;
        float y = a.y - mLocusArrow.getHeight() / 2.0f;
        if (degrees == 270) {
            y = a.y - mLocusArrow.getHeight() / 2.0f;
        }
        canvas.drawBitmap(mLocusArrow, x, y, mPaint);
        canvas.rotate(-degrees, a.x, a.y);
    }

    /**
     * Wrong direction
     */
    private void drawErrorDirection(Canvas canvas, GesturePoint a, GesturePoint b) {
        // get the angle
        float degrees = getDegrees(a, b);
        // according to two directions of rotation
        canvas.rotate(degrees, a.x, a.y);
        float x = a.x + mPaintRadius / 2;
        float y = a.y - mLocusArrow.getHeight() / 2.0f;
        if (degrees == 270) {
            y = a.y - mLocusArrow.getHeight() / 2.0f;
        }
        // draw an arrow
        canvas.drawBitmap(mLocusArrowError, x, y, mPaint);
        //turn around
        canvas.rotate(-degrees, a.x, a.y);
    }

    /**
     * The second verify is wrong and set the select points to error
     */
    private void verifySecondError() {
        for (GesturePoint p : mSelectPoints) {
            p.state = CircleState.STATE_CHECK_ERROR;
        }
    }

    /**
     * Setting the password
     */
    private void settingPass(int[] indexs) {
        if (mIndexs == null || mIndexs.length < mPasswordMinLength) {
            // First
            mIndexs = indexs;
            if (mCompleteListener != null) {
                mCompleteListener.onComplete(mIndexs);
            }
        } else if (mIndexs != null && mIndexs.length >= mPasswordMinLength) {
            // verify the second
            if (mIndexs.length == indexs.length) {
                for (int i = 0; i < mIndexs.length; i++) {
                    if (mIndexs[i] != indexs[i]) {
                        isCorrect = false;
                        break;
                    }
                }
            } else {
                isCorrect = false;
            }
            if (!isCorrect) {
                verifySecondError();
                if (mCompleteListener != null) {
                    mCompleteListener.onError(OnCompleteListener.ERROR_PASSWORD_VERIFY_ERROR);
                }
                postInvalidate();
            } else {
                String password = arrayToString(indexs);
                if (mSaveUtils.putString(mContext, KEY, password)) {
                    if (mCompleteListener != null) {
                        mCompleteListener.onCompleteSetting(indexs);
                    }
                }
            }
        }
    }

    /**
     * Verify the local password is right
     *
     * @param indexs the local save in file
     */
    private void verifyOldPassword(int[] indexs) {
        if (mIndexs.length == indexs.length) {
            for (int i = 0; i < mIndexs.length; i++) {
                if (mIndexs[i] != indexs[i]) {
                    isCorrect = false;
                    break;
                }
            }
        } else {
            isCorrect = false;
        }
        if (!isCorrect) {
            mRecordErrorTimes++;
            verifySecondError();
            if (mCompleteListener != null) {
                mCompleteListener.onError(OnCompleteListener.ERROR_PASSWORD_ERROR);
            }
            if (mRecordErrorTimes >= mErrorTimes) {
                mHandler.postDelayed(new Runnable() {
                    public void run() {
                        mRecordErrorTimes = 0;
                    }
                }, mErrorDelayedTime);
            }
            postInvalidate();
        } else {
            mRecordErrorTimes = 0;
            if (mCompleteListener != null) {
                mCompleteListener.onComplete(indexs);
            }
        }
    }

    /**
     * 1 = 30 degrees<br> 2 = 45 degrees<br> 4 = 60 degrees
     */
    private float switchDegrees(float x, float y) {
        return (float) pointTotoDegrees(x, y);
    }


    /**
     * Get the angle
     */
    private float getDegrees(GesturePoint a, GesturePoint b) {
        float ax = a.x;// a.index % 3;
        float ay = a.y;// a.index / 3;
        float bx = b.x;// b.index % 3;
        float by = b.y;// b.index / 3;
        float degrees = 0;
        if (bx == ax) {
            // y-axis equal, 90 or 270
            if (by > ay) {
                // In the bottom of the y-axis, 90
                degrees = 90;
            } else if (by < ay) {
                //Y-axis in the above, 270
                degrees = 270;
            }
        } else if (by == ay) {
            // y-axis equal, 0 or 180
            if (bx > ax) {
                // In the bottom of the y-axis, 90
                degrees = 0;
            } else if (bx < ax) {
                // Y-axis in the above, 270
                degrees = 180;
            }
        } else {
            if (bx > ax) {
                // In the right side of the y-axis,270~90
                if (by > ay) {
                    // In the bottom of the y-axis, 0~90
                    degrees = 0;
                    degrees = degrees + switchDegrees(Math.abs(by - ay), Math.abs(bx - ax));
                } else if (by < ay) {
                    // Y-axis in the above, 270~0
                    degrees = 360;
                    degrees = degrees - switchDegrees(Math.abs(by - ay), Math.abs(bx - ax));
                }

            } else if (bx < ax) {
                // The left y-axis, 90~270
                if (by > ay) {
                    // In the bottom of the y-axis, 180 ~ 270
                    degrees = 90;
                    degrees = degrees + switchDegrees(Math.abs(bx - ax), Math.abs(by - ay));
                } else if (by < ay) {
                    // Y-axis in the above, 90~180
                    degrees = 270;
                    degrees = degrees - switchDegrees(Math.abs(bx - ax), Math.abs(by - ay));
                }

            }

        }
        return degrees;
    }

    /**
     * Determine whether the point within a circle
     */
    private boolean checkInRound(float sx, float sy, float r, float x, float y) {
        return Math.sqrt((sx - x) * (sx - x) + (sy - y) * (sy - y)) < r;
    }

    /**
     * Calculate two coordinate angles
     */
    private double pointTotoDegrees(double x, double y) {
        return Math.toDegrees(Math.atan2(x, y));
    }

    private String arrayToString(int[] array) {
        StringBuilder sb = new StringBuilder();
        for (int i : array) {
            sb.append(i + ",");
        }
        String result = sb.toString();
        if (mEncryptMethod != null) {
            result = mEncryptMethod.encrypt(result);
        }
        return result;
    }

    private int[] stringToArray(String string) {
        if (mEncryptMethod != null) {
            string = mEncryptMethod.decrypt(string);
        }
        if (string.contains(",")) {
            String[] subs = string.split(",");
            List<Integer> tmp = new ArrayList<Integer>();
            for (String sub : subs) {
                if (!TextUtils.isEmpty(sub)) {
                    tmp.add(Integer.parseInt(sub));
                }
            }
            int size = tmp.size();
            int[] result = new int[size];
            for (int i = 0; i < size; i++) {
                result[i] = tmp.get(i);
            }
            return result;
        }
        return null;
    }

    /**
     * The touch finish listener
     */
    public interface OnCompleteListener {

        /**
         * The password is short
         */
        int ERROR_PASSWORD_SHORT = 1;

        /**
         * The second password verify faile
         */
        int ERROR_PASSWORD_VERIFY_ERROR = 2;

        /**
         * Enter password is wrong
         */
        int ERROR_PASSWORD_ERROR = 3;
        /**
         * Verify faile {@link #mErrorTimes} and then verify after {@link #mErrorDelayedTime} millis
         */
        int ERROR_PASSWORD_ERROR_DELAYED = 4;

        /**
         * Draw finish
         *
         * @param indexs the draw point index
         */
        void onComplete(int[] indexs);

        /**
         * Finish setting password
         */
        void onCompleteSetting(int[] indexs);

        /**
         * Selecting points
         *
         * @param selectPoints the points
         */
        void onSelecting(int[] selectPoints);

        /**
         * Draw error
         *
         * @param errorMessage <li>{@link #ERROR_PASSWORD_SHORT}</li>
         *                     <li>{@link #ERROR_PASSWORD_VERIFY_ERROR}</li>
         *                     <li>{@link #ERROR_PASSWORD_ERROR}</li>
         *                     <li>{@link #ERROR_PASSWORD_ERROR_DELAYED}</li>
         */
        void onError(int errorMessage);
    }

    /**
     * The encrypt/decrypt method implemented by user
     */
    public interface EncryptPassword {
        /**
         * Encrypt password to save
         *
         * @param resourceString the resource string
         * @return the string after encrypt
         */
        String encrypt(String resourceString);

        /**
         * Decrypt string to password
         *
         * @param encryptString the resource string of passwrod after encrypt
         * @return the string after decrypt
         */
        String decrypt(String encryptString);
    }

    public Paint getPaint() {
        return mPaint;
    }

    /**
     * Detecting the presence or absence password
     *
     * @return true if the password exist, other return false
     */
    public boolean checkVerifyPassword() {
        String password = mSaveUtils.getString(mContext, KEY);
        if (TextUtils.isEmpty(password)) {
            return false;
        }
        if (mEncryptMethod != null) {
            password = mEncryptMethod.decrypt(password);
        }
        if (password.contains(",")) {
            return password.split(",").length >= mPasswordMinLength;
        }
        return false;
    }

    public void destory() {
        if (mLocusRoundOriginal != null && !mLocusRoundOriginal.isRecycled()) {
            mLocusRoundOriginal.recycle();
        }
        if (mLocusRoundClick != null && !mLocusRoundClick.isRecycled()) {
            mLocusRoundClick.recycle();
        }
        if (mLocusArrow != null && !mLocusArrow.isRecycled()) {
            mLocusArrow.recycle();
        }
        if (mLocusRoundError != null && !mLocusRoundError.isRecycled()) {
            mLocusRoundError.recycle();
        }
        if (mLocusArrowError != null && !mLocusArrowError.isRecycled()) {
            mLocusArrowError.recycle();
        }
    }

    /**
     * Change the state to {@link LOCK_STATUS#LOCK_SETTING}
     */
    public void resetPassword() {
        mRecordErrorTimes = 0;
        mIndexs = null;
        resetPoint();
        isCorrect = true;
        postInvalidate();
        mLockState = LOCK_STATUS.LOCK_SETTING;
        mSaveUtils.putString(mContext, KEY, "");
    }

    /**
     * Set the main boundary padding
     */
    public void setCirclePadd(float circlePadd) {
        mCirclePadd = circlePadd;
    }

    /**
     * Set the touch line sreoke width
     */
    public void setStrokeWidth(int strokeWidth) {
        mStrokeWidth = strokeWidth;
    }

    /**
     * Set clear view delayed time after finish draw
     */
    public void setDelayedClearTime(int delayedClearTime) {
        mDelayedClearView = delayedClearTime;
    }

    /**
     * Set the time after draw error
     */
    public void setErrorDelayedClearTime(int errorDelayedClearTime) {
        mClearDelayedTime = errorDelayedClearTime;
    }

    /**
     * Whether show arrow image
     */
    public void setDirectionImage(boolean showDirectionImage) {
        isDirectionImage = showDirectionImage;
    }

    /**
     * Set complete listener
     */
    public void setOnCompleteListener(OnCompleteListener mCompleteListener) {
        this.mCompleteListener = mCompleteListener;
    }

    /**
     * Get lock view style<br>
     * This method called after {@link #setEncryptPassword(EncryptPassword)}<br>
     * If have no {@link EncryptPassword} then call this method immediate
     */
    public LOCK_STATUS getLockViewStyle() {
        initLockStyle();
        return mLockState;
    }

    public void setEncryptPassword(EncryptPassword method) {
        mEncryptMethod = method;
        initLockStyle();
    }

    public void setErrorTime(int times) {
        mErrorTimes = times;
    }

    public void setLocusRoundOriginal(Bitmap locusRoundOriginal) {
        this.mLocusRoundOriginal = locusRoundOriginal;
        if (mLocusRoundOriginal.getHeight() != mLocusRoundOriginal.getWidth()) {
            throw new RuntimeException("the bitmap is not rectangle");
        }
    }

    public void setLocusRoundClick(Bitmap locusRoundClick) {
        this.mLocusRoundClick = locusRoundClick;
        if (mLocusRoundClick.getHeight() != mLocusRoundClick.getWidth()) {
            throw new RuntimeException("the bitmap is not rectangle");
        }
    }

    public void setLocusRoundError(Bitmap locusRoundError) {
        this.mLocusRoundError = locusRoundError;
        if (mLocusRoundError.getHeight() != mLocusRoundError.getWidth()) {
            throw new RuntimeException("the bitmap is not rectangle");
        }
    }

    public void setLocusArrow(Bitmap locusArrow) {
        this.mLocusArrow = locusArrow;
    }

    public void setLocusArrowError(Bitmap locusArrowError) {
        this.mLocusArrowError = locusArrowError;
    }

    private class GesturePoint {
        public float x;
        public float y;
        public int index = 0;
        /**
         * This point state
         */
        public CircleState state = CircleState.STATE_NORMAL;

        @SuppressWarnings("unused")
        public GesturePoint() {
        }

        public GesturePoint(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }
}


