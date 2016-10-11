package com.zyj.ieasytools.act.mainActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.zyj.ieasytools.act.mainActivity.childViews.BaseMainPresenter;
import com.zyj.ieasytools.data.DatabaseUtils;
import com.zyj.ieasytools.library.db.DatabaseColumns;
import com.zyj.ieasytools.library.encrypt.PasswordEntry;

import static com.zyj.ieasytools.act.otherDatabaseActivity.OtherDBActivity.CLOASE_DIALOG;
import static com.zyj.ieasytools.act.otherDatabaseActivity.OtherDBActivity.SWITCH_RESULT;
import static com.zyj.ieasytools.act.otherDatabaseActivity.OtherDBActivity.SWITCH_RESULT_NULL;
import static com.zyj.ieasytools.act.otherDatabaseActivity.OtherDBActivity.SWITCH_RESULT_RECOPY;

/**
 * Created by ZYJ on 8/18/16.
 */
public class MainPresenter implements IMainContract.Presenter {

    /**
     * The switch database broadcast
     */
    public static final String BROADCAST_SWITCH_DATABASE = BaseMainPresenter.class.getName();
    /**
     * The switch database path
     */
    public static final String BROADCAST_SWITCH_DATABASE_PATH = "_path";
    /**
     * The switch database name
     */
    public static final String BROADCAST_SWITCH_DATABASE_NAME = "_name";
    /**
     * The switch database password
     */
    public static final String BROADCAST_SWITCH_DATABASE_PD = "_pd";

    private final IMainContract.View mView;

    /**
     * Record the category
     * <li>{@link PasswordEntry#CATEGORY_WEB}</li>
     * <li>{@link PasswordEntry#CATEGORY_EMAIL}</li>
     * <li>{@link PasswordEntry#CATEGORY_WALLET}</li>
     * <li>{@link PasswordEntry#CATEGORY_APP}</li>
     * <li>{@link PasswordEntry#CATEGORY_GAME}</li>
     * <li>{@link PasswordEntry#CATEGORY_OTHER}</li>
     */
    private String mCategory = PasswordEntry.CATEGORY_WEB;

    /**
     * Switch database borodcast
     */
    private BroadcastReceiver mSwitchDatabaseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String path = intent.getStringExtra(BROADCAST_SWITCH_DATABASE_PATH);
            String name = intent.getStringExtra(BROADCAST_SWITCH_DATABASE_NAME);
            String password = intent.getStringExtra(BROADCAST_SWITCH_DATABASE_PD);
            new Thread(() -> {
                switchDatabase(name, path, password);
            }).start();
        }
    };

    public MainPresenter(IMainContract.View view) {
        this.mView = view;
        mView.setPresenter(this);
        LocalBroadcastManager.getInstance(mView.getContext()).registerReceiver(mSwitchDatabaseReceiver, new IntentFilter(BROADCAST_SWITCH_DATABASE));
    }

    private void switchDatabase(final String name, final String path, final String password) {
        Intent intent = new Intent(CLOASE_DIALOG);
        if (isEmpty(name, path, password)) {
            intent.putExtra(SWITCH_RESULT, SWITCH_RESULT_NULL);
            LocalBroadcastManager.getInstance(mView.getContext()).sendBroadcast(intent);
            return;
        }
        String current = mView.getCurrentDatabaseName();
        if (!TextUtils.isEmpty(current) && current.equals(name)) {
            intent.putExtra(SWITCH_RESULT, SWITCH_RESULT_RECOPY);
            LocalBroadcastManager.getInstance(mView.getContext()).sendBroadcast(intent);
        } else {
            intent.putExtra(SWITCH_RESULT, mView.onSwitchDatabase(name, path, password));
            LocalBroadcastManager.getInstance(mView.getContext()).sendBroadcast(intent);
        }
    }

    private boolean isEmpty(String... args) {
        boolean empty = false;
        for (String string : args) {
            empty = (empty || TextUtils.isEmpty(string));
        }
        return empty;
    }

    @Override
    public void setCategory(String mCategory) {
        this.mCategory = mCategory;
    }

    @Override
    public String getCategory() {
        return mCategory;
    }

    @Override
    public boolean hasOtherDatabase() {
        return DatabaseUtils.getDatabasePathsBesidesCurrent(mView.getContext().getApplicationContext()).size() > 0;
    }

    @Override
    public boolean isOurDatabase() {
        return DatabaseColumns.EncryptColumns.DATABASE_NAME.equals(mView.getCurrentDatabaseName());
    }

    @Override
    public void destory() {
        LocalBroadcastManager.getInstance(mView.getContext()).unregisterReceiver(mSwitchDatabaseReceiver);
    }
}
