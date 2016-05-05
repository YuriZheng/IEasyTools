package com.zyj.ieasytools.library.encrypt;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * DESEncrypt encrypt class
 *
 * @author yuri.zheng 2016/04/25
 */
public class DESEncrypt extends BaseEncrypt {

    /**
     * Mode and Fill mode,{@link #MODE} can't be modified
     */
    private final String MODE = "DES/ECB/ISO10126Padding";

    private DESEncrypt(String privateKey) {
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
        try {
            Key deskey = generateKey(getPrivateKey());
            Cipher cipher = Cipher.getInstance(MODE);
            cipher.init(Cipher.ENCRYPT_MODE, deskey);
            byte[] results = cipher.doFinal(resourceString.getBytes());
            return Base64Encrypt.Base64Utils.encode(results);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resourceString;
    }

    @Override
    protected String protectedDecrypt(String encryptString) {
        try {
            Key deskey = generateKey(getPrivateKey());
            Cipher cipher = Cipher.getInstance(MODE);
            cipher.init(Cipher.DECRYPT_MODE, deskey);
            return new String(cipher.doFinal(Base64Encrypt.Base64Utils.decode(encryptString.toCharArray())));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encryptString;
    }

    @Override
    public void destory() {
        // TODO Auto-generated method stub
    }

}