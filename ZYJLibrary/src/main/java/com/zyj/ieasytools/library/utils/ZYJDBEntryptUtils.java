package com.zyj.ieasytools.library.utils;

import android.content.Context;
import android.text.TextUtils;

import com.zyj.ieasytools.library.db.DatabaseColumns;
import com.zyj.ieasytools.library.db.ZYJEncrypts;
import com.zyj.ieasytools.library.encrypt.AESEncrypt;
import com.zyj.ieasytools.library.encrypt.BaseEncrypt;
import com.zyj.ieasytools.library.encrypt.EncryptFactory;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Get the encrypt util
 * Created by yuri.zheng on 2016/5/6.
 */
public final class ZYJDBEntryptUtils {

    private static final String TEST_FROM = "!@#$%^&*()_+~zhengyujie497393102";

    /**
     * Get the settings default encrypt bean
     */
    public static BaseEncrypt getSettingsEncrypt(Context context) {
        return EncryptFactory.getInstance().getInstance(AESEncrypt.class, ZYJUtils.getSettingPassword(context));
    }

    /**
     * Get our encrypt database
     *
     * @param context  context
     * @param password the database's password
     * @return return {@link ZYJEncrypts}
     */
    public static ZYJEncrypts getCurrentEncryptDatabase(Context context, String password) {
        return getEncryptDatabaseFromPath(context, "", password);
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
        try {
            Class<?> cls = Class.forName(ZYJEncrypts.class.getName());
            Constructor<?> con = cls.getDeclaredConstructor(new Class<?>[]{Context.class});
            con.setAccessible(true);
            // init the object
            ZYJEncrypts encrypt = (ZYJEncrypts) con.newInstance(new Object[]{context});
            // open the database
            if (TextUtils.isEmpty(path)) {
                Method method = ZYJEncrypts.class.getDeclaredMethod("openDatabase", String.class);
                method.setAccessible(true);
                method.invoke(encrypt, password);
            } else {
                Method method = ZYJEncrypts.class.getDeclaredMethod("openDatabase", String.class, String.class);
                method.setAccessible(true);
                method.invoke(encrypt, path, password);
            }
            // create database table
            Method method = ZYJEncrypts.class.getDeclaredMethod("initDatabase");
            method.setAccessible(true);
            method.invoke(encrypt);
            return encrypt;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get our database file<br>
     * "/storage/emulated/0" + {@link ZYJUtils#ROOR_PATH} + {@link com.zyj.ieasytools.library.db.DatabaseColumns.EncryptColumns#DATABASE_NAME}<br>
     *
     * @param context
     * @return the file of database, if create new file faile,then return null
     */
    public static File getCurrentDatabasePath(Context context) {
        String path = ZYJUtils.getExternalRootPath() + "/" + DatabaseColumns.EncryptColumns.DATABASE_NAME;
        File file = new File(path);
//        String dir = context.getDir("ZYJ", Activity.MODE_PRIVATE).getPath();
//        File file = new File(dir + "/" + DatabaseColumns.EncryptColumns.DATABASE_NAME);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
//        file.setWritable(true);
//        file.setReadable(true);
        return file;
    }

    /**
     * Check encrypt password
     *
     * @param context  context
     * @param method   encrypt method, the string is decrypted
     * @param password the password
     * @param from     the source string, the string is not encrypted
     * @param to       the decrypt string, the string is not encrypted
     * @param version  the app's version
     * @return true if the password is right,other return false
     */
    public static boolean checkEncryptPassword(Context context, String method, String password, String from, String to, int version) {
        BaseEncrypt encrypt = EncryptFactory.getInstance().getInstance(EncryptFactory.getClassFromMethod(method), password);
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
        BaseEncrypt encrypt = EncryptFactory.getInstance().getInstance(EncryptFactory.getClassFromMethod(method), password);
        args[0] = TEST_FROM;
        args[1] = encrypt.encrypt(args[0], version);
        return args;
    }

}
