package com.zyj.ieasytools.library.encrypt;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: Yuri.zheng<br>
 * Date: 8/21/16<br>
 * Email: 497393102@qq.com<br>
 *
 * Factory method
 */
public final class EncryptFactory {

    /**
     * A singleton
     */
    private static EncryptFactory mInstance = null;

    /**
     * Temporary save
     */
    @SuppressWarnings("unused")
    private Map<String, BaseEncrypt> mWeakReferences = new HashMap<String, BaseEncrypt>();

    private EncryptFactory() {
    }

    public static EncryptFactory getInstance() {
        if (mInstance == null) {
            mInstance = new EncryptFactory();
        }
        return mInstance;
    }

    /**
     * Get the encrypt class
     *
     * @param method     the method
     * @param privateKey the password
     * @return return a encrypt object
     */
//    public BaseEncrypt getEncryptInstance(String method, String privateKey) {
//        return getEncryptInstance(getClassFromMethod(method), privateKey);
//    }

    /**
     * Get the encrypt class
     *
     * @param method     the method
     * @param privateKey the password
     * @return return a encrypt object
     */
    public BaseEncrypt getEncryptInstance(String method, String privateKey) {
        if (BaseEncrypt.ENCRYPT_AES.equals(method)) {
            return new AESEncrypt(privateKey);
        } else if (BaseEncrypt.ENCRYPT_BASE_64.equals(method)) {
            return new Base64Encrypt(privateKey);
        } else if (BaseEncrypt.ENCRYPT_BLOWFISH.equals(method)) {
            return new BlowfishEncrypt(privateKey);
        } else if (BaseEncrypt.ENCRYPT_DES.equals(method)) {
            return new DESEncrypt(privateKey);
        } else if (BaseEncrypt.ENCRYPT_RC4.equals(method)) {
            return new RC4Encrypt(privateKey);
        } else {
            throw new RuntimeException("The method is error: " + method);
        }
    }

    // Reflection
    private BaseEncrypt getEncryptInstance(Class<?> clz, String privateKey) {
        // BaseEncrypt encrypt = mWeakReferences.get(clazz.getName());
        BaseEncrypt encrypt = null;
        try {
            Class<?> cls = Class.forName(clz.getName());
            Constructor<?> con = cls.getDeclaredConstructor(new Class<?>[]{String.class});
            con.setAccessible(true);
            encrypt = (BaseEncrypt) con.newInstance(new Object[]{privateKey});
            // mWeakReferences.put(clazz.getName(), encrypt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encrypt;
    }

    /**
     * Get the class from encrypt method
     *
     * @param method the method
     * @return return the class
     */
    private Class<?> getClassFromMethod(String method) {
        if (BaseEncrypt.ENCRYPT_AES.equals(method)) {
            return AESEncrypt.class;
        } else if (BaseEncrypt.ENCRYPT_BASE_64.equals(method)) {
            return Base64Encrypt.class;
        } else if (BaseEncrypt.ENCRYPT_BLOWFISH.equals(method)) {
            return BlowfishEncrypt.class;
        } else if (BaseEncrypt.ENCRYPT_DES.equals(method)) {
            return DESEncrypt.class;
        } else if (BaseEncrypt.ENCRYPT_RC4.equals(method)) {
            return RC4Encrypt.class;
        } else {
            throw new RuntimeException("The method is error: " + method);
        }
    }

    public void onDestory() {
        // if (mWeakReferences != null)
        // {
        // for (String encrypt : mWeakReferences.keySet())
        // {
        // mWeakReferences.get(encrypt).destory();
        // }
        // mWeakReferences.clear();
        // }
        // mWeakReferences = null;
        mInstance = null;
    }

    public void onCreate() {
        // mWeakReferences = new HashMap<String, BaseEncrypt>();
        // mWeakReferences.clear();
    }
}
