package com.zyj.ieasytools.act.mainActivity.otherView;

import com.zyj.ieasytools.R;
import com.zyj.ieasytools.act.mainActivity.BaseMainView;
import com.zyj.ieasytools.act.mainActivity.MainActivity;

/**
 * Created by yuri.zheng on 2016/5/24.
 */
public class OtherView extends BaseMainView<IOtherContract.Presenter> {

    public OtherView(MainActivity context) {
        super(context, R.layout.group_other_layout);
    }

    @Override
    public void setPresenter(IOtherContract.Presenter presenter) {

    }
}
