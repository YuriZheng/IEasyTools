package com.zyj.ieasytools.library.utils;

import android.content.Context;
import android.text.TextUtils;

import com.zyj.ieasytools.library.db.DatabaseColumns;
import com.zyj.ieasytools.library.db.ZYJEncrypts;
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
 * Get the encrypt util
 * Created by yuri.zheng on 2016/5/6.
 */
public final class ZYJDBEntryptUtils {

    private static final String TEST_FROM = "!@#$%^&*()_+~zhengyujie497393102";

    private static final String OUR_DATABASE_KEY = "our";
    /**
     * Keep the instance of {@link ZYJEncrypts} by database's path
     */
    private static Map<String, ZYJEncrypts> sEncryptMap = new HashMap<String, ZYJEncrypts>();

    /**
     * Get the settings default encrypt bean
     */
    public static BaseEncrypt getSettingsEncrypt(Context context) {
        return EncryptFactory.getInstance().getEncryptInstance(BaseEncrypt.ENCRYPT_AES, ZYJUtils.getSettingPassword(context));
    }

    /**
     * Get our encrypt database
     *
     * @param context  context
     * @param password the database's password
     * @return return {@link ZYJEncrypts}
     */
    public static ZYJEncrypts getCurrentEncryptDatabase(Context context, String password) {
        ZYJEncrypts e = sEncryptMap.get(OUR_DATABASE_KEY);
        if (e == null) {
            return getEncryptDatabaseFromPath(context, OUR_DATABASE_KEY, password);
        } else {
            return e;
        }
    }

    /**
     * Get encrypt database from path
     *
     * @param context  context
     * @param path     the database's path
     * @param password the database's password
     * @return return {@link ZYJEncrypts}
     */
    public static ZYJEncrypts getEncryptDatabaseFromPath(Context context, String path, String password) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        ZYJEncrypts entrypt = sEncryptMap.get(path);
        if (entrypt != null) {
            return entrypt;
        }
        try {
            Class<?> cls = Class.forName(ZYJEncrypts.class.getName());
            Constructor<?> con = cls.getDeclaredConstructor(new Class<?>[]{Context.class});
            con.setAccessible(true);
            // init the object
            ZYJEncrypts encrypt = (ZYJEncrypts) con.newInstance(new Object[]{context});
            // open our database
            if (OUR_DATABASE_KEY.equals(path)) {
                // Current encrypt database
                Method method = ZYJEncrypts.class.getDeclaredMethod("openDatabase", String.class);
                method.setAccessible(true);
                method.invoke(encrypt, password);
            } else {
                // Other encrypt database
                Method method = ZYJEncrypts.class.getDeclaredMethod("openDatabase", String.class, String.class);
                method.setAccessible(true);
                method.invoke(encrypt, path, password);
            }
            // create database table
            Method method = ZYJEncrypts.class.getDeclaredMethod("initDatabase");
            method.setAccessible(true);
            method.invoke(encrypt);
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
    public static File getCurrentDatabasePath(Context context, boolean isCreate) {
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
    public static List<String> getDatabasePathsBesidesCurrent(Context context) {
        List<String> paths = new ArrayList<String>();
        String root = context.getDatabasePath(DatabaseColumns.EncryptColumns.DATABASE_NAME).getAbsolutePath();
        File file = new File(new File(root).getParent());
        if (file.exists()) {
            String[] pp = file.list();
            if (pp != null) {
                for (String s : file.list()) {
                    if (!s.equals(DatabaseColumns.EncryptColumns.DATABASE_NAME)) {
                        String path = file.getAbsolutePath() + "/" + s;
                        ZYJUtils.logD(ZYJDBEntryptUtils.class, "Has other database: " + path);
                        paths.add(path);
                    }
                }
            }
        }
        return paths;
    }

    /**
     * Destory all entrypt instance
     */
    public static void destoryEntrypt() {
        for (String path : sEncryptMap.keySet()) {
            ZYJEncrypts e = sEncryptMap.get(path);
            if (e != null && !e.isDestory()) {
                e.onDestroy();
            }
            e = null;
        }
        sEncryptMap.clear();
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
    public static boolean checkEncryptPassword(String method, String password, String from, String to, int version) {
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
    public static String[] generateTestTo(String method, String password, int version) {
        String[] args = new String[2];
        BaseEncrypt encrypt = EncryptFactory.getInstance().getEncryptInstance(method, password);
        args[0] = TEST_FROM;
        args[1] = encrypt.encrypt(args[0], version);
        return args;
    }

}
