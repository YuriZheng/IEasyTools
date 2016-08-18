package com.zyj.ieasytools.act.mainActivity.childViews.otherView;

import com.zyj.ieasytools.act.mainActivity.childViews.BaseMainPresenter;

/**
 * Created by ZYJ on 8/13/16.
 */
public class OtherPresenter extends BaseMainPresenter<IOtherContract.View> implements IOtherContract.Presenter {

    public OtherPresenter(IOtherContract.View view) {
        super(view);
        view.setPresenter(this);
    }
}
