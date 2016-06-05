package com.zyj.jni;

/**
 * Created by yuri.zheng on 2016/6/5.
 */
public final class ZYJJniLib {

    static {
        System.loadLibrary("zyj_ndk_itools");
    }

    private static ZYJJniLib sInstance;

    public static ZYJJniLib getInstance() {
        if (sInstance == null) {
            sInstance = new ZYJJniLib();
        }
        return sInstance;
    }

    private ZYJJniLib() {
    }

    public native String generateSettingCode(String resourceCode);

}
