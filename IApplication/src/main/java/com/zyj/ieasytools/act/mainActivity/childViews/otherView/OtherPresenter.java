package com.zyj.ieasytools.act.mainActivity.childViews.otherView;

import com.zyj.ieasytools.act.mainActivity.childViews.BaseMainPresenter;

/**
 * Author: Yuri.zheng<br>
 * Date: 8/21/16<br>
 * Email: 497393102@qq.com<br>
 */
public class OtherPresenter extends BaseMainPresenter<IOtherContract.View> implements IOtherContract.Presenter {

    public OtherPresenter(IOtherContract.View view) {
        super(view);
        view.setPresenter(this);
    }
}
