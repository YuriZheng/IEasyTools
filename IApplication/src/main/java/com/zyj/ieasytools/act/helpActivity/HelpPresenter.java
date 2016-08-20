package com.zyj.ieasytools.act.helpActivity;

/**
 * 作者：yuri.zheng<br>
 * 时间：2016/8/20<br>
 */
public class HelpPresenter implements IHelpContract.Presenter {

    private final IHelpContract.View mView;

    public HelpPresenter(IHelpContract.View mView) {
        this.mView = mView;
        mView.setPresenter(this);
    }
}
