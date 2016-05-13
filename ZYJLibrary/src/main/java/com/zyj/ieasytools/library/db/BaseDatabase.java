package com.zyj.ieasytools.library.db;

import android.content.Context;
import android.text.TextUtils;

import com.zyj.ieasytools.library.encrypt.BaseEncrypt;
import com.zyj.ieasytools.library.utils.ZYJUtils;

import net.sqlcipher.database.SQLiteDatabase;

import java.io.File;

public abstract class BaseDatabase {

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
            ZYJUtils.logD(getClass(), "Drop table" + tableName);
            mSQLDatabase.getSQLDatabase().execSQL("DROP TABLE IF EXISTS " + tableName);
        } else {
            ZYJUtils.logW(getClass(), "The database open faile" + (mSQLDatabase != null ? mSQLDatabase.getStateCode() : "MySQLiteDatabase is null"));
        }
    }

    /**
     * Create table in the database
     *
     * @param createSql the sql
     */
    protected void creatTable(String createSql) {
        if (checkDatabaseOpenState(mSQLDatabase)) {
            ZYJUtils.logI(getClass(), "Database onCreate: " + createSql);
            mSQLDatabase.getSQLDatabase().execSQL(createSql);
        } else {
            ZYJUtils.logW(getClass(), "The database open faile: " + (mSQLDatabase != null ? mSQLDatabase.getStateCode() : "MySQLiteDatabase is null"));
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
        if (TextUtils.isEmpty(path) || TextUtils.isEmpty(password)
                || password.length() < BaseEncrypt.ENCRYPT_PRIVATE_KEY_LENGTH_MIN
                || password.length() > BaseEncrypt.ENCRYPT_PRIVATE_KEY_LENGTH_MAX) {
            return new MySQLiteDatabase().setSQL(null, DATABASE_OPEN_PASSWORD);
        }
        if (mSQLDatabase != null) {
            return mSQLDatabase;
        }
        mSQLDatabase = new MySQLiteDatabase();
        try {
            File file = new File(path);
            if (!file.exists()) {
                if (!ZYJUtils.createFile(path)) {
                    mSQLDatabase.setSQL(null, DATABASE_OPEN_FILE_EXCEPTION);
                    return mSQLDatabase;
                }
            }
            if (!file.canRead()) {
                mSQLDatabase.setSQL(null, DATABASE_OPEN_FILE_EXCEPTION);
                return mSQLDatabase;
            }
            // Debug
            if (ZYJUtils.isPasswordDebug) {
                password = "";
            }
            SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(path, password, null);
            if (database != null) {
                int newVersion = getVersion();
                if (database.getVersion() > newVersion) {
                    throw new RuntimeException("The new database version cann't below the old");
                } else if (database.getVersion() < newVersion) {
                    onUpgrade(database, database.getVersion(), newVersion);
                }
                database.setVersion(newVersion);
                mSQLDatabase.setSQL(database, DATABASE_OPEN_SUCCESS);
            } else {
                mSQLDatabase.setSQL(null, DATABASE_OPEN_UNKNOW);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mSQLDatabase.setSQL(null, DATABASE_OPEN_PASSWORD);
        }
        return mSQLDatabase;
    }

    /**
     * Create database file in the app's cache path
     *
     * @param context  context
     * @param name     the database file's name
     * @param password the database's password
     * @return {@link #getSQLiteDatabase(String, String)}
     */
    protected MySQLiteDatabase getSQLiteDatabase(Context context, String name, String password) {
        return getSQLiteDatabase(context.getDatabasePath(name).getAbsolutePath(), password);
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

        protected MySQLiteDatabase setSQL(SQLiteDatabase database, int stateCode) {
            this.mSQLDatabase = database;
            this.mStateCode = stateCode;
            return this;
        }
    }

}
