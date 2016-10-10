package com.zyj.ieasytools.act.mainActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.zyj.ieasytools.data.DatabaseUtils;
import com.zyj.ieasytools.library.encrypt.PasswordEntry;

import static com.zyj.ieasytools.act.mainActivity.childViews.BaseMainPresenter.BROADCAST_SWITCH_DATABASE_O;

/**
 * Created by ZYJ on 8/18/16.
 */
public class MainPresenter implements IMainContract.Presenter {

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

    /**
     * Switch database borodcast
     */
    private BroadcastReceiver mSwitchDatabaseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String category = intent.getStringExtra(BROADCAST_SWITCH_DATABASE_O);
            if (!TextUtils.isEmpty(category) && BROADCAST_SWITCH_DATABASE_O.equals(category)) {
                isOurDatabase = true;
            } else {
                isOurDatabase = false;
            }
        }
    };

    public MainPresenter(IMainContract.View view) {
        this.mView = view;
        mView.setPresenter(this);
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
    public boolean switchDatabase(String name, String path) {

        return true;
    }

    @Override
    public boolean isOurDatabase() {
        return isOurDatabase;
    }

    @Override
    public void destory() {
    }
}
