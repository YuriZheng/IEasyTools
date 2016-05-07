package com.zyj.ieasytools.library.db;

import android.content.Context;

import com.zyj.ieasytools.library.encrypt.PasswordEntry;
import com.zyj.ieasytools.library.utils.ZYJUtils;

import net.sqlcipher.database.SQLiteDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yuri.zheng on 2016/5/5.
 */
public class ZYJEncrypts extends BaseDatabase {

    private static MaintainEncryptClass mMaintain;

    /**
     * @param context context
     */
    public ZYJEncrypts(Context context) {
        super(context);
        if (mMaintain == null) {
            mMaintain = new MaintainEncryptClass();
        }
        ZYJUtils.logD(getClass(), "Key: " + context.getClass().getName());
        mMaintain.add(context.getClass().getName(), this);
    }

    /**
     * Destory all instance,Call when exit app
     */
    public static void destory() {
        if (mMaintain != null) {
            mMaintain.destory();
        }
        mMaintain = null;
    }

    /**
     * Open database
     *
     * @param path     the database path
     * @param password the database password
     * @return {@link #DATABASE_OPEN_SUCCESS}<br>
     * {@link #DATABASE_OPEN_FILE_EXCEPTION}<br>
     * {@link #DATABASE_OPEN_PASSWORD}<br>
     * {@link #DATABASE_OPEN_UNKNOW}<br>
     */
    public MySQLiteDatabase openDatabase(String path, String password) {
        return getSQLiteDatabase(path, password);
    }

    /**
     * Open our database in the app
     *
     * @param password
     * @return {@link #openDatabase(String, String)}
     */
    public MySQLiteDatabase openDatabase(String password) {
        return openDatabase(mContext.getDatabasePath(DatabaseColumns.EncryptColumns.DATABASE_NAME).getAbsolutePath(), password);
    }

    private int checkDatabase() {
        if (mSQLDatabase == null) {
            return DATABASE_OPEN_FILE_EXCEPTION;
        }
        if (mSQLDatabase.getStateCode() != DATABASE_OPEN_SUCCESS) {
            return mSQLDatabase.getStateCode();
        }
        return DATABASE_OPEN_SUCCESS;
    }

    public int insertEntry(PasswordEntry entry) {
        int state = checkDatabase();
        if (state != DATABASE_OPEN_SUCCESS) {
            return state;
        }

        return 0;
    }

    public int deleteEntry(String uuid) {
        int state = checkDatabase();
        if (state != DATABASE_OPEN_SUCCESS) {
            return state;
        }

        return 0;
    }

    public int modifyEntry(PasswordEntry entry) {
        int state = checkDatabase();
        if (state != DATABASE_OPEN_SUCCESS) {
            return state;
        }

        return 0;
    }

    public PasswordEntry queryEntry(String uuid) {
        int state = checkDatabase();
        if (state != DATABASE_OPEN_SUCCESS) {
            return null;
        }

        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected int getVersion() {
        return 0;
    }

    @Override
    protected void onUpgrade(SQLiteDatabase sqliteDatabase, int oldVersion, int newVersion) {

    }

    /**
     * Maintain the {@link ZYJEncrypts} instance
     */
    public static class MaintainEncryptClass {
        /**
         * The list of {@link ZYJEncrypts}
         */
        protected Map<String, ZYJEncrypts> mWeekRefresh = new HashMap<String, ZYJEncrypts>();

        protected MaintainEncryptClass() {

        }

        /**
         * Add a {@link ZYJEncrypts}
         */
        protected void add(String key, ZYJEncrypts z) {
            mWeekRefresh.put(key, z);
        }

        /**
         * Remove {@link ZYJEncrypts}
         */
        protected ZYJEncrypts remove(String key) {
            return mWeekRefresh.remove(key);
        }

        /**
         * Destory all the {@link ZYJEncrypts} instance
         */
        protected void destory() {
            for (String key : mWeekRefresh.keySet()) {
                ZYJEncrypts z = mWeekRefresh.get(key);
                if (z != null) {
                    z.onDestroy();
                }
            }
        }
    }
}
