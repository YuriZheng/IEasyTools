package com.zyj.ieasytools.act.aboutActivity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;

import com.zyj.ieasytools.R;
import com.zyj.ieasytools.act.BaseActivity;

/**
 * Author: Yuri.zheng<br>
 * Date: 8/21/16<br>
 * Email: 497393102@qq.com<br>
 */
public class AboutActivity extends BaseActivity implements IAboutContract.View {

    private Toolbar mToolbar;

    private IAboutContract.Presenter mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_layout);

        mToolbar = getViewById(R.id.toolbar);
        setToolbarTitle(getResources().getString(R.string.settings_about));
        setSupportActionBar(mToolbar);

        setToolbarSubtitle("Sub");

        new AboutPresenter(this);
    }

    @Override
    public void setToolbarTitle(String title) {
        mToolbar.setTitle(title);
    }

    @Override
    public void setToolbarSubtitle(String title) {
        mToolbar.setSubtitle(title);
    }

    @Override
    public void setPresenter(IAboutContract.Presenter presenter) {
        mPresenter = presenter;
    }
}
