package com.zyj.ieasytools.act;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Toast;

import com.zyj.ieasytools.R;
import com.zyj.ieasytools.library.db.ZYJSettings;
import com.zyj.ieasytools.library.utils.ZYJUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuri.zheng on 2016/5/20.
 */
public class EnterActivity extends BaseActivity {

    private final int PERMISSION_REQUEST_CODE = 0x55;

    private ZYJSettings mSettings;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter_layout);
        insertDummyContactWrapper();
    }

    public void onViewClick(View view) {
        if (mSettings == null) {
            mSettings = ZYJSettings.getInstance(this);
        }
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
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
