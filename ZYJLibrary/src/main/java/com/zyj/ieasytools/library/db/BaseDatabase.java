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

public abstract class BaseDatabase {

    protected Class<?> TAG = getClass();

    protected Context mContext;

    protected MySQLiteDatabase mSQLDatabase;

    /**
     * The database open success
     */
    public static final int DATABASE_OPEN_SUCCESS = 0;
    /**
     * The databse file is not exists or cann't read
     */
    public static final int DATABASE_OPEN_FILE_EXCEPTION = -0xA;
    /**
     * Error password to open database
     */
    public static final int DATABASE_OPEN_PASSWORD = DATABASE_OPEN_FILE_EXCEPTION << 1;
    /**
     * Unknow error
     */
    public static final int DATABASE_OPEN_UNKNOW = DATABASE_OPEN_FILE_EXCEPTION << 2;

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
        if (checkDatabaseOpenState(mSQLDatabase)) {
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
        if (checkDatabaseOpenState(mSQLDatabase)) {
            ZYJUtils.logI(TAG, "Database onCreate: " + createSql);
            mSQLDatabase.getSQLDatabase().execSQL(createSql);
        } else {
            ZYJUtils.logW(TAG, "The database open faile: " + (mSQLDatabase != null ? mSQLDatabase.getStateMessage() : "MySQLiteDatabase is null"));
        }
    }

    public void onDestroy() {
        if (checkDatabaseOpenState(mSQLDatabase)) {
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
            mSQLDatabase.setSQL(null, DATABASE_OPEN_PASSWORD);
            return mSQLDatabase;
        }
        SQLiteDatabase database = null;
        try {
            File file = new File(path);
            if (!file.exists()) {
                mSQLDatabase.setSQL(null, DATABASE_OPEN_FILE_EXCEPTION);
                return mSQLDatabase;
            }
            if (!file.canRead() || !file.canWrite()) {
                mSQLDatabase.setSQL(null, DATABASE_OPEN_FILE_EXCEPTION);
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
                mSQLDatabase.setSQL(database, DATABASE_OPEN_SUCCESS);
            } else {
                mSQLDatabase.setSQL(null, DATABASE_OPEN_UNKNOW);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            if (database != null && database.isOpen()) {
                database.close();
            }
            mSQLDatabase.setSQL(null, DATABASE_OPEN_PASSWORD);
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
     * Check the {@link MySQLiteDatabase} is null
     *
     * @param my the checked instance
     * @return the instance is useful then return true, other return false
     */
    protected boolean checkDatabaseOpenState(MySQLiteDatabase my) {
        if (my == null || my.getStateCode() != DATABASE_OPEN_SUCCESS || my.getSQLDatabase() == null) {
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
         * {@link #DATABASE_OPEN_SUCCESS}<br>
         * {@link #DATABASE_OPEN_FILE_EXCEPTION}<br>
         * {@link #DATABASE_OPEN_PASSWORD}<br>
         * {@link #DATABASE_OPEN_UNKNOW}<br>
         */
        private int mStateCode;

        protected MySQLiteDatabase() {
        }

        public SQLiteDatabase getSQLDatabase() {
            return mSQLDatabase;
        }

        /**
         * return {@link #mStateCode}
         */
        public int getStateCode() {
            return mStateCode;
        }

        public String getStateMessage() {
            switch (getStateCode()) {
                case DATABASE_OPEN_SUCCESS:
                    return "database open success";
                case DATABASE_OPEN_FILE_EXCEPTION:
                    return "database open file exception";
                case DATABASE_OPEN_PASSWORD:
                    return "database open password wrong";
                case DATABASE_OPEN_UNKNOW:
                    return "database open unknow";
                default:
                    return "unknow";
            }
        }

        protected MySQLiteDatabase setSQL(SQLiteDatabase database, int stateCode) {
            this.mSQLDatabase = database;
            this.mStateCode = stateCode;
            return this;
        }
    }

}
