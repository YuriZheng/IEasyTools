package com.zyj.ieasytools.library.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;

import com.zyj.ieasytools.library.encrypt.BaseEncrypt;
import com.zyj.jni.ZYJJniLib;

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
    public static final boolean isPasswordDebug = true;

    /**
     * Logcat debug
     */
    private static boolean isLogDebug = true;

    /**
     * Some funcation debug
     */
    public static final boolean isFunctionDebug = true;

    private static String TAG = "zyj";

    private static final String ROOR_PATH = "/.i_EasyTools/";

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
    public static DisplayMetrics getDisplayMetrics(Context context) {
        return context.getResources().getDisplayMetrics();
    }

    /**
     * @param context
     * @return one is version string, other is version code
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
     * "/storage/emulated/0" + {@link #ROOR_PATH}<br>
     * "sdcard"
     *
     * @return if available, return the path, otherwise return null
     */
    public static String getExternalRootPath() {
        if (isExternalCardExist()) {
            String root = Environment.getExternalStorageDirectory().getAbsolutePath() + ROOR_PATH;
            File rootDir = new File(root);
            if (!rootDir.exists()) {
                if (!rootDir.mkdirs()) {
                    ZYJUtils.logW(ZYJUtils.class, "Can't create dirs");
                    return null;
                }
            }
            if (rootDir.canRead() && rootDir.canWrite()) {
                return root;
            }
            ZYJUtils.logW(ZYJUtils.class, "Can't read or write");
            return null;
        } else {
            return null;
        }
    }

    /**
     * Get the setting encrypt password
     *
     * @return return the password
     */
    public static String getSettingPassword(Context context) {
        long firstInstallTime = 497393102;
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            firstInstallTime = packageInfo.firstInstallTime;
        } catch (Exception e) {
            e.printStackTrace();
        }
        ZYJUtils.logD(ZYJUtils.class,"Jni: " + ZYJJniLib.getInstance().generateSettingCode("1"));
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
