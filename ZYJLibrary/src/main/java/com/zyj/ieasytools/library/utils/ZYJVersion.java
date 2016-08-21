package com.zyj.ieasytools.library.utils;

import android.content.Context;

/**
 * Author: Yuri.zheng<br>
 * Date: 8/21/16<br>
 * Email: 497393102@qq.com<br>
 *
 * Define the app's version, This version code is used in encrypt class,expanded in the future, so the app's version must equal with {@link ZYJVersion}'s max value
 * Created by yuri.zheng on 2016/5/7.
 */
public final class ZYJVersion {

    // *********************************************************************************************
    // *********************************************************************************************
    // Recording the old version here
    public static final int ZERO_VERSION = 0;

    public static final int FIRST_VERSION = 1;
    // *********************************************************************************************
    // *********************************************************************************************

    /**
     * The first version code
     */
//    public static final int MAX_VERSION = 1;

    private static final int CURRENT_VERSION = FIRST_VERSION;

    private ZYJVersion() {

    }

    /**
     * Check {@link ZYJVersion}'s max value whether equal with app's version
     */
    public static void checkLastVersion(final Context c) {
        new Thread() {
            public void run() {
                int version = Integer.parseInt(ZYJUtils.getVersion(c)[1].toString());
                if (version != CURRENT_VERSION) {
                    throw new RuntimeException("The version is " + version + ", but the max is " + CURRENT_VERSION);
                }
            }
        }.start();
    }

    public static int getCurrentVersion() {
        return CURRENT_VERSION;
    }

}
