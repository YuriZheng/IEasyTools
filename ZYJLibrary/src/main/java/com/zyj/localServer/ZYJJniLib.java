package com.zyj.localserver;

/**
 * Author: Yuri.zheng<br>
 * Date: 8/21/16<br>
 * Email: 497393102@qq.com<br>
 */
public final class ZYJJniLib {

    static {
        System.loadLibrary("zyj_ndk_itools");
    }

    protected ZYJJniLib() {
    }

    public native String generateSettingCode(String resourceCode);

}
