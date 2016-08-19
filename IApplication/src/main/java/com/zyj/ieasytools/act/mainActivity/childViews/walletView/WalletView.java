package com.zyj.ieasytools.act.mainActivity.childViews.walletView;

import com.zyj.ieasytools.R;
import com.zyj.ieasytools.act.mainActivity.childViews.BaseMainView;
import com.zyj.ieasytools.act.mainActivity.MainActivity;
import com.zyj.ieasytools.library.encrypt.PasswordEntry;

/**
 * Created by yuri.zheng on 2016/5/24.
 */
public class WalletView extends BaseMainView<IWalletContract.Presenter> implements IWalletContract.View {

    public WalletView(MainActivity context) {
        super(context, R.layout.group_wallet_layout);
        mCategory = PasswordEntry.CATEGORY_WALLET;
    }
}
