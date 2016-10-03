package com.zyj.ieasytools.act.mainActivity.childViews;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;

import com.zyj.ieasytools.data.EntryptImple;
import com.zyj.ieasytools.data.IEntrypt;
import com.zyj.ieasytools.library.db.DatabaseColumns;

/**
 * Author: Yuri.zheng<br>
 * Date: 8/21/16<br>
 * Email: 497393102@qq.com<br>
 */
public abstract class BaseMainPresenter<V extends IViewsView> {

    /**
     * The switch database broadcast
     */
    public static final String BROADCAST_SWITCH_DATABASE = BaseMainPresenter.class.getName();
    /**
     * The switch database path
     */
    public static final String BROADCAST_SWITCH_DATABASE_PATH = "_path";
    /**
     * The switch database password
     */
    public static final String BROADCAST_SWITCH_DATABASE_PD = "_pd";
    /**
     * Switch to our database
     */
    public static final String BROADCAST_SWITCH_DATABASE_O = "__o";

    protected final V mView;
    protected IEntrypt mEntrypt;

    /**
     * Switch database borodcast
     */
    private BroadcastReceiver mSwitchDatabaseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String category = intent.getStringExtra(BROADCAST_SWITCH_DATABASE_O);
            if (!TextUtils.isEmpty(category) && BROADCAST_SWITCH_DATABASE_O.equals(category)) {
                mEntrypt = new EntryptImple(mView.getContext());
            } else {
                String path = intent.getStringExtra(BROADCAST_SWITCH_DATABASE_PATH);
                String pd = intent.getStringExtra(BROADCAST_SWITCH_DATABASE_PD);
                mEntrypt = new EntryptImple(mView.getContext(), path, pd);
            }
            mView.onReload();
        }
    };

    public BaseMainPresenter(V view) {
        mView = view;
        mView.getContext().registerReceiver(mSwitchDatabaseReceiver, new IntentFilter(BROADCAST_SWITCH_DATABASE));
        mEntrypt = new EntryptImple(mView.getContext());
    }

    public void destory() {
        mView.getContext().unregisterReceiver(mSwitchDatabaseReceiver);
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
