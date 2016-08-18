package com.zyj.ieasytools.act.mainActivity.childViews.webView;

import com.zyj.ieasytools.act.mainActivity.childViews.BaseMainPresenter;
import com.zyj.ieasytools.data.EntryptImple;
import com.zyj.ieasytools.library.db.DatabaseColumns;

/**
 * Created by ZYJ on 8/13/16.
 */
public class WebPresenter extends BaseMainPresenter<IWebContract.View> implements IWebContract.Presenter {

    public WebPresenter(IWebContract.View view) {
        super(view);
        view.setPresenter(this);
    }

    @Override
    public void requestEntryByCategory() {
        if (mEntrypt == null) {
            mEntrypt = EntryptImple.getEntryptImple(mView.getContext());
        }
        if (mEntrypt != null) {
            mView.setDatas(mEntrypt.queryEntry(new String[]{
                    DatabaseColumns.EncryptColumns._TITLE,
                    DatabaseColumns.EncryptColumns._USERNAME,
                    DatabaseColumns.EncryptColumns._DESCRIPTION,
                    DatabaseColumns.EncryptColumns._REMARKS
            }, null, null, null, null));
        }
    }
}
