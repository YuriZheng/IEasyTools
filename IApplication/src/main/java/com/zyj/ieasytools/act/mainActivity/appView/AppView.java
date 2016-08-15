package com.zyj.ieasytools.act.mainActivity.appView;

import com.zyj.ieasytools.R;
import com.zyj.ieasytools.act.mainActivity.BaseMainView;
import com.zyj.ieasytools.act.mainActivity.MainActivity;

/**
 * Created by yuri.zheng on 2016/5/24.
 */
public class AppView extends BaseMainView<IAppContract.Presenter> {

    public AppView(MainActivity context) {
        super(context, R.layout.group_app_layout);
    }

    @Override
    public void setPresenter(IAppContract.Presenter presenter) {

    }
}
