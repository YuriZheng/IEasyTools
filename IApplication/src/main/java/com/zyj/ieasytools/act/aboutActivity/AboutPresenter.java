package com.zyj.ieasytools.act.aboutActivity;

/**
 * Author: Yuri.zheng<br>
 * Date: 8/21/16<br>
 * Email: 497393102@qq.com<br>
 */
public class AboutPresenter implements IAboutContract.Presenter {

    private final IAboutContract.View mView;

    public AboutPresenter(IAboutContract.View mView) {
        this.mView = mView;
        mView.setPresenter(this);
    }
}
