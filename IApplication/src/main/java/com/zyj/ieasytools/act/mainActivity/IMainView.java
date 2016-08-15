package com.zyj.ieasytools.act.mainActivity;

import android.view.View;

import com.zyj.ieasytools.act.IBaseView;

/**
 * Created by ZYJ on 8/13/16.
 */
public interface IMainView<T extends IMainPresenter> extends IBaseView<T> {

    /**
     * Get the root view
     *
     * @return return the root view instance
     */
    View getView();
}
