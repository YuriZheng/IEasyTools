package com.zyj.ieasytools.act.mainActivity;

import android.content.Context;

import com.zyj.ieasytools.act.IBasePresenter;
import com.zyj.ieasytools.act.IBaseView;

/**
 * Author: Yuri.zheng<br>
 * Date: 8/21/16<br>
 * Email: 497393102@qq.com<br>
 */
public interface IMainContract {

    interface View extends IBaseView<Presenter> {

        void actionProgressBar(boolean show);

        void enableAddButton(boolean enable);

        Context getContext();

        String getCurrentDatabaseName();

        int onSwitchDatabase(final String name, final String path, final String password);

        void snackBar(int message, int actionRes, boolean isLong, android.view.View.OnClickListener listener);

        void dismissSnackBar(int message, int actionRes);

    }

    interface Presenter extends IBasePresenter {

        void setCategory(String mCategory);

        String getCategory();

        boolean hasOtherDatabase();

        boolean isOurDatabase();

        void destory();

    }

}
