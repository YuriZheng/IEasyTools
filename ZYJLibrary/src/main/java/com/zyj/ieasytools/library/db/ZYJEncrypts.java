package com.zyj.ieasytools.library.db;

import android.content.Context;

import net.sqlcipher.database.SQLiteDatabase;

/**
 * Created by yuri.zheng on 2016/5/5.
 */
public class ZYJEncrypts extends BaseDatabase {

    /**
     * Singleton
     */
    private static ZYJEncrypts mInstance;

    public static ZYJEncrypts getInstance(Context c) {
        if (mInstance == null) {
            mInstance = new ZYJEncrypts(c);
        }
        return mInstance;
    }

    private ZYJEncrypts(Context context) {
        super(context);
    }


    /**
     * Open database
     *
     * @param password the database password
     * @return {@link #DATABASE_OPEN_SUCCESS}<br>
     * {@link #DATABASE_OPEN_FILE_EXCEPTION}<br>
     * {@link #DATABASE_OPEN_PASSWORD}<br>
     * {@link #DATABASE_OPEN_UNKNOW}<br>
     */
    public MySQLiteDatabase openDatabase(String password) {
        return null;
    }

    @Override
    protected int getVersion() {
        return 0;
    }

    @Override
    protected void onUpgrade(SQLiteDatabase sqliteDatabase, int oldVersion, int newVersion) {

    }
}
