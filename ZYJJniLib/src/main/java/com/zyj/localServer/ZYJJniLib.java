package com.zyj.localserver;

/**
 * Created by yuri.zheng on 2016/6/5.
 */
public final class ZYJJniLib {

    static {
        System.loadLibrary("zyj_ndk_itools");
    }

    protected ZYJJniLib() {
    }

    public native String generateSettingCode(String resourceCode);

}
