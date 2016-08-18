package com.zyj.ieasytools.act.mainActivity.otherView;

import com.zyj.ieasytools.act.mainActivity.BaseMainPresenter;

/**
 * Created by ZYJ on 8/13/16.
 */
public class OtherPresenter extends BaseMainPresenter<IOtherContract.View> implements IOtherContract.Presenter {

    public OtherPresenter(IOtherContract.View view) {
        super(view);
        view.setPresenter(this);
    }
}
