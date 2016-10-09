package com.zyj.ieasytools.act.otherDatabaseActivity;

/**
 * Author: Yuri.zheng<br>
 * Date: 09/10/2016<br>
 * Email: 497393102@qq.com<br>
 */

public class OtherDBPresenter implements IOtherDBContract.Presenter {

    private IOtherDBContract.View mView;

    public OtherDBPresenter(IOtherDBContract.View view) {
        this.mView = view;
    }
}
