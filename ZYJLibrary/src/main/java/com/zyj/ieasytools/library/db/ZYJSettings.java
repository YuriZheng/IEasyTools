package com.zyj.ieasytools.library.db;

import android.content.Context;
import android.text.TextUtils;

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

    private MySQLiteDatabase mSQLiteDatabase;

    private final int VERSION = 1;

    private ZYJSettings(Context c) {
        super(c);
        openDatabase();
        creatTable(DatabaseColumns.SettingColumns.CREATE_SETTING_TABLE_SQL);
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
    public MySQLiteDatabase openDatabase() {
        // Use the machine code to encrypt
        return getSQLiteDatabase(mContext, DatabaseColumns.SettingColumns.DATABASE_NAME, ZYJUtils.getMachineCode(mContext));
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
        if (value == null) {
            value = "";
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
//        if (hasEntry(entry.getKey()).equals(NOT_DATA_KEY)) {
//            return insertEntry(entry) != null;
//        } else {
//            return updateEntry(entry) > 0;
//        }

        return true;
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
        try {
            String value = getProperties(key);
            if (TextUtils.isEmpty(value)) {
                return defaultString;
            }
            return value;
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
//        String value = hasEntry(key);
//        if (NOT_DATA_KEY.equals(value)) {
//            value = "";
//        }
//        return value;
        return "";
    }

    @Override
    protected int getVersion() {
        return VERSION;
    }

    @Override
    protected void onUpgrade(SQLiteDatabase sqliteDatabase, int oldVersion, int newVersion) {

    }
}
