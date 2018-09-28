package com.zyj.ieasytools.act.seachActivity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.zyj.ieasytools.R;
import com.zyj.ieasytools.act.BaseActivity;

/**
 * Author: Yuri.zheng<br>
 * Date: 8/22/16<br>
 * Email: 497393102@qq.com<br>
 */
public class SeachActivity extends BaseActivity implements ISeachContract.View {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_layout);
    }

    @Override
    public void setPresenter(ISeachContract.Presenter presenter) {

    }
}
