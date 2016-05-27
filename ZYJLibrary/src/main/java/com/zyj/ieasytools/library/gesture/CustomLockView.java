package com.zyj.ieasytools.library.gesture;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by yuri.zheng on 2016/5/27.
 */
public class CustomLockView extends View {
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
    //九宫格的圆
    private GesturePoint[][] mPoints = new GesturePoint[3][3];
    //圆的半径
    private float mPaintRadius = 0;
    //选中圆的集合
    private List<GesturePoint> mSelectPoints = new ArrayList<GesturePoint>();
    //判断是否正在绘制并且未到达下一个点
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
     *
     */
    private float mCirclePadd = 0;
    //是否可操作
    private boolean isTouch = true;
    //密码最小长度
    private int passwordMinLength = 3;
    //判断是否触摸屏幕
    private boolean isTouching = false;
    //刷新
    private TimerTask task = null;
    //计时器
    private Timer timer = new Timer();
    //监听
    private OnCompleteListener mCompleteListener;
    //清除痕迹的时间
    private long CLEAR_TIME = 0;
    //错误限制 默认为4次
    private int errorTimes = 4;
    //记录上一次滑动的密码
    private int[] mIndexs = null;
    //记录当前第几次触发 默认为0次
    private int showTimes = 0;
    //当前密码是否正确 默认为正确
    private boolean isCorrect = true;
    //是否显示滑动方向 默认为显示
    private boolean isDirectionImage = true;
    //验证或者设置 0:设置 1:验证
    private int status = 0;
    //用于执行清除界面
    private Handler mHandler = new Handler();

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private String mPsswordShortMessage;

    //用于定时执行清除界面
    private Runnable run = new Runnable() {
        @Override
        public void run() {
            mHandler.removeCallbacks(run);
            reset();
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
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                if (!isInitCacheFlag) {
                    initCache();
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
        //绘制圆以及显示当前状态
        drawToCanvas(canvas);
    }

    /**
     * 初始化Cache信息
     */
    private void initCache() {
        mViewWidth = this.getWidth();
        mViewHeight = this.getHeight();
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
            isInitCacheFlag = true;
        }
    }

    private boolean checkLocusBitmapValid() {
        return mLocusRoundOriginal != null && mLocusRoundClick != null && mLocusArrow != null
                && mLocusRoundError != null && mLocusArrowError != null;
    }

    /**
     * 图像绘制
     *
     * @param canvas
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
                    if (p.state == GesturePoint.CircleState.STATE_CHECK) {
                        canvas.drawBitmap(mLocusRoundClick, p.x - mPaintRadius, p.y - mPaintRadius, mPaint);
                    } else if (p.state == GesturePoint.CircleState.STATE_CHECK_ERROR) {
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
        // 不可操作
//        if (!isTouch) {
//            return false;
//        }
        isCorrect = true;
        mHandler.removeCallbacks(run);
        mMovingNoPoint = false;
        float ex = event.getX();
        float ey = event.getY();
        boolean isFinish = false;
        GesturePoint p = null;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: // 点下
                // 如果正在清除密码,则取消
                if (task != null) {
                    task.cancel();
                    task = null;
                }
                // 删除之前的点
                reset();
                p = checkSelectPoint(ex, ey);
                if (p != null) {
                    isTouching = true;
                }
                break;
            case MotionEvent.ACTION_MOVE: // 移动
                if (isTouching) {
                    p = checkSelectPoint(ex, ey);
                    if (p == null) {
                        mMovingNoPoint = true;
                        mMoveingX = ex;
                        mMoveingY = ey;
                    }
                }
                break;
            case MotionEvent.ACTION_UP: // 提起
                p = checkSelectPoint(ex, ey);
                isTouching = false;
                isFinish = true;
                break;
            default:
                mMovingNoPoint = true;
                break;
        }
        if (!isFinish && isTouching && p != null) {
            int rk = crossPoint(p);
            if (rk == 2) {
                //与非最后一重叠
                mMovingNoPoint = true;
                mMoveingX = ex;
                mMoveingY = ey;
            } else if (rk == 0) {
                //一个新点
                p.state = GesturePoint.CircleState.STATE_CHECK;
                addPoint(p);
            }
        }
        if (isFinish) {
            mHandler.postDelayed(run, 1500);
            if (this.mSelectPoints.size() == 1) {
                this.reset();
            } else if (this.mSelectPoints.size() < passwordMinLength) {
                clearPassword();
                Toast.makeText(this.getContext(), mPsswordShortMessage, Toast.LENGTH_SHORT).show();
            } else if (mCompleteListener != null) {
                if (this.mSelectPoints.size() >= passwordMinLength) {
                    int[] indexs = new int[mSelectPoints.size()];
                    for (int i = 0; i < mSelectPoints.size(); i++) {
                        indexs[i] = mSelectPoints.get(i).index;
                    }
                    if (status == 0) {
                        invalidatePass(indexs);
                    } else if (status == 1) {
                        invalidateOldPsw(indexs);
                    }
                }
            }
        }
        postInvalidate();
        return true;
    }

    /**
     * 向选中点集合中添加一个点
     *
     * @param point
     */
    private void addPoint(GesturePoint point) {
        this.mSelectPoints.add(point);
    }

    /**
     * 检查点是否被选择
     *
     * @param x
     * @param y
     * @return
     */
    private GesturePoint checkSelectPoint(float x, float y) {
        for (int i = 0; i < mPoints.length; i++) {
            for (int j = 0; j < mPoints[i].length; j++) {
                GesturePoint p = mPoints[i][j];
                if (LockUtil.checkInRound(p.x, p.y, mPaintRadius, (int) x, (int) y)) {
                    return p;
                }
            }
        }
        return null;
    }

    /**
     * 判断点是否有交叉 返回 0,新点 ,1 与上一点重叠 2,与非最后一点重叠
     *
     * @param p
     * @return
     */
    private int crossPoint(GesturePoint p) {
        // 重叠的不最后一个则 reset
        if (mSelectPoints.contains(p)) {
            if (mSelectPoints.size() > 2) {
                // 与非最后一点重叠
                if (mSelectPoints.get(mSelectPoints.size() - 1).index != p.index) {
                    return 2;
                }
            }
            return 1; // 与最后一点重叠
        } else {
            return 0; // 新点
        }
    }

    /**
     * 重置点状态
     */
    public void reset() {
        for (GesturePoint p : mSelectPoints) {
            p.state = GesturePoint.CircleState.STATE_NORMAL;
        }
        mSelectPoints.clear();
    }

    /**
     * 清空当前信息
     */
    public void clearCurrent() {
        showTimes = 0;
        errorTimes = 4;
        isCorrect = true;
        reset();
        postInvalidate();
    }

    /**
     * 画两点的连接
     *
     * @param canvas
     * @param a
     * @param b
     */
    private void drawLine(Canvas canvas, GesturePoint a, GesturePoint b) {
        mPaint.setColor(Color.YELLOW);
        mPaint.setStrokeWidth(3);
        canvas.drawLine(a.x, a.y, b.x, b.y, mPaint);
    }

    /**
     * 错误线
     *
     * @param canvas
     * @param a
     * @param b
     */
    private void drawErrorLine(Canvas canvas, GesturePoint a, GesturePoint b) {
        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(3);
        canvas.drawLine(a.x, a.y, b.x, b.y, mPaint);
    }

    /**
     * 绘制方向图标
     *
     * @param canvas
     * @param a
     * @param b
     */
    private void drawDirection(Canvas canvas, GesturePoint a, GesturePoint b) {
        //获取角度
        float degrees = LockUtil.getDegrees(a, b);
        //根据两点方向旋转
        canvas.rotate(degrees, a.x, a.y);
        float x = a.x + mPaintRadius / 2;
        float y = a.y - mLocusArrow.getHeight() / 2.0f;
        if (degrees == 270) {
            y = a.y - mLocusArrow.getHeight() / 2.0f;
        }
        //绘制箭头
        canvas.drawBitmap(mLocusArrow, x, y, mPaint);
        //旋转方向
        canvas.rotate(-degrees, a.x, a.y);
    }

    /**
     * 错误方向
     *
     * @param canvas
     * @param a
     * @param b
     */
    private void drawErrorDirection(Canvas canvas, GesturePoint a, GesturePoint b) {
        //获取角度
        float degrees = LockUtil.getDegrees(a, b);
        //根据两点方向旋转
        canvas.rotate(degrees, a.x, a.y);
        float x = a.x + mPaintRadius / 2;
        float y = a.y - mLocusArrow.getHeight() / 2.0f;
        if (degrees == 270) {
            y = a.y - mLocusArrow.getHeight() / 2.0f;
        }
        //绘制箭头
        canvas.drawBitmap(mLocusArrowError, x, y, mPaint);
        //旋转方向
        canvas.rotate(-degrees, a.x, a.y);
    }

    /**
     * 清除密码
     */
    private void clearPassword() {
        clearPassword(CLEAR_TIME);
    }

    /**
     * 清除密码
     */
    private void clearPassword(final long time) {
        if (time > 1) {
            if (task != null) {
                task.cancel();
            }
            postInvalidate();
            task = new TimerTask() {
                public void run() {
                    reset();
                    postInvalidate();
                }
            };
            timer.schedule(task, time);
        } else {
            reset();
            postInvalidate();
        }
    }

    /**
     * 设置已经选中的为错误
     */
    private void error() {
        for (GesturePoint p : mSelectPoints) {
            p.state = GesturePoint.CircleState.STATE_CHECK_ERROR;
        }
    }

    /**
     * 验证设置密码，滑动两次密码是否相同
     *
     * @param indexs
     */
    private void invalidatePass(int[] indexs) {
        if (showTimes == 0) {
            mIndexs = indexs;
            mCompleteListener.onComplete(indexs);
            showTimes++;
            reset();
        } else if (showTimes == 1) {
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
                error();
                mCompleteListener.onError();
                postInvalidate();
            } else {
                mCompleteListener.onComplete(indexs);
            }
        }
    }

    /**
     * 验证本地密码与当前滑动密码是否相同
     *
     * @param indexs
     */
    private void invalidateOldPsw(int[] indexs) {
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
            errorTimes--;
            error();
            mCompleteListener.onError();
            postInvalidate();
        } else {
            mCompleteListener.onComplete(indexs);
        }
    }

    /**
     * 设置监听
     *
     * @param mCompleteListener
     */
    public void setOnCompleteListener(OnCompleteListener mCompleteListener) {
        this.mCompleteListener = mCompleteListener;
    }

    /**
     * 轨迹球画完监听事件
     */
    public interface OnCompleteListener {
        /**
         * 画完了
         */
        public void onComplete(int[] indexs);

        /**
         * 绘制错误
         */
        public void onError();
    }

    public Paint getPaint() {
        return mPaint;
    }

    public int getErrorTimes() {
        return errorTimes;
    }

    public void setErrorTimes(int errorTimes) {
        this.errorTimes = errorTimes;
    }

    public int[] getmIndexs() {
        return mIndexs;
    }

    public void setmIndexs(int[] mIndexs) {
        this.mIndexs = mIndexs;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isDirectionImage() {
        return isDirectionImage;
    }

    public void setShow(boolean isDirectionImage) {
        this.isDirectionImage = isDirectionImage;
    }

    public void setCirclePadd(float circlePadd) {
        mCirclePadd = circlePadd;
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
}
