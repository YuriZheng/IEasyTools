package com.zyj.ieasytools.library.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.StatFs;
import android.util.DisplayMetrics;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by yuri.zheng on 2016/4/26.
 */
public final class ZYJUtils {

    /**
     * To see the database's data
     */
    public static final boolean isPasswordDebug = true;

    /**
     * Logcat debug
     */
    private static boolean isLogDebug = true;

    /**
     * Some funcation debug
     */
    private static boolean isFunctionDebug = true;

    private static String TAG = "zyj";

    public static void logD(Class<?> clz, String msg) {
        if (isLogDebug && msg != null) {
            Log.d(TAG, clz.getSimpleName() + ": " + msg);
        }
    }

    public static void logI(Class<?> clz, String msg) {
        if (isLogDebug && msg != null) {
            Log.i(TAG, clz.getSimpleName() + ": " + msg);
        }
    }

    public static void logW(Class<?> clz, String msg) {
        if (isLogDebug && msg != null) {
            Log.w(TAG, clz.getSimpleName() + ": " + msg);
        }
    }

    public static void logE(Class<?> clz, String msg) {
        if (isLogDebug && msg != null) {
            Log.e(TAG, clz.getSimpleName() + ": " + msg);
        }
    }

    public static void logSys(Class<?> clz, String msg) {
        if (isLogDebug && msg != null) {
            System.out.println(clz.getSimpleName() + ": " + msg);
        }
    }

    public static void setFunctionDebug(boolean debug) {
        isFunctionDebug = debug;
    }

    public static void setLogDebug(boolean debug) {
        isLogDebug = debug;
    }

    /**
     * Date to long
     *
     * @param date
     * @return
     */
    public static long TdateToLong(Date date) {
        return date.getTime();
    }

    /**
     * Long to string
     *
     * @param time
     * @return
     */
    public static String TlongToString(long time) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time);
    }

    /**
     * String to long
     *
     * @param c
     * @param timeString
     * @return
     */
    public static long TstringtoLong(Context c, String timeString) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = formatter.parse(timeString);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * @param context interface quote
     * @return return a array, one is width, other is height
     */
    public static int[] getDisplayMetrics(Activity context) {
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int pixels[] = new int[2];
        pixels[0] = dm.widthPixels;
        pixels[1] = dm.heightPixels;
        return pixels;
    }

    /**
     * @param context
     * @return one is version string, other is version
     */
    public static Object[] getVersion(Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packInfo;
        Object[] o = new Object[2];
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            o[0] = packInfo.versionName;
            o[1] = packInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return o;
    }

    /**
     * Get root path<br>
     * "/system"
     *
     * @return
     */
    public static String getRootDirectoryPath() {
        return Environment.getRootDirectory().getPath();
    }

    /**
     * Get data path<br>
     * "/data"
     *
     * @return
     */
    public static String getDataDirectoryPath() {
        return Environment.getDataDirectory().getPath();
    }

    /**
     * Judge the external card exist
     *
     * @return exist will return true, otherwise return false
     */
    public static boolean isExternalCardExist() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * Get sdcard path if sd is available<br>
     * "/storage/emulated/0"<br>
     * "sdcard"
     *
     * @return if available, return the path, otherwise return null
     */
    public static String getExternalCardPath() {
        if (isExternalCardExist()) {
            return Environment.getExternalStorageDirectory().getPath();
        } else {
            return null;
        }
    }

    /**
     * Get the system available memory size
     *
     * @return return the memory size
     */
    public static long getSystemAvailableMemorySize() {
        StatFs stat = new StatFs(getDataDirectoryPath());
        long blockSize = stat.getBlockSizeLong();
        long availableBlocks = stat.getAvailableBlocksLong();
        return availableBlocks * blockSize;
    }

    /**
     * Get the system totle memory size
     *
     * @return
     */
    public static long getSystemTotleMemorySize() {
        StatFs stat = new StatFs(getDataDirectoryPath());
        long blockSize = stat.getBlockSizeLong();
        long totalBlocks = stat.getBlockCountLong();
        return totalBlocks * blockSize;
    }

    /**
     * Get the external available memory size
     *
     * @return return the memory size, if the sdcard not exist ,return -1
     */
    public static long getExternalAvailableMemorySize() {
        if (isExternalCardExist()) {
            StatFs stat = new StatFs(getExternalCardPath());
            long blockSize = stat.getBlockSizeLong();
            long availableBlocks = stat.getAvailableBlocksLong();
            return availableBlocks * blockSize;
        } else {
            return -1;
        }
    }

    /**
     * Get the external totle memory size
     *
     * @return return the totle memory size, if the sdcard not exist ,return -1
     */
    public static long getExternalTotalMemorySize() {
        if (isExternalCardExist()) {
            StatFs stat = new StatFs(getExternalCardPath());
            long blockSize = stat.getBlockSizeLong();
            long totalBlocks = stat.getBlockCountLong();
            return totalBlocks * blockSize;
        } else {
            return -1;
        }
    }

}
