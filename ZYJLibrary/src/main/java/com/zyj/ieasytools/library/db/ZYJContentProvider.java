package com.zyj.ieasytools.library.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;

import com.zyj.ieasytools.library.utils.ZYJUtils;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteQueryBuilder;

/**
 * Created by yuri.zheng on 2016/5/5.
 */
public class ZYJContentProvider extends ContentProvider {

    /**
     * The URI matcher
     */
    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    public static final String SCHEME = "content";

    public static final String HOST = ZYJContentProvider.class.getPackage().getName();
    public static final String PORT = "itools";
    public static final String PATH_SETTINGS = "settings";
    public static final String ENCRYPT_SETTINGS = "encrypt";

    public static final int SETTINGS_ALARMS_BATCH = 0X1;
    public static final int SETTINGS_ALARMS_ID = 0X2;
    public static final int ENCRYPT_ALARMS_BATCH = 0X3;
    public static final int ENCRYPT_ALARMS_ID = 0X4;

    // content://com.zyj.ieasytools.library.db:itools/settings
    public static final Uri SEETINGS_URI = Uri.parse(SCHEME + "://" + HOST + ":" + PORT + "/" + PATH_SETTINGS);
    // content://com.zyj.ieasytools.library.db:itools/encrypt
    public static final Uri USER_URI = Uri.parse(SCHEME + "://" + HOST + ":" + PORT + "/" + ENCRYPT_SETTINGS);

    /**
     * A batch
     */
    public static final String SHARE_LIST_TYPE = HOST + ".dir/";
    /**
     * A single
     */
    public static final String SHARE_TYPE = HOST + ".item/";

    static {
        sURIMatcher.addURI(HOST + ":" + PORT, PATH_SETTINGS, SETTINGS_ALARMS_BATCH);
        sURIMatcher.addURI(HOST + ":" + PORT, PATH_SETTINGS + "/#", SETTINGS_ALARMS_ID);

        sURIMatcher.addURI(HOST + ":" + PORT, ENCRYPT_SETTINGS, ENCRYPT_ALARMS_BATCH);
        sURIMatcher.addURI(HOST + ":" + PORT, ENCRYPT_SETTINGS + "/#", ENCRYPT_ALARMS_ID);
    }

    private SQLiteDatabase mEncryptDb;
    private SQLiteDatabase mSettingDb;

    @Override
    public boolean onCreate() {
        ZYJUtils.logD(getClass(), "onCreate");
        return true;
    }

    private boolean openSettingsDatabase() {
        if (mSettingDb == null) {
            // The instance must be completed, otherwise occur error
            BaseDatabase.MySQLiteDatabase settingMy = ZYJSettings.getInstance(getContext()).openDatabase();
            if (settingMy != null) {
                mSettingDb = settingMy.getSQLDatabase();
            }
        }
        return mSettingDb != null;
    }

    private boolean openEncryptDatabase() {
        if (mEncryptDb == null) {
            // Passowrd is null, and get the realy instance after entered app
            BaseDatabase.MySQLiteDatabase encryptMy = ZYJEncrypts.getInstance(getContext()).openDatabase("");
            if (encryptMy != null) {
                mEncryptDb = encryptMy.getSQLDatabase();
            }
        }
        return mEncryptDb != null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        int match = sURIMatcher.match(uri);
        switch (match) {
            case SETTINGS_ALARMS_BATCH:
            case ENCRYPT_ALARMS_BATCH:
                return SHARE_LIST_TYPE;
            case SETTINGS_ALARMS_ID:
            case ENCRYPT_ALARMS_ID:
                return SHARE_TYPE;
            default: {
                throw new IllegalArgumentException("Unknown URI: " + uri);
            }
        }
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        int match = sURIMatcher.match(uri);
        SQLiteDatabase db = null;
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        if (match == SETTINGS_ALARMS_BATCH || match == SETTINGS_ALARMS_ID) {
            if (openSettingsDatabase()) {
                db = mSettingDb;
            }
            qb.setTables(DatabaseColumns.SettingColumns.DATABASE_NAME);
            if (match == SETTINGS_ALARMS_ID) {
                qb.appendWhere(DatabaseColumns.SettingColumns._ID + "=");
                qb.appendWhere(uri.getPathSegments().get(1));
            }
        }
        if (match == ENCRYPT_ALARMS_BATCH || match == ENCRYPT_ALARMS_ID) {
            if (openEncryptDatabase()) {
                db = mEncryptDb;
            }
            qb.setTables(DatabaseColumns.EncryptColumns.DATABASE_NAME);
            if (match == ENCRYPT_ALARMS_ID) {
                qb.appendWhere(DatabaseColumns.EncryptColumns._ID + "=");
                qb.appendWhere(uri.getPathSegments().get(1));
            }
        }
        if (db == null) {
            return null;
        }
        Cursor ret = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        if (ret != null) {
            ret.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return ret;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int match = sURIMatcher.match(uri);
        SQLiteDatabase db = null;
        if (match == SETTINGS_ALARMS_BATCH) {
            if (openSettingsDatabase()) {
                db = mSettingDb;
            }
            if (db == null) {
                return null;
            }
            ContentValues filteredValues = new ContentValues();
            filteredValues.put(DatabaseColumns.SettingColumns._KEY, values.getAsString(DatabaseColumns.SettingColumns._KEY));
            filteredValues.put(DatabaseColumns.SettingColumns._VALUE, values.getAsString(DatabaseColumns.SettingColumns._VALUE));
            long rowId = db.insert(DatabaseColumns.SettingColumns.TABLE_NAME, null, filteredValues);
            if (rowId != -1) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
            return SEETINGS_URI;
        } else if (match == ENCRYPT_ALARMS_BATCH) {
            if (openEncryptDatabase()) {
                db = mEncryptDb;
            }
            if (db == null) {
                return null;
            }
            ContentValues filteredValues = new ContentValues();
            filteredValues.put(DatabaseColumns.EncryptColumns._UUID, values.getAsString(DatabaseColumns.EncryptColumns._UUID));
            filteredValues.put(DatabaseColumns.EncryptColumns._CATEGORY, values.getAsString(DatabaseColumns.EncryptColumns._CATEGORY));
            filteredValues.put(DatabaseColumns.EncryptColumns._TITLE, values.getAsString(DatabaseColumns.EncryptColumns._TITLE));
            filteredValues.put(DatabaseColumns.EncryptColumns._USERNAME, values.getAsString(DatabaseColumns.EncryptColumns._USERNAME));
            filteredValues.put(DatabaseColumns.EncryptColumns._PASSWORD, values.getAsString(DatabaseColumns.EncryptColumns._PASSWORD));
            filteredValues.put(DatabaseColumns.EncryptColumns._ADDRESS, values.getAsString(DatabaseColumns.EncryptColumns._ADDRESS));
            filteredValues.put(DatabaseColumns.EncryptColumns._DESCRIPTION, values.getAsString(DatabaseColumns.EncryptColumns._DESCRIPTION));
            filteredValues.put(DatabaseColumns.EncryptColumns._EMAIL, values.getAsString(DatabaseColumns.EncryptColumns._EMAIL));
            filteredValues.put(DatabaseColumns.EncryptColumns._PHONE, values.getAsString(DatabaseColumns.EncryptColumns._PHONE));
            filteredValues.put(DatabaseColumns.EncryptColumns._QUESTION_1, values.getAsString(DatabaseColumns.EncryptColumns._QUESTION_1));
            filteredValues.put(DatabaseColumns.EncryptColumns._QUESTION_ANSWER_1, values.getAsString(DatabaseColumns.EncryptColumns._QUESTION_ANSWER_1));
            filteredValues.put(DatabaseColumns.EncryptColumns._QUESTION_2, values.getAsString(DatabaseColumns.EncryptColumns._QUESTION_2));
            filteredValues.put(DatabaseColumns.EncryptColumns._QUESTION_ANSWER_2, values.getAsString(DatabaseColumns.EncryptColumns._QUESTION_ANSWER_2));
            filteredValues.put(DatabaseColumns.EncryptColumns._QUESTION_3, values.getAsString(DatabaseColumns.EncryptColumns._QUESTION_3));
            filteredValues.put(DatabaseColumns.EncryptColumns._QUESTION_ANSWER_3, values.getAsString(DatabaseColumns.EncryptColumns._QUESTION_ANSWER_3));
            filteredValues.put(DatabaseColumns.EncryptColumns._ADD_TIME, values.getAsString(DatabaseColumns.EncryptColumns._ADD_TIME));
            filteredValues.put(DatabaseColumns.EncryptColumns._MODIFY_TIME, values.getAsString(DatabaseColumns.EncryptColumns._MODIFY_TIME));
            filteredValues.put(DatabaseColumns.EncryptColumns._ENCRYPTION_METHOD, values.getAsString(DatabaseColumns.EncryptColumns._ENCRYPTION_METHOD));
            filteredValues.put(DatabaseColumns.EncryptColumns._ENCRYPTION_TEST_FROM, values.getAsString(DatabaseColumns.EncryptColumns._ENCRYPTION_TEST_FROM));
            filteredValues.put(DatabaseColumns.EncryptColumns._ENCRYPTION_TEST_TO, values.getAsString(DatabaseColumns.EncryptColumns._ENCRYPTION_TEST_TO));
            filteredValues.put(DatabaseColumns.EncryptColumns._REMARKS, values.getAsString(DatabaseColumns.EncryptColumns._REMARKS));
            filteredValues.put(DatabaseColumns.EncryptColumns._Version, values.getAsString(DatabaseColumns.EncryptColumns._Version));

            filteredValues.put(DatabaseColumns.EncryptColumns._Reserve_0, values.getAsString(DatabaseColumns.EncryptColumns._Reserve_0));
            filteredValues.put(DatabaseColumns.EncryptColumns._Reserve_1, values.getAsString(DatabaseColumns.EncryptColumns._Reserve_1));
            filteredValues.put(DatabaseColumns.EncryptColumns._Reserve_2, values.getAsString(DatabaseColumns.EncryptColumns._Reserve_2));
            filteredValues.put(DatabaseColumns.EncryptColumns._Reserve_3, values.getAsString(DatabaseColumns.EncryptColumns._Reserve_3));
            filteredValues.put(DatabaseColumns.EncryptColumns._Reserve_4, values.getAsString(DatabaseColumns.EncryptColumns._Reserve_4));

            long rowId = db.insert(DatabaseColumns.EncryptColumns.TABLE_NAME, null, filteredValues);
            if (rowId != -1) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
            return USER_URI;
        } else {
            throw new IllegalArgumentException("Unknown/Invalid URI " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = null;
        int count = -1;
        int match = sURIMatcher.match(uri);
        String where = "";
        if (selection != null) {
            if (match == SETTINGS_ALARMS_BATCH || match == ENCRYPT_ALARMS_BATCH) {
                where = "( " + selection + " )";
            } else {
                where = "( " + selection + " ) AND ";
            }
        }
        if (match == SETTINGS_ALARMS_ID || match == ENCRYPT_ALARMS_ID) {
            String segment = uri.getPathSegments().get(1);
            long rowId = Long.parseLong(segment);
            where += " ( " + BaseColumns._ID + " = " + rowId + " ) ";
        }
        if (match == SETTINGS_ALARMS_ID || match == SETTINGS_ALARMS_BATCH) {
            if (openSettingsDatabase()) {
                db = mSettingDb;
            }
            if (db == null) {
                return count;
            }
            count = db.delete(DatabaseColumns.SettingColumns.TABLE_NAME, where, selectionArgs);
        } else if (match == ENCRYPT_ALARMS_ID || match == ENCRYPT_ALARMS_BATCH) {
            if (openEncryptDatabase()) {
                db = mEncryptDb;
            }
            if (db == null) {
                return count;
            }
            count = db.delete(DatabaseColumns.EncryptColumns.TABLE_NAME, where, selectionArgs);
        } else {
            throw new UnsupportedOperationException("Cannot delete URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = null;
        int count = -1;
        int match = sURIMatcher.match(uri);
        String where = "";
        if (selection != null) {
            if (match == SETTINGS_ALARMS_BATCH || match == ENCRYPT_ALARMS_BATCH) {
                where = "( " + selection + " )";
            } else {
                where = "( " + selection + " ) AND ";
            }
        }
        if (match == SETTINGS_ALARMS_ID || match == ENCRYPT_ALARMS_ID) {
            String segment = uri.getPathSegments().get(1);
            long rowId = Long.parseLong(segment);
            where += " ( " + BaseColumns._ID + " = " + rowId + " ) ";
        }
        if (values.size() > 0) {
            if (match == SETTINGS_ALARMS_ID || match == SETTINGS_ALARMS_BATCH) {
                if (openSettingsDatabase()) {
                    db = mSettingDb;
                }
                if (db == null) {
                    return count;
                }
                count = db.update(DatabaseColumns.SettingColumns.TABLE_NAME, values, where, selectionArgs);
            } else if (match == ENCRYPT_ALARMS_ID || match == ENCRYPT_ALARMS_BATCH) {
                if (openEncryptDatabase()) {
                    db = mEncryptDb;
                }
                if (db == null) {
                    return count;
                }
                count = db.update(DatabaseColumns.EncryptColumns.TABLE_NAME, values, where, selectionArgs);
            } else {
                throw new UnsupportedOperationException("Cannot delete URI: " + uri);
            }
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
