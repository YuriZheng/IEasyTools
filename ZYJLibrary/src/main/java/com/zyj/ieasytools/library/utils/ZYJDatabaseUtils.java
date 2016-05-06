package com.zyj.ieasytools.library.utils;

import android.content.Context;

import com.zyj.ieasytools.library.encrypt.AESEncrypt;
import com.zyj.ieasytools.library.encrypt.BaseEncrypt;
import com.zyj.ieasytools.library.encrypt.EncryptFactory;

/**
 * Get the encrypt util
 * Created by yuri.zheng on 2016/5/6.
 */
public final class ZYJDatabaseUtils {

    /**
     * Get the settings default encrypt bean
     */
    public static BaseEncrypt getSettingsEncrypt(Context context) {
        String code = ZYJUtils.getMachineCode(context);
        if (code.length() < BaseEncrypt.ENCRYPT_PRIVATE_KEY_LENGTH_MIN) {
            code = code + code;
        } else if (code.length() > BaseEncrypt.ENCRYPT_PRIVATE_KEY_LENGTH_MAX) {
            code = code.substring(0, BaseEncrypt.ENCRYPT_PRIVATE_KEY_LENGTH_MAX - 1);
        }
        return EncryptFactory.getInstance().getInstance(AESEncrypt.class, code);
    }

}
