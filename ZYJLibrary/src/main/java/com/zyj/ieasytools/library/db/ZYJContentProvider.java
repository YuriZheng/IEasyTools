package com.zyj.ieasytools.library.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;

import com.zyj.ieasytools.library.utils.ZYJDatabaseUtils;
import com.zyj.ieasytools.library.utils.ZYJVersion;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteQueryBuilder;

import java.lang.reflect.Method;

/**
 * Author: Yuri.zheng<br>
 * Date: 8/21/16<br>
 * Email: 497393102@qq.com<br>
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

    public static final int SETTINGS_ALARMS_BATCH = 0X1;
    public static final int SETTINGS_ALARMS_ID = 0X2;
    public static final int ENCRYPT_ALARMS_BATCH = 0X3;
    public static final int ENCRYPT_ALARMS_ID = 0X4;

    // content://com.zyj.ieasytools.library.db:itools/settings
    public static final Uri SEETINGS_URI = Uri.parse(SCHEME + "://" + HOST + ":" + PORT + "/" + PATH_SETTINGS);

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
    }

    /**
     * Only listenered the setting
     */
    private SQLiteDatabase mSettingDb;

    @Override
    public boolean onCreate() {
        ZYJVersion.checkLastVersion(getContext());
        return true;
    }

    private boolean openSettingsDatabase() {
        if (mSettingDb == null || !mSettingDb.isOpen()) {
            // The instance must be completed, otherwise occur error
            ZYJDatabaseSettings settings = ZYJDatabaseUtils.getSettingsInstance(getContext());
            try {
                Method method = ZYJDatabaseSettings.class.getDeclaredMethod("openDatabase");
                method.setAccessible(true);
                BaseDatabase.MySQLiteDatabase settingMy = (BaseDatabase.MySQLiteDatabase) method.invoke(settings);
                if (settingMy != null) {
                    mSettingDb = settingMy.getSQLDatabase();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return mSettingDb != null;
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
            qb.setTables(DatabaseColumns.SettingColumns.TABLE_NAME);
            if (match == SETTINGS_ALARMS_ID) {
                qb.appendWhere(DatabaseColumns.SettingColumns._ID + "=");
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
            } else {
                throw new UnsupportedOperationException("Cannot delete URI: " + uri);
            }
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
