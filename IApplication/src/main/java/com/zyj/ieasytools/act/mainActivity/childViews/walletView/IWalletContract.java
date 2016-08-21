package com.zyj.ieasytools.act.mainActivity.childViews.walletView;

import com.zyj.ieasytools.act.mainActivity.childViews.IViewsPresenter;
import com.zyj.ieasytools.act.mainActivity.childViews.IViewsView;

/**
 * Author: Yuri.zheng<br>
 * Date: 8/21/16<br>
 * Email: 497393102@qq.com<br>
 * This specifies the contract between the view and the presenter.
 */
public interface IWalletContract {


    interface View extends IViewsView<Presenter> {

    }

    interface Presenter extends IViewsPresenter {

    }

}
