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

import com.zyj.ieasytools.library.db.ZYJContentProvider;
import com.zyj.ieasytools.library.db.ZYJSettings;
import com.zyj.ieasytools.library.utils.ZYJUtils;
import com.zyj.ieasytools.utils.SettingsConstant;

/**
 * Created by yuri.zheng on 2016/5/11.
 */
public class BaseActivity extends AppCompatActivity {

    private final String LOCAL_BROADCAST = "111111111";

    protected Class<?> TAG = getClass();

    private LocalBroadcastManager mLocalBroadcastManager;

    private ContentObserver mListener;

    protected ZYJSettings mSettings;
    protected Handler mHandler;

    private BroadcastReceiver mFinishReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            ZYJUtils.logD(TAG, "local broadcast finish");
            finish();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHandler = new Handler(getMainLooper());
        mListener = new ContentObserver(mHandler) {
            public void onChange(boolean selfChange, Uri uri) {
                ZYJUtils.logD(TAG, uri.toString());
                BaseActivity.this.onChange(selfChange, uri);
            }
        };
        mSettings = ZYJSettings.getInstance(this);
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        mLocalBroadcastManager.registerReceiver(mFinishReceiver, new IntentFilter(LOCAL_BROADCAST));
        getContentResolver().registerContentObserver(ZYJContentProvider.SEETINGS_URI, true, mListener);
    }

    private boolean checkTimeOutOrPassword() {
        // TODO: 2016/5/26 首先判断密码是否为空，暂留


        long lastTime = mSettings.getLongProperties(SettingsConstant.SETTINGS_PAUSE_TIME, -1);
        if (lastTime < 0) {
            ZYJUtils.logD(TAG, "LastTime: " + lastTime + " and time out");
            return true;
        }
        // TODO: 2016/5/25 这里的默认时间为设置里面的默认时间，暂定10秒
        long timeOut = mSettings.getLongProperties(SettingsConstant.SETTINGS_PASSWORD_TIME_OUT, 1000 * 10);
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
            verifyEnterPassword();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == InputEnterPasswordActivity.REQUEST_CODE) {
            if (resultCode == InputEnterPasswordActivity.RESULT_CODE_SUCCESS) {
                verifyEnterPasswordSuccess();
            } else if (resultCode == InputEnterPasswordActivity.RESULT_CODE_FAILE) {
                // verify faile, then finish all activity
                mLocalBroadcastManager.sendBroadcast(new Intent(LOCAL_BROADCAST));
            }
        }
    }

    /**
     * Enter verify activity and listener the result
     */
    private void verifyEnterPassword() {
        startActivityForResult(new Intent(this, InputEnterPasswordActivity.class), InputEnterPasswordActivity.REQUEST_CODE);
    }

    /**
     * The password verify success
     */
    protected void verifyEnterPasswordSuccess() {
        ZYJUtils.logD(TAG, "verifyEnterPasswordSuccess");
    }

    /**
     * Listen for setting<br>
     * {@link ContentObserver#onChange(boolean, Uri)}
     */
    public void onChange(boolean selfChange, Uri uri) {
        // Subclass override method
    }
}
