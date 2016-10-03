package com.zyj.ieasytools.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zyj.ieasytools.R;
import com.zyj.ieasytools.data.DatabaseUtils;
import com.zyj.ieasytools.data.SettingsConstant;
import com.zyj.ieasytools.library.db.BaseDatabase;
import com.zyj.ieasytools.library.db.ZYJDatabaseEncrypts;
import com.zyj.ieasytools.library.db.ZYJDatabaseSettings;
import com.zyj.ieasytools.library.encrypt.BaseEncrypt;
import com.zyj.ieasytools.library.utils.ZYJPreferencesUtils;
import com.zyj.ieasytools.library.utils.ZYJUtils;

/**
 * Author: Yuri.zheng<br>
 * Date: 8/21/16<br>
 * Email: 497393102@qq.com<br>
 */
public class InputEnterPasswordDialog extends Dialog {

    private final Class TAG = getClass();

    // TODO: 6/11/16 修改默认的验证方法
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
    /**
     * Direct return, verify fail
     */
    public static final int VERIFY_STATE_FAILE = -0XB;

    // Verify faile and verify again delayed time
    private final int mTimeOut = 1000 * 60 * 1;

    private final Context mContext;

    // Main view, switch views in it
    private final RelativeLayout mMainView;
    // The verify main view
    private BaseVerifyView mVerifyView;
    // The setting
    private ZYJDatabaseSettings mSettings;

    // Setting or Verify call back
    private VerifyResultCallBack mResultCallBack;
    /**
     * <li>{@link #ENTER_PASSWORD_INPUT}</li>
     * <li>{@link #ENTER_PASSWORD_GESTURE}</li>
     * <li>{@link #ENTER_PASSWORD_IMITATE_IOS}</li>
     * <li>{@link #ENTER_PASSWORD_FINGERPRINT}</li>
     */
    private int mEnterStyle;
    // Setting or Verify: true is setting password, false is verify password
    private final boolean isSettingPassword;

    private Handler mHandler;

    public InputEnterPasswordDialog(Context context, boolean isSetting) {
        super(context, R.style.enter_password_dialog_style);
        mSettings = DatabaseUtils.getSettingsInstance(context);
        if (mSettings == null) {
            ZYJUtils.logW(TAG, "Settings is null");
            dismiss();
        }
        mContext = context;
        mHandler = new Handler(mContext.getMainLooper());
        mMainView = new RelativeLayout(mContext);
        mEnterStyle = mSettings.getIntProperties(SettingsConstant.SETTINGS_PASSWORD_INPUT_STYLE, ENTER_PASSWORD_INPUT);

        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.gravity = Gravity.BOTTOM;
        lp.height = mContext.getResources().getDisplayMetrics().heightPixels - mContext.getResources().getDimensionPixelSize
                (Resources.getSystem().getIdentifier("status_bar_height", "dimen", "android"));
        getWindow().setAttributes(lp);

        isSettingPassword = isSetting;
        String password = mSettings.getStringProperties(SettingsConstant.SETTINGS_SAVE_ENTER_PASSWORD, null);
        if (TextUtils.isEmpty(password) && mEnterStyle == ENTER_PASSWORD_FINGERPRINT) {
            // Fingerprint can't to setting password
            mEnterStyle = ENTER_PASSWORD_INPUT;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(mMainView);
        int title = -1;
        long lastTime = mSettings.getLongProperties(SettingsConstant.SETTINGS_VERIFY_STATE_LAST_TIME, -1);
        if (lastTime > 0 && ((lastTime + mTimeOut) > System.currentTimeMillis())) {
            mVerifyView = new VerifyTimeOut();
            title = R.string.verify_password_no_match;
        } else {
            switch (mEnterStyle) {
                case ENTER_PASSWORD_GESTURE:
                    mVerifyView = new VerifyGestureView();
                    title = R.string.verify_enterpassword_gesture;
                    break;
                case ENTER_PASSWORD_IMITATE_IOS:
                    mVerifyView = new VerifyIosView();
                    title = R.string.verify_enterpassword_ios;
                    break;
                case ENTER_PASSWORD_FINGERPRINT:
                    mVerifyView = new VerifyFingerprintView();
                    title = R.string.verify_enterpassword_fingerprint;
                    break;
                case ENTER_PASSWORD_INPUT:
                    mVerifyView = new VerifyInputView();
                    title = R.string.verify_enterpassword_input;
                    break;
            }
        }
        addToolbar(title);
        if (mVerifyView != null) {
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            lp.addRule(RelativeLayout.BELOW, R.id.toolbar);
            mMainView.addView(mVerifyView.iMain, lp);
        }
        setOnShowListener(new OnShowListener() {
            public void onShow(DialogInterface dialog) {
                if (mResultCallBack != null) {
                    mVerifyView.iVerifyCallBack = mResultCallBack;
                }
            }
        });
        setOnDismissListener(new OnDismissListener() {
            public void onDismiss(DialogInterface dialog) {
                if (mVerifyView != null) {
                    mVerifyView.dismiss();
                }
            }
        });
    }

    private void addToolbar(int title) {
        View toolbarLayout = LayoutInflater.from(mContext).inflate(R.layout.toolbar_layout, null);
        toolbarLayout.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        TextView titleView = (TextView) toolbarLayout.findViewById(R.id.title);
        float size = ZYJPreferencesUtils.getFloat(mContext, SettingsConstant.TOOLBAR_TITLE_SIZE);
        if (size > 0) {
            titleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
        }
        titleView.setText(title);
        toolbarLayout.measure(0, 0);
        mMainView.addView(toolbarLayout, RelativeLayout.LayoutParams.MATCH_PARENT, toolbarLayout.getMeasuredHeight());
    }

    /**
     * Set the callback: {@link VerifyResultCallBack}
     */
    public InputEnterPasswordDialog setResultCallBack(VerifyResultCallBack callBack) {
        mResultCallBack = callBack;
        return this;
    }

    /**
     * The base view to verify password
     */
    private abstract class BaseVerifyView implements View.OnClickListener {
        // The max count of wrong number
        protected final int WRONG_COUNT = 5;
        // The delayed time to dismiss this dialog
        protected final int iDelayed = 1000 * 1;
        // The main view
        protected final View iMain;
        // The Sub title view
        protected final TextView iSubTitle;
        // Verify result call back
        protected VerifyResultCallBack iVerifyCallBack;
        // Record input password last time
        protected String iRecordPassword;
        // Record the count of wrong number
        protected int iRecordCount = 0;

        protected boolean iSuccess = false;

        protected void dismiss() {
            if (mResultCallBack != null) {
                mResultCallBack.verifyEnterPasswordCallBack(iSuccess);
            }
            if (!iSuccess) {
                long lastTime = mSettings.getLongProperties(SettingsConstant.SETTINGS_VERIFY_STATE_LAST_TIME, -1);
                if (lastTime > 0) {
                    // verify time out, do nothing
                } else if (lastTime == -1) {
                    mSettings.putLongProperties(SettingsConstant.SETTINGS_VERIFY_STATE_LAST_TIME, VERIFY_STATE_FAILE);
                }
            } else {
                ZYJUtils.logD(TAG, "Verify success: " + mSettings.remvoeEntry(SettingsConstant.SETTINGS_VERIFY_STATE_LAST_TIME));
            }
        }

        protected BaseVerifyView(int layoutRes) {
            iMain = LayoutInflater.from(mContext).inflate(layoutRes, null);
            iSubTitle = (TextView) iMain.findViewById(R.id.sub_title);
            float size = ZYJPreferencesUtils.getFloat(mContext, SettingsConstant.TOOLBAR_TITLE_SIZE);
            if (size > 0) {
                iSubTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, size - 5);
            }
            if (isSettingPassword) {
                iSubTitle.setText(R.string.enter_password_setting);
            } else {
                iSubTitle.setText(R.string.enter_password_verify);
            }
        }

        protected <T extends View> T findViewById(int resId) {
            return (T) iMain.findViewById(resId);
        }

        protected void verifyFinish() {
            mHandler.postDelayed(new Runnable() {
                public void run() {
                    InputEnterPasswordDialog.this.dismiss();
                }
            }, iDelayed);
        }

    }

    /**
     * Input view to verify password
     */
    private class VerifyInputView extends BaseVerifyView {

        private EditText mInputText;

        private VerifyInputView() {
            super(R.layout.input_input_layout);

            mInputText = findViewById(R.id.input_password);

            iMain.findViewById(R.id.sure).setOnClickListener(this);

            View clear = findViewById(R.id.clear);
            if (!isSettingPassword) {
                clear.setVisibility(View.GONE);
            } else {
                clear.setOnClickListener(this);
            }
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.clear:
                    clear();
                    break;
                case R.id.sure:
                    verify(v);
                    break;
            }
        }

        private void verify(final View v) {
            String input = mInputText.getText().toString();
            mInputText.setText("");
            if (input.length() < BaseEncrypt.ENCRYPT_PRIVATE_KEY_LENGTH_MIN) {
                iSubTitle.setText(R.string.password_short);
                return;
            }
            if (input.length() > BaseEncrypt.ENCRYPT_PRIVATE_KEY_LENGTH_MAX) {
                iSubTitle.setText(R.string.password_long);
                return;
            }
            if (isSettingPassword) {
                if (TextUtils.isEmpty(iRecordPassword)) {
                    iRecordPassword = input;
                    iSubTitle.setText(R.string.verify_enter_copy);
                } else {
                    if (iRecordPassword.equals(input)) {
                        ZYJDatabaseEncrypts encrypt = DatabaseUtils.getCurrentEncryptDatabase(mContext, input);
                        if (encrypt == null || !encrypt.validDatabase()) {
                            if (encrypt == null) {
                                iSubTitle.setText(mContext.getResources().getString(R.string.database_open_error, "null"));
                            } else {
                                BaseDatabase.DATABASE_OPEN_STATE state = encrypt.getDatabaseState();
                                if (state == BaseDatabase.DATABASE_OPEN_STATE.DATABASE_OPEN_FILE_EXCEPTION) {
                                    iSubTitle.setText(mContext.getResources().getString(R.string.database_open_file_exception));
                                } else if (state == BaseDatabase.DATABASE_OPEN_STATE.DATABASE_OPEN_PASSWORD) {
                                    iSubTitle.setText(mContext.getResources().getString(R.string.database_open_password_wrong));
                                } else if (state == BaseDatabase.DATABASE_OPEN_STATE.DATABASE_OPEN_UNKNOW) {
                                    iSubTitle.setText(mContext.getResources().getString(R.string.database_open_open_unknow));
                                } else {
                                    iSubTitle.setText(mContext.getResources().getString(R.string.database_open_unknow));
                                }
                            }
                            iSuccess = false;
                            mInputText.setEnabled(false);
                            v.setOnClickListener(null);
                            verifyFinish();
                        } else {
                            // Save the password
                            mSettings.putStringProperties(SettingsConstant.SETTINGS_SAVE_ENTER_PASSWORD, input);
                            iSubTitle.setText(R.string.setting_password_finish);
                            iSuccess = true;
                            mInputText.setEnabled(false);
                            v.setOnClickListener(null);
                            verifyFinish();
                        }
                    } else {
                        iSubTitle.setText(R.string.verify_password_no_match);
                    }
                }
            } else {
                ZYJDatabaseEncrypts encrypt = DatabaseUtils.getCurrentEncryptDatabase(mContext, input);
                if (encrypt == null || !encrypt.validDatabase()) {
                    if (encrypt == null) {
                        iSubTitle.setText(mContext.getResources().getString(R.string.database_open_error, "null"));
                    } else {
                        BaseDatabase.DATABASE_OPEN_STATE state = encrypt.getDatabaseState();
                        if (state == BaseDatabase.DATABASE_OPEN_STATE.DATABASE_OPEN_FILE_EXCEPTION) {
                            iSubTitle.setText(mContext.getResources().getString(R.string.database_open_file_exception));
                        } else if (state == BaseDatabase.DATABASE_OPEN_STATE.DATABASE_OPEN_PASSWORD) {
                            iSubTitle.setText(mContext.getResources().getString(R.string.database_open_password_wrong));
                        } else if (state == BaseDatabase.DATABASE_OPEN_STATE.DATABASE_OPEN_UNKNOW) {
                            iSubTitle.setText(mContext.getResources().getString(R.string.database_open_open_unknow));
                        } else {
                            iSubTitle.setText(mContext.getResources().getString(R.string.database_open_unknow));
                        }
                    }
                    iRecordCount++;
                    if (iRecordCount >= WRONG_COUNT) {
                        iSuccess = false;
                        iSubTitle.setText(mContext.getResources().getString(R.string.verify_five_minutes_later, mTimeOut / 1000 / 60));
                        // TODO: 2016/8/22  输入五次，mTimeOut分钟之后再试
                        mSettings.putLongProperties(SettingsConstant.SETTINGS_VERIFY_STATE_LAST_TIME, System.currentTimeMillis());
                        mInputText.setEnabled(false);
                        v.setOnClickListener(null);
                        verifyFinish();
                    } else {
                        iSubTitle.setText(mContext.getResources().getString(R.string.verify_error_count, iRecordCount));
                    }
                } else {
                    iSuccess = true;
                    iSubTitle.setText(R.string.verify_passed);
                    mInputText.setEnabled(false);
                    v.setOnClickListener(null);
                    verifyFinish();
                }
            }
        }

        private void clear() {
            iSubTitle.setText(R.string.enter_password_setting);
            mInputText.setText("");
            iRecordPassword = "";
            iRecordCount = 0;
        }
    }

    /**
     * Gesture view to verify password
     */
    private class VerifyGestureView extends BaseVerifyView {
        private VerifyGestureView() {
            super(R.layout.gesture_input_layout);
        }

        @Override
        public void onClick(View v) {

        }

        private void verify() {

        }
    }

    /**
     * Imitate ios view to verify password
     */
    private class VerifyIosView extends BaseVerifyView {
        private VerifyIosView() {
            super(R.layout.ios_input_layout);
        }

        @Override
        public void onClick(View v) {

        }

        private void verify() {

        }
    }

    /**
     * Fingerprint ios view to verify password
     */
    private class VerifyFingerprintView extends BaseVerifyView {
        private VerifyFingerprintView() {
            super(R.layout.fingerprint_input_layout);
        }

        @Override
        public void onClick(View v) {

        }

        private void verify() {

        }
    }

    private class VerifyTimeOut extends BaseVerifyView {
        public VerifyTimeOut() {
            super(R.layout.verify_time_out_input_layout);
        }

        @Override
        public void onClick(View v) {

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
