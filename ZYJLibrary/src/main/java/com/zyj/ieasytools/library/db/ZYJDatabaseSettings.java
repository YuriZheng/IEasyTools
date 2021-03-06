package com.zyj.ieasytools.library.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import com.zyj.ieasytools.library.encrypt.BaseEncrypt;
import com.zyj.ieasytools.library.utils.ZYJDatabaseUtils;
import com.zyj.ieasytools.library.utils.ZYJUtils;
import com.zyj.ieasytools.library.utils.ZYJVersion;

import net.sqlcipher.database.SQLiteDatabase;

import java.util.AbstractMap;
import java.util.Map;

/**
 * Author: Yuri.zheng<br>
 * Date: 8/21/16<br>
 * Email: 497393102@qq.com<br>
 */
public class ZYJDatabaseSettings extends BaseDatabase {

    private BaseEncrypt mEncrypt;

    /**
     * The app's version, for the encrypt method.(expanded in the future)
     */
    private final int APP_VERSION;

    private MySQLiteDatabase mSQLiteDatabase;

    /**
     * Singleton mode, so call {@link #onDestroy()} to destory resources
     */
    private ZYJDatabaseSettings(Context c) {
        super(c);
        mSQLiteDatabase = openDatabase();
        creatTable(DatabaseColumns.SettingColumns.CREATE_SETTING_TABLE_SQL);
        mEncrypt = ZYJDatabaseUtils.getSettingsEncrypt(c);
        APP_VERSION = ZYJVersion.getCurrentVersion();
    }

    /**
     * Verify the setting valid
     *
     * @return true is valid, other is invalid
     */
    public synchronized boolean verifyValidSetting() {
        if (mSQLiteDatabase == null) {
            return false;
        }
        if (mSQLiteDatabase.getStateCode() != DATABASE_OPEN_STATE.DATABASE_OPEN_SUCCESS) {
            return false;
        }
        if (mSQLiteDatabase.getSQLDatabase() == null) {
            return false;
        }
        if (!mSQLiteDatabase.getSQLDatabase().isOpen()) {
            return false;
        }
        return true;
    }


    /**
     * Open database
     *
     * @return {@link DATABASE_OPEN_STATE#DATABASE_OPEN_SUCCESS}<br>
     * {@link DATABASE_OPEN_STATE#DATABASE_OPEN_FILE_EXCEPTION}<br>
     * {@link DATABASE_OPEN_STATE#DATABASE_OPEN_PASSWORD}<br>
     * {@link DATABASE_OPEN_STATE#DATABASE_OPEN_UNKNOW}<br>
     */
    private MySQLiteDatabase openDatabase() {
        // Use the machine code to encrypt
        return getSQLiteDatabase(mContext, DatabaseColumns.SettingColumns.DATABASE_NAME, ZYJUtils.getSettingPassword(mContext));
    }

    /**
     * {@link #putStringProperties(String, String)}
     */
    public boolean putBooleanProperties(String key, boolean value) {
        return putStringProperties(key, String.valueOf(value));
    }

    /**
     * {@link #putStringProperties(String, String)}
     */
    public boolean putIntProperties(String key, int value) {
        return putStringProperties(key, value + "");
    }

    /**
     * {@link #putStringProperties(String, String)}
     */
    public boolean putLongProperties(String key, long value) {
        return putStringProperties(key, value + "");
    }

    /**
     * {@link #putProperties(Map.Entry)}
     */
    public boolean putStringProperties(String key, String value) {
        if (TextUtils.isEmpty(key)) {
            throw new RuntimeException("The key is null");
        }
        if (mEncrypt == null) {
            return false;
        }
        key = mEncrypt.encrypt(key, APP_VERSION);
        if (TextUtils.isEmpty(value)) {
            value = "";
        }
        if (value != null) {
            value = mEncrypt.encrypt(value, APP_VERSION);
        }
        Map.Entry<String, String> entry = new AbstractMap.SimpleEntry<String, String>(key, value + "");
        return putProperties(entry);
    }

    /**
     * Put string properties to database, will update this record when the
     * database has this key, others insert this record
     *
     * @param entry record's entry
     * @return update or insert success, then return true, others return false
     */
    private boolean putProperties(Map.Entry<String, String> entry) {
        if (checkEncryptEntry(entry.getKey())) {
            return updateSetting(entry) > 0;
        } else {
            return insertSetting(entry) != null;
        }
    }

    /**
     * Get the properties of Integer
     *
     * @param key
     * @return find this properties and return the value, others return -1
     * @throws NumberFormatException if {@code string} cannot be parsed as an integer value.
     */
    public int getIntProperties(String key, int defaultValue) {
        return Integer.parseInt(getStringProperties(key, String.valueOf(defaultValue)));
    }

    /**
     * Get the properties of Long
     *
     * @param key
     * @return find this properties and return the value, others return -1
     * @throws NumberFormatException if {@code string} cannot be parsed as a long value.
     */
    public long getLongProperties(String key, long defaultValue) {
        return Long.parseLong(getStringProperties(key, String.valueOf(defaultValue)));
    }

    /**
     * Get the properties of Boolean
     *
     * @param key
     * @return find this properties and return the value, others return false
     */
    public boolean getBooleanProperties(String key, boolean defaultValue) {
        return Boolean.parseBoolean(getStringProperties(key, String.valueOf(defaultValue)));
    }

    /**
     * {@link #getProperties(String)}
     */
    public String getStringProperties(String key, String defaultString) {
        if (mEncrypt == null) {
            return defaultString;
        }
        key = mEncrypt.encrypt(key, APP_VERSION);
        try {
            String value = getProperties(key);
            if (TextUtils.isEmpty(value)) {
                return defaultString;
            }
            return mEncrypt.decrypt(value, APP_VERSION);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultString;
        }
    }

    /**
     * Whether the database has this key,we will return "" when the value is
     * null
     *
     * @param key record's key
     * @return if record's value is "" , return "" , others return the value
     */
    private String getProperties(String key) {
        Cursor c = mContext.getContentResolver().query(ZYJContentProvider.SEETINGS_URI, null, DatabaseColumns.SettingColumns._KEY + "=?",
                new String[]{key}, null);
        String value = "";
        if (c == null || c.getCount() < 1) {
            if (c != null) {
                c.close();
            }
            return "";
        } else {
            if (c.getCount() > 1) {
                ZYJUtils.logW(TAG, "This properties has repeated: " + key);
            }
            if (c.moveToNext()) {
                value = c.getString(c.getColumnIndex(DatabaseColumns.SettingColumns._VALUE));
            }
        }
        if (c != null) {
            c.close();
        }
        return value;
    }

    /**
     * Remove the key
     */
    public boolean remvoeEntry(String key) {
        if (TextUtils.isEmpty(key)) {
            throw new RuntimeException("The key is null");
        }
        if (mEncrypt == null) {
            return false;
        }
        key = mEncrypt.encrypt(key, APP_VERSION);
        if (checkEncryptEntry(key)) {
            int count = mContext.getContentResolver().delete(ZYJContentProvider.SEETINGS_URI, DatabaseColumns.SettingColumns._KEY + "=?", new String[]{key});
            ZYJUtils.logD(TAG, "Delete: " + count);
            return count > 0;
        } else {
            ZYJUtils.logW(TAG, "No such column: " + key);
            return true;
        }
    }

    /**
     * Check this entry in database
     *
     * @return Has this key then return true
     */
    public boolean checkEntry(String key) {
        if (TextUtils.isEmpty(key)) {
            throw new RuntimeException("The key is null");
        }
        if (mEncrypt == null) {
            return false;
        }
        key = mEncrypt.encrypt(key, APP_VERSION);
        return checkEncryptEntry(key);
    }

    /**
     * {@link #checkEntry(String)}
     */
    private boolean checkEncryptEntry(String key) {
        Cursor c = mContext.getContentResolver().query(ZYJContentProvider.SEETINGS_URI, new String[]{DatabaseColumns.SettingColumns._KEY},
                DatabaseColumns.SettingColumns._KEY + "=?", new String[]{key}, null);
        if (c == null) {
            return false;
        }
        if (c.getCount() >= 1) {
            c.close();
            return true;
        } else {
            c.close();
            return false;
        }
    }

    private int updateSetting(Map.Entry<String, String> entry) {
        ContentValues values = new ContentValues();
        String key = entry.getKey();
        String value = entry.getValue();
        values.put(DatabaseColumns.SettingColumns._VALUE, value);
        ZYJUtils.logD(TAG, "update properties: " + key + "<" + value + ">");
        return mContext.getContentResolver().update(ZYJContentProvider.SEETINGS_URI, values, DatabaseColumns.SettingColumns._KEY + "=?", new String[]{key});
    }

    private Uri insertSetting(Map.Entry<String, String> entry) {
        ContentValues values = new ContentValues();
        String key = entry.getKey();
        String value = entry.getValue();
        values.put(DatabaseColumns.SettingColumns._KEY, key);
        values.put(DatabaseColumns.SettingColumns._VALUE, value);
        ZYJUtils.logD(TAG, "insert properties: " + key + "<" + value + ">");
        return mContext.getContentResolver().insert(ZYJContentProvider.SEETINGS_URI, values);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected int getVersion() {
        return Integer.MAX_VALUE;
    }

    @Override
    protected void onUpgrade(SQLiteDatabase sqliteDatabase, int oldVersion, int newVersion) {
        // Never upgrade
    }
}
