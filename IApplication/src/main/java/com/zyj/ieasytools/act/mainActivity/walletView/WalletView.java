package com.zyj.ieasytools.act.mainActivity.walletView;

import com.zyj.ieasytools.R;
import com.zyj.ieasytools.act.mainActivity.BaseMainView;
import com.zyj.ieasytools.act.mainActivity.MainActivity;

/**
 * Created by yuri.zheng on 2016/5/24.
 */
public class WalletView extends BaseMainView<IWalletContract.Presenter> {

    public WalletView(MainActivity context) {
        super(context, R.layout.group_wallet_layout);
    }

    @Override
    public void setPresenter(IWalletContract.Presenter presenter) {

    }
}
