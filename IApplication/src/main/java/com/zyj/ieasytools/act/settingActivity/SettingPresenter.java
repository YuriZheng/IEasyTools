package com.zyj.ieasytools.act.settingActivity;

/**
 * 作者：yuri.zheng<br>
 * 时间：2016/8/20<br>
 */
public class SettingPresenter implements ISettingContract.Presenter {

    private final ISettingContract.View mView;

    public SettingPresenter(ISettingContract.View mView) {
        this.mView = mView;
    }
}
