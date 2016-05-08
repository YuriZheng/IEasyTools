package com.zyj.ieasytools.library.encrypt;

import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 * AES encrypt class,Nees a private password to encrypt or decrypt string<br>
 * Default encrypt class
 *
 * @author yuri.zheng 2016/04/25
 */
public class AESEncrypt extends BaseEncrypt {

    /**
     * Mode and Fill mode,{@link #MODE} can't be modified
     */
    private final String MODE = "AES";

    private AESEncrypt(String privateKey) {
        super(privateKey, ENCRYPT_AES);
    }

    private SecretKey generateKey(String keyStr) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        SecureRandom sr = null;
        if (android.os.Build.VERSION.SDK_INT >= 17) {
            sr = SecureRandom.getInstance("SHA1PRNG", "Crypto");
        } else {
            sr = SecureRandom.getInstance("SHA1PRNG");
        }
        sr.setSeed(keyStr.getBytes("utf-8"));
        kgen.init(128, sr); // 192 and 256 bits may not be available
        SecretKey skey = kgen.generateKey();
        return skey;
    }

    @Override
    protected String protectedEncrypt(String resourceString) {
        try {
            Key key = generateKey(getPrivateKey());
            Cipher cipher = Cipher.getInstance(MODE);
            byte[] byteContent = resourceString.getBytes("utf-8");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return Base64Encrypt.Base64Utils.parseByte2HexStr(cipher.doFinal(byteContent));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected String protectedDecrypt(String encryptString) {
        try {
            byte[] data = Base64Encrypt.Base64Utils.parseHexStr2Byte(encryptString);
            Key key = generateKey(getPrivateKey());
            Cipher cipher = Cipher.getInstance(MODE);
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(cipher.doFinal(data));
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
