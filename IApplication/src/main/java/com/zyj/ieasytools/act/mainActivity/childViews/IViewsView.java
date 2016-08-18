package com.zyj.ieasytools.act.mainActivity.childViews;

import android.view.View;

/**
 * Created by ZYJ on 8/13/16.
 */
public interface IViewsView<T extends IViewsPresenter> extends com.zyj.ieasytools.act.IBaseView<T> {

    /**
     * Get the root view
     *
     * @return return the root view instance
     */
    View getView();
}
