package com.zyj.ieasytools.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zyj.ieasytools.R;
import com.zyj.ieasytools.library.encrypt.BaseEncrypt;
import com.zyj.ieasytools.library.encrypt.EncryptFactory;
import com.zyj.ieasytools.library.gesture.CustomLockView;
import com.zyj.ieasytools.library.utils.ZYJUtils;

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

    private BaseEncrypt mEncrypt;

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
        mEncrypt = EncryptFactory.getInstance().getInstance(BaseEncrypt.ENCRYPT_AES, "497393102");
        final View v = LayoutInflater.from(mContext).inflate(R.layout.gesture_input_layout, null);
        mMainView.addView(v, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

        mLockView = (CustomLockView) v.findViewById(R.id.lock_view);

        mLockView.setLocusRoundOriginal(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.gesture_unselected));
        mLockView.setLocusRoundClick(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.gesture_selected));
        mLockView.setLocusArrow(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.gesture_trianglebrown));
        mLockView.setLocusRoundError(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.gesture_selected_error));
        mLockView.setLocusArrowError(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.gesture_trianglebrownerror));

        mLockView.setCirclePadd(100);

        mLockView.setEncryptPassword(new CustomLockView.EncryptPassword() {
            @Override
            public String encrypt(String resourceString) {
                return mEncrypt.encrypt(resourceString, 1);
            }

            @Override
            public String decrypt(String encryptString) {
                return mEncrypt.decrypt(encryptString, 1);
            }
        });

        mLockView.setOnCompleteListener(new CustomLockView.OnCompleteListener() {
            @Override
            public void onComplete(int[] indexs) {
                if (mLockView.getLockViewStyle() == CustomLockView.LOCK_STATUS.LOCK_VERIFY) {
                    dismiss();
                    ZYJUtils.logD(TAG, "验证通过");
                }
            }

            @Override
            public void onError(int errorMessage) {
                ZYJUtils.logD(TAG, "Error: " + errorMessage);
            }

            @Override
            public void onCompleteSetting(int[] indexs) {
                ZYJUtils.logD(TAG, "onCompleteSetting: " + indexs.length);
                dismiss();
            }

            @Override
            public void onSelecting(int[] selectPoints) {
                StringBuilder sb = new StringBuilder();
                for (int index : selectPoints) {
                    sb.append(index + ", ");
                }
                ZYJUtils.logD(TAG, "onSelecting: " + sb.toString());
            }
        });
        final TextView text = (TextView) v.findViewById(R.id.title);
        v.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mLockView.resetPassword();
                text.setText(mLockView.getLockViewStyle() == CustomLockView.LOCK_STATUS.LOCK_SETTING ? "设置密码页面" : "验证密码页面");
            }
        });
        v.findViewById(R.id.cancle).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });

        mLockView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                mLockView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                text.setText(mLockView.getLockViewStyle() == CustomLockView.LOCK_STATUS.LOCK_SETTING ? "设置密码页面" : "验证密码页面");
            }
        });

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
