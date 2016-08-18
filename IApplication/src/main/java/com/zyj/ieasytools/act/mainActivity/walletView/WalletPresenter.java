package com.zyj.ieasytools.act.mainActivity.walletView;

import com.zyj.ieasytools.act.mainActivity.BaseMainPresenter;

/**
 * Created by ZYJ on 8/13/16.
 */
public class WalletPresenter extends BaseMainPresenter<IWalletContract.View> implements IWalletContract.Presenter {

    public WalletPresenter(IWalletContract.View view) {
        super(view);
        view.setPresenter(this);
    }

}
