package com.zyj.ieasytools.act.mainActivity.childViews.webView;

import android.content.Context;

import com.zyj.ieasytools.act.mainActivity.childViews.IViewsPresenter;
import com.zyj.ieasytools.act.mainActivity.childViews.IViewsView;
import com.zyj.ieasytools.library.encrypt.PasswordEntry;

import java.util.List;

/**
 * Created by ZYJ on 8/13/16.
 * This specifies the contract between the view and the presenter.
 */
public interface IWebContract {


    interface View extends IViewsView<Presenter> {

        Context getContext();

        void setDatas(List<PasswordEntry> list);

    }

    interface Presenter extends IViewsPresenter {

        void requestEntryByCategory();

    }

}
