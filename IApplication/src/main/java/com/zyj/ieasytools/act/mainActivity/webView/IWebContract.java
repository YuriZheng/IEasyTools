package com.zyj.ieasytools.act.mainActivity.webView;

import android.content.Context;

import com.zyj.ieasytools.act.mainActivity.IMainPresenter;
import com.zyj.ieasytools.act.mainActivity.IMainView;
import com.zyj.ieasytools.library.encrypt.PasswordEntry;

import java.util.List;

/**
 * Created by ZYJ on 8/13/16.
 * This specifies the contract between the view and the presenter.
 */
public interface IWebContract {


    interface View extends IMainView<Presenter> {

        Context getContext();

        void setDatas(List<PasswordEntry> list);

    }

    interface Presenter extends IMainPresenter {

        void requestEntryByCategory();

    }

}
