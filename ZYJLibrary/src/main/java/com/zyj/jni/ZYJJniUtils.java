package com.zyj.jni;

import java.io.UnsupportedEncodingException;

/**
 * Created by yuri.zheng on 2016/6/5.
 */
public final class ZYJJniUtils {

    private static ZYJJniUtils sInstance;

    private static ZYJJniLib sJniLib;

    public static ZYJJniUtils getInstance() {
        if (sInstance == null) {
            sInstance = new ZYJJniUtils();
        }
        return sInstance;
    }

    private static ZYJJniLib getJniLib() {
        if (sJniLib == null) {
            sJniLib = new ZYJJniLib();
        }
        return sJniLib;
    }

    private ZYJJniUtils() {
    }

    public String generateSettingCode(String resourceCode) {
        try {
            String tmp = new String(resourceCode.getBytes("UTF-8"));
            return getJniLib().generateSettingCode(tmp);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

}
