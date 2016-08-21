package com.zyj.ieasytools.act.mainActivity.childViews.appView;

import com.zyj.ieasytools.act.mainActivity.childViews.BaseMainPresenter;

/**
 * Author: Yuri.zheng<br>
 * Date: 8/21/16<br>
 * Email: 497393102@qq.com<br>
 */
public class AppPresenter extends BaseMainPresenter<IAppContract.View> implements IAppContract.Presenter {

    public AppPresenter(IAppContract.View view) {
        super(view);
        view.setPresenter(this);
    }
}
