package com.zyj.ieasytools.act.mainActivity.otherView;

import com.zyj.ieasytools.act.mainActivity.BaseMainPresenter;
import com.zyj.ieasytools.data.IData;

/**
 * Created by ZYJ on 8/13/16.
 */
public class OtherPresenter extends BaseMainPresenter<IData, IOtherContract.View> implements IOtherContract.Presenter {

    public OtherPresenter(IData presenter, IOtherContract.View view) {
        super(presenter, view);
        view.setPresenter(this);
    }
}
