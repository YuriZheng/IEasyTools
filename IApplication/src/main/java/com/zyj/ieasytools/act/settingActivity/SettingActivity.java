package com.zyj.ieasytools.act.settingActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zyj.ieasytools.R;
import com.zyj.ieasytools.act.BaseActivity;
import com.zyj.ieasytools.data.SettingsConstant;
import com.zyj.ieasytools.library.utils.ZYJPreferencesUtils;
import com.zyj.ieasytools.library.utils.ZYJUtils;

/**
 * Author: Yuri.zheng<br>
 * Date: 8/21/16<br>
 * Email: 497393102@qq.com<br>
 */
public class SettingActivity extends BaseActivity implements ISettingContract.View, View.OnClickListener {

    private TextView mToolbarTextView;

    private RelativeLayout mPasswordTimeOut;
    private TextView mPasswordTime;

    private RelativeLayout mExportFile;
    private RelativeLayout mImportFile;

    private ISettingContract.Presenter mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_layout);

        mToolbarTextView = (TextView) findViewById(R.id.title);
        mToolbarTextView.setText(R.string.settings_settings);
        float size = ZYJPreferencesUtils.getFloat(this, SettingsConstant.TOOLBAR_TITLE_SIZE);
        if (size > 0) {
            mToolbarTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
        }

        mPasswordTimeOut = (RelativeLayout) findViewById(R.id.password_time_out);
        mPasswordTime = (TextView) findViewById(R.id.password_time);

        mExportFile = (RelativeLayout) findViewById(R.id.export_file);
        mImportFile = (RelativeLayout) findViewById(R.id.import_file);

        mPasswordTimeOut.setOnClickListener(this);
        mExportFile.setOnClickListener(this);
        mImportFile.setOnClickListener(this);

        findViewById(R.id.back).setOnClickListener(this);

        new SettingPresenter(this);

        mPresenter.getTimeOut();
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void setPresenter(ISettingContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.password_time_out:
                showChoosePasswordTime();
                break;
            case R.id.import_file:
                break;
            case R.id.export_file:
                break;
            case R.id.back:
                onBackPressed();
                break;
        }
    }

    private void showChoosePasswordTime() {
        String[] times = getResources().getStringArray(R.array.password_time_out_array);
        long time = mPresenter.getTimeOut();
        if (time < 0) {
            time = SettingPresenter.PASSWORD_TIME_OUT_1;
        }
        int select = 0;
        if (time == SettingPresenter.PASSWORD_TIME_OUT_1) {
            select = 0;
        } else if (time == SettingPresenter.PASSWORD_TIME_OUT_2) {
            select = 1;
        } else if (time == SettingPresenter.PASSWORD_TIME_OUT_3) {
            select = 2;
        } else if (time == SettingPresenter.PASSWORD_TIME_OUT_4) {
            select = 3;
        } else if (time == SettingPresenter.PASSWORD_TIME_OUT_5) {
            select = 4;
        } else if (time == SettingPresenter.PASSWORD_TIME_OUT_6) {
            select = 5;
        } else {
            select = -1;
            ZYJUtils.logD(TAG, "Error: " + time);
        }
        AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("");
        builder.setSingleChoiceItems(times, select, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                mPresenter.setTimeOut((which));
            }
        });
    }

    public long getTimeForIndex(int select) {
        if (select == 0) {
            return SettingPresenter.PASSWORD_TIME_OUT_1;
        } else if (select == 1) {
            return SettingPresenter.PASSWORD_TIME_OUT_2;
        } else if (select == 2) {
            return SettingPresenter.PASSWORD_TIME_OUT_3;
        } else if (select == 3) {
            return SettingPresenter.PASSWORD_TIME_OUT_4;
        } else if (select == 4) {
            return SettingPresenter.PASSWORD_TIME_OUT_5;
        } else if (select == 5) {
            return SettingPresenter.PASSWORD_TIME_OUT_6;
        } else {
            ZYJUtils.logD(TAG, "Error: " + select);
            return -1;
        }
    }
}
