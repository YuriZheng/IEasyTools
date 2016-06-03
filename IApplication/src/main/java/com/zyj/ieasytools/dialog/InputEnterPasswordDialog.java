package com.zyj.ieasytools.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.zyj.ieasytools.R;
import com.zyj.ieasytools.library.db.ZYJSettings;
import com.zyj.ieasytools.library.gesture.CustomLockView;
import com.zyj.ieasytools.library.utils.ZYJUtils;
import com.zyj.ieasytools.utils.SettingsConstant;

/**
 * Created by yuri.zheng on 2016/5/26.
 */
public class InputEnterPasswordDialog extends Dialog implements Dialog.OnDismissListener {

    private final Class TAG = getClass();

    /**
     * Enter password style: gesture
     */
    public static final int ENTER_PASSWORD_GESTURE = 0XC1;
    /**
     * Enter password style: imitate ios
     */
    public static final int ENTER_PASSWORD_IMITATE_IOS = 0XC2;
    /**
     * Enter password style: fingerprint
     */
    public static final int ENTER_PASSWORD_FINGERPRINT = 0XC3;
    /**
     * Enter password style: input
     */
    public static final int ENTER_PASSWORD_INPUT = 0XC4;

    private final Context mContext;

    /**
     * Main view, switch views in it
     */
    private final RelativeLayout mMainView;

    private ZYJSettings mSettings;

    private VerifyResultCallBack mResultCallBack;

    private final int mEnterStyle;

    /**
     * 暂时使用此变量调试
     */
    // TODO: 2016/5/26 暂留
    private boolean mSuccess = false;

    public InputEnterPasswordDialog(Context context) {
        super(context, R.style.enter_password_dialog_style);
        mSettings = ZYJSettings.getInstance(context);
        if (mSettings == null) {
            ZYJUtils.logW(TAG, "Settings is null");
            dismiss();
        }
        mContext = context;
        mMainView = new RelativeLayout(mContext);
        setOnDismissListener(this);
        mEnterStyle = mSettings.getIntProperties(SettingsConstant.SETTINGS_PASSWORD_INPUT_STYLE, ENTER_PASSWORD_INPUT);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(mMainView);

        View v = null;
        switch (mEnterStyle) {
            case ENTER_PASSWORD_GESTURE:
                v = LayoutInflater.from(mContext).inflate(R.layout.gesture_input_layout, null);
                break;
            case ENTER_PASSWORD_IMITATE_IOS:
                v = LayoutInflater.from(mContext).inflate(R.layout.ios_input_layout, null);
                break;
            case ENTER_PASSWORD_FINGERPRINT:
                v = LayoutInflater.from(mContext).inflate(R.layout.fingerprint_input_layout, null);
                break;
            case ENTER_PASSWORD_INPUT:
                v = LayoutInflater.from(mContext).inflate(R.layout.input_input_layout, null);
                break;
        }
        mMainView.addView(v, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
//
//        EffectButtonView view = (EffectButtonView) findViewById(R.id.test);
//        view.setRimVisible(false);
//
//        view.setCircle(true);
//        view.setTextColor(Color.GREEN);
//        view.setText("好");
//        view.setTextSize(60);
    }

    private void test1() {
        android.support.v4.hardware.fingerprint.FingerprintManagerCompat compat = android.support.v4.hardware.fingerprint.FingerprintManagerCompat.from(mContext);
        // 获取是否支持指纹
        boolean exit = compat.isHardwareDetected();
        // 获取是否至少有一个指纹
        compat.hasEnrolledFingerprints();

        android.hardware.fingerprint.FingerprintManager manager = (android.hardware.fingerprint.FingerprintManager) mContext.getSystemService(Context.FINGERPRINT_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            boolean exit = manager.isHardwareDetected();
//            ZYJUtils.logD(TAG, "Exit: " + exit);
//            if (exit) {
//                ZYJUtils.logD(TAG, "至少一个：" + manager.hasEnrolledFingerprints());
//            }
        }
    }

    private CustomLockView mLockView;

    private void test() {
//        mLockView = (CustomLockView) v.findViewById(R.id.lock_view);
        mLockView.setLocusRoundOriginal(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.gesture_unselected));
        mLockView.setLocusRoundClick(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.gesture_selected));
        mLockView.setLocusArrow(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.gesture_trianglebrown));
        mLockView.setLocusRoundError(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.gesture_selected_error));
        mLockView.setLocusArrowError(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.gesture_trianglebrownerror));

        mLockView.setCirclePadd(100);

        mLockView.setEncryptPassword(new CustomLockView.EncryptPassword() {
            @Override
            public String encrypt(String resourceString) {
//                return mEncrypt.encrypt(resourceString, 1);
                return resourceString;
            }

            @Override
            public String decrypt(String encryptString) {
//                return mEncrypt.decrypt(encryptString, 1);
                return encryptString;
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
        mLockView.resetPassword();
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
