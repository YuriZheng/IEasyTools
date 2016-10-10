package com.zyj.ieasytools.act.mainActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import com.zyj.ieasytools.act.mainActivity.childViews.BaseMainPresenter;
import com.zyj.ieasytools.data.DatabaseUtils;
import com.zyj.ieasytools.library.encrypt.PasswordEntry;

import static com.zyj.ieasytools.act.otherDatabaseActivity.OtherDBActivity.CLOASE_DIALOG;

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

    private boolean isOurDatabase = true;

    private String mCurrentDatabaseName;

    /**
     * Switch database borodcast
     */
    private BroadcastReceiver mSwitchDatabaseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String path = intent.getStringExtra(BROADCAST_SWITCH_DATABASE_PATH);
            String name = intent.getStringExtra(BROADCAST_SWITCH_DATABASE_NAME);
            String password = intent.getStringExtra(BROADCAST_SWITCH_DATABASE_PD);
            switchDatabase(name, path, password);
        }
    };

    public MainPresenter(IMainContract.View view) {
        this.mView = view;
        mView.setPresenter(this);
        mView.getContext().registerReceiver(mSwitchDatabaseReceiver, new IntentFilter(BROADCAST_SWITCH_DATABASE));
    }

    private void switchDatabase(final String name, final String path, final String password) {
        // TODO: 10/10/2016 在这里进行数据库的切换 
//        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(path) && new File(path).canRead()) {
//            ZYJUtils.logD(getClass(), "-----Name：" + name);
//            ZYJUtils.logD(getClass(), "-----Path：" + path);
//        } else {
//            Toast.makeText(this, "Error name: " + name + "\nError path: " + path, Toast.LENGTH_LONG).show();
//        }
        LocalBroadcastManager.getInstance(mView.getContext()).sendBroadcast(new Intent(CLOASE_DIALOG));
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
        return isOurDatabase;
    }

    @Override
    public void destory() {
        mView.getContext().unregisterReceiver(mSwitchDatabaseReceiver);
    }

    @Override
    public String getCurrentDatabaseName() {
        return "";
    }
}
