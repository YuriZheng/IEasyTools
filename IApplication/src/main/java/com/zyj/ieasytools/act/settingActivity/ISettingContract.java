package com.zyj.ieasytools.act.settingActivity;

import android.content.Context;
import android.net.Uri;

import com.zyj.ieasytools.act.IBasePresenter;
import com.zyj.ieasytools.act.IBaseView;

/**
 * Author: Yuri.zheng<br>
 * Date: 8/21/16<br>
 * Email: 497393102@qq.com<br>
 */
public interface ISettingContract {

    interface View extends IBaseView<Presenter> {

        void setTimeOutText();

        Context getContext();

        void actionProgressBar(int title, String message, int progress, boolean show);

        void snackBar(int message, int actionRes, boolean isLong, android.view.View.OnClickListener listener);

        void snackBar(String message, String actionRes, boolean isLong, android.view.View.OnClickListener listener);

        void dismissSnackBar(int message, int actionRes);

        void closeApp();

    }

    interface Presenter extends IBasePresenter {

        long getTimeOut();

        void setTimeOut(long time);

        /**
         * Export our database file to Android/data/com.zyj.ieasytools/files
         */
        void exportFile();

        /**
         * Import other database file to application dir
         */
        void importFile(Uri sourcePath);

        /**
         * Get the directory, if has hostory then return path of last time
         */
        String getRecordRootPath();

        /**
         * Save the path
         */
        void restoryDirectoryPath(Uri path);

    }

}
