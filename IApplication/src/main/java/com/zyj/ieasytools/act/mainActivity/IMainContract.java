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

        void showSnackbarToast();

        void enableAddButton(boolean enable);

        Context getContext();

    }

    interface Presenter extends IBasePresenter {

        void setCategory(String mCategory);

        String getCategory();

        boolean hasOtherDatabase();

        boolean isOurDatabase();

        void destory();

    }

}
