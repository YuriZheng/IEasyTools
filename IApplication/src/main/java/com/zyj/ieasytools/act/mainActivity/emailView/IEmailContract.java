package com.zyj.ieasytools.act.mainActivity.emailView;

import com.zyj.ieasytools.act.mainActivity.IMainPresenter;
import com.zyj.ieasytools.act.mainActivity.IMainView;

/**
 * Created by ZYJ on 8/13/16.
 * This specifies the contract between the view and the presenter.
 */
public interface IEmailContract {


    interface View extends IMainView<Presenter> {

    }

    interface Presenter extends IMainPresenter {
    }

}
