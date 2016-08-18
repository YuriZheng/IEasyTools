package com.zyj.ieasytools.act.mainActivity.emailView;

import com.zyj.ieasytools.R;
import com.zyj.ieasytools.act.mainActivity.BaseMainView;
import com.zyj.ieasytools.act.mainActivity.MainActivity;

/**
 * Created by yuri.zheng on 2016/5/24.
 */
public class EmailView extends BaseMainView<IEmailContract.Presenter> implements IEmailContract.View{

    public EmailView(MainActivity context) {
        super(context, R.layout.group_email_layout);
    }

    @Override
    public void verifyEnterPasswordSuccess() {

    }

    @Override
    public void onReload() {

    }
}
