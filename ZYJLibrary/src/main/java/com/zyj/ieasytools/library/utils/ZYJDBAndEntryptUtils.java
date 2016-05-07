package com.zyj.ieasytools.library.utils;

import android.content.Context;

import com.zyj.ieasytools.library.encrypt.AESEncrypt;
import com.zyj.ieasytools.library.encrypt.BaseEncrypt;
import com.zyj.ieasytools.library.encrypt.EncryptFactory;

/**
 * Get the encrypt util
 * Created by yuri.zheng on 2016/5/6.
 */
public final class ZYJDBAndEntryptUtils {

    /**
     * Get the settings default encrypt bean
     */
    public static BaseEncrypt getSettingsEncrypt(Context context) {
        return   EncryptFactory.getInstance().getInstance(AESEncrypt.class, ZYJUtils.getSettingPassword(context));
    }

}
