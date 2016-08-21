package com.zyj.ieasytools.act.helpActivity;

/**
 * Author: Yuri.zheng<br>
 * Date: 8/21/16<br>
 * Email: 497393102@qq.com<br>
 */
public class HelpPresenter implements IHelpContract.Presenter {

    private final IHelpContract.View mView;

    public HelpPresenter(IHelpContract.View mView) {
        this.mView = mView;
        mView.setPresenter(this);
    }
}
