package com.zyj.ieasytools.act.mainActivity.childViews;

import android.text.TextUtils;

import com.zyj.ieasytools.data.DatabaseUtils;
import com.zyj.ieasytools.data.EntryptImple;
import com.zyj.ieasytools.data.IEntrypt;
import com.zyj.ieasytools.library.db.BaseDatabase;
import com.zyj.ieasytools.library.db.DatabaseColumns;
import com.zyj.ieasytools.library.utils.ZYJVersion;

import static com.zyj.ieasytools.act.otherDatabaseActivity.OtherDBActivity.SWITCH_RESULT_EXCEPTION;
import static com.zyj.ieasytools.act.otherDatabaseActivity.OtherDBActivity.SWITCH_RESULT_NULL;
import static com.zyj.ieasytools.act.otherDatabaseActivity.OtherDBActivity.SWITCH_RESULT_PASSWORD;
import static com.zyj.ieasytools.act.otherDatabaseActivity.OtherDBActivity.SWITCH_RESULT_SUCCESS;
import static com.zyj.ieasytools.act.otherDatabaseActivity.OtherDBActivity.SWITCH_RESULT_UNKNOW;
import static com.zyj.ieasytools.library.db.DatabaseColumns.DATABASE_FILE_SUFFIX;

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

    public String getDatabaseRealyName() {
        String name = mEntrypt.getDatabaseName();
        if (!TextUtils.isEmpty(name)) {
            name = name.substring(0, name.lastIndexOf("."));
            name = DatabaseUtils.getDefaultEncryptEntry(mView.getContext()).decrypt(name, ZYJVersion.getCurrentVersion());
        }
        return name + "." + DATABASE_FILE_SUFFIX;
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

    /**
     * {@link IViewsPresenter#onSwitchDatabase(String, String, String)}
     */
    public int onSwitchDatabase(final String name, final String path, final String password) {
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(path) || TextUtils.isEmpty(password)) {
            return SWITCH_RESULT_NULL;
        }
        mEntrypt = new EntryptImple(mView.getContext(), path, password);
        if (!mEntrypt.validDatabase()) {
            BaseDatabase.DATABASE_OPEN_STATE state = mEntrypt.getDatabaseState();
            switch (state) {
                case DATABASE_OPEN_FILE_EXCEPTION:
                    return SWITCH_RESULT_EXCEPTION;
                case DATABASE_OPEN_PASSWORD:
                    return SWITCH_RESULT_PASSWORD;
                case DATABASE_OPEN_UNKNOW:
                    return SWITCH_RESULT_UNKNOW;
            }
        }
        return SWITCH_RESULT_SUCCESS;
    }
}
