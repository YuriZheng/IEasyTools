package com.zyj.ieasytools.act.settingActivity;

import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import com.zyj.ieasytools.R;
import com.zyj.ieasytools.data.DatabaseUtils;
import com.zyj.ieasytools.library.db.ZYJDatabaseSettings;
import com.zyj.ieasytools.library.utils.ZYJUtils;
import com.zyj.ieasytools.library.utils.ZYJVersion;

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

    private final String EXPORT_FILE_NAME = "MyPD";

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
        // /data/data/com.zyj.ieasytools/databases/encrypt.izyj
        File currentDatabasePath = DatabaseUtils.getCurrentDatabasePath(mView.getContext(), false);
        if (!currentDatabasePath.exists()) {
            mView.snackBar(R.string.settings_export_exists, R.string.application_reboot, false, (view) -> {
                mView.closeApp();
            });
        } else {
            File tagDir = mView.getContext().getExternalFilesDir(null);
            if (tagDir == null) {
                mView.snackBar(R.string.settings_export_sd_error, R.string.application_reboot, false, (view) -> {
                    mView.closeApp();
                });
            } else {
                // /storage/emulated/0/Android/data/com.zyj.ieasytools/files/i_easytools.izyj
                String exportPath = tagDir.getAbsolutePath() + "/" + EXPORT_FILE_NAME + "." + DATABASE_FILE_SUFFIX;
                final int titleRes = R.string.settings_export_title;
                final String tmpPath = exportPath.contains("Android") ? ("Android" + exportPath.split("Android")[1]) : exportPath;
                final String message = mView.getContext().getString(R.string.settings_export_file, tmpPath);
                mView.actionProgressBar(titleRes, message, 0, true);
                new Thread(() -> {
                    copyFile(currentDatabasePath.getAbsolutePath(), exportPath, (progress) -> {
                        if (progress >= 0) {
                            if (progress >= 100) {
                                // Success
                                mView.actionProgressBar(-1, "", 100, false);
                                mView.snackBar(mView.getContext().getString(R.string.settings_export_exists_success, tmpPath), "", true, null);
                            } else {
                                mView.actionProgressBar(titleRes, message, (int) progress, true);
                            }
                        } else {
                            mView.actionProgressBar(-1, "", 100, false);
                            mView.snackBar(R.string.settings_export_exception, android.R.string.ok, false, (view) -> {
                                exportFile();
                            });
                        }
                    });
                }).start();
            }
        }
    }

    @Override
    public void importFile(Uri sourcePath) {
        final String realPath = sourcePath.getPath();
        if (TextUtils.isEmpty(realPath)) {
            mView.snackBar(R.string.settings_import_path_error, -1, false, null);
        } else {
            if (!realPath.endsWith("." + DATABASE_FILE_SUFFIX)) {
                mView.snackBar(R.string.settings_import_error_suffix, -1, false, null);
                return;
            }
            final String realName = realPath.substring(realPath.lastIndexOf("/") + 1, realPath.lastIndexOf("."));
            File file = new File(realPath);
            if (!file.isFile() || !file.canRead()) {
                mView.snackBar(R.string.settings_import_file_error, -1, false, null);
            } else {
                File currentDatabasePath = DatabaseUtils.getCurrentDatabasePath(mView.getContext(), false);
                if (currentDatabasePath == null || !currentDatabasePath.canRead()) {
                    mView.snackBar(R.string.settings_import_database_path_error, -1, false, null);
                } else {
                    new Thread(() -> {
                        final String tagName = DatabaseUtils.getDefaultEncryptEntry(mView.getContext()).encrypt(realName, ZYJVersion.getCurrentVersion());
                        ZYJUtils.logD(getClass(), "RealName: " + realName + ", Rename: " + tagName);
                        final String tagPath = currentDatabasePath.getParentFile().getAbsolutePath() + "/" + tagName + "." + DATABASE_FILE_SUFFIX;
                        if (new File(tagPath).exists()) {
                            String message = mView.getContext().getString(R.string.settings_import_already, realName + "." + DATABASE_FILE_SUFFIX);
                            mView.snackBar(message, "", false, null);
                        } else {
                            startImport(realPath, tagPath);
                        }
                    }).start();
                }
            }
        }
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
    public void restoryDirectoryPath(Uri path) {
        if (mSettings != null && path != null && !TextUtils.isEmpty(path.getPath())) {
            File file = new File(path.getPath());
            if (file != null && file.exists()) {
                File parent = file.getParentFile();
                if (parent != null && parent.exists()) {
                    String p = parent.getAbsolutePath();
                    ZYJUtils.logD(getClass(), "Restory path: " + p);
                    mSettings.putStringProperties(SETTINGS_DIRECTORY_RECORD_PATH, p);
                }
            }
        }
    }

    private void startImport(final String oldPath, final String newPath) {
        final int titleRes = R.string.settings_import_title;
        mView.actionProgressBar(titleRes, "", 100, true);
        copyFile(oldPath, newPath, (progress) -> {
            if (progress >= 0) {
                if (progress >= 100) {
                    // Success
                    mView.actionProgressBar(-1, "", 100, false);
                    mView.snackBar(R.string.settings_import_success, -1, false, null);
                } else {
                    mView.actionProgressBar(titleRes, "", (int) progress, true);
                }
            } else {
                mView.actionProgressBar(-1, "", 100, false);
                mView.snackBar(R.string.settings_export_exception, android.R.string.ok, false, (view) -> {
                    mView.dismissSnackBar(R.string.settings_export_exception, android.R.string.ok);
                });
            }
        });
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
