package com.zyj.ieasytools.act.mainActivity.walletView;

import com.zyj.ieasytools.act.mainActivity.BaseMainPresenter;
import com.zyj.ieasytools.act.mainActivity.webView.IWebContract;
import com.zyj.ieasytools.data.IData;

/**
 * Created by ZYJ on 8/13/16.
 */
public class WalletPresenter extends BaseMainPresenter<IData, IWebContract.View> implements IWebContract.Presenter {

    public WalletPresenter(IData presenter, IWebContract.View view) {
        super(presenter, view);
        view.setPresenter(this);
    }

}
