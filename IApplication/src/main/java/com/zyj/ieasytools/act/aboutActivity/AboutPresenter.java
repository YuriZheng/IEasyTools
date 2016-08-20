package com.zyj.ieasytools.act.aboutActivity;

/**
 * 作者：yuri.zheng<br>
 * 时间：2016/8/20<br>
 */
public class AboutPresenter implements IAboutContract.Presenter {

    private final IAboutContract.View mView;

    public AboutPresenter(IAboutContract.View mView) {
        this.mView = mView;
        mView.setPresenter(this);
    }
}
