package com.zyj.ieasytools.act.mainActivity.childViews.otherView;

import com.zyj.ieasytools.R;
import com.zyj.ieasytools.act.mainActivity.MainActivity;
import com.zyj.ieasytools.act.mainActivity.childViews.BaseMainView;
import com.zyj.ieasytools.library.encrypt.PasswordEntry;

/**
 * Author: Yuri.zheng<br>
 * Date: 8/21/16<br>
 * Email: 497393102@qq.com<br>
 */
public class OtherView extends BaseMainView<IOtherContract.Presenter> implements IOtherContract.View {

    public OtherView(MainActivity context) {
        super(context, R.layout.group_other_layout);
        mCategory = PasswordEntry.CATEGORY_OTHER;
        new OtherPresenter(this);
    }
}
