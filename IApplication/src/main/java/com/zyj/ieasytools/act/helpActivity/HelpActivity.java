package com.zyj.ieasytools.act.helpActivity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;

import com.zyj.ieasytools.R;
import com.zyj.ieasytools.act.BaseActivity;

/**
 * Author: Yuri.zheng<br>
 * Date: 8/21/16<br>
 * Email: 497393102@qq.com<br>
 * <p/>
 * 查看密码
 */
public class HelpActivity extends BaseActivity implements IHelpContract.View {

    private Toolbar mToolbar;
    private IHelpContract.Presenter mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_layout);

        mToolbar = getViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.settings_help);
        setSupportActionBar(mToolbar);

        new HelpPresenter(this);
    }

    @Override
    public void setPresenter(IHelpContract.Presenter presenter) {

    }
}
