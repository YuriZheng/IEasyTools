package com.zyj.ieasytools.act.mainActivity.childViews;

/**
 * Author: Yuri.zheng<br>
 * Date: 8/21/16<br>
 * Email: 497393102@qq.com<br>
 */
public interface IViewsPresenter extends com.zyj.ieasytools.act.IBasePresenter {

    /**
     * Get the database name
     */
    String getDatabaseRealyName();

    /**
     * Get the datas by category
     */
    void onReload(String category);

    /**
     * Switch database
     */
    int onSwitchDatabase(final String name, final String path, final String password);

    /**
     * Valid the database
     */
    boolean valid();

    /**
     * Destroy resources
     */
    void destory();
}
