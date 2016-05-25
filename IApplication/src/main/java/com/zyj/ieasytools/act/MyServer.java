package com.zyj.ieasytools.act;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.zyj.ieasytools.library.db.ZYJSettings;
import com.zyj.ieasytools.library.utils.ZYJUtils;

/**
 * Created by yuri.zheng on 2016/5/25.
 */
public class MyServer extends Service {

    private ZYJSettings mSetting;
    private MyBinder mBinder;

    private Handler mHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler();
        mBinder = new MyBinder();
        ZYJUtils.logD(getClass(), "onCreate");
    }

    private ZYJSettings getSetting() {
        if (mSetting == null) {
            mSetting = ZYJSettings.getInstance(this);
        }
        return mSetting;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getSetting();
        ZYJUtils.logD(getClass(), "onStartCommand : " + (getSetting() != null));
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getSetting() != null) {
            // clear the password
            getSetting().onDestroy();
        }
        ZYJUtils.logD(getClass(), "onDestroy");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class MyBinder extends Binder {
        public MyServer getService() {
            return MyServer.this;
        }
    }
}
