package com.zyj.ieasytools.data;

import android.content.Context;
import android.text.TextUtils;

import com.zyj.ieasytools.library.db.ZYJEncrypts;
import com.zyj.ieasytools.library.db.ZYJSettings;
import com.zyj.ieasytools.library.encrypt.PasswordEntry;
import com.zyj.ieasytools.library.utils.ZYJDBEntryptUtils;

import java.io.File;
import java.util.List;

/**
 * Created by yuri.zheng on 2016/6/17.
 */
public final class EntryptImple implements IEntrypt {

    private ZYJEncrypts mZYJEncrypts;

    public EntryptImple(Context context) {
        String password = ZYJSettings.getInstance(context).getStringProperties(SettingsConstant.SETTINGS_SAVE_ENTER_PASSWORD, null);
        if (!TextUtils.isEmpty(password)) {
            mZYJEncrypts = getCurrentEncryptDatabase(context, password);
        }
    }

    @Override
    public void setEncryptListener(ZYJEncrypts.EncryptListener l) {
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
    public List<PasswordEntry> queryEntry(String selection, String[] selectionArgs, String groupBy, String password) {
        return mZYJEncrypts.queryEntry(selection, selectionArgs, groupBy, password);
    }

    @Override
    public int getAllRecord() {
        return mZYJEncrypts.getAllRecord();
    }

    /**
     * Get our encrypt database
     *
     * @param context  context
     * @param password the database's password
     * @return return {@link ZYJEncrypts}
     */
    public static ZYJEncrypts getCurrentEncryptDatabase(Context context, String password) {
        return ZYJDBEntryptUtils.getCurrentEncryptDatabase(context, password);
    }

    public static ZYJEncrypts getEncryptDatabaseFromPath(Context context, String path, String password) {
        return ZYJDBEntryptUtils.getEncryptDatabaseFromPath(context, path, password);
    }

    public static File getCurrentDatabasePath(Context context, boolean isCreate) {
        return ZYJDBEntryptUtils.getCurrentDatabasePath(context, isCreate);
    }

    public static List<String> getDatabasePathsBesidesCurrent(Context context) {
        return ZYJDBEntryptUtils.getDatabasePathsBesidesCurrent(context);
    }

    public static void destoryEntrypt() {
        ZYJDBEntryptUtils.destoryEntrypt();
    }

    public static boolean checkEncryptPassword(String method, String password, String from, String to, int version) {
        return ZYJDBEntryptUtils.checkEncryptPassword(method, password, from, to, version);
    }

    public static String[] generateTestTo(String method, String password, int version) {
        return ZYJDBEntryptUtils.generateTestTo(method, password, version);
    }

}
