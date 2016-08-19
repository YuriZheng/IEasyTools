package com.zyj.ieasytools.act.mainActivity.childViews;

import com.zyj.ieasytools.data.EntryptImple;
import com.zyj.ieasytools.data.IEntrypt;
import com.zyj.ieasytools.library.db.DatabaseColumns;
import com.zyj.ieasytools.library.encrypt.PasswordEntry;

import java.util.List;

/**
 * Created by ZYJ on 8/13/16.
 */
public abstract class BaseMainPresenter<V extends IViewsView> {

    protected final V mView;
    protected IEntrypt mEntrypt;

    public BaseMainPresenter(V view) {
        mView = view;
    }

    /**
     * {@link IViewsPresenter#requestEntryByCategory(String)}
     */
    public void requestEntryByCategory(String category) {
        if (mEntrypt == null) {
            mEntrypt = EntryptImple.getEntryptImple(mView.getContext());
        }
        if (mEntrypt != null) {
            List<PasswordEntry> list = mEntrypt.queryEntry(null, null, null, null, null);
            mView.setDatas(mEntrypt.queryEntry(new String[]{
                    DatabaseColumns.EncryptColumns._TITLE,
                    DatabaseColumns.EncryptColumns._USERNAME,
                    DatabaseColumns.EncryptColumns._DESCRIPTION,
                    DatabaseColumns.EncryptColumns._REMARKS
            }, DatabaseColumns.EncryptColumns._CATEGORY + "=?", new String[]{category}, null, null));
        }
    }
}
