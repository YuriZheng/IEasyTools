package com.zyj.ieasytools.act.mainActivity;

import android.content.Context;

import com.zyj.ieasytools.act.IBasePresenter;
import com.zyj.ieasytools.act.IBaseView;

/**
 * Created by ZYJ on 8/18/16.
 */
public interface IMainContract {

    interface View extends IBaseView<Presenter> {

        void actionProgressBar(boolean show);

        void showSnackbarToast();

        Context getContext();

    }

    interface Presenter extends IBasePresenter {

        void setCategory(String mCategory);

        String getCategory();

        boolean hasOtherDatabase();

    }

}
