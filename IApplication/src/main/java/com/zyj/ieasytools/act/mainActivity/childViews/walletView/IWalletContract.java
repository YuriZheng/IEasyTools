package com.zyj.ieasytools.act.mainActivity.childViews.walletView;

import com.zyj.ieasytools.act.mainActivity.childViews.IViewsPresenter;
import com.zyj.ieasytools.act.mainActivity.childViews.IViewsView;

/**
 * Created by ZYJ on 8/13/16.
 * This specifies the contract between the view and the presenter.
 */
public interface IWalletContract {


    interface View extends IViewsView<Presenter> {

    }

    interface Presenter extends IViewsPresenter {

    }

}
