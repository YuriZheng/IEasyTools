package com.zyj.ieasytools.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import com.zyj.ieasytools.library.utils.ZYJUtils;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yuri.zheng on 2016/6/8.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private Class<?> TAG = getClass();

    private Thread.UncaughtExceptionHandler mUncaughtHandler;

    private static CrashHandler sInstance;

    private Context mContext;

    private DateFormat mFformatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

    private Map<String, String> infos = new HashMap<String, String>();

    public static CrashHandler getInstance(Context context) {
        if (sInstance == null)
            sInstance = new CrashHandler(context);
        return sInstance;
    }

    private CrashHandler(Context context) {
        this.mContext = context;
        mUncaughtHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mUncaughtHandler != null) {
            //如果用户没有处理则让系统默认的异常处理器来处理
            mUncaughtHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                ZYJUtils.logW(TAG, "error : \n" + Log.getStackTraceString(e));
            }
            //退出程序
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

    private boolean handleException(Throwable ex) {
        return true;
    }

    public void collectDeviceInfo(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                infos.put("versionName", versionName);
                infos.put("versionCode", versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
            ZYJUtils.logW(TAG, "an error occured when collect package info\n" + Log.getStackTraceString(e));
        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                infos.put(field.getName(), field.get(null).toString());
                ZYJUtils.logW(TAG, field.getName() + " : " + field.get(null));
            } catch (Exception e) {
                ZYJUtils.logW(TAG, "an error occured when collect crash info\n" + Log.getStackTraceString(e));
            }
        }
    }
}
