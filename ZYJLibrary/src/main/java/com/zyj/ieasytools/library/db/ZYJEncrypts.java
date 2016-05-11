package com.zyj.ieasytools.library.db;

import android.content.ContentValues;
import android.content.Context;

import com.zyj.ieasytools.library.encrypt.BaseEncrypt;
import com.zyj.ieasytools.library.encrypt.EncryptFactory;
import com.zyj.ieasytools.library.encrypt.PasswordEntry;
import com.zyj.ieasytools.library.utils.ZYJDBEntryptUtils;
import com.zyj.ieasytools.library.utils.ZYJUtils;
import com.zyj.ieasytools.library.utils.ZYJVersion;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <h5>principle: <h5/>
 * Data storage after encrypted <br>
 * Read data after decrypted <br><br>
 * Created by yuri.zheng on 2016/5/5.
 */
public class ZYJEncrypts extends BaseDatabase {

    /**
     * Password error
     */
    public static final int ERROR_PASSWORD = -0x99;
    /**
     * Read only
     */
    public static final int ERROR_READ_ONLY = ERROR_PASSWORD - 1;

    private final int VERSION = 1;
    private static MaintainEncryptClass mMaintain;

    private boolean isCurrentDatabase = false;

    private EncryptListener mListener;

    /**
     * Control the access permission, so It's a private constructor
     *
     * @param context context
     */
    private ZYJEncrypts(Context context) {
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
     * @see ZYJDBEntryptUtils#getEncryptDatabaseFromPath(Context, String, String)
     */
    private MySQLiteDatabase openDatabase(String path, String password) {
        isCurrentDatabase = false;
        return getSQLiteDatabase(path, password);
    }

    /**
     * Open our database in the app
     *
     * @param password
     * @return {@link #openDatabase(String, String)}
     * @see ZYJDBEntryptUtils#getEncryptDatabaseFromPath(Context, String, String)
     */
    @SuppressWarnings("unused")
    private MySQLiteDatabase openDatabase(String password) {
        File file = ZYJDBEntryptUtils.getCurrentDatabasePath(mContext);
        ZYJUtils.logI(getClass(), "Path: " + file.getAbsolutePath());
        MySQLiteDatabase my = null;
        if (file == null) {
            my = openDatabase(null, null);
        } else {
            my = openDatabase(file.getAbsolutePath(), password);
        }
        isCurrentDatabase = true;
        return my;
    }

    /**
     * @see ZYJDBEntryptUtils#getEncryptDatabaseFromPath(Context, String, String)
     */
    @SuppressWarnings("unused")
    private void initDatabase() {
        creatTable(DatabaseColumns.EncryptColumns.CREATE_SETTING_TABLE_SQL);
    }

    /**
     * Check the database state
     */
    private int checkDatabase() {
        if (mSQLDatabase == null) {
            return DATABASE_OPEN_FILE_EXCEPTION;
        }
        if (mSQLDatabase.getStateCode() != DATABASE_OPEN_SUCCESS) {
            return mSQLDatabase.getStateCode();
        }
        if (mSQLDatabase.getStateCode() == DATABASE_OPEN_SUCCESS && mSQLDatabase.getSQLDatabase() == null) {
            throw new RuntimeException("The state is success but the database is null");
        }
        return DATABASE_OPEN_SUCCESS;
    }

    /**
     * Set encrypt listener
     */
    public void setEncryptListener(EncryptListener l) {
        mListener = l;
    }

    /**
     * This database weather is ours
     *
     * @return true if open ourself,other return false
     */
    public boolean isCurrentDatabase() {
        return isCurrentDatabase;
    }

    /**
     * Insert a entry. Insert after encrypt the entry
     *
     * @param entry    the entry
     * @param password the encrypt password
     * @return If the record is in database and the password is wrong,return {@link #ERROR_PASSWORD} <br>
     * {@link SQLiteDatabase#insert(String, String, ContentValues)}
     */
    public long insertEntry(PasswordEntry entry, String password) {
        if (!isCurrentDatabase) {
            return ERROR_READ_ONLY;
        }
        int state = checkDatabase();
        if (state != DATABASE_OPEN_SUCCESS) {
            return state;
        }
        SQLiteDatabase d = mSQLDatabase.getSQLDatabase();
        if (checkUUID(d, entry.getUuid())) {
            return updateEntry(entry, password);
        } else {
            ContentValues v = getContentValues(entry, password);
            ZYJUtils.logD(getClass(), "insert: " + entry);
            if (mListener != null) {
                mListener.finishInsert();
            }
            return d.insert(DatabaseColumns.EncryptColumns.TABLE_NAME, null, v);
        }
    }

    /**
     * Delete record by {@link PasswordEntry#uuid}
     *
     * @param entry    the record
     * @param password the password
     * @return if the password is wrong,then return {@link #ERROR_PASSWORD}, other return {@link SQLiteDatabase#delete(String, String, String[])}
     */
    public int deleteEntry(PasswordEntry entry, String password) {
        if (!isCurrentDatabase) {
            return ERROR_READ_ONLY;
        }
        int state = checkDatabase();
        if (state != DATABASE_OPEN_SUCCESS) {
            return state;
        }
        if (ZYJDBEntryptUtils.checkEncryptPassword(mContext, entry.getEncryptionMethod(), password, entry.getTestFrom(), entry.getTestTo(), entry.getEncryptVersion())) {
            return ERROR_PASSWORD;
        }
        BaseEncrypt encrypt = EncryptFactory.getInstance().getInstance(EncryptFactory.getClassFromMethod(entry.getEncryptionMethod()), password);
        SQLiteDatabase d = mSQLDatabase.getSQLDatabase();
        String uuid = encrypt.encrypt(entry.getUuid(), ZYJVersion.FIRST_VERSION);
        ZYJUtils.logD(getClass(), "delete: " + entry);
        if (mListener != null) {
            mListener.finishDelete();
        }
        return d.delete(DatabaseColumns.EncryptColumns.TABLE_NAME, DatabaseColumns.EncryptColumns._UUID + "=?", new String[]{uuid});
    }

    /**
     * Update the record
     *
     * @param entry    the record
     * @param password the password
     * @return if the password is wrong,then return {@link #ERROR_PASSWORD}, other return {@link SQLiteDatabase#update(String, ContentValues, String, String[])}
     */
    public long updateEntry(PasswordEntry entry, String password) {
        if (!isCurrentDatabase) {
            return ERROR_READ_ONLY;
        }
        int state = checkDatabase();
        if (state != DATABASE_OPEN_SUCCESS) {
            return state;
        }
        if (ZYJDBEntryptUtils.checkEncryptPassword(mContext, entry.getEncryptionMethod(), password, entry.getTestFrom(), entry.getTestTo(), entry.getEncryptVersion())) {
            return ERROR_PASSWORD;
        }
        SQLiteDatabase d = mSQLDatabase.getSQLDatabase();
        ContentValues v = getContentValues(entry, password);
        String uuid = v.getAsString(DatabaseColumns.EncryptColumns._UUID);
        ZYJUtils.logD(getClass(), "update: " + entry);
        if (mListener != null) {
            mListener.finishModify();
        }
        return d.update(DatabaseColumns.EncryptColumns.TABLE_NAME, v, DatabaseColumns.EncryptColumns._UUID + "=?", new String[]{uuid});
    }

    /**
     * Query records
     *
     * @param selection     condition
     * @param selectionArgs condition args
     * @param groupBy       sort
     * @param password      password,If a record is wrong, then ignore this record
     * @return return {@link List} of {@link PasswordEntry}
     */
    public List<PasswordEntry> queryEntry(String selection, String[] selectionArgs, String groupBy, String password) {
        List<PasswordEntry> list = new ArrayList<PasswordEntry>();
        int state = checkDatabase();
        if (state != DATABASE_OPEN_SUCCESS) {
            return list;
        }
        SQLiteDatabase d = mSQLDatabase.getSQLDatabase();
        Cursor c = null;
        try {
            c = d.query(DatabaseColumns.EncryptColumns.TABLE_NAME, null, selection, selectionArgs, groupBy, null, null);
            while (c.moveToNext()) {
                PasswordEntry entry = getPasswrodEntry(c, password);
                if (entry != null) {
                    list.add(entry);
                }
            }
        } finally {
            if (c != null) {
                c.close();
            }
            if (mListener != null) {
                mListener.finishQuery();
            }
        }
        return list;
    }

    /**
     * Get the all datas count
     *
     * @return return the count
     */
    public int getAllRecord() {
        int state = checkDatabase();
        if (state != DATABASE_OPEN_SUCCESS) {
            return state;
        }
        Cursor c = null;
        try {
            SQLiteDatabase d = mSQLDatabase.getSQLDatabase();
            c = d.query(DatabaseColumns.EncryptColumns.TABLE_NAME, new String[]{DatabaseColumns.EncryptColumns._UUID},
                    null, null, null, null, null);
            return c.getCount();
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    private ContentValues getContentValues(PasswordEntry entry, String password) {
        BaseEncrypt encrypt = EncryptFactory.getInstance().getInstance(EncryptFactory.getClassFromMethod(entry.getEncryptionMethod()), password);
        ContentValues v = new ContentValues();
        v.put(DatabaseColumns.EncryptColumns._UUID, encrypt.encrypt(entry.getUuid(), ZYJVersion.FIRST_VERSION));
        v.put(DatabaseColumns.EncryptColumns._CATEGORY, encrypt.encrypt(entry.p_category, ZYJVersion.FIRST_VERSION));
        v.put(DatabaseColumns.EncryptColumns._TITLE, encrypt.encrypt(entry.p_title, ZYJVersion.FIRST_VERSION));
        v.put(DatabaseColumns.EncryptColumns._USERNAME, encrypt.encrypt(entry.p_username, ZYJVersion.FIRST_VERSION));
        v.put(DatabaseColumns.EncryptColumns._PASSWORD, encrypt.encrypt(entry.p_password, ZYJVersion.FIRST_VERSION));
        v.put(DatabaseColumns.EncryptColumns._ADDRESS, encrypt.encrypt(entry.p_address, ZYJVersion.FIRST_VERSION));
        v.put(DatabaseColumns.EncryptColumns._DESCRIPTION, encrypt.encrypt(entry.p_description, ZYJVersion.FIRST_VERSION));
        v.put(DatabaseColumns.EncryptColumns._EMAIL, encrypt.encrypt(entry.p_email, ZYJVersion.FIRST_VERSION));
        v.put(DatabaseColumns.EncryptColumns._PHONE, encrypt.encrypt(entry.p_phone, ZYJVersion.FIRST_VERSION));
        v.put(DatabaseColumns.EncryptColumns._QUESTION_1, encrypt.encrypt(entry.p_q_1, ZYJVersion.FIRST_VERSION));
        v.put(DatabaseColumns.EncryptColumns._QUESTION_ANSWER_1, encrypt.encrypt(entry.p_q_a_1, ZYJVersion.FIRST_VERSION));
        v.put(DatabaseColumns.EncryptColumns._QUESTION_2, encrypt.encrypt(entry.p_q_2, ZYJVersion.FIRST_VERSION));
        v.put(DatabaseColumns.EncryptColumns._QUESTION_ANSWER_2, encrypt.encrypt(entry.p_q_a_2, ZYJVersion.FIRST_VERSION));
        v.put(DatabaseColumns.EncryptColumns._QUESTION_3, encrypt.encrypt(entry.p_q_3, ZYJVersion.FIRST_VERSION));
        v.put(DatabaseColumns.EncryptColumns._QUESTION_ANSWER_3, encrypt.encrypt(entry.p_q_a_3, ZYJVersion.FIRST_VERSION));
        v.put(DatabaseColumns.EncryptColumns._REMARKS, encrypt.encrypt(entry.p_remarks, ZYJVersion.FIRST_VERSION));
        v.put(DatabaseColumns.EncryptColumns._ADD_TIME, entry.getAddTime());
        v.put(DatabaseColumns.EncryptColumns._MODIFY_TIME, entry.p_modify_time);
        v.put(DatabaseColumns.EncryptColumns._ENCRYPTION_METHOD, entry.getEncryptionMethod());
        v.put(DatabaseColumns.EncryptColumns._ENCRYPTION_TEST_FROM, entry.getTestFrom());
        v.put(DatabaseColumns.EncryptColumns._ENCRYPTION_TEST_TO, entry.getTestTo());
        v.put(DatabaseColumns.EncryptColumns._Version, entry.p_version);
        //Reserve
        v.put(DatabaseColumns.EncryptColumns._Reserve_0, "");
        v.put(DatabaseColumns.EncryptColumns._Reserve_1, "");
        v.put(DatabaseColumns.EncryptColumns._Reserve_2, "");
        v.put(DatabaseColumns.EncryptColumns._Reserve_3, "");
        v.put(DatabaseColumns.EncryptColumns._Reserve_4, "");
        return v;
    }

    private PasswordEntry getPasswrodEntry(Cursor c, String password) {
        String from = c.getString(c.getColumnIndex(DatabaseColumns.EncryptColumns._ENCRYPTION_TEST_FROM));
        String to = c.getString(c.getColumnIndex(DatabaseColumns.EncryptColumns._ENCRYPTION_TEST_TO));
        String method = c.getString(c.getColumnIndex(DatabaseColumns.EncryptColumns._ENCRYPTION_METHOD));
        long addTime = c.getLong(c.getColumnIndex(DatabaseColumns.EncryptColumns._ADD_TIME));
        long modifyTime = c.getLong(c.getColumnIndex(DatabaseColumns.EncryptColumns._MODIFY_TIME));
        int version = c.getInt(c.getColumnIndex(DatabaseColumns.EncryptColumns._Version));

        if (ZYJDBEntryptUtils.checkEncryptPassword(mContext, method, password, from, to, version)) {
            ZYJUtils.logW(getClass(), c.getString(c.getColumnIndex(DatabaseColumns.EncryptColumns._ID))
                    + " record password wrong");
            return null;
        }
        BaseEncrypt encrypt = EncryptFactory.getInstance().getInstance(EncryptFactory.getClassFromMethod(method), password);
        String uuid = c.getString(c.getColumnIndex(DatabaseColumns.EncryptColumns._UUID));
        PasswordEntry entry = new PasswordEntry(encrypt.decrypt(uuid, version), addTime, method, from, to, version);

        entry.p_category = encrypt.decrypt(c.getString(c.getColumnIndex(DatabaseColumns.EncryptColumns._CATEGORY)), version);
        entry.p_title = encrypt.decrypt(c.getString(c.getColumnIndex(DatabaseColumns.EncryptColumns._TITLE)), version);
        entry.p_username = encrypt.decrypt(c.getString(c.getColumnIndex(DatabaseColumns.EncryptColumns._USERNAME)), version);
        entry.p_password = encrypt.decrypt(c.getString(c.getColumnIndex(DatabaseColumns.EncryptColumns._PASSWORD)), version);
        entry.p_address = encrypt.decrypt(c.getString(c.getColumnIndex(DatabaseColumns.EncryptColumns._ADDRESS)), version);
        entry.p_description = encrypt.decrypt(c.getString(c.getColumnIndex(DatabaseColumns.EncryptColumns._DESCRIPTION)), version);
        entry.p_email = encrypt.decrypt(c.getString(c.getColumnIndex(DatabaseColumns.EncryptColumns._EMAIL)), version);
        entry.p_phone = encrypt.decrypt(c.getString(c.getColumnIndex(DatabaseColumns.EncryptColumns._PHONE)), version);
        entry.p_q_1 = encrypt.decrypt(c.getString(c.getColumnIndex(DatabaseColumns.EncryptColumns._QUESTION_1)), version);
        entry.p_q_a_1 = encrypt.decrypt(c.getString(c.getColumnIndex(DatabaseColumns.EncryptColumns._QUESTION_ANSWER_1)), version);
        entry.p_q_2 = encrypt.decrypt(c.getString(c.getColumnIndex(DatabaseColumns.EncryptColumns._QUESTION_2)), version);
        entry.p_q_a_2 = encrypt.decrypt(c.getString(c.getColumnIndex(DatabaseColumns.EncryptColumns._QUESTION_ANSWER_2)), version);
        entry.p_q_3 = encrypt.decrypt(c.getString(c.getColumnIndex(DatabaseColumns.EncryptColumns._QUESTION_3)), version);
        entry.p_q_a_3 = encrypt.decrypt(c.getString(c.getColumnIndex(DatabaseColumns.EncryptColumns._QUESTION_ANSWER_3)), version);
        entry.p_remarks = encrypt.decrypt(c.getString(c.getColumnIndex(DatabaseColumns.EncryptColumns._REMARKS)), version);
        entry.p_modify_time = modifyTime;
        // Reserve
        // DatabaseColumns.EncryptColumns._Reserve_0
        // DatabaseColumns.EncryptColumns._Reserve_1
        // DatabaseColumns.EncryptColumns._Reserve_2
        // DatabaseColumns.EncryptColumns._Reserve_3
        // DatabaseColumns.EncryptColumns._Reserve_4
        return entry;
    }

    /**
     * @param uuid decrypted string
     * @return return {@link Cursor#getCount()} > 0
     */
    private boolean checkUUID(SQLiteDatabase d, String uuid) {
        Cursor c = null;
        try {
            c = d.query(DatabaseColumns.EncryptColumns.TABLE_NAME, new String[]{DatabaseColumns.EncryptColumns._UUID},
                    DatabaseColumns.EncryptColumns._UUID + "=?", new String[]{uuid}, null, null, null);
            return c.getCount() > 0;
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected int getVersion() {
        return VERSION;
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
            ZYJEncrypts insted = mWeekRefresh.put(key, z);
            if (insted != null) {
                insted.onDestroy();
            }
        }

        /**
         * Remove {@link ZYJEncrypts}
         */
        protected void remove(String key) {
            ZYJEncrypts insted = mWeekRefresh.remove(key);
            if (insted != null) {
                insted.onDestroy();
            }
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
            mWeekRefresh.clear();
        }
    }

    /**
     * Encrypt or Decrypt listener after finish action
     */
    public interface EncryptListener {

        /**
         * Insert finished callback
         */
        void finishInsert();

        /**
         * Delete finished callback
         */
        void finishDelete();

        /**
         * Modify finished callback
         */
        void finishModify();

        /**
         * Query a list of {@link PasswordEntry} finished callback
         */
        void finishQuery();
    }
}
