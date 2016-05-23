package com.zyj.ieasytools.library.encrypt;

import android.text.TextUtils;

import com.zyj.ieasytools.library.utils.ZYJVersion;

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
     * @param privateKey can't be null
     */
    protected BaseEncrypt(String privateKey, String method) {
        this.mPrivateKey = privateKey;

        ENCRYPT_STYLE = method;

        if (TextUtils.isEmpty(privateKey) || privateKey.length() < ENCRYPT_PRIVATE_KEY_LENGTH_MIN ||
                privateKey.length() > ENCRYPT_PRIVATE_KEY_LENGTH_MAX) {
            throw new RuntimeException("The private key is error, Key is " + (privateKey != null ? privateKey : "null"));
        }
    }

    public String getEncryptMethod() {
        return ENCRYPT_STYLE;
    }

    public String getPrivateKey() {
        return mPrivateKey;
    }

    /**
     * Add the encrypt listener<br>
     * Best not to use
     *
     * @param listener the listener
     * @hide
     */
    public void addOnEncryptListener(EncryptListener listener) {
        mListener.add(listener);
    }

    /**
     * Remove the encrypt listener<br>
     * Best not to use
     *
     * @param listener the listener
     * @hide
     */
    public void removeOnEncryptListener(EncryptListener listener) {
        mListener.remove(listener);
    }

    /**
     * Encrypt the string,the message send by {@link EncryptListener}
     *
     * @param resourceString the encrypt string
     * @param version        the encrypt version, different version for different encrypt method
     * @return {@link #protectedEncrypt}
     */
    public String encrypt(String resourceString, int version) {
        startEncrypt(resourceString);
        String after = "";
        // Distinguish encrypt method from diff version
        switch (version) {
            case ZYJVersion.MAX_VERSION:
                after = protectedEncrypt(resourceString);
                break;
        }
        endEncrypt(after);
        return after;
    }

    /**
     * Encrypt the string,the message send by {@link EncryptListener}
     *
     * @param encryptString the encrypt string
     * @param version       the encrypt version, different version for different encrypt method
     * @return {@link #protectedDecrypt}
     */
    public String decrypt(String encryptString, int version) {
        startDecrypt(encryptString);
        String after = "";
        // Distinguish decrypt method from diff version
        switch (version) {
            case ZYJVersion.MAX_VERSION:
                after = protectedDecrypt(encryptString);
                break;
        }
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
