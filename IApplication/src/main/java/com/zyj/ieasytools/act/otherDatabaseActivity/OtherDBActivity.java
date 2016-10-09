package com.zyj.ieasytools.act.otherDatabaseActivity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.zyj.ieasytools.act.BaseActivity;

/**
 * Author: Yuri.zheng<br>
 * Date: 09/10/2016<br>
 * Email: 497393102@qq.com<br>
 */

public class OtherDBActivity  extends BaseActivity implements IOtherDBContract.View {

    private IOtherDBContract.Presenter mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setPresenter(IOtherDBContract.Presenter presenter) {
        mPresenter = presenter;
    }

}