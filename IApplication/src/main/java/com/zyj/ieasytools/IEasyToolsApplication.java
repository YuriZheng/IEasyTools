package com.zyj.ieasytools;

import android.app.Application;

import com.zyj.ieasytools.utils.CrashHandler;

/**
 * Author: Yuri.zheng<br>
 * Date: 8/21/16<br>
 * Email: 497393102@qq.com<br>
 */
public class IEasyToolsApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Init settings database
//        ZYJSettings.getInstance(this);
        CrashHandler.getInstance(getApplicationContext());
    }

}
