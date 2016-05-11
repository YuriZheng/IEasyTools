package com.zyj.ieasytools.library.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;

import com.zyj.ieasytools.library.BuildConfig;
import com.zyj.ieasytools.library.encrypt.BaseEncrypt;

import java.io.File;
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
    public static final boolean isPasswordDebug = BuildConfig.PASSWORD_DEBUG;

    /**
     * Logcat debug
     */
    private static boolean isLogDebug = BuildConfig.LOG_DEBUG;

    /**
     * Some funcation debug
     */
    private static final boolean isFunctionDebug = BuildConfig.FUNCTION_DEBUG;

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
     * Create file in path
     *
     * @param path the file's path
     * @return create success will return true, other will return false
     */
    public static boolean createFile(String path) {
        File file = new File(path);
        if (path.contains("/")) {
            File dir = new File(path.substring(0, path.lastIndexOf("/")));
            if (!dir.exists()) {
                boolean createDir = dir.mkdirs();
                if (!createDir) {
                    return false;
                }
            }
        }
        try {
            return file.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get the setting encrypt password
     *
     * @return return the password
     */
    public static String getSettingPassword(Context context) {
        long firstInstallTime = 497393102l;
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            firstInstallTime = packageInfo.firstInstallTime;
        } catch (Exception e) {
            e.printStackTrace();
        }
        final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String code = tm.getDeviceId() + "*" + firstInstallTime;
        StringBuilder sb = new StringBuilder(code);
        if (sb.length() < BaseEncrypt.ENCRYPT_PRIVATE_KEY_LENGTH_MIN) {
            while (code.length() < BaseEncrypt.ENCRYPT_PRIVATE_KEY_LENGTH_MIN) {
                code = code + code;
            }
        } else if (sb.length() > BaseEncrypt.ENCRYPT_PRIVATE_KEY_LENGTH_MAX) {
            code = code.substring(0, BaseEncrypt.ENCRYPT_PRIVATE_KEY_LENGTH_MAX);
        }
        sb.setLength(0);
        sb.append(code);
        return sb.toString();
    }

}
