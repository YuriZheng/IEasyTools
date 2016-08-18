package com.zyj.ieasytools.act.mainActivity.appView;

import com.zyj.ieasytools.act.mainActivity.BaseMainPresenter;

/**
 * Created by ZYJ on 8/13/16.
 */
public class AppPresenter extends BaseMainPresenter<IAppContract.View> implements IAppContract.Presenter {

    public AppPresenter(IAppContract.View view) {
        super(view);
        view.setPresenter(this);
    }
}
