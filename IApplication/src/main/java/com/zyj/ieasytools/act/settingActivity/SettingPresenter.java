package com.zyj.ieasytools.act.settingActivity;

import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;

import com.zyj.ieasytools.data.DatabaseUtils;
import com.zyj.ieasytools.library.db.ZYJDatabaseSettings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import static com.zyj.ieasytools.data.SettingsConstant.SETTINGS_DIRECTORY_RECORD_PATH;
import static com.zyj.ieasytools.data.SettingsConstant.SETTINGS_PASSWORD_TIME_OUT;
import static com.zyj.ieasytools.data.SettingsConstant.SETTINGS_PASSWORD_TIME_OUT_DEFAULT_VALUE;

/**
 * Author: Yuri.zheng<br>
 * Date: 8/21/16<br>
 * Email: 497393102@qq.com<br>
 */
public class SettingPresenter implements ISettingContract.Presenter {

    /**
     * Ten minutes
     */
    public static final long PASSWORD_TIME_OUT_1 = 1000 * 60 * 10;
    /**
     * Thirty minutes
     */
    public static final long PASSWORD_TIME_OUT_2 = 1000 * 60 * 30;
    /**
     * One hour
     */
    public static final long PASSWORD_TIME_OUT_3 = 1000 * 60 * 60;
    /**
     * Two hours
     */
    public static final long PASSWORD_TIME_OUT_4 = 1000 * 60 * 60 * 2;
    /**
     * One day
     */
    public static final long PASSWORD_TIME_OUT_5 = 1000 * 60 * 60 * 24;
    /**
     * Never
     */
    public static final long PASSWORD_TIME_OUT_6 = Long.MAX_VALUE >> 1;

    private final ISettingContract.View mView;
    private final ZYJDatabaseSettings mSettings;
    private Handler mHandler;

    public SettingPresenter(ISettingContract.View mView) {
        this.mView = mView;
        mView.setPresenter(this);
        mSettings = DatabaseUtils.getSettingsInstance(mView.getContext());
        mHandler = new Handler(mView.getContext().getApplicationContext().getMainLooper());
    }

    @Override
    public long getTimeOut() {
        return mSettings != null ? mSettings.getLongProperties(SETTINGS_PASSWORD_TIME_OUT, SETTINGS_PASSWORD_TIME_OUT_DEFAULT_VALUE) : -1;
    }

    @Override
    public void setTimeOut(long time) {
        if (mSettings != null) {
            mSettings.putLongProperties(SETTINGS_PASSWORD_TIME_OUT, time);
        }
    }

    @Override
    public void exportFile() {
        mView.actionProgressBar("标题", "正在干什么干什么什么什么什么什么什么什么什么", true);
        new Thread(() -> {
            try {
                Thread.sleep(10000);
                mHandler.post(() -> {
                    mView.actionProgressBar("", "", false);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public String getRecordRootPath() {
        String path = null;
        if (mSettings != null) {
            path = mSettings.getStringProperties(SETTINGS_DIRECTORY_RECORD_PATH, null);
        }
        if (TextUtils.isEmpty(path)) {
            path = Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        return path;
    }

    @Override
    public void restoryDirectoryPath(String path) {
        if (mSettings != null && !TextUtils.isEmpty(path)) {
            mSettings.putStringProperties(SETTINGS_DIRECTORY_RECORD_PATH, path);
        }
    }

    private boolean copyFile(String oldPath, String newPath) {
        try {
            int byteread = 0;
            File oldfile = new File(oldPath);
            File newFile = new File(newPath);
            if (newFile.exists()) {
                newFile.delete();
            }
            newFile.createNewFile();
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1024];
                while ((byteread = inStream.read(buffer)) != -1) {
                    fs.write(buffer, 0, byteread);
                }
                fs.flush();
                fs.close();
                inStream.close();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
