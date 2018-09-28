package com.zyj.localserver;

import java.io.UnsupportedEncodingException;

/**
 * Author: Yuri.zheng<br>
 * Date: 8/21/16<br>
 * Email: 497393102@qq.com<br>
 */
public final class ZYJLocalUtils {

    private static ZYJLocalUtils sInstance;

    private static com.zyj.localserver.ZYJJniLib sJniLib;

    public static ZYJLocalUtils getInstance() {
        if (sInstance == null) {
            sInstance = new ZYJLocalUtils();
        }
        return sInstance;
    }

    private static com.zyj.localserver.ZYJJniLib getJniLib() {
        if (sJniLib == null) {
            sJniLib = new com.zyj.localserver.ZYJJniLib();
        }
        return sJniLib;
    }

    private ZYJLocalUtils() {
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
