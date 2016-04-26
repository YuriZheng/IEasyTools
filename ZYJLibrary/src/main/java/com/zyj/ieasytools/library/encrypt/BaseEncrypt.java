package com.zyj.ieasytools.library.encrypt;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * The base encrypt class<br>
 * Note:The private key's length must been range {@link #ENCRYPT_PRIVATE_KEY_LENGTH_MIN}-{@link #ENCRYPT_PRIVATE_KEY_LENGTH_MAX}
 *
 * @author yuri.zheng 2016/04/25
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

    /**
     * DES encrypt method
     */
    public static final String ENCRYPT_DES = "DES";
    /**
     * AES encrypt method, Default
     */
    public static final String ENCRYPT_AES = "AES";
    /**
     * RC4 encrypt method
     */
    public static final String ENCRYPT_RC4 = "RC4";
    /**
     * BASE64 encrypt method
     */
    public static final String ENCRYPT_BASE_64 = "BASE64";
    /**
     * BLOWFISH encrypt method
     */
    public static final String ENCRYPT_BLOWFISH = "BLOWFISH";

    private List<EncryptListener> mListener = new ArrayList<EncryptListener>();

    /**
     * Method style:<br>
     * {@link #ENCRYPT_DES}<br>
     * {@link #ENCRYPT_AES}<br>
     * {@link #ENCRYPT_RC4}<br>
     * {@link #ENCRYPT_BASE_64}<br>
     * {@link #ENCRYPT_BLOWFISH}<br>
     */
    protected final String ENCRYPT_STYLE;

    /**
     * Protected encrypt method
     *
     * @param sourceString the source string to encrypt
     * @return the encrypt result, if encyrpt failure then return null
     */
    protected abstract String protectedEncrypt(String sourceString);

    /**
     * Protected decrypt method
     *
     * @param sourceString the source string to decrypt
     * @return the decrypt result, if decrypt failure then return null
     */
    protected abstract String protectedDecrypt(String sourceString);

    /**
     * Call by client to recycle some resources
     */
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
    protected BaseEncrypt(String privateKey, String publicKey, String method) {
        this.mPrivateKey = privateKey;
        this.mPublicKey = publicKey;

        ENCRYPT_STYLE = method;

        if (TextUtils.isEmpty(privateKey) || privateKey.length() < ENCRYPT_PRIVATE_KEY_LENGTH_MIN ||
                privateKey.length() > ENCRYPT_PRIVATE_KEY_LENGTH_MAX) {
            throw new RuntimeException("The private key is error, Key is " + (privateKey != null ? privateKey : "null"));
        }

        if (!TextUtils.isEmpty(publicKey)) {
            if (publicKey.length() < ENCRYPT_PRIVATE_KEY_LENGTH_MIN || publicKey.length() > ENCRYPT_PRIVATE_KEY_LENGTH_MAX) {
                throw new RuntimeException("The public key's length is error: " + publicKey.length());
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

    /**
     * Add the encrypt listener
     *
     * @param listener the listener
     */
    public void addOnEncryptListener(EncryptListener listener) {
        mListener.add(listener);
    }

    /**
     * Remove the encrypt listener
     *
     * @param listener the listener
     */
    public void removeOnEncryptListener(EncryptListener listener) {
        mListener.remove(listener);
    }

    /**
     * Encrypt the string,the message send by {@link EncryptListener}
     *
     * @param resourceString the encrypt string
     * @return {@link #protectedEncrypt}
     */
    public String encrypt(String resourceString) {
        startEncrypt(resourceString);
        String after = protectedEncrypt(resourceString);
        endEncrypt(after);
        return after;
    }

    /**
     * Encrypt the string,the message send by {@link EncryptListener}
     *
     * @param encryptString the encrypt string
     * @return {@link #protectedDecrypt}
     */
    public String decrypt(String encryptString) {
        startDecrypt(encryptString);
        String after = protectedDecrypt(encryptString);
        endDecrypt(after);
        return after;
    }

    private void startEncrypt(String encrypt) {
        for (EncryptListener listener : mListener) {
            listener.startEncrypt(encrypt);
        }
    }

    private void endEncrypt(String result) {
        for (EncryptListener listener : mListener) {
            listener.endEncrypt(result);
        }
    }

    private void startDecrypt(String ecrypt) {
        for (EncryptListener listener : mListener) {
            listener.startDecrypt(ecrypt);
        }
    }

    private void endDecrypt(String result) {
        for (EncryptListener listener : mListener) {
            listener.endDecrypt(result);
        }
    }

    /**
     *
     */
    public static interface EncryptListener {

        /**
         * Start encrypt
         *
         * @param encrypt the resource string
         */
        void startEncrypt(String encrypt);

        /**
         * End of encrypt
         *
         * @param result the string after encrypted
         */
        void endEncrypt(String result);

        /**
         * Start decrypt
         *
         * @param ecrypt the resource string
         */
        void startDecrypt(String ecrypt);

        /**
         * End of decrypt
         *
         * @param result the string after decrypt
         */
        void endDecrypt(String result);

    }
}
