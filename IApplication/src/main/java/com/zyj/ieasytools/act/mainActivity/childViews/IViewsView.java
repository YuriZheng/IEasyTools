package com.zyj.ieasytools.act.mainActivity.childViews;

import android.content.Context;
import android.view.View;

import com.zyj.ieasytools.library.encrypt.PasswordEntry;

import java.util.List;

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

    /**
     * Get the context
     */
    Context getContext();

    /**
     * Set the listView data
     */
    void setDatas(List<PasswordEntry> list);
}
