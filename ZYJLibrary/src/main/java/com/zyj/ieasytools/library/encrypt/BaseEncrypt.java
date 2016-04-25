package com.zyj.ieasytools.library.encrypt;

import android.text.TextUtils;

/**
 * @author Administrator
 *         Note:The private key's length must been range 6-32
 */
public abstract class BaseEncrypt {

    /**
     * Min length
     */
    public static final int ENCRYPT_PRIVATE_KEY_LENGTH_MIN = 6;
    /**
     * Max length
     */
    public static final int ENCRYPT_PRIVATE_KEY_LENGTH_MAX = 32;

    // ********************************************************************************
    // Encryption methos
    public static final String ENCRYPT_DES = "DES";
    /**
     * Default encrypt method
     */
    public static final String ENCRYPT_AES = "AES";
    public static final String ENCRYPT_RC4 = "RC4";
    public static final String ENCRYPT_BASE_64 = "BASE64";
    public static final String ENCRYPT_BLOWFISH = "BLOWFISH";
    // End encryption
    // ********************************************************************************

    private IItemEncryptListener mEncryptListener = null;

    protected String ENCRYPT_STYLE = ENCRYPT_AES;

    protected abstract String protectedEncrypt(String resourceString);

    protected abstract String protectedDecrypt(String encryptString);

    public abstract void destory();

    /**
     * All encrypt class use this key
     */
    private String mPrivateKey = null;

    /**
     * Only used once
     */
    private String mPublicKey = null;

    /**
     * @param privateKey can't be null
     * @param publicKey  not used, be null or ""
     */
    public BaseEncrypt(String privateKey, String publicKey) {
        this.mPrivateKey = privateKey;
        this.mPublicKey = publicKey;
        if (TextUtils.isEmpty(privateKey)) {
            throw new RuntimeException("The private can't be null");
        }
        if (privateKey.length() < ENCRYPT_PRIVATE_KEY_LENGTH_MIN) {
            throw new RuntimeException("The private key length more than 6");
        }

        if (privateKey.length() > ENCRYPT_PRIVATE_KEY_LENGTH_MAX) {
            throw new RuntimeException("The private key length less than 32");
        }

        if (!TextUtils.isEmpty(publicKey)) {
            if (publicKey.length() < ENCRYPT_PRIVATE_KEY_LENGTH_MIN) {
                throw new RuntimeException("The publicKey key length more than 6");
            }

            if (publicKey.length() > ENCRYPT_PRIVATE_KEY_LENGTH_MAX) {
                throw new RuntimeException("The publicKey key length less than 32");
            }
        }

    }

    public String getEncryptMethod() {
        return ENCRYPT_STYLE;
    }

    public String getPrivateKey() {
        return mPrivateKey;
    }

    public String getPublicKey() {
        return mPublicKey;
    }

    public void setOnEncryptListener(IItemEncryptListener listener) {
        this.mEncryptListener = listener;
    }

    public String encrypt(String resourceString) {
        if (mEncryptListener != null) {
            mEncryptListener.beforeItemAction(resourceString);
        }
        String after = protectedEncrypt(resourceString);
        if (mEncryptListener != null) {
            mEncryptListener.endItemAction(resourceString, after);
        }
        return after;
    }

    public String decrypt(String encryptString) {
        if (mEncryptListener != null) {
            mEncryptListener.beforeItemAction(encryptString);
        }
        String after = protectedDecrypt(encryptString);
        if (mEncryptListener != null) {
            mEncryptListener.endItemAction(encryptString, after);
        }
        return after;
    }
}
