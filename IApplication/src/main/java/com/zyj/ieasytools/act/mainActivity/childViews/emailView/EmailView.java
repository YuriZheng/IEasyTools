package com.zyj.ieasytools.act.mainActivity.childViews.emailView;

import com.zyj.ieasytools.R;
import com.zyj.ieasytools.act.mainActivity.childViews.BaseMainView;
import com.zyj.ieasytools.act.mainActivity.MainActivity;
import com.zyj.ieasytools.library.encrypt.PasswordEntry;

/**
 * Created by yuri.zheng on 2016/5/24.
 */
public class EmailView extends BaseMainView<IEmailContract.Presenter> implements IEmailContract.View {

    public EmailView(MainActivity context) {
        super(context, R.layout.group_email_layout);
        mCategory = PasswordEntry.CATEGORY_EMAIL;
    }
}
