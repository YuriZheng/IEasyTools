package com.zyj.ieasytools.dialog;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zyj.ieasytools.R;
import com.zyj.ieasytools.act.helpActivity.HelpActivity;
import com.zyj.ieasytools.data.DatabaseUtils;
import com.zyj.ieasytools.data.SettingsConstant;
import com.zyj.ieasytools.library.db.BaseDatabase;
import com.zyj.ieasytools.library.db.ZYJDatabaseEncrypts;
import com.zyj.ieasytools.library.db.ZYJDatabaseSettings;
import com.zyj.ieasytools.library.encrypt.BaseEncrypt;
import com.zyj.ieasytools.library.utils.ZYJPreferencesUtils;
import com.zyj.ieasytools.library.utils.ZYJUtils;
import com.zyj.ieasytools.library.views.EffectButtonView;

import static com.zyj.ieasytools.act.helpActivity.HelpActivity.ENTER_KEY;
import static com.zyj.ieasytools.act.helpActivity.HelpActivity.ENTER_NEED_VERIFY_KEY;
import static com.zyj.ieasytools.act.helpActivity.HelpActivity.ENTER_STYLE_ENTER_PASSWORD;

/**
 * Author: Yuri.zheng<br>
 * Date: 8/21/16<br>
 * Email: 497393102@qq.com<br>
 */
public class InputEnterPasswordDialog extends Dialog {

    private final Class TAG = getClass();

    // TODO: 6/11/16 修改默认的验证方法
    /**
     * Enter password style: imitate ios
     */
    public static final int ENTER_PASSWORD_IMITATE_IOS = 0XC1;
    /**
     * Enter password style: fingerprint
     */
    public static final int ENTER_PASSWORD_FINGERPRINT = 0XC2;
    /**
     * Direct return, verify fail
     */
    public static final int VERIFY_STATE_FAILE = -0XB;

    // Verify faile and verify again delayed time
    private final int mTimeOut = 1000 * 60 * 1;

    /**
     * Database file and password already exists. Verify. User setting enter style.
     */
    private final int ENTER_STYLE_FILE_PASSWORD = 0x1;
    /**
     * Database file already exists. Verify. Input enter style forced.
     */
    private final int ENTER_STYLE_FILE = 0x2;
    /**
     * Password already exists. Verify. User setting enter style.
     */
    private final int ENTER_STYLE_PASSWORD = 0x4;
    /**
     * Database file and password not exists. Init password. Input enter style forced.
     */
    private final int ENTER_STYLE_ = 0x8;

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
     * <li>{@link #ENTER_PASSWORD_IMITATE_IOS}</li>
     * <li>{@link #ENTER_PASSWORD_FINGERPRINT}</li>
     */
    private final int mEnterStyle;
    /**
     * <li>{@link #ENTER_STYLE_FILE_PASSWORD}</li>
     * <li>{@link #ENTER_STYLE_FILE}</li>
     * <li>{@link #ENTER_STYLE_PASSWORD}</li>
     * <li>{@link #ENTER_STYLE_}</li>
     */
    private final int mSettingPasswordStyle;

    private Handler mHandler;

    public InputEnterPasswordDialog(Context context) {
        super(context, R.style.enter_password_dialog_style);
        mSettings = DatabaseUtils.getSettingsInstance(context);
        if (mSettings == null) {
            ZYJUtils.logW(TAG, "Settings is null");
            dismiss();
        }
        mContext = context;
        mHandler = new Handler(mContext.getMainLooper());
        mMainView = new RelativeLayout(mContext);

        try {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.gravity = Gravity.BOTTOM;
            lp.height = mContext.getResources().getDisplayMetrics().heightPixels - mContext.getResources().getDimensionPixelSize
                    (Resources.getSystem().getIdentifier("status_bar_height", "dimen", "android"));
            getWindow().setAttributes(lp);
        } catch (Exception e) {
            ZYJUtils.logW(getClass(), e.getLocalizedMessage());
        }

        boolean existsFile = DatabaseUtils.getCurrentDatabasePath(mContext, false).exists();
        boolean hasPassword = !TextUtils.isEmpty(mSettings.getStringProperties(SettingsConstant.SETTINGS_SAVE_ENTER_PASSWORD, null));
        if (existsFile && hasPassword) {
            mSettingPasswordStyle = ENTER_STYLE_FILE_PASSWORD;
        } else if (existsFile && !hasPassword) {
            mSettingPasswordStyle = ENTER_STYLE_FILE;
        } else if (!existsFile && hasPassword) {
            mSettingPasswordStyle = ENTER_STYLE_PASSWORD;
        } else {
            mSettingPasswordStyle = ENTER_STYLE_;
        }
        if (mSettingPasswordStyle == ENTER_STYLE_FILE || mSettingPasswordStyle == ENTER_STYLE_) {
            mEnterStyle = ENTER_PASSWORD_IMITATE_IOS;
        } else {
            mEnterStyle = mSettings.getIntProperties(SettingsConstant.SETTINGS_PASSWORD_INPUT_STYLE, ENTER_PASSWORD_IMITATE_IOS);
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
                case ENTER_PASSWORD_IMITATE_IOS:
                    mVerifyView = new VerifyIosView();
                    title = R.string.verify_enterpassword_ios;
                    break;
                case ENTER_PASSWORD_FINGERPRINT:
                    mVerifyView = new VerifyFingerprintView();
                    title = R.string.verify_enterpassword_fingerprint;
                    break;
            }
        }
        if (!(mVerifyView instanceof VerifyIosView)) {
            addToolbar(title);
        }
        if (mVerifyView != null) {
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            lp.addRule(RelativeLayout.BELOW, R.id.toolbar);
            mMainView.addView(mVerifyView.iMain, lp);
        }
        setOnShowListener((dialog) -> {
            if (mResultCallBack != null) {
                mVerifyView.iVerifyCallBack = mResultCallBack;
            }
        });
        setOnDismissListener((dialog) -> {
            if (mVerifyView != null) {
                mVerifyView.dismiss();
            }
        });
    }

    private void addToolbar(int title) {
        View toolbarLayout = LayoutInflater.from(mContext).inflate(R.layout.toolbar_layout, null);
        toolbarLayout.findViewById(R.id.back).setOnClickListener((v) -> {
            dismiss();
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
     * <li>{@link #ENTER_STYLE_FILE_PASSWORD}</li>
     * <li>{@link #ENTER_STYLE_FILE}</li>
     * <li>{@link #ENTER_STYLE_PASSWORD}</li>
     * <li>{@link #ENTER_STYLE_}</li><p>
     * 文件存在，密码存在：验证密码，进入的样式按照用户设置的样式<br><p>
     * 文件存在，密码不存在：验证密码然后保存，进入的样式强制设置为输入样式<br><p>
     * 文件不存在，密码存在：验证密码，然后使用密码新建文件，进入样式为用户设置样式<br><p>
     * 文件不存在，密码不存在：初始化设置，进入的样式强制设置为输入样式
     */
    protected int getSettingPasswordStyle() {
        return mSettingPasswordStyle;
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

        protected BaseVerifyView(int layoutRes) {
            iMain = LayoutInflater.from(mContext).inflate(layoutRes, null);
            iSubTitle = (TextView) iMain.findViewById(R.id.sub_title);
            float size = ZYJPreferencesUtils.getFloat(mContext, SettingsConstant.TOOLBAR_TITLE_SIZE);
            if (size > 0) {
                iSubTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, size - 5);
            }
            if (getSettingPasswordStyle() == ENTER_STYLE_) {
                iSubTitle.setText(R.string.enter_password_setting);
            } else {
                iSubTitle.setText(R.string.enter_password_verify);
            }
        }

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

        protected void verifyFinish() {
            mHandler.postDelayed(() -> {
                InputEnterPasswordDialog.this.dismiss();
            }, iDelayed);
        }

        protected <T extends View> T findViewById(int resId) {
            return (T) iMain.findViewById(resId);
        }

    }

    /**
     * Imitate ios view to verify password
     */
    private class VerifyIosView extends BaseVerifyView {

        private EffectButtonView[] mButtons = new EffectButtonView[10];
        private HorizontalScrollView mScrollView;
        private LinearLayout mToastPoint;
        private TextView mLeftButton;

        private Toast mToast = null;

        private boolean enableClick = true;

        private final int mDuration = 100;

        private VerifyIosView() {
            super(R.layout.ios_input_layout);

            mScrollView = findViewById(R.id.scrollView);
            mToastPoint = findViewById(R.id.input_point);

            mLeftButton = findViewById(R.id.del_password);

            setEffectButtonAttrs();
            findViewById(R.id.password_question).setOnClickListener(this);
            findViewById(R.id.enter_password).setOnClickListener(this);
            mLeftButton.setOnClickListener(this);

        }

        private void setEffectButtonAttrs() {
            GridLayout grid = findViewById(R.id.grid_layout);
            for (int i = 0; i < grid.getChildCount(); i++) {
                mButtons[i] = (EffectButtonView) grid.getChildAt(i);
            }
            int length = mButtons.length;
            mButtons[length - 1] = findViewById(R.id.point_zero);

            for (int i = 0; i < length; i++) {
                mButtons[i].setRimStrokeWidth(mContext.getResources().getDimension(R.dimen.iod_verify_effect_button_rim_width));
                mButtons[i].setOnClickListener(mEffectListener);
                mButtons[i].setText(mButtons[i].getTag().toString());
                mButtons[i].setTextSize(mContext.getResources().getDimension(R.dimen.iod_verify_keyboard_textsize));
            }
        }

        private View.OnClickListener mEffectListener = (v) -> {
            if (enableClick) {
                settingPassword(v.getTag().toString());
                setLeftBottomButton(R.string.verify_enter_del);
                if (getSettingPasswordStyle() == ENTER_STYLE_) {
                    iSubTitle.setText(R.string.enter_password_setting);
                } else {
                    iSubTitle.setText(R.string.enter_password_verify);
                }
            }
        };

        private void settingPassword(final String tag) {
            if (mToastPoint.getChildCount() > BaseEncrypt.ENCRYPT_PRIVATE_KEY_LENGTH_MAX) {
                toast(R.string.password_long);
                return;
            }
            final EffectButtonView view = getPointView();
            view.setTag(tag);
            mToastPoint.setOnHierarchyChangeListener(new ViewGroup.OnHierarchyChangeListener() {
                public void onChildViewAdded(View parent, View child) {
                    if (child.equals(view)) {
                        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) view.getLayoutParams();
                        mScrollView.fling(mToastPoint.getWidth() + view.getWidth() + lp.rightMargin);
                        mHandler.postDelayed(() -> {
                            view.select();
                        }, 150);
                    }
                    mToastPoint.setOnHierarchyChangeListener(null);
                }

                public void onChildViewRemoved(View parent, View child) {

                }
            });
            mToastPoint.addView(view);
        }

        private void toast(int messageRes) {
            if (mToast != null) {
                mToast.setText(messageRes);
                mToast.setDuration(Toast.LENGTH_SHORT);
            } else {
                mToast = Toast.makeText(mContext, messageRes, Toast.LENGTH_SHORT);
            }
            mToast.show();
        }

        private void verify(final String password) {
            if (password.length() < BaseEncrypt.ENCRYPT_PRIVATE_KEY_LENGTH_MIN) {
                iSubTitle.setText(R.string.password_short);
                return;
            }
            if (password.length() > BaseEncrypt.ENCRYPT_PRIVATE_KEY_LENGTH_MAX) {
                iSubTitle.setText(R.string.password_long);
                return;
            }
            if (getSettingPasswordStyle() == ENTER_STYLE_) {
                if (TextUtils.isEmpty(iRecordPassword)) {
                    iRecordPassword = password;
                    setLeftBottomButton(R.string.verify_enter_clear);
                    mToastPoint.removeAllViews();
                    iSubTitle.setText(R.string.verify_enter_copy);
                } else {
                    if (iRecordPassword.equals(password)) {
                        ZYJDatabaseEncrypts encrypt = DatabaseUtils.getCurrentEncryptDatabase(mContext, password);
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
                            enableClick = false;
                            verifyFinish();
                        } else {
                            // Save the password
                            mSettings.putStringProperties(SettingsConstant.SETTINGS_SAVE_ENTER_PASSWORD, password);
                            iSubTitle.setText(R.string.setting_password_finish);
                            iSuccess = true;
                            enableClick = false;
                            verifyFinish();
                        }
                    } else {
                        iSubTitle.setText(R.string.verify_password_no_match);
                    }
                }
            } else {
                enableClick = false;
                ZYJDatabaseEncrypts encrypt = DatabaseUtils.getCurrentEncryptDatabase(mContext, password);
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
                        enableClick = false;
                        verifyFinish();
                    } else {
                        iSubTitle.setText(mContext.getResources().getString(R.string.verify_error_count, iRecordCount));
                    }
                } else {
                    iSuccess = true;
                    iSubTitle.setText(R.string.verify_passed);
                    verifyFinish();
                }
            }
        }

        private EffectButtonView getPointView() {
            EffectButtonView view = new EffectButtonView(mContext);
            view.setRimStrokeWidth(mContext.getResources().getDimension(R.dimen.iod_verify_small_effect_button_rim_width));
            int w_h = mContext.getResources().getDimensionPixelOffset(R.dimen.iod_verify_small_point);
            int l_r = mContext.getResources().getDimensionPixelOffset(R.dimen.iod_verify_small_padd);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(w_h, w_h);
            lp.leftMargin = l_r;
            lp.rightMargin = l_r;
            view.setLayoutParams(lp);
            return view;
        }

        @Override
        public void onClick(View v) {
            if (!enableClick) {
                return;
            }
            int id = v.getId();
            if (id == R.id.password_question) {
                Intent intent = new Intent(getContext(), HelpActivity.class);
                intent.putExtra(ENTER_KEY, ENTER_STYLE_ENTER_PASSWORD);
                intent.putExtra(ENTER_NEED_VERIFY_KEY, false);
                getContext().startActivity(intent);
            } else if (id == R.id.del_password) {
                if (mLeftButton.getAlpha() <= 0) {
                    return;
                }
                String o = v.getTag().toString();
                Integer i = Integer.parseInt(o);
                if (i == R.string.verify_enter_clear) {
                    clear();
                    setLeftBottomButton(-1);
                } else if (i == R.string.verify_enter_del) {
                    if (mToastPoint.getChildCount() > 0) {
                        final EffectButtonView lastView = (EffectButtonView) mToastPoint.getChildAt(mToastPoint.getChildCount() - 1);
                        lastView.unSelect();
                        mHandler.postDelayed(() -> {
                            mToastPoint.removeView(lastView);
                        }, lastView.getAnimatorTime());
                        if (mToastPoint.getChildCount() == 1) {
                            if (!TextUtils.isEmpty(iRecordPassword)) {
                                setLeftBottomButton(R.string.verify_enter_clear);
                            } else {
                                setLeftBottomButton(-1);
                            }
                        }
                    } else {
                        if (!TextUtils.isEmpty(iRecordPassword)) {
                            setLeftBottomButton(R.string.verify_enter_clear);
                        } else {
                            setLeftBottomButton(-1);
                        }
                    }
                }
            } else if (id == R.id.enter_password) {
                StringBuilder sb = new StringBuilder(BaseEncrypt.ENCRYPT_PRIVATE_KEY_LENGTH_MAX);
                int count = mToastPoint.getChildCount();
                for (int i = 0; i < count; i++) {
                    EffectButtonView view = (EffectButtonView) mToastPoint.getChildAt(i);
                    sb.append(view.getTag().toString());
                }
                ZYJUtils.logD(getClass(), "Password: " + sb.toString());
                verify(sb.toString());
            }
        }

        private void setLeftBottomButton(int res) {
            mLeftButton.setTag(new Integer(res));
            if (res < 0) {
                if (mLeftButton.getAlpha() >= 1) {
                    ObjectAnimator.ofFloat(mLeftButton, "alpha", 1, 0).setDuration(mDuration).start();
                }
                return;
            }
            if (res > 0) {
                if (mLeftButton.getAlpha() <= 0) {
                    ObjectAnimator.ofFloat(mLeftButton, "alpha", 0, 1).setDuration(mDuration).start();
                }
                mLeftButton.setText(res);
                return;
            }
        }

        private void clear() {
            iSubTitle.setText(R.string.enter_password_setting);
            mToastPoint.removeAllViews();
            iRecordPassword = "";
            iRecordCount = 0;
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
