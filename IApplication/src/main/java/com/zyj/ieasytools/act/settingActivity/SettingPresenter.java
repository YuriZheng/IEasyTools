package com.zyj.ieasytools.act.settingActivity;

import android.os.Environment;
import android.text.TextUtils;

import com.zyj.ieasytools.R;
import com.zyj.ieasytools.data.DatabaseUtils;
import com.zyj.ieasytools.library.db.ZYJDatabaseSettings;
import com.zyj.ieasytools.library.utils.ZYJUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import static com.zyj.ieasytools.data.SettingsConstant.SETTINGS_DIRECTORY_RECORD_PATH;
import static com.zyj.ieasytools.data.SettingsConstant.SETTINGS_PASSWORD_TIME_OUT;
import static com.zyj.ieasytools.data.SettingsConstant.SETTINGS_PASSWORD_TIME_OUT_DEFAULT_VALUE;
import static com.zyj.ieasytools.library.db.DatabaseColumns.DATABASE_FILE_SUFFIX;

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

    private final String EXPORT_FILE_NAME = "i_easytools";

    // Copy exception
    private final int COPY_FILE_EXCEPTION = -1;
    // Copy file is not exist
    private final int COPY_FILE_EXIST = -2;

    private final ISettingContract.View mView;
    private final ZYJDatabaseSettings mSettings;

    public SettingPresenter(ISettingContract.View mView) {
        this.mView = mView;
        mView.setPresenter(this);
        mSettings = DatabaseUtils.getSettingsInstance(mView.getContext());
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
        // /data/user/0/com.zyj.ieasytools/databases/encrypt.izyj
        File currentDatabasePath = DatabaseUtils.getCurrentDatabasePath(mView.getContext(), false);
        if (!currentDatabasePath.exists()) {
            mView.toast(R.string.settings_exportint_exists);
        } else {
            File tagDir = mView.getContext().getExternalFilesDir(null);
            if (tagDir == null) {
                mView.toast(R.string.settings_exportint_sd_error);
            } else {
                String exportPath = tagDir.getAbsolutePath() + "/" + EXPORT_FILE_NAME + "." + DATABASE_FILE_SUFFIX;
                final int titleRes = R.string.settings_exportint_title;
                String tmpPath = null;
                if (exportPath.contains("Android")) {
                    tmpPath = "Android" + exportPath.split("Android")[1];
                }
                final String message = mView.getContext().getString(R.string.settings_exportint_file, tmpPath);
                mView.actionProgressBar(titleRes, message, 0, true);
                new Thread(() -> {
                    // /storage/emulated/0/Android/data/com.zyj.ieasytools/files/i_easytools.izyj
                    copyFile(currentDatabasePath.getAbsolutePath(), exportPath, (progress) -> {
                        if (progress >= 0) {
                            if (progress >= 100) {
                                mView.actionProgressBar(-1, "", 100, false);
                            } else {
                                mView.actionProgressBar(titleRes, message, (int) progress, true);
                            }
                        } else {
                            mView.actionProgressBar(-1, "", 100, false);
                            mView.toast(R.string.settings_exportint_exception);
                        }
                    });
                }).start();
            }
        }
    }

    @Override
    public void importFile() {
        // 上次写到这里了,开始写导入文件!!!
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

    private void copyFile(String oldPath, String newPath, CopyProgressListener listener) {
        try {
            // Sleep 500 to show progress dialog
            Thread.sleep(500);
            File oldfile = new File(oldPath);
            File newFile = new File(newPath);
            if (oldfile.exists()) {
                ZYJUtils.logD(getClass(), "Copy from: " + oldPath + "\n" + "to: " + newPath);
                if (newFile.exists()) {
                    ZYJUtils.logW(getClass(), "The copy tag file is exists, delete: " + newFile.delete());
                }
                newFile.createNewFile();
                long totleSize = oldfile.length();
                int byteread = 0;
                float readSize = 0;
                InputStream inStream = new FileInputStream(oldPath);
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[64];
                while ((byteread = inStream.read(buffer)) != -1) {
                    fs.write(buffer, 0, byteread);
                    readSize += byteread;
                    float progress = (readSize / totleSize) * 100;
                    if (listener != null) {
                        listener.setProgress(progress);
                    }
                }
                if (listener != null) {
                    listener.setProgress(100);
                }
                fs.flush();
                fs.close();
                inStream.close();
            } else {
                if (listener != null) {
                    listener.setProgress(COPY_FILE_EXIST);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            ZYJUtils.logW(getClass(), "Copy file faile: " + e.getLocalizedMessage());
            if (listener != null) {
                listener.setProgress(COPY_FILE_EXCEPTION);
            }
        }
    }

    interface CopyProgressListener {
        /**
         * @param progress the progress of copy<br>
         *                 {@link #COPY_FILE_EXCEPTION}<br>
         *                 {@link #COPY_FILE_EXIST}<br>
         */
        void setProgress(float progress);
    }
}
