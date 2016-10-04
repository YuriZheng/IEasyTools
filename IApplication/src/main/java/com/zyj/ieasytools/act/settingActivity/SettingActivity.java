package com.zyj.ieasytools.act.settingActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zyj.ieasytools.R;
import com.zyj.ieasytools.act.BaseActivity;
import com.zyj.ieasytools.act.helpActivity.HelpActivity;
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

    private ProgressDialog mProgressBar;

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

        setTimeOutText();
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
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("file/");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                try {
                    startActivityForResult(Intent.createChooser(intent, getString(R.string.settings_import_file_title)), Activity.RESULT_OK);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(this, R.string.settings_import_no_manager, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.export_file:
                // TODO: 10/4/16 直接导出到一个指定路径
                mPresenter.exportFile();
                break;
            case R.id.back:
                onBackPressed();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Activity.RESULT_OK && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            chooseFile(uri.toString());
        }
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        setTimeOutText();
    }

    @Override
    public void setTimeOutText() {
        mPasswordTime.setText(getTimeString(mPresenter.getTimeOut()));
    }

    private String getTimeString(long time) {
        final String[] times = getResources().getStringArray(R.array.password_time_out_array);
        if (time == SettingPresenter.PASSWORD_TIME_OUT_1) {
            return times[0];
        } else if (time == SettingPresenter.PASSWORD_TIME_OUT_2) {
            return times[1];
        } else if (time == SettingPresenter.PASSWORD_TIME_OUT_3) {
            return times[2];
        } else if (time == SettingPresenter.PASSWORD_TIME_OUT_4) {
            return times[3];
        } else if (time == SettingPresenter.PASSWORD_TIME_OUT_5) {
            return times[4];
        } else if (time == SettingPresenter.PASSWORD_TIME_OUT_6) {
            return times[5];
        } else {
            ZYJUtils.logD(TAG, "Error: " + time);
            return times[1];
        }
    }

    private long getTimeLong(String timeString) {
        final String[] times = getResources().getStringArray(R.array.password_time_out_array);
        if (TextUtils.isEmpty(timeString)) {
            return SettingPresenter.PASSWORD_TIME_OUT_1;
        } else if (times[0].equals(timeString)) {
            return SettingPresenter.PASSWORD_TIME_OUT_1;
        } else if (times[1].equals(timeString)) {
            return SettingPresenter.PASSWORD_TIME_OUT_2;
        } else if (times[2].equals(timeString)) {
            return SettingPresenter.PASSWORD_TIME_OUT_3;
        } else if (times[3].equals(timeString)) {
            return SettingPresenter.PASSWORD_TIME_OUT_4;
        } else if (times[4].equals(timeString)) {
            return SettingPresenter.PASSWORD_TIME_OUT_5;
        } else if (times[5].equals(timeString)) {
            return SettingPresenter.PASSWORD_TIME_OUT_6;
        } else {
            return -1;
        }
    }

    private void showChoosePasswordTime() {
        final String[] times = getResources().getStringArray(R.array.password_time_out_array);
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
        builder.setTitle(R.string.settings_password_time_out_title);
        builder.setSingleChoiceItems(times, select, (dialog, which) -> {
            if (which == 5) {
                showWarnDialog();
            } else {
                mPresenter.setTimeOut(getTimeLong(times[which]));
            }
            dialog.dismiss();
        });
        builder.setNegativeButton(R.string.add_see_password_help, ((dialog, which) -> {
            Intent i = new Intent(getApplicationContext(), HelpActivity.class);
            startActivity(i);
            dialog.dismiss();
        }));
        builder.create().show();
    }

    private void showWarnDialog() {
        AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle(R.string.settings_password_time_warn);
        builder.setMessage(R.string.settings_password_time_never);
        builder.setIcon(android.R.drawable.stat_sys_warning);
        builder.setNegativeButton(android.R.string.ok, (dialog, which) -> {
            mPresenter.setTimeOut(SettingPresenter.PASSWORD_TIME_OUT_6);
            dialog.dismiss();
        });
        builder.setPositiveButton(android.R.string.cancel, (dialog, which) -> {
            dialog.dismiss();
        });
        builder.create().show();
    }

    private void chooseFile(String path) {
        ZYJUtils.logD(getClass(), "Path: " + path);
    }

    @Override
    public void actionProgressBar(String title, String message, boolean show) {
        if (show) {
            if (mProgressBar == null) {
                mProgressBar = new ProgressDialog(this, android.R.style.Widget_DeviceDefault_Light_ProgressBar);
            }
            mProgressBar.setTitle(title);
            mProgressBar.setMessage(message);
            mProgressBar.show();
        } else {
            if (mProgressBar != null && mProgressBar.isShowing()) {
                mProgressBar.dismiss();
            }
        }
    }
}
