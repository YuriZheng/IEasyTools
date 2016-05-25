package com.zyj.ieasytools.act;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.zyj.ieasytools.R;
import com.zyj.ieasytools.library.utils.ZYJUtils;
import com.zyj.ieasytools.utils.SettingsConstant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuri.zheng on 2016/5/20.
 */
public class EnterActivity extends BaseActivity {

    private final int PERMISSION_REQUEST_CODE = 0x55;

    private AppCompatTextView mPasswordTitle;
    private EditText mInputEdit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!checkTimeOut()) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
            return;
        }
        setTranslucentStatus();

        setContentView(R.layout.enter_layout);

        mPasswordTitle = (AppCompatTextView) findViewById(R.id.password_title);
        mInputEdit = (EditText) findViewById(R.id.input_edit);

        mPasswordTitle.setText("请输入进入密码");

        insertDummyContactWrapper();
    }

    private boolean checkTimeOut() {
        long lastTime = mSettings.getLongProperties(SettingsConstant.SETTINGS_PAUSE_TIME, -1);
        if (lastTime < 0) {
            return true;
        }
        // TODO: 2016/5/25 这里的默认时间为设置里面的默认时间，一定有值
        long timeOut = mSettings.getLongProperties(SettingsConstant.SETTINGS_PASSWORD_TIME_OUT, 0);
        if (lastTime + timeOut > System.currentTimeMillis()) {
            return true;
        }
        return false;
    }

    private void setTranslucentStatus() {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
        window.setNavigationBarColor(Color.TRANSPARENT);
    }

    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.ok_id:
                // TODO: 2016/5/25 在此处进行数据库密码判断，正确才能进行下一步
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
                break;
            case R.id.switch_id:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mSettings.onDestroy();
    }

    /**
     * Request permissions
     */
    private void insertDummyContactWrapper() {
        List<String> permissionsList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (permissionsList.size() > 0) {
            String[] permissionsArray = new String[permissionsList.size()];
            for (int i = 0; i < permissionsList.size(); i++) {
                permissionsArray[i] = permissionsList.get(i);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(this, permissionsArray, PERMISSION_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                toastPermissions(permissions, grantResults);
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void toastPermissions(@NonNull String[] permissions, @NonNull int[] grantResults) {
        ZYJUtils.logD(getClass(), "Size1: " + permissions.length + ", Size2: " + grantResults.length);
        for (int i = 0; i < permissions.length; i++) {
            if (permissions[i].equals(Manifest.permission.READ_PHONE_STATE)) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    // 没有读取手机信息权限
                    toastString("没有手机信息读取权限");
                    finish();
                    return;
                }
            }
            if (permissions[i].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE) || permissions[i].equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    // 没有读取手机信息权限
                    toastString("没有手机SD卡读取权限");
                    finish();
                    return;
                }
            }
        }
    }

    private void toastString(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
