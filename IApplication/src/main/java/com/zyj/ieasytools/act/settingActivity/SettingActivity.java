package com.zyj.ieasytools.act.settingActivity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;

import com.zyj.ieasytools.R;
import com.zyj.ieasytools.act.BaseActivity;

/**
 * 作者：yuri.zheng<br>
 * 时间：2016/8/19<br>
 */
public class SettingActivity extends BaseActivity implements ISettingContract.View {

    private Toolbar mToolbar;

    private ISettingContract.Presenter mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_layout);

        mToolbar = getViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.settings_settings);
        setSupportActionBar(mToolbar);

        new SettingPresenter(this);
    }

    @Override
    public void setPresenter(ISettingContract.Presenter presenter) {
        mPresenter = presenter;
    }
}
