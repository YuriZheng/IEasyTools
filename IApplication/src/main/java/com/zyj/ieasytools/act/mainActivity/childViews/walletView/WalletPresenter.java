package com.zyj.ieasytools.act.mainActivity.childViews.walletView;

import com.zyj.ieasytools.act.mainActivity.childViews.BaseMainPresenter;

/**
 * Author: Yuri.zheng<br>
 * Date: 8/21/16<br>
 * Email: 497393102@qq.com<br>
 */
public class WalletPresenter extends BaseMainPresenter<IWalletContract.View> implements IWalletContract.Presenter {

    public WalletPresenter(IWalletContract.View view) {
        super(view);
        view.setPresenter(this);
    }

}
