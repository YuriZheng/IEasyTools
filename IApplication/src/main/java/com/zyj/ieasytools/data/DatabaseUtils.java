package com.zyj.ieasytools.data;

import android.content.Context;

import com.zyj.ieasytools.library.db.ZYJDatabaseEncrypts;
import com.zyj.ieasytools.library.db.ZYJDatabaseSettings;
import com.zyj.ieasytools.library.encrypt.BaseEncrypt;
import com.zyj.ieasytools.library.utils.ZYJDatabaseUtils;

import java.io.File;
import java.util.List;

/**
 * Author: Yuri.zheng<br>
 * Date: 10/3/16<br>
 * Email: 497393102@qq.com<br>
 */

public final class DatabaseUtils {

    /**
     * {@link ZYJDatabaseUtils#getCurrentEncryptDatabase(Context, String)}
     */
    public static ZYJDatabaseEncrypts getCurrentEncryptDatabase(Context context, String password) {
        return ZYJDatabaseUtils.getCurrentEncryptDatabase(context, password);
    }

    /**
     * {@link ZYJDatabaseUtils#getEncryptDatabaseFromPath(Context, String, String)}
     */
    public static ZYJDatabaseEncrypts getEncryptDatabaseFromPath(Context context, String path, String password) {
        return ZYJDatabaseUtils.getEncryptDatabaseFromPath(context, path, password);
    }

    /**
     * {@link ZYJDatabaseUtils#getSettingsInstance(Context)}
     */
    public static ZYJDatabaseSettings getSettingsInstance(Context c) {
        return ZYJDatabaseUtils.getSettingsInstance(c);
    }

    /**
     * {@link ZYJDatabaseUtils#getCurrentDatabasePath(Context, boolean)}
     */
    public static File getCurrentDatabasePath(Context context, boolean isCreate) {
        return ZYJDatabaseUtils.getCurrentDatabasePath(context, isCreate);
    }

    /**
     * Get current database path, if the database file is null, then return null
     */
    public static String getCurrentDatabaseName(Context context) {
        File file = getCurrentDatabasePath(context, false);
        if (file != null) {
            return file.getAbsolutePath();
        }
        return null;
    }

    /**
     * {@link ZYJDatabaseUtils#getDatabasePathsBesidesCurrent(Context)}
     */
    public static List<String> getDatabasePathsBesidesCurrent(Context context) {
        return ZYJDatabaseUtils.getDatabasePathsBesidesCurrent(context);
    }

    /**
     * {@link ZYJDatabaseUtils#destoryDatabases(String)}
     */
    public static void destoryDatabases(String key) {
        ZYJDatabaseUtils.destoryDatabases(key);
    }

    /**
     * {@link ZYJDatabaseUtils#getSettingsEncrypt(Context)}
     */
    public static BaseEncrypt getDefaultEncryptEntry(Context c) {
        return ZYJDatabaseUtils.getSettingsEncrypt(c.getApplicationContext());
    }

}
