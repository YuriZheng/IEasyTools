package com.zyj.ieasytools.data;

import android.content.Context;
import android.text.TextUtils;

import com.zyj.ieasytools.library.db.ZYJDatabaseEncrypts;
import com.zyj.ieasytools.library.encrypt.PasswordEntry;
import com.zyj.ieasytools.library.utils.ZYJDatabaseUtils;

import java.io.File;
import java.util.List;

/**
 * Author: Yuri.zheng<br>
 * Date: 8/21/16<br>
 * Email: 497393102@qq.com<br>
 */
public final class EntryptImple implements IEntrypt {

    private ZYJDatabaseEncrypts mZYJEncrypts;

    private EntryptImple(Context context) {
        String password = ZYJDatabaseUtils.getSettingsInstance(context).getStringProperties(SettingsConstant.SETTINGS_SAVE_ENTER_PASSWORD, null);
        if (!TextUtils.isEmpty(password)) {
            mZYJEncrypts = getCurrentEncryptDatabase(context, password);
        }
    }

    public static EntryptImple getEntryptImple(Context context) {
        EntryptImple e = new EntryptImple(context);
        if (e.mZYJEncrypts == null) {
            return null;
        } else {
            return e;
        }
    }

    @Override
    public void setEncryptListener(ZYJDatabaseEncrypts.EncryptListener l) {
        mZYJEncrypts.setEncryptListener(l);
    }

    @Override
    public boolean isCurrentDatabase() {
        return mZYJEncrypts.isCurrentDatabase();
    }

    @Override
    public boolean isDestory() {
        return mZYJEncrypts.isDestory();
    }

    @Override
    public boolean validDatabase() {
        return mZYJEncrypts.validDatabase();
    }

    @Override
    public long insertEntry(PasswordEntry entry, String password) {
        return mZYJEncrypts.insertEntry(entry, password);
    }

    @Override
    public int deleteEntry(PasswordEntry entry, String password) {
        return mZYJEncrypts.deleteEntry(entry, password);
    }

    @Override
    public long updateEntry(PasswordEntry entry, String password) {
        return mZYJEncrypts.updateEntry(entry, password);
    }

    @Override
    public List<PasswordEntry> queryEntry(String[] columns, String selection, String[] selectionArgs, String orderBy, String password) {
        return mZYJEncrypts.queryEntry(columns, selection, selectionArgs, orderBy, password);
    }

    /**
     * Get our encrypt database
     *
     * @param context  context
     * @param password the database's password
     * @return return {@link ZYJDatabaseEncrypts}
     */
    public static ZYJDatabaseEncrypts getCurrentEncryptDatabase(Context context, String password) {
        return ZYJDatabaseUtils.getCurrentEncryptDatabase(context, password);
    }

    public static ZYJDatabaseEncrypts getEncryptDatabaseFromPath(Context context, String path, String password) {
        return ZYJDatabaseUtils.getEncryptDatabaseFromPath(context, path, password);
    }

    public static File getCurrentDatabasePath(Context context, boolean isCreate) {
        return ZYJDatabaseUtils.getCurrentDatabasePath(context, isCreate);
    }

    public static List<String> getDatabasePathsBesidesCurrent(Context context) {
        return ZYJDatabaseUtils.getDatabasePathsBesidesCurrent(context);
    }

    public static void destoryEntrypt() {
        ZYJDatabaseUtils.destoryDatabases();
    }

    public static boolean checkEncryptPassword(String method, String password, String from, String to, int version) {
        return ZYJDatabaseUtils.checkEncryptPassword(method, password, from, to, version);
    }

    public static String[] generateTestTo(String method, String password, int version) {
        return ZYJDatabaseUtils.generateTestTo(method, password, version);
    }

}
