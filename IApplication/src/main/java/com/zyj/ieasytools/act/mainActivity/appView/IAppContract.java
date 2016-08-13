package com.zyj.ieasytools.act.mainActivity.appView;

import com.zyj.ieasytools.act.mainActivity.IMainPresenter;
import com.zyj.ieasytools.act.mainActivity.IMainView;

/**
 * Created by ZYJ on 8/13/16.
 * This specifies the contract between the view and the presenter.
 */
public interface IAppContract {


    interface View extends IMainView<Presenter> {

    }

    interface Presenter extends IMainPresenter {

    }

}
