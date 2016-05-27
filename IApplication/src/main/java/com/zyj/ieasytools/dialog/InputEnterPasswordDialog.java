package com.zyj.ieasytools.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.zyj.ieasytools.R;
import com.zyj.ieasytools.library.gesture.CustomLockView;

/**
 * Created by yuri.zheng on 2016/5/26.
 */
public class InputEnterPasswordDialog extends Dialog implements Dialog.OnDismissListener {

    private final Class TAG = getClass();

    private final Context mContext;

    /**
     * Main view, switch views in it
     */
    private final RelativeLayout mMainView;

    private VerifyResultCallBack mResultCallBack;

    private CustomLockView mLockView;

    /**
     * 暂时使用此变量调试
     */
    // TODO: 2016/5/26 暂留
    private boolean mSuccess = false;

    public InputEnterPasswordDialog(Context context) {
        super(context, R.style.enter_password_dialog_style);
        mContext = context;
        mMainView = new RelativeLayout(mContext);
        setOnDismissListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View v = LayoutInflater.from(mContext).inflate(R.layout.gesture_input_layout, null);
        mMainView.addView(v, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

        mLockView = (CustomLockView) v.findViewById(R.id.lock_view);

        mLockView.setLocusRoundOriginal(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.gesture_unselected));
        mLockView.setLocusRoundClick(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.gesture_selected));
        mLockView.setLocusArrow(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.gesture_trianglebrown));
        mLockView.setLocusRoundError(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.gesture_selected_error));
        mLockView.setLocusArrowError(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.gesture_trianglebrownerror));

        mLockView.setCirclePadd(100);

        setContentView(mMainView);
    }

    /**
     * Set the callback: {@link VerifyResultCallBack}
     */
    public void setResultCallBack(VerifyResultCallBack callBack) {
        mResultCallBack = callBack;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (mResultCallBack != null) {
            mResultCallBack.verifyEnterPasswordCallBack(mSuccess);
        }
    }

    /**
     * Listener verify result
     */
    public interface VerifyResultCallBack {

        /**
         * Verify the password result callback
         *
         * @param success return result
         */
        void verifyEnterPasswordCallBack(boolean success);

    }
}
