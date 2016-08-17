package com.zyj.ieasytools.act;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;

import com.zyj.ieasytools.dialog.InputEnterPasswordDialog;
import com.zyj.ieasytools.library.db.ZYJContentProvider;
import com.zyj.ieasytools.library.db.ZYJDatabaseSettings;
import com.zyj.ieasytools.library.utils.ZYJDatabaseUtils;
import com.zyj.ieasytools.library.utils.ZYJUtils;
import com.zyj.ieasytools.data.EntryptImple;
import com.zyj.ieasytools.data.SettingsConstant;

import java.lang.reflect.Field;

/**
 * Created by yuri.zheng on 2016/5/11.
 */
public class BaseActivity extends AppCompatActivity {

    private final String LOCAL_BROADCAST = "verify_faile_finish";

    protected Class<?> TAG = getClass();

    /**
     * Local broadcast to finish activity when verify faile
     */
    private LocalBroadcastManager mLocalBroadcastManager;

    /**
     * Listener settings change
     */
    private ContentObserver mListener;

    /**
     * The setting database
     */
    protected ZYJDatabaseSettings mSettings;
    /**
     * The main handler
     */
    protected Handler mHandler;

    /**
     * Verify dialog,set the callback {@link InputEnterPasswordDialog#setResultCallBack(InputEnterPasswordDialog.VerifyResultCallBack)}
     * to handle the result
     */
    private InputEnterPasswordDialog mInputDialog;

    /**
     * Reveive the broadcast to finish itself
     */
    private BroadcastReceiver mFinishReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            ZYJUtils.logD(TAG, "local broadcast finish");
            finish();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);

        mHandler = new Handler(getMainLooper());
        mListener = new ContentObserver(mHandler) {
            public void onChange(boolean selfChange, Uri uri) {
                ZYJUtils.logD(TAG, uri.toString());
                BaseActivity.this.onChange(selfChange, uri);
            }
        };
        mSettings = ZYJDatabaseUtils.getSettingsInstance(this);
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        mLocalBroadcastManager.registerReceiver(mFinishReceiver, new IntentFilter(LOCAL_BROADCAST));
        getContentResolver().registerContentObserver(ZYJContentProvider.SEETINGS_URI, true, mListener);
    }

    private boolean checkTimeOutOrPassword() {
        long verifyLastTime = mSettings.getLongProperties(SettingsConstant.SETTINGS_VERIFY_STATE_LAST_TIME, -1);
        if (verifyLastTime > 0 || verifyLastTime == InputEnterPasswordDialog.VERIFY_STATE_FAILE) {
            ZYJUtils.logD(TAG, "verify last time: " + verifyLastTime);
            return true;
        }
        long lastTime = mSettings.getLongProperties(SettingsConstant.SETTINGS_PAUSE_TIME, -1);
        if (lastTime < 0) {
            ZYJUtils.logD(TAG, "LastTime: " + lastTime + " and init");
            return true;
        }
        // TODO: 2016/5/25 这里的默认时间为设置里面的默认时间，暂定1分钟
        long timeOut = mSettings.getLongProperties(SettingsConstant.SETTINGS_PASSWORD_TIME_OUT, 1000 * 60 * 1);
        long current = System.currentTimeMillis();
        boolean time = (lastTime + timeOut) < current;
        ZYJUtils.logD(TAG, "Current time: " + current + ", Time out: " + (lastTime + timeOut) + (time ? " and time out" : ""));
        return time;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocalBroadcastManager.unregisterReceiver(mFinishReceiver);
        getContentResolver().unregisterContentObserver(mListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkTimeOutOrPassword()) {
            mHandler.postDelayed(new Runnable() {
                public void run() {
                    verifyEnterPassword();
                }
            }, 250);
        } else {
            ZYJUtils.logD(TAG, "time not out");
            verifyEnterPasswordSuccess();
        }
        ZYJUtils.logD(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        ZYJUtils.logD(TAG, "onPause");
        if (mSettings.verifyValidSetting()) {
            mSettings.putLongProperties(SettingsConstant.SETTINGS_PAUSE_TIME, System.currentTimeMillis());
        }
    }

    /**
     * Enter verify dialog and listener the result
     */
    private void verifyEnterPassword() {
        if (mInputDialog != null && mInputDialog.isShowing()) {
            ZYJUtils.logD(TAG, "verifing...");
            return;
        }
        mInputDialog = new InputEnterPasswordDialog(this, !EntryptImple.getCurrentDatabasePath(this, false).exists());
        mInputDialog.setResultCallBack(mVerifyCallBack);
        mInputDialog.show();
    }

    private InputEnterPasswordDialog.VerifyResultCallBack mVerifyCallBack = new InputEnterPasswordDialog.VerifyResultCallBack() {
        @Override
        public void verifyEnterPasswordCallBack(boolean success) {
            if (success) {
                ZYJUtils.logD(TAG, "verifyEnterPasswordSuccess");
                verifyEnterPasswordSuccess();
            } else {
                // verify faile, clear the buffer of password
                // TODO: 2016/5/26 清空密码
                ZYJUtils.logD(TAG, "verifyEnterPasswordFaile");
                mLocalBroadcastManager.sendBroadcast(new Intent(LOCAL_BROADCAST));
            }
        }
    };

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
}
