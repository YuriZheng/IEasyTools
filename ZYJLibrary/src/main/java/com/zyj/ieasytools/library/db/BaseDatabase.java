package com.zyj.ieasytools.library.db;

import android.content.Context;

import com.zyj.ieasytools.library.utils.ZYJUtils;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

import java.io.File;
import java.util.List;

public abstract class BaseDatabase<T> extends SQLiteOpenHelper {

    public SQLiteDatabase mSQLDatabase = null;

    // ===========================================================================
    public static final int DATABASE_OPEN_SUCCESS = 0x00;
    public static final int DATABASE_OPEN_FILE_EXCEPTION = 0x01;
    public static final int DATABASE_OPEN_PASSWORD = 0x02;
    public static final int DATABASE_OPEN_UNKNOW = 0x03;

    // ===========================================================================

    public BaseDatabase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    protected void dropTable(SQLiteDatabase db, String tabletName) {
        ZYJUtils.logD(getClass(), "Drop table" + tabletName);
        db.execSQL("DROP TABLE IF EXISTS " + tabletName);
    }

    protected void creatTable(SQLiteDatabase db, String createSql) {
        ZYJUtils.logD(getClass(), "Database onCreate: " + createSql);
        db.execSQL(createSql);
    }

    /**
     * @return the row ID of the newly inserted row, or -1 if an error occurred
     */
    public abstract long addEntry(T entry);

    /**
     * @return the number of rows affected if a whereClause is passed in, 0
     * otherwise. To remove all rows and get a count pass "1" as the
     * whereClause.
     */
    public abstract int delEntry(String key);

    /**
     * @return the number of rows affected
     */
    public abstract int modifyEntry(T entry);

    /**
     * @return Get all entry
     */
    public abstract List<T> queryEntry();

    public void onDestroy() {
        if (mSQLDatabase != null) {
            mSQLDatabase.close();
        }
        this.close();
    }

    protected int getSQLiteDatabase(String path, String password) {
        try {
            File file = new File(path);
            if (!file.exists() || !file.canRead()) {
                return DATABASE_OPEN_FILE_EXCEPTION;
            }
            mSQLDatabase = SQLiteDatabase.openOrCreateDatabase(path, "", null);
            if (mSQLDatabase != null) {
                return DATABASE_OPEN_SUCCESS;
            } else {
                return DATABASE_OPEN_UNKNOW;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return DATABASE_OPEN_PASSWORD;
        }
    }

}
