package com.zyj.ieasytools.act.feedbackActivity;

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
public class FeedbackActivity extends BaseActivity implements IFeedbackContract.View {

    private Toolbar mToolbar;

    private IFeedbackContract.Presenter mPresenter;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_layout);

        mToolbar = getViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.settings_feedback);
        setSupportActionBar(mToolbar);

        new FeedbackPresenter(this);
    }

    @Override
    public void setPresenter(IFeedbackContract.Presenter presenter) {
        mPresenter = presenter;
    }
}
