package com.zyj.ieasytools;

import android.app.Application;

/**
 * Created by yuri.zheng on 2016/4/8.
 */
public class IEasyToolsApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Init settings database
//        ZYJSettings.getInstance(this);
    }

}
