package com.zyj.ieasytools.act.mainActivity.childViews.otherView;

import com.zyj.ieasytools.R;
import com.zyj.ieasytools.act.mainActivity.MainActivity;
import com.zyj.ieasytools.act.mainActivity.childViews.BaseMainView;
import com.zyj.ieasytools.library.encrypt.PasswordEntry;

/**
 * Created by yuri.zheng on 2016/5/24.
 */
public class OtherView extends BaseMainView<IOtherContract.Presenter> implements IOtherContract.View {

    public OtherView(MainActivity context) {
        super(context, R.layout.group_other_layout);
        mCategory = PasswordEntry.CATEGORY_OTHER;
    }
}
