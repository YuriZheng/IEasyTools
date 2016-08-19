package com.zyj.ieasytools.act.mainActivity.childViews.webView;

import com.zyj.ieasytools.act.mainActivity.childViews.BaseMainPresenter;

/**
 * Created by ZYJ on 8/13/16.
 */
public class WebPresenter extends BaseMainPresenter<IWebContract.View> implements IWebContract.Presenter {

    public WebPresenter(IWebContract.View view) {
        super(view);
        view.setPresenter(this);
    }
}
