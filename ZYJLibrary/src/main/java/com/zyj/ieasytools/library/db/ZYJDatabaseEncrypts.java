package com.zyj.ieasytools.library.db;

import android.content.ContentValues;
import android.content.Context;
import android.text.TextUtils;

import com.zyj.ieasytools.library.encrypt.BaseEncrypt;
import com.zyj.ieasytools.library.encrypt.EncryptFactory;
import com.zyj.ieasytools.library.encrypt.PasswordEntry;
import com.zyj.ieasytools.library.utils.ZYJDatabaseUtils;
import com.zyj.ieasytools.library.utils.ZYJUtils;
import com.zyj.ieasytools.library.utils.ZYJVersion;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: Yuri.zheng<br>
 * Date: 8/21/16<br>
 * Email: 497393102@qq.com<br>
 *
 * <h5>principle: <h5/>
 * Data storage after encrypted <br>
 * Read data after decrypted <br><br>
 * Created by yuri.zheng on 2016/5/5.
 */
public class ZYJDatabaseEncrypts extends BaseDatabase {

    /**
     * Password error
     */
    public static final int ERROR_PASSWORD = -0x99;
    /**
     * Read only
     */
    public static final int ERROR_READ_ONLY = ERROR_PASSWORD - 1;

    private final int VERSION = 1;

    private boolean isCurrentDatabase = false;
    private boolean isDestory = false;

    private EncryptListener mListener;

    /**
     * Control the access permission, so It's a private constructor<br>
     *
     * @param context context
     */
    private ZYJDatabaseEncrypts(Context context) {
        super(context);
    }

    /**
     * Open database
     *
     * @param path     the database path
     * @param password the database password
     * @return {@link DATABASE_OPEN_STATE#DATABASE_OPEN_SUCCESS}<br>
     * {@link DATABASE_OPEN_STATE#DATABASE_OPEN_FILE_EXCEPTION}<br>
     * {@link DATABASE_OPEN_STATE#DATABASE_OPEN_PASSWORD}<br>
     * {@link DATABASE_OPEN_STATE#DATABASE_OPEN_UNKNOW}<br>
     * @see ZYJDatabaseUtils#getEncryptDatabaseFromPath(Context, String, String)
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
     * @see ZYJDatabaseUtils#getEncryptDatabaseFromPath(Context, String, String)
     */
    @SuppressWarnings("unused")
    private MySQLiteDatabase openDatabase(String password) {
        File file = ZYJDatabaseUtils.getCurrentDatabasePath(mContext, true);
        MySQLiteDatabase my = null;
        if (file == null) {
            ZYJUtils.logD(TAG, "Path: null");
            my = openDatabase(null, null);
        } else {
            String path = file.getAbsolutePath();
            ZYJUtils.logD(TAG, "Path: " + path);
            my = openDatabase(path, password);
        }
        isCurrentDatabase = true;
        return my;
    }

    /**
     * @see ZYJDatabaseUtils#getEncryptDatabaseFromPath(Context, String, String)
     */
    @SuppressWarnings("unused")
    private void initDatabase() {
        creatTable(DatabaseColumns.EncryptColumns.CREATE_SETTING_TABLE_SQL);
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
     * Destory flag
     */
    public boolean isDestory() {
        return isDestory;
    }

    /**
     * Valid of database
     */
    public boolean validDatabase() {
        return checkDatabaseValid();
    }

    public DATABASE_OPEN_STATE getDatabaseState() {
        if (mSQLDatabase == null) {
            return DATABASE_OPEN_STATE.DATABASE_OPEN_FILE_EXCEPTION;
        }
        return mSQLDatabase.getStateCode();
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
        if (!checkDatabaseValid()) {
            return -1;
        }
        SQLiteDatabase d = mSQLDatabase.getSQLDatabase();
        if (checkUUID(d, entry.getUuid())) {
            return updateEntry(entry, password);
        } else {
            ContentValues v = getContentValues(entry, password);
            ZYJUtils.logD(TAG, "insert: " + entry);
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
        if (!checkDatabaseValid()) {
            return -1;
        }
        if (ZYJDatabaseUtils.checkEncryptPassword(entry.getEncryptionMethod(), password, entry.getTestFrom(), entry.getTestTo(), entry.getEncryptVersion())) {
            return ERROR_PASSWORD;
        }
        SQLiteDatabase d = mSQLDatabase.getSQLDatabase();
        String uuid = entry.getUuid();
        ZYJUtils.logD(TAG, "delete: " + entry);
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
        if (!checkDatabaseValid()) {
            return -1;
        }
        if (ZYJDatabaseUtils.checkEncryptPassword(entry.getEncryptionMethod(), password, entry.getTestFrom(), entry.getTestTo(), entry.getEncryptVersion())) {
            return ERROR_PASSWORD;
        }
        SQLiteDatabase d = mSQLDatabase.getSQLDatabase();
        ContentValues v = getContentValues(entry, password);
        String uuid = v.getAsString(DatabaseColumns.EncryptColumns._UUID);
        ZYJUtils.logD(TAG, "update: " + entry);
        if (mListener != null) {
            mListener.finishModify();
        }
        return d.update(DatabaseColumns.EncryptColumns.TABLE_NAME, v, DatabaseColumns.EncryptColumns._UUID + "=?", new String[]{uuid});
    }

    private ContentValues getContentValues(PasswordEntry entry, String password) {
        BaseEncrypt encrypt = EncryptFactory.getInstance().getEncryptInstance(entry.getEncryptionMethod(), password);
        ContentValues v = new ContentValues();

        v.put(DatabaseColumns.EncryptColumns._UUID, entry.getUuid());
        v.put(DatabaseColumns.EncryptColumns._TITLE, entry.p_title);
        v.put(DatabaseColumns.EncryptColumns._USERNAME, entry.p_username);
        v.put(DatabaseColumns.EncryptColumns._CATEGORY, entry.p_category);
        v.put(DatabaseColumns.EncryptColumns._DESCRIPTION, entry.p_description);
        v.put(DatabaseColumns.EncryptColumns._REMARKS, entry.p_remarks);

        v.put(DatabaseColumns.EncryptColumns._PASSWORD, encrypt.encrypt(entry.p_password, ZYJVersion.getCurrentVersion()));
        v.put(DatabaseColumns.EncryptColumns._ADDRESS, encrypt.encrypt(entry.p_address, ZYJVersion.getCurrentVersion()));
        v.put(DatabaseColumns.EncryptColumns._EMAIL, encrypt.encrypt(entry.p_email, ZYJVersion.getCurrentVersion()));
        v.put(DatabaseColumns.EncryptColumns._PHONE, encrypt.encrypt(entry.p_phone, ZYJVersion.getCurrentVersion()));
        v.put(DatabaseColumns.EncryptColumns._QUESTION_1, encrypt.encrypt(entry.p_q_1, ZYJVersion.getCurrentVersion()));
        v.put(DatabaseColumns.EncryptColumns._QUESTION_ANSWER_1, encrypt.encrypt(entry.p_q_a_1, ZYJVersion.getCurrentVersion()));
        v.put(DatabaseColumns.EncryptColumns._QUESTION_2, encrypt.encrypt(entry.p_q_2, ZYJVersion.getCurrentVersion()));
        v.put(DatabaseColumns.EncryptColumns._QUESTION_ANSWER_2, encrypt.encrypt(entry.p_q_a_2, ZYJVersion.getCurrentVersion()));
        v.put(DatabaseColumns.EncryptColumns._QUESTION_3, encrypt.encrypt(entry.p_q_3, ZYJVersion.getCurrentVersion()));
        v.put(DatabaseColumns.EncryptColumns._QUESTION_ANSWER_3, encrypt.encrypt(entry.p_q_a_3, ZYJVersion.getCurrentVersion()));
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

    private PasswordEntry getPasswrodEntry(List<String> columnsList, Cursor c, String password) {
        String uuid = null;
        String from = null;
        String to = null;
        String method = null;
        long addTime = 0;
        int version = 0;
        if (columnsList == null || columnsList.contains(DatabaseColumns.EncryptColumns._UUID)) {
            uuid = c.getString(c.getColumnIndex(DatabaseColumns.EncryptColumns._UUID));
        }
        if (columnsList == null || columnsList.contains(DatabaseColumns.EncryptColumns._ENCRYPTION_TEST_FROM)) {
            from = c.getString(c.getColumnIndex(DatabaseColumns.EncryptColumns._ENCRYPTION_TEST_FROM));
        }
        if (columnsList == null || columnsList.contains(DatabaseColumns.EncryptColumns._ENCRYPTION_TEST_TO)) {
            to = c.getString(c.getColumnIndex(DatabaseColumns.EncryptColumns._ENCRYPTION_TEST_TO));
        }
        if (columnsList == null || columnsList.contains(DatabaseColumns.EncryptColumns._ENCRYPTION_METHOD)) {
            method = c.getString(c.getColumnIndex(DatabaseColumns.EncryptColumns._ENCRYPTION_METHOD));
        }
        if (columnsList == null || columnsList.contains(DatabaseColumns.EncryptColumns._ADD_TIME)) {
            addTime = c.getLong(c.getColumnIndex(DatabaseColumns.EncryptColumns._ADD_TIME));
        }
        if (columnsList == null || columnsList.contains(DatabaseColumns.EncryptColumns._Version)) {
            version = c.getInt(c.getColumnIndex(DatabaseColumns.EncryptColumns._Version));
        }
        PasswordEntry entry = new PasswordEntry(uuid, addTime, method, from, to, version);
        int titleColumn = c.getColumnIndex(DatabaseColumns.EncryptColumns._TITLE);
        if (titleColumn >= 0) {
            entry.p_title = c.getString(titleColumn);
        }
        int userColumn = c.getColumnIndex(DatabaseColumns.EncryptColumns._USERNAME);
        if (userColumn >= 0) {
            entry.p_username = c.getString(userColumn);
        }
        int descriptionColumn = c.getColumnIndex(DatabaseColumns.EncryptColumns._DESCRIPTION);
        if (descriptionColumn >= 0) {
            entry.p_description = c.getString(descriptionColumn);
        }
        int remarkColumn = c.getColumnIndex(DatabaseColumns.EncryptColumns._REMARKS);
        if (remarkColumn >= 0) {
            entry.p_remarks = c.getString(remarkColumn);
        }
        int modifyColumn = c.getColumnIndex(DatabaseColumns.EncryptColumns._MODIFY_TIME);
        if (modifyColumn >= 0) {
            entry.p_modify_time = c.getLong(modifyColumn);
        }
        int categoryColumn = c.getColumnIndex(DatabaseColumns.EncryptColumns._CATEGORY);
        if (categoryColumn >= 0) {
            entry.p_category = c.getString(categoryColumn);
        }

        if (!TextUtils.isEmpty(password)) {
            BaseEncrypt encrypt = EncryptFactory.getInstance().getEncryptInstance(method, password);
            if (ZYJDatabaseUtils.checkEncryptPassword(method, password, from, to, version)) {
                ZYJUtils.logW(TAG, c.getString(c.getColumnIndex(DatabaseColumns.EncryptColumns._ID))
                        + " record password wrong");
            } else {
                int passwordColumn = c.getColumnIndex(DatabaseColumns.EncryptColumns._PASSWORD);
                if (passwordColumn >= 0) {
                    entry.p_password = encrypt.decrypt(c.getString(passwordColumn), version);
                }
                int addressColumn = c.getColumnIndex(DatabaseColumns.EncryptColumns._ADDRESS);
                if (addressColumn >= 0) {
                    entry.p_address = encrypt.decrypt(c.getString(addressColumn), version);
                }
                int emailColumn = c.getColumnIndex(DatabaseColumns.EncryptColumns._EMAIL);
                if (emailColumn >= 0) {
                    entry.p_email = encrypt.decrypt(c.getString(emailColumn), version);
                }
                int phoneColumn = c.getColumnIndex(DatabaseColumns.EncryptColumns._PHONE);
                if (phoneColumn >= 0) {
                    entry.p_phone = encrypt.decrypt(c.getString(phoneColumn), version);
                }
                int q1Column = c.getColumnIndex(DatabaseColumns.EncryptColumns._QUESTION_1);
                if (q1Column >= 0) {
                    entry.p_q_1 = encrypt.decrypt(c.getString(q1Column), version);
                }
                int a1Column = c.getColumnIndex(DatabaseColumns.EncryptColumns._QUESTION_ANSWER_1);
                if (a1Column >= 0) {
                    entry.p_q_a_1 = encrypt.decrypt(c.getString(a1Column), version);
                }
                int q2Column = c.getColumnIndex(DatabaseColumns.EncryptColumns._QUESTION_2);
                if (q2Column >= 0) {
                    entry.p_q_2 = encrypt.decrypt(c.getString(q2Column), version);
                }
                int a2Column = c.getColumnIndex(DatabaseColumns.EncryptColumns._QUESTION_ANSWER_2);
                if (a2Column >= 0) {
                    entry.p_q_a_2 = encrypt.decrypt(c.getString(a2Column), version);
                }
                int q3Column = c.getColumnIndex(DatabaseColumns.EncryptColumns._QUESTION_3);
                if (q3Column >= 0) {
                    entry.p_q_3 = encrypt.decrypt(c.getString(q3Column), version);
                }
                int a3Column = c.getColumnIndex(DatabaseColumns.EncryptColumns._QUESTION_ANSWER_3);
                if (a3Column >= 0) {
                    entry.p_q_a_3 = encrypt.decrypt(c.getString(a3Column), version);
                }
            }
        }
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
        Cursor c = d.query(DatabaseColumns.EncryptColumns.TABLE_NAME, new String[]{DatabaseColumns.EncryptColumns._UUID},
                DatabaseColumns.EncryptColumns._UUID + "=?", new String[]{uuid}, null, null, null);
        if (c == null) {
            return false;
        }
        boolean has = c.getCount() > 0;
        c.close();
        return has;
    }

    /**
     * Query records
     *
     * @param selection     condition
     * @param selectionArgs condition args
     * @param orderBy       sort
     * @param password      password,If a record is wrong, then ignore this record
     * @return return {@link List} of {@link PasswordEntry}
     */
    public List<PasswordEntry> queryEntry(String[] columns, String selection, String[] selectionArgs, String orderBy, String password) {
        List<PasswordEntry> list = new ArrayList<PasswordEntry>();
        if (!checkDatabaseValid()) {
            return list;
        }
        List<String> set = getColumns(columns);
        if (set != null) {
            String[] newColumns = new String[set.size()];
            for (int i = 0; i < set.size(); i++) {
                newColumns[i] = set.get(i);
            }
            columns = newColumns;
        }
        SQLiteDatabase d = mSQLDatabase.getSQLDatabase();
        Cursor c = d.query(DatabaseColumns.EncryptColumns.TABLE_NAME, columns, selection, selectionArgs, null, null, orderBy);
        if (c == null) {
            return list;
        }
        while (c.moveToNext()) {
            PasswordEntry entry = getPasswrodEntry(set, c, password);
            if (entry != null) {
                list.add(entry);
            }
        }
        c.close();
        if (mListener != null) {
            mListener.finishQuery();
        }
        return list;
    }

    private List<String> getColumns(String[] columns) {
        if (columns != null) {
            List<String> columnsList = new ArrayList<>();
            for (String n : columns) {
                columnsList.add(n);
            }
            if (!columnsList.contains(DatabaseColumns.EncryptColumns._UUID)) {
                columnsList.add(DatabaseColumns.EncryptColumns._UUID);
            }
            if (!columnsList.contains(DatabaseColumns.EncryptColumns._ENCRYPTION_TEST_FROM)) {
                columnsList.add(DatabaseColumns.EncryptColumns._ENCRYPTION_TEST_FROM);
            }
            if (!columnsList.contains(DatabaseColumns.EncryptColumns._ENCRYPTION_TEST_TO)) {
                columnsList.add(DatabaseColumns.EncryptColumns._ENCRYPTION_TEST_TO);
            }
            if (!columnsList.contains(DatabaseColumns.EncryptColumns._ENCRYPTION_METHOD)) {
                columnsList.add(DatabaseColumns.EncryptColumns._ENCRYPTION_METHOD);
            }
            if (!columnsList.contains(DatabaseColumns.EncryptColumns._ADD_TIME)) {
                columnsList.add(DatabaseColumns.EncryptColumns._ADD_TIME);
            }
            if (!columnsList.contains(DatabaseColumns.EncryptColumns._Version)) {
                columnsList.add(DatabaseColumns.EncryptColumns._Version);
            }
            return columnsList;
        }
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isDestory = true;
    }

    @Override
    protected int getVersion() {
        return VERSION;
    }

    @Override
    protected void onUpgrade(SQLiteDatabase sqliteDatabase, int oldVersion, int newVersion) {
        // TODO: 2016/5/17
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
