package com.zyj.ieasytools.act.mainActivity.appView;

import com.zyj.ieasytools.act.mainActivity.BaseMainPresenter;
import com.zyj.ieasytools.data.IData;

/**
 * Created by ZYJ on 8/13/16.
 */
public class AppPresenter extends BaseMainPresenter<IData, IAppContract.View> implements IAppContract.Presenter {

    public AppPresenter(IData data, IAppContract.View view) {
        super(data, view);
        view.setPresenter(this);
    }
}
