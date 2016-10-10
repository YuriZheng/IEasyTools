package com.zyj.ieasytools.data;

import android.content.Context;
import android.text.TextUtils;

import com.zyj.ieasytools.library.db.ZYJDatabaseEncrypts;
import com.zyj.ieasytools.library.encrypt.PasswordEntry;

import java.util.List;

/**
 * Author: Yuri.zheng<br>
 * Date: 8/21/16<br>
 * Email: 497393102@qq.com<br>
 */
public final class EntryptImple implements IEntrypt {

    private ZYJDatabaseEncrypts mZYJEncrypts;

    /**
     * The key of database, if is null, mean our db
     */
    private String mRecordkey;

    /**
     * Get our database
     */
    public EntryptImple(Context context) {
        String password = DatabaseUtils.getSettingsInstance(context).getStringProperties(SettingsConstant.SETTINGS_SAVE_ENTER_PASSWORD, null);
        if (!TextUtils.isEmpty(password)) {
            mZYJEncrypts = DatabaseUtils.getCurrentEncryptDatabase(context, password);
            mRecordkey = null;
        }
    }

    /**
     * Get other database
     */
    public EntryptImple(Context context, String path, String password) {
        if (!TextUtils.isEmpty(path) && !TextUtils.isEmpty(password)) {
            mZYJEncrypts = DatabaseUtils.getEncryptDatabaseFromPath(context, path, password);
            mRecordkey = path;
        }
    }

    @Override
    public void setEncryptListener(ZYJDatabaseEncrypts.EncryptListener l) {
        if (mZYJEncrypts != null) {
            mZYJEncrypts.setEncryptListener(l);
        }
    }

    @Override
    public boolean isCurrentDatabase() {
        return mZYJEncrypts != null ? mZYJEncrypts.isCurrentDatabase() : false;
    }

    @Override
    public boolean isDestory() {
        return mZYJEncrypts != null ? mZYJEncrypts.isDestory() : false;
    }

    @Override
    public void destroy() {
        DatabaseUtils.destoryDatabases(mRecordkey);
    }

    @Override
    public boolean validDatabase() {
        return mZYJEncrypts != null ? mZYJEncrypts.validDatabase() : false;
    }

    @Override
    public long insertEntry(PasswordEntry entry, String password) {
        return mZYJEncrypts != null ? mZYJEncrypts.insertEntry(entry, password) : -1;
    }

    @Override
    public int deleteEntry(PasswordEntry entry, String password) {
        return mZYJEncrypts != null ? mZYJEncrypts.deleteEntry(entry, password) : -1;
    }

    @Override
    public long updateEntry(PasswordEntry entry, String password) {
        return mZYJEncrypts != null ? mZYJEncrypts.updateEntry(entry, password) : -1;
    }

    @Override
    public List<PasswordEntry> queryEntry(String[] columns, String selection, String[] selectionArgs, String orderBy, String password) {
        return mZYJEncrypts != null ? mZYJEncrypts.queryEntry(columns, selection, selectionArgs, orderBy, password) : null;
    }
}
