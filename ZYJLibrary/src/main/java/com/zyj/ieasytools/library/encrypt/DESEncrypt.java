package com.zyj.ieasytools.library.encrypt;

import android.text.TextUtils;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * Author: Yuri.zheng<br>
 * Date: 8/21/16<br>
 * Email: 497393102@qq.com<br>
 *
 * DESEncrypt encrypt class
 */
public class DESEncrypt extends BaseEncrypt {

    /**
     * Mode and Fill mode,{@link #MODE} can't be modified
     */
    private final String MODE = "DES/ECB/ISO10126Padding";

    protected DESEncrypt(String privateKey) {
        super(privateKey, ENCRYPT_DES);
    }

    private SecretKey generateKey(String keyStr) throws Exception {
        DESKeySpec desKey = new DESKeySpec(keyStr.getBytes());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey securekey = keyFactory.generateSecret(desKey);
        return securekey;
    }

    @Override
    protected String protectedEncrypt(String resourceString) {
        if (TextUtils.isEmpty(resourceString)) {
            return "";
        }
        try {
            Key deskey = generateKey(getPrivateKey());
            Cipher cipher = Cipher.getInstance(MODE);
            cipher.init(Cipher.ENCRYPT_MODE, deskey);
            byte[] results = cipher.doFinal(resourceString.getBytes());
            return Base64Encrypt.Base64Utils.encode(results);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected String protectedDecrypt(String encryptString) {
        if (TextUtils.isEmpty(encryptString)) {
            return "";
        }
        try {
            Key deskey = generateKey(getPrivateKey());
            Cipher cipher = Cipher.getInstance(MODE);
            cipher.init(Cipher.DECRYPT_MODE, deskey);
            return new String(cipher.doFinal(Base64Encrypt.Base64Utils.decode(encryptString.toCharArray())));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void destory() {
        // TODO Auto-generated method stub
    }

}
