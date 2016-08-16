package com.zyj.ieasytools.act.mainActivity;

import com.zyj.ieasytools.data.IData;

/**
 * Created by ZYJ on 8/13/16.
 */
public abstract class BaseMainPresenter<D extends IData, V extends IMainView> {

    protected final D mModel;
    protected final V mView;

    public BaseMainPresenter(D presenter, V view) {
        mModel = presenter;
        mView = view;
    }
}
