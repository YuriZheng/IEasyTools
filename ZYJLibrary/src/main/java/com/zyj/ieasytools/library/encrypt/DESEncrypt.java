package com.zyj.ieasytools.library.encrypt;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * Need a password<br>
 * Finish:Password length:6-32(In fact,the length is lager size)
 *
 * @author ZYJ:2015-5-17
 */
public class DESEncrypt extends BaseEncrypt {

    //DES way -->> ECB：Electronic code book mode、CBC：Encrypted packet link mode、CFB：Encrypted feedback mode、OFB：Output feedback mode

    public DESEncrypt(String privateKey, String publicKey) {
        super(privateKey, publicKey);
        ENCRYPT_STYLE = ENCRYPT_DES;
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
            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, deskey);
            byte[] results = cipher.doFinal(resourceString.getBytes());
            return Base64Encrypt.Base64Utils.encode(results);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resourceString;
    }

    @Override
    public String getPrivateKey() {
        // If the key's length less than 6, so append '0' to 8
        String key = super.getPrivateKey();
        int size = key.length() - 8;
        if (size < 0) {
            for (int i = 0; i < Math.abs(size); i++) {
                key += "*";
            }
        }
        return key;
    }

    @Override
    protected String protectedDecrypt(String encryptString) {
        try {
            Key deskey = generateKey(getPrivateKey());
            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
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
