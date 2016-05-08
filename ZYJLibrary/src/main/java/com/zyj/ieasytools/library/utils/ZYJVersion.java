package com.zyj.ieasytools.library.utils;

import android.content.Context;

import java.lang.reflect.Field;

/**
 * Define the app's version, This version code is used in encrypt class,expanded in the future, so the app's version must equal with {@link ZYJVersion}'s max value
 * Created by yuri.zheng on 2016/5/7.
 */
public final class ZYJVersion {

    /**
     * The first version code
     */
    public static final int FIRST_VERSION = 1;

    private ZYJVersion() {

    }

    /**
     * Check {@link ZYJVersion}'s max value whether equal with app's version
     */
    public static void checkLastVersion(final Context c) {
        new Thread() {
            public void run() {
                int version = Integer.parseInt(ZYJUtils.getVersion(c)[1].toString());
                ZYJVersion v = new ZYJVersion();
                Class userCla = (Class) v.getClass();
                Field[] fs = userCla.getDeclaredFields();
                // get the max field
                int max = FIRST_VERSION;
                for (int i = 0; i < fs.length; i++) {
                    Field f = fs[i];
                    try {
                        Object val = f.get(v);
                        String type = f.getType().toString();
                        if (type.endsWith("int") || type.endsWith("Integer")) {
                            int value = Integer.parseInt(val.toString());
                            max = max > value ? max : value;
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                ZYJUtils.logD(ZYJVersion.class, "The version is " + version + ", but the max is " + max);
                if (version != max) {
                    throw new RuntimeException("The version is " + version + ", but the max is " + max);
                }
            }
        }.start();
    }

}
