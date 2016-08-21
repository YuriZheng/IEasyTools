package com.zyj.ieasytools.act.mainActivity.childViews.walletView;

import com.zyj.ieasytools.R;
import com.zyj.ieasytools.act.mainActivity.childViews.BaseMainView;
import com.zyj.ieasytools.act.mainActivity.MainActivity;
import com.zyj.ieasytools.library.encrypt.PasswordEntry;

/**
 * Author: Yuri.zheng<br>
 * Date: 8/21/16<br>
 * Email: 497393102@qq.com<br>
 */
public class WalletView extends BaseMainView<IWalletContract.Presenter> implements IWalletContract.View {

    public WalletView(MainActivity context) {
        super(context, R.layout.group_wallet_layout);
        mCategory = PasswordEntry.CATEGORY_WALLET;
    }
}
