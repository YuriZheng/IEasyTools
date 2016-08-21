package com.zyj.ieasytools.act.mainActivity.childViews.webView;

import com.zyj.ieasytools.act.mainActivity.childViews.BaseMainPresenter;

/**
 * Author: Yuri.zheng<br>
 * Date: 8/21/16<br>
 * Email: 497393102@qq.com<br>
 */
public class WebPresenter extends BaseMainPresenter<IWebContract.View> implements IWebContract.Presenter {

    public WebPresenter(IWebContract.View view) {
        super(view);
        view.setPresenter(this);
    }
}
