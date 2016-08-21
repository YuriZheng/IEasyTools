package com.zyj.ieasytools.library.db;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.zyj.ieasytools.library.encrypt.BaseEncrypt;
import com.zyj.ieasytools.library.utils.ZYJUtils;

import net.sqlcipher.SQLException;
import net.sqlcipher.database.SQLiteDatabase;

import java.io.File;
import java.io.IOException;

/**
 * Author: Yuri.zheng<br>
 * Date: 8/21/16<br>
 * Email: 497393102@qq.com<br>
 */
public abstract class BaseDatabase {

    protected Class<?> TAG = getClass();

    protected Context mContext;

    protected MySQLiteDatabase mSQLDatabase;

    /**
     * Open database state
     */
    public enum DATABASE_OPEN_STATE {
        /**
         * The database open success
         */
        DATABASE_OPEN_SUCCESS,
        /**
         * The databse file is not exists or cann't read
         */
        DATABASE_OPEN_FILE_EXCEPTION,
        /**
         * Error password to open database
         */
        DATABASE_OPEN_PASSWORD,
        /**
         * Unknow error
         */
        DATABASE_OPEN_UNKNOW
    }

    public BaseDatabase(Context context) {
        this.mContext = context;
        SQLiteDatabase.loadLibs(context);
    }

    /**
     * Get the database's version
     */
    protected abstract int getVersion();

    /**
     * Will call when the database need upgrade
     */
    protected abstract void onUpgrade(SQLiteDatabase sqliteDatabase, int oldVersion, int newVersion);

    /**
     * Drop the table from database
     *
     * @param tableName the tablet name
     */
    @SuppressWarnings("unused")
    protected void dropTable(String tableName) {
        if (checkDatabaseValid()) {
            ZYJUtils.logD(TAG, "Drop table" + tableName);
            mSQLDatabase.getSQLDatabase().execSQL("DROP TABLE IF EXISTS " + tableName);
        } else {
            ZYJUtils.logW(TAG, "The database open faile" + (mSQLDatabase != null ? mSQLDatabase.getStateMessage() : "MySQLiteDatabase is null"));
        }
    }

    /**
     * Create table in the database
     *
     * @param createSql the sql
     */
    protected void creatTable(String createSql) {
        if (checkDatabaseValid()) {
            ZYJUtils.logI(TAG, "Database onCreate: " + createSql);
            mSQLDatabase.getSQLDatabase().execSQL(createSql);
        } else {
            ZYJUtils.logW(TAG, "The database open faile: " + (mSQLDatabase != null ? mSQLDatabase.getStateMessage() : "MySQLiteDatabase is null"));
        }
    }

    public void onDestroy() {
        if (checkDatabaseValid()) {
            if (mSQLDatabase.getSQLDatabase().isOpen()) {
                mSQLDatabase.getSQLDatabase().close();
            }
        }
        mSQLDatabase = null;
    }

    /**
     * Create the database in the path
     *
     * @param path     the database's path
     * @param password the database's password
     * @return return the open state {@link MySQLiteDatabase}<br>
     */
    protected MySQLiteDatabase getSQLiteDatabase(String path, String password) {
        if (mSQLDatabase != null) {
            return mSQLDatabase;
        }
        mSQLDatabase = new MySQLiteDatabase();
        if (TextUtils.isEmpty(path) || TextUtils.isEmpty(password)
                || password.length() < BaseEncrypt.ENCRYPT_PRIVATE_KEY_LENGTH_MIN
                || password.length() > BaseEncrypt.ENCRYPT_PRIVATE_KEY_LENGTH_MAX) {
            mSQLDatabase.setSQL(null, DATABASE_OPEN_STATE.DATABASE_OPEN_PASSWORD);
            return mSQLDatabase;
        }
        SQLiteDatabase database = null;
        try {
            File file = new File(path);
            if (!file.exists()) {
                mSQLDatabase.setSQL(null, DATABASE_OPEN_STATE.DATABASE_OPEN_FILE_EXCEPTION);
                return mSQLDatabase;
            }
            if (!file.canRead() || !file.canWrite()) {
                mSQLDatabase.setSQL(null, DATABASE_OPEN_STATE.DATABASE_OPEN_FILE_EXCEPTION);
                return mSQLDatabase;
            }
            // Debug
            if (ZYJUtils.isPasswordDebug) {
                password = "";
            }
            database = SQLiteDatabase.openDatabase(path, password, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            if (database != null) {
                if (database.getVersion() > getVersion()) {
                    throw new RuntimeException("The new database version cann't below the old");
                }
                if (database.needUpgrade(getVersion())) {
                    onUpgrade(database, database.getVersion(), getVersion());
                    database.setVersion(getVersion());
                }
                mSQLDatabase.setSQL(database, DATABASE_OPEN_STATE.DATABASE_OPEN_SUCCESS);
            } else {
                mSQLDatabase.setSQL(null, DATABASE_OPEN_STATE.DATABASE_OPEN_UNKNOW);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            if (database != null && database.isOpen()) {
                database.close();
            }
            mSQLDatabase.setSQL(null, DATABASE_OPEN_STATE.DATABASE_OPEN_PASSWORD);
        }
        return mSQLDatabase;
    }

    /**
     * Create database file in the app's cache path<br>
     * Get settings database
     *
     * @param context  context
     * @param name     the database file's name
     * @param password the database's password
     * @return {@link #getSQLiteDatabase(String, String)}
     */
    protected MySQLiteDatabase getSQLiteDatabase(Context context, String name, String password) {
        String dir = context.getDir("", Activity.MODE_PRIVATE).getPath();
        File file = new File(dir + "/" + name);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return getSQLiteDatabase(null, null);
            }
        }
        return getSQLiteDatabase(file.getAbsolutePath(), password);
    }

    /**
     * Check the database is valid
     *
     * @return the instance is useful then return true, other return false
     */
    protected boolean checkDatabaseValid() {
        if (mSQLDatabase == null || mSQLDatabase.getStateCode() != DATABASE_OPEN_STATE.DATABASE_OPEN_SUCCESS || mSQLDatabase.getSQLDatabase() == null) {
            return false;
        }
        return true;
    }

    /**
     * Wrapper the database instance and state
     */
    protected class MySQLiteDatabase {
        private SQLiteDatabase mSQLDatabase = null;
        /**
         * {@link DATABASE_OPEN_STATE#DATABASE_OPEN_SUCCESS}<br>
         * {@link DATABASE_OPEN_STATE#DATABASE_OPEN_FILE_EXCEPTION}<br>
         * {@link DATABASE_OPEN_STATE#DATABASE_OPEN_PASSWORD}<br>
         * {@link DATABASE_OPEN_STATE#DATABASE_OPEN_UNKNOW}<br>
         */
        private DATABASE_OPEN_STATE mStateCode;

        protected MySQLiteDatabase() {
        }

        public SQLiteDatabase getSQLDatabase() {
            return mSQLDatabase;
        }

        /**
         * return {@link #mStateCode}
         */
        public DATABASE_OPEN_STATE getStateCode() {
            return mStateCode;
        }

        public String getStateMessage() {
            DATABASE_OPEN_STATE state = getStateCode();
            if (state == DATABASE_OPEN_STATE.DATABASE_OPEN_SUCCESS) {
                return "database open success";
            } else if (state == DATABASE_OPEN_STATE.DATABASE_OPEN_FILE_EXCEPTION) {
                return "database open file exception";
            } else if (state == DATABASE_OPEN_STATE.DATABASE_OPEN_PASSWORD) {
                return "database open password wrong";
            } else if (state == DATABASE_OPEN_STATE.DATABASE_OPEN_UNKNOW) {
                return "database open unknow";
            } else {
                return "unknow";
            }
        }

        protected MySQLiteDatabase setSQL(SQLiteDatabase database, DATABASE_OPEN_STATE stateCode) {
            this.mSQLDatabase = database;
            this.mStateCode = stateCode;
            return this;
        }
    }

}
