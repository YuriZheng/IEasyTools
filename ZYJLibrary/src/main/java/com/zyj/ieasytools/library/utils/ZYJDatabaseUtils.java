package com.zyj.ieasytools.library.utils;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.zyj.ieasytools.library.db.DatabaseColumns;
import com.zyj.ieasytools.library.db.ZYJDatabaseEncrypts;
import com.zyj.ieasytools.library.db.ZYJDatabaseSettings;
import com.zyj.ieasytools.library.encrypt.BaseEncrypt;
import com.zyj.ieasytools.library.encrypt.EncryptFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Yuri.zheng<br>
 * Date: 8/21/16<br>
 * Email: 497393102@qq.com<br>
 * <p>
 * Get the encrypt util
 */
public final class ZYJDatabaseUtils {

    /**
     * Our database flag in map
     */
    public static final String OUR_DATABASE_KEY = "_our";

    /**
     * Singleton ZYJDatabaseSettings, Destory database settings instance in server
     */
    private static ZYJDatabaseSettings mInstance;

    private static final String TEST_FROM = "!@#$%^&*()_+~zhengyujie497393102";

    /**
     * Keep the instance of {@link ZYJDatabaseEncrypts} by database's path
     */
    private static Map<String, ZYJDatabaseEncrypts> sEncryptMap = new HashMap<>();

    /**
     * Get the settings default encrypt bean
     */
    public synchronized static BaseEncrypt getSettingsEncrypt(Context context) {
        return EncryptFactory.getInstance().getEncryptInstance(BaseEncrypt.ENCRYPT_AES, ZYJUtils.getSettingPassword(context));
    }

    /**
     * Get settings instance
     */
    public synchronized static ZYJDatabaseSettings getSettingsInstance(Context c) {
        if (mInstance == null || !mInstance.verifyValidSetting()) {
            try {
                Class<?> cls = Class.forName(ZYJDatabaseSettings.class.getName());
                Constructor<?> con = cls.getDeclaredConstructor(new Class<?>[]{Context.class});
                con.setAccessible(true);
                ZYJDatabaseSettings settings = (ZYJDatabaseSettings) con.newInstance(new Object[]{c.getApplicationContext()});
                mInstance = settings;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return mInstance;
    }

    /**
     * Get our encrypt database
     *
     * @param context  context
     * @param password the database's password
     * @return return {@link ZYJDatabaseEncrypts}
     */
    public synchronized static ZYJDatabaseEncrypts getCurrentEncryptDatabase(Context context, String password) {
        return getEncryptDatabaseFromPath(context.getApplicationContext(), OUR_DATABASE_KEY, password);
    }

    /**
     * Get encrypt database from path
     *
     * @param context  context
     * @param path     the database's path
     * @param password the database's password
     * @return return {@link ZYJDatabaseEncrypts}
     */
    public synchronized static ZYJDatabaseEncrypts getEncryptDatabaseFromPath(Context context, String path, String password) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        ZYJDatabaseEncrypts entrypt = sEncryptMap.get(path);
        if (entrypt != null && entrypt.validDatabase()) {
            return entrypt;
        }
        try {
            Class<?> cls = Class.forName(ZYJDatabaseEncrypts.class.getName());
            Constructor<?> con = cls.getDeclaredConstructor(new Class<?>[]{Context.class});
            con.setAccessible(true);
            // init the object
            ZYJDatabaseEncrypts encrypt = (ZYJDatabaseEncrypts) con.newInstance(new Object[]{context});
            // open our database
            if (OUR_DATABASE_KEY.equals(path)) {
                // Current encrypt database
                Method method = ZYJDatabaseEncrypts.class.getDeclaredMethod("openDatabase", String.class);
                method.setAccessible(true);
                method.invoke(encrypt, password);
            } else {
                // Other encrypt database
                Method method = ZYJDatabaseEncrypts.class.getDeclaredMethod("openDatabase", String.class, String.class);
                method.setAccessible(true);
                method.invoke(encrypt, path, password);
            }
            // create database table
            Method method = ZYJDatabaseEncrypts.class.getDeclaredMethod("initDatabase");
            method.setAccessible(true);
            method.invoke(encrypt);
            ZYJUtils.logD(ZYJDatabaseUtils.class, "Cache database: " + path + ", Encrypt: " + encrypt);
            sEncryptMap.put(path, encrypt);
            return encrypt;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get our database file<br>
     * "/data/data/com.zyj.ieasytools/databases/XXX.izyj"<br>
     *
     * @return the file of database, if create new file faile,then return null
     */
    @Nullable
    public synchronized static File getCurrentDatabasePath(Context context, boolean isCreate) {
        String root = context.getDatabasePath(DatabaseColumns.EncryptColumns.DATABASE_NAME).getAbsolutePath();
        File file = new File(root);
        if (!isCreate) {
            return file;
        }
        if (!file.exists()) {
            file.getParent();
            File dir = new File(file.getParent());
            if (!dir.exists()) {
                dir.mkdirs();
            }
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return file;
    }

    /**
     * Get other database paths
     */
    public synchronized static List<String> getDatabasePathsBesidesCurrent(Context context) {
        List<String> paths = new ArrayList<String>();
        String root = context.getDatabasePath(DatabaseColumns.EncryptColumns.DATABASE_NAME).getAbsolutePath();
        File file = new File(new File(root).getParent());
        if (file.exists()) {
            String[] pp = file.list();
            if (pp != null) {
                for (String s : file.list()) {
                    if (!s.equals(DatabaseColumns.EncryptColumns.DATABASE_NAME)) {
                        String path = file.getAbsolutePath() + "/" + s;
                        ZYJUtils.logD(ZYJDatabaseUtils.class, "Has other database: " + path);
                        paths.add(path);
                    }
                }
            }
        }
        return paths;
    }

    /**
     * Destory entrypt by key
     */
    public synchronized static void destoryDatabases(String key) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        ZYJUtils.logD(ZYJDatabaseUtils.class, "Destory database: " + key);
        ZYJDatabaseEncrypts e = sEncryptMap.remove(key);
        if (e != null && !e.isDestory()) {
            e.onDestroy();
        }
        e = null;
    }

    /**
     * Make sure all database instance had destory, Only call once in this application onDestory() method
     */
    public synchronized static void destoryAllDatabase() {
        if (sEncryptMap != null && sEncryptMap.size() > 0) {
            for (String path : sEncryptMap.keySet()) {
                ZYJDatabaseEncrypts e = sEncryptMap.remove(path);
                if (e != null && !e.isDestory()) {
                    e.onDestroy();
                }
                e = null;
            }
            sEncryptMap.clear();
        }
    }

    /**
     * Check encrypt password
     *
     * @param method   encrypt method, the string is decrypted
     * @param password the password
     * @param from     the source string, the string is not encrypted
     * @param to       the decrypt string, the string is not encrypted
     * @param version  the app's version
     * @return true if the password is right,other return false
     */
    public synchronized static boolean checkEncryptPassword(String method, String password, String from, String to, int version) {
        BaseEncrypt encrypt = EncryptFactory.getInstance().getEncryptInstance(method, password);
        String decryptString = encrypt.decrypt(from, version);
        return to.equals(decryptString);
    }

    /**
     * Get the testTo string
     *
     * @param method   encrypt method, the string is decrypted
     * @param password the password
     * @param version  the app's version
     * @return return arrays of test string,args[0]=from,args[1]=to
     */
    public synchronized static String[] generateTestTo(String method, String password, int version) {
        String[] args = new String[2];
        BaseEncrypt encrypt = EncryptFactory.getInstance().getEncryptInstance(method, password);
        args[0] = TEST_FROM;
        args[1] = encrypt.encrypt(args[0], version);
        return args;
    }

}
