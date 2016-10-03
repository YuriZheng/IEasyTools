package com.zyj.ieasytools.act.settingActivity;

import com.zyj.ieasytools.data.DatabaseUtils;
import com.zyj.ieasytools.data.SettingsConstant;
import com.zyj.ieasytools.library.db.ZYJDatabaseSettings;

/**
 * Author: Yuri.zheng<br>
 * Date: 8/21/16<br>
 * Email: 497393102@qq.com<br>
 */
public class SettingPresenter implements ISettingContract.Presenter {

    /**
     * Ten minutes
     */
    public static final long PASSWORD_TIME_OUT_1 = 1000 * 60 * 10;
    /**
     * Thirty minutes
     */
    public static final long PASSWORD_TIME_OUT_2 = 1000 * 60 * 30;
    /**
     * One hour
     */
    public static final long PASSWORD_TIME_OUT_3 = 1000 * 60 * 60;
    /**
     * Two hours
     */
    public static final long PASSWORD_TIME_OUT_4 = 1000 * 60 * 60 * 2;
    /**
     * One day
     */
    public static final long PASSWORD_TIME_OUT_5 = 1000 * 60 * 60 * 24;
    /**
     * Never
     */
    public static final long PASSWORD_TIME_OUT_6 = Long.MAX_VALUE >> 1;

    private final ISettingContract.View mView;

    private final ZYJDatabaseSettings mSettings;

    public SettingPresenter(ISettingContract.View mView) {
        this.mView = mView;
        mView.setPresenter(this);
        mSettings = DatabaseUtils.getSettingsInstance(mView.getContext());
    }

    @Override
    public long getTimeOut() {
        return mSettings != null ? mSettings.getLongProperties(SettingsConstant.SETTINGS_PASSWORD_TIME_OUT, SettingsConstant.SETTINGS_PASSWORD_TIME_OUT_DEFAULT_VALUE) : -1;
    }

    @Override
    public void setTimeOut(long time) {
        if (mSettings != null) {
            mSettings.putLongProperties(SettingsConstant.SETTINGS_PASSWORD_TIME_OUT, time);
        }
    }
}
