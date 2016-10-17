package com.zyj.ieasytools.act;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;

import com.zyj.ieasytools.data.DatabaseUtils;
import com.zyj.ieasytools.data.SettingsConstant;
import com.zyj.ieasytools.dialog.InputEnterPasswordDialog;
import com.zyj.ieasytools.library.db.ZYJContentProvider;
import com.zyj.ieasytools.library.db.ZYJDatabaseSettings;
import com.zyj.ieasytools.library.utils.ZYJUtils;

import java.lang.reflect.Field;

/**
 * Author: Yuri.zheng<br>
 * Date: 8/21/16<br>
 * Email: 497393102@qq.com<br>
 */
public class BaseActivity extends AppCompatActivity {

    protected final Class<?> TAG = getClass();

    /**
     * Whether authentication is required, default needed<br>
     * true mean need
     */
    protected boolean needVerifyPassword = true;

    /**
     * Logic class
     */
    private BasePresenter mBasePresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);

        mBasePresenter = new BasePresenter(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBasePresenter.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBasePresenter.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBasePresenter.onPause();
    }

    /**
     * Exit application in V
     */
    protected void exitApp() {
        mBasePresenter.exitApp();
    }

    /**
     * Get toolbar fields
     */
    protected <T> T getToolbarChildView(Toolbar toolbar, String value, Class<T> clz) {
        try {
            Field colorFid = Toolbar.class.getDeclaredField(value);
            colorFid.setAccessible(true);
            return (T) colorFid.get(toolbar);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get the view by id
     *
     * @param id the view's id
     * @return return the view instance
     */
    protected <T extends View> T getViewById(int id) {
        return (T) getWindow().findViewById(id);
    }

    /**
     * The password verify success
     */
    protected void verifyEnterPasswordSuccess() {
        // Subclass override method
    }

    /**
     * Listen for setting<br>
     * {@link ContentObserver#onChange(boolean, Uri)}
     */
    public void onChange(boolean selfChange, Uri uri) {
        // Subclass override method
    }

    private class BasePresenter implements IBasePresenter {

        private final String LOCAL_BROADCAST = "verify_faile_finish";

        /**
         * The setting database
         */
        private final ZYJDatabaseSettings mSettings;

        private Context mContext;

        /**
         * The main handler
         */
        protected Handler mHandler;
        /**
         * Local broadcast to finish activity when verify faile
         */
        private LocalBroadcastManager mLocalBroadcastManager;
        /**
         * Listener settings change
         */
        private ContentObserver mListener;
        /**
         * Verify dialog,set the callback {@link InputEnterPasswordDialog#setResultCallBack(InputEnterPasswordDialog.VerifyResultCallBack)}
         * to handle the result
         */
        private InputEnterPasswordDialog mInputDialog;

        private BasePresenter(Context context) {
            mContext = context;
            this.mSettings = DatabaseUtils.getSettingsInstance(context);

            mHandler = new Handler(getMainLooper());
            mListener = new ContentObserver(mHandler) {
                public void onChange(boolean selfChange, Uri uri) {
                    ZYJUtils.logD(TAG, uri.toString());
                    BaseActivity.this.onChange(selfChange, uri);
                }
            };

            mLocalBroadcastManager = LocalBroadcastManager.getInstance(mContext);
            mLocalBroadcastManager.registerReceiver(mFinishReceiver, new IntentFilter(LOCAL_BROADCAST));
            getContentResolver().registerContentObserver(ZYJContentProvider.SEETINGS_URI, true, mListener);
        }

        @NonNull
        private Long getLastTime() {
            return mSettings != null ? mSettings.getLongProperties(SettingsConstant.SETTINGS_VERIFY_STATE_LAST_TIME, -1) : -1;
        }

        @NonNull
        private Long getPauseTime() {
            return mSettings != null ? mSettings.getLongProperties(SettingsConstant.SETTINGS_PAUSE_TIME,
                    -1) : -1;
        }

        @NonNull
        private Long getTimeOut() {
            return mSettings != null ? mSettings.getLongProperties(SettingsConstant.SETTINGS_PASSWORD_TIME_OUT,
                    SettingsConstant.SETTINGS_PASSWORD_TIME_OUT_DEFAULT_VALUE) : -1;
        }

        private void putPauseTime() {
            if (mSettings != null && mSettings.verifyValidSetting()) {
                mSettings.putLongProperties(SettingsConstant.SETTINGS_PAUSE_TIME, System.currentTimeMillis());
            }
        }

        private void onResume() {
            ZYJUtils.logD(TAG, "onResume");
            if (!needVerifyPassword) {
                ZYJUtils.logD(TAG, "not need verify");
                return;
            }
            if (checkTimeOutOrPassword()) {
                mHandler.postDelayed(() -> {
                    verifyEnterPassword();
                }, 250);
            } else {
                ZYJUtils.logD(TAG, "time not out");
                verifyEnterPasswordSuccess();
            }
        }

        private void onPause() {
            ZYJUtils.logD(TAG, "onPause");
            putPauseTime();
        }

        private void onDestroy() {
            mLocalBroadcastManager.unregisterReceiver(mFinishReceiver);
            getContentResolver().unregisterContentObserver(mListener);
        }

        private InputEnterPasswordDialog.VerifyResultCallBack mVerifyCallBack = (success) -> {
            if (success) {
                ZYJUtils.logD(TAG, "verifyEnterPasswordSuccess");
                BaseActivity.this.verifyEnterPasswordSuccess();
            } else {
                // verify faile, clear the buffer of password
                // TODO: 2016/5/26 清空密码 待确认
                ZYJUtils.logD(TAG, "verifyEnterPasswordFaile");
                exitApp();
            }
        };

        /**
         * Reveive the broadcast to finish itself
         */
        private BroadcastReceiver mFinishReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                ZYJUtils.logD(TAG, "local broadcast finish");
                finish();
            }
        };

        /**
         * Enter verify dialog and listener the result
         */
        private void verifyEnterPassword() {
            if (mInputDialog != null && mInputDialog.isShowing()) {
                ZYJUtils.logD(TAG, "verifing...");
                return;
            }
            mInputDialog = new InputEnterPasswordDialog(mContext);
            mInputDialog.setResultCallBack(mVerifyCallBack);
            mInputDialog.show();
        }

        private boolean checkTimeOutOrPassword() {
            long timeOut = getTimeOut();
            if (timeOut <= -1) {
                return true;
            }
            long verifyLastTime = getLastTime();
            if (verifyLastTime > 0 || verifyLastTime == InputEnterPasswordDialog.VERIFY_STATE_FAILE) {
                ZYJUtils.logD(TAG, "verify last time: " + verifyLastTime);
                return true;
            }
            long lastTime = getPauseTime();
            if (lastTime < 0) {
                ZYJUtils.logD(TAG, "LastTime: " + lastTime + " and init");
                return true;
            }
            long current = System.currentTimeMillis();
            boolean time = (lastTime + timeOut) < current;
            ZYJUtils.logD(TAG, "Current time: " + current + ", Time out: " + (lastTime + timeOut) + (time ? " and time out" : ""));
            return time;
        }

        private void exitApp() {
            mLocalBroadcastManager.sendBroadcast(new Intent(LOCAL_BROADCAST));
        }
    }
}
