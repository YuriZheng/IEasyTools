package com.zyj.ieasytools.library.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import com.zyj.ieasytools.library.encrypt.BaseEncrypt;
import com.zyj.ieasytools.library.utils.ZYJDBAndEntryptUtils;
import com.zyj.ieasytools.library.utils.ZYJUtils;

import net.sqlcipher.database.SQLiteDatabase;

import java.util.AbstractMap;
import java.util.Map;

/**
 * Created by yuri.zheng on 2016/5/5.
 */
public class ZYJSettings extends BaseDatabase {

    /**
     * Singleton
     */
    private static ZYJSettings mInstance;

    private BaseEncrypt mEncrypt;

    /**
     * Database's version
     */
    private final int VERSION = 1;

    /**
     * The app's version, for the encrypt method.(expanded in the future)
     */
    private final int APP_VERSION;

    private ZYJSettings(Context c) {
        super(c);
        openDatabase();
        creatTable(DatabaseColumns.SettingColumns.CREATE_SETTING_TABLE_SQL);
        mEncrypt = ZYJDBAndEntryptUtils.getSettingsEncrypt(c);
        APP_VERSION = Integer.parseInt(ZYJUtils.getVersion(c)[1].toString());
    }

    public static ZYJSettings getInstance(Context c) {
        if (mInstance == null) {
            mInstance = new ZYJSettings(c);
        }
        return mInstance;
    }

    /**
     * Open database
     *
     * @return {@link #DATABASE_OPEN_SUCCESS}<br>
     * {@link #DATABASE_OPEN_FILE_EXCEPTION}<br>
     * {@link #DATABASE_OPEN_PASSWORD}<br>
     * {@link #DATABASE_OPEN_UNKNOW}<br>
     */
    private MySQLiteDatabase openDatabase() {
        // Use the machine code to encrypt
        return getSQLiteDatabase(mContext, DatabaseColumns.SettingColumns.DATABASE_NAME, ZYJUtils.getSettingPassword(mContext));
    }

    /**
     * @see {@link #putStringProperties(String, String)}
     */
    public boolean putBooleanProperties(String key, boolean value) {
        return putStringProperties(key, String.valueOf(value));
    }

    /**
     * @see {@link #putStringProperties(String, String)} vaule must above zeor
     */
    public boolean putIntProperties(String key, int value) {
        return putStringProperties(key, value + "");
    }

    /**
     * @see {@link #putStringProperties(String, String)} vaule must above zeor
     */
    public boolean putLongProperties(String key, long value) {
        return putStringProperties(key, value + "");
    }

    /**
     * @see {@link #putProperties(Map.Entry)}
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
        if (checkEntry(entry.getKey())) {
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
     * @throws Exception The value must is -1 or below zero
     */
    public int getIntProperties(String key, int defaultValue) {
        return Integer.parseInt(getStringProperties(key, String.valueOf(defaultValue)));
    }

    /**
     * Get the properties of Long
     *
     * @param key
     * @return find this properties and return the value, others return -1
     * @throws Exception The value must is -1 or below zero
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
     * @see {@link #getProperties(String)}
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
            return "";
        } else {
            if (c.getCount() > 1) {
                ZYJUtils.logW(getClass(), "This properties has repeated: " + key);
            }
            if (c.moveToNext()) {
                value = c.getString(c.getColumnIndex(DatabaseColumns.SettingColumns._VALUE));
            }
        }
        return value;
    }

    /**
     * Remove the key
     */
    public boolean remvoeEntry(String key) {
        return mContext.getContentResolver().delete(ZYJContentProvider.SEETINGS_URI, DatabaseColumns.SettingColumns._KEY + "=?", new String[]{key}) > 0;
    }

    /**
     * Check this entry in database
     *
     * @return Has this key then return true
     */
    public boolean checkEntry(String key) {
        Cursor c = mContext.getContentResolver().query(ZYJContentProvider.SEETINGS_URI, null, DatabaseColumns.SettingColumns._KEY + "=?",
                new String[]{key}, null);
        try {
            return c != null ? (c.getCount() == 1) : false;
        } finally {
            c.close();
        }
    }

    private int updateSetting(Map.Entry<String, String> entry) {
        ContentValues values = new ContentValues();
        String key = entry.getKey();
        String value = entry.getValue();
        values.put(DatabaseColumns.SettingColumns._VALUE, value);
        ZYJUtils.logD(getClass(), "update properties: " + key + "<" + value + ">");
        return mContext.getContentResolver().update(ZYJContentProvider.SEETINGS_URI, values, DatabaseColumns.SettingColumns._KEY + "=?", new String[]{key});
    }

    private Uri insertSetting(Map.Entry<String, String> entry) {
        ContentValues values = new ContentValues();
        String key = entry.getKey();
        String value = entry.getValue();
        values.put(DatabaseColumns.SettingColumns._KEY, key);
        values.put(DatabaseColumns.SettingColumns._VALUE, value);
        ZYJUtils.logD(getClass(), "insert properties: " + key + "<" + value + ">");
        return mContext.getContentResolver().insert(ZYJContentProvider.SEETINGS_URI, values);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mInstance = null;
    }

    @Override
    protected int getVersion() {
        return VERSION;
    }

    @Override
    protected void onUpgrade(SQLiteDatabase sqliteDatabase, int oldVersion, int newVersion) {
        // Never upgrade
    }
}