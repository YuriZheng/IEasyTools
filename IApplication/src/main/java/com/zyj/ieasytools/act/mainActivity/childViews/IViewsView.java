package com.zyj.ieasytools.act.mainActivity.childViews;

import android.content.Context;
import android.view.View;

import com.zyj.ieasytools.library.encrypt.PasswordEntry;

import java.util.List;

/**
 * Author: Yuri.zheng<br>
 * Date: 8/21/16<br>
 * Email: 497393102@qq.com<br>
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
