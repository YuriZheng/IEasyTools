package com.zyj.ieasytools.act.mainActivity.childViews;

import com.zyj.ieasytools.data.IEntrypt;

/**
 * Created by ZYJ on 8/13/16.
 */
public abstract class BaseMainPresenter<V extends IViewsView> {

    protected final V mView;
    protected IEntrypt mEntrypt;

    public BaseMainPresenter(V view) {
        mView = view;
    }
}
