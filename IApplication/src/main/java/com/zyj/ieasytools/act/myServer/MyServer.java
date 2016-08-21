package com.zyj.ieasytools.act.myServer;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.zyj.ieasytools.library.db.ZYJDatabaseSettings;
import com.zyj.ieasytools.library.utils.ZYJDatabaseUtils;
import com.zyj.ieasytools.library.utils.ZYJUtils;

/**
 * Author: Yuri.zheng<br>
 * Date: 8/21/16<br>
 * Email: 497393102@qq.com<br>
 */
public class MyServer extends Service {

    private Class<?> TAG;

    private ZYJDatabaseSettings mSetting;
    private MyBinder mBinder;

    private Handler mHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler(getMainLooper());
        mBinder = new MyBinder();
        TAG = getClass();
        ZYJUtils.logD(TAG, "onCreate");
    }

    private ZYJDatabaseSettings getSetting() {
        if (mSetting == null) {
            mSetting = ZYJDatabaseUtils.getSettingsInstance(this);
        }
        return mSetting;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getSetting();
        ZYJUtils.logD(TAG, "onStartCommand : " + (getSetting() != null));
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getSetting() != null) {
            getSetting().onDestroy();
        }
        ZYJUtils.logD(TAG, "onDestroy");
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
