package com.zyj.ieasytools.library.encrypt;

import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 * Need a passsword<br>
 * Finish: The call method that distinguishes 4.2 above
 *
 * @author ZYJ:2015-5-17
 */
public class AESEncrypt extends BaseEncrypt {

    public AESEncrypt(String privateKey, String publicKey) {
        super(privateKey, publicKey);
        ENCRYPT_STYLE = ENCRYPT_AES;
    }

//    private SecretKey generateKey(String keyStr) throws Exception {
//        KeyGenerator kgen = KeyGenerator.getInstance("AES");
//        kgen.init(128, new SecureRandom(keyStr.getBytes()));
//        SecretKey secretKey = kgen.generateKey();
//        byte[] enCodeFormat = secretKey.getEncoded();
//        SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
//        System.out.println("1111111: " + key.getFormat());
//        return key;
//    }

    private SecretKey generateKey(String keyStr) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        SecureRandom sr = null;
        if (android.os.Build.VERSION.SDK_INT >= 17) {
            sr = SecureRandom.getInstance("SHA1PRNG", "Crypto");
        } else {
            sr = SecureRandom.getInstance("SHA1PRNG");
        }
        sr.setSeed(keyStr.getBytes());
        kgen.init(128, sr); // 192 and 256 bits may not be available
        SecretKey skey = kgen.generateKey();
        return skey;
    }

    @Override
    protected String protectedEncrypt(String resourceString) {
        try {
            Key key = generateKey(getPrivateKey());
            Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
            byte[] byteContent = resourceString.getBytes("utf-8");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] result = cipher.doFinal(byteContent);
            return Base64Encrypt.Base64Utils.parseByte2HexStr(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resourceString;
    }

    @Override
    protected String protectedDecrypt(String encryptString) {
        try {
            byte[] data = Base64Encrypt.Base64Utils.parseHexStr2Byte(encryptString);
            Key key = generateKey(getPrivateKey());
            Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] result = cipher.doFinal(data);
            return new String(result);
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
