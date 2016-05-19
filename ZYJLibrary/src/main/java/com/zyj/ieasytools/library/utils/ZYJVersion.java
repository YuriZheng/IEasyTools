package com.zyj.ieasytools.library.utils;

import android.content.Context;

/**
 * Define the app's version, This version code is used in encrypt class,expanded in the future, so the app's version must equal with {@link ZYJVersion}'s max value
 * Created by yuri.zheng on 2016/5/7.
 */
public final class ZYJVersion {

    // *********************************************************************************************
    // *********************************************************************************************
    // Recording the old version here
    public static final int ZERO_VERSION = 0;
    // *********************************************************************************************
    // *********************************************************************************************

    /**
     * The first version code
     */
    public static final int MAX_VERSION = 1;

    private ZYJVersion() {

    }

    /**
     * Check {@link ZYJVersion}'s max value whether equal with app's version
     */
    public static void checkLastVersion(final Context c) {
        new Thread() {
            public void run() {
                int version = Integer.parseInt(ZYJUtils.getVersion(c)[1].toString());
                if (version != MAX_VERSION) {
                    throw new RuntimeException("The version is " + version + ", but the max is " + MAX_VERSION);
                }
            }
        }.start();
    }

}
