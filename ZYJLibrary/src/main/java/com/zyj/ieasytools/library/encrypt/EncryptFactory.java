package com.zyj.ieasytools.library.encrypt;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * Factory method
 *
 * @author yuri.zheng 2016/04/25
 */
public final class EncryptFactory {
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

    public BaseEncrypt getInstance(Class<?> clazz, String privateKey, String publicKey) {
        // BaseEncrypt encrypt = mWeakReferences.get(clazz.getName());
        BaseEncrypt encrypt = null;
        try {
            Class<?> cls = Class.forName(clazz.getName());
            Constructor<?> con = cls.getDeclaredConstructor(new Class<?>[]{String.class, String.class});
            con.setAccessible(true);
            encrypt = (BaseEncrypt) con.newInstance(new Object[]{privateKey, publicKey});
            // mWeakReferences.put(clazz.getName(), encrypt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encrypt;
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
