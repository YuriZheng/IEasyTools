package com.zyj.ieasytools.act.settingActivity;

/**
 * Author: Yuri.zheng<br>
 * Date: 8/21/16<br>
 * Email: 497393102@qq.com<br>
 */
public class SettingPresenter implements ISettingContract.Presenter {

    private final ISettingContract.View mView;

    public SettingPresenter(ISettingContract.View mView) {
        this.mView = mView;
    }
}
