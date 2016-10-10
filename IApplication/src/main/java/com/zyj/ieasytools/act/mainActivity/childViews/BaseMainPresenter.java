package com.zyj.ieasytools.act.mainActivity.childViews;

import com.zyj.ieasytools.data.EntryptImple;
import com.zyj.ieasytools.data.IEntrypt;
import com.zyj.ieasytools.library.db.DatabaseColumns;

/**
 * Author: Yuri.zheng<br>
 * Date: 8/21/16<br>
 * Email: 497393102@qq.com<br>
 */
public abstract class BaseMainPresenter<V extends IViewsView> {

    protected final V mView;
    protected IEntrypt mEntrypt;

    public BaseMainPresenter(V view) {
        mView = view;

        mEntrypt = new EntryptImple(mView.getContext());
    }

    public void destory() {
        mEntrypt.destroy();
    }

    /**
     * {@link IViewsPresenter#requestEntryByCategory(String)}
     */
    public void requestEntryByCategory(String category) {
        if (mEntrypt != null) {
            mView.setDatas(mEntrypt.queryEntry(new String[]{
                    DatabaseColumns.EncryptColumns._TITLE,
                    DatabaseColumns.EncryptColumns._USERNAME,
                    DatabaseColumns.EncryptColumns._DESCRIPTION,
                    DatabaseColumns.EncryptColumns._REMARKS
            }, DatabaseColumns.EncryptColumns._CATEGORY + "=?", new String[]{category}, null, null));
        }
    }
}
