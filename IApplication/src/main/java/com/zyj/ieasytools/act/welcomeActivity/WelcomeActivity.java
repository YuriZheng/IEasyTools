package com.zyj.ieasytools.act.welcomeActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.zyj.ieasytools.R;
import com.zyj.ieasytools.act.mainActivity.MainActivity;
import com.zyj.ieasytools.library.utils.ZYJUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Yuri.zheng<br>
 * Date: 8/21/16<br>
 * Email: 497393102@qq.com<br>
 */
public class WelcomeActivity extends AppCompatActivity {

    private final Class<?> TAG = getClass();

    /**
     * Permission request code
     */
    private final int PERMISSION_REQUEST_CODE = 0x55;

    /**
     * Permissions flag,mark the permissions has authorized
     */
    private boolean mPermissions = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTranslucentStatus();
        ImageView image = new ImageView(this);
        image.setImageResource(R.mipmap.start_up_bg);
        insertDummyContactWrapper();
        setContentView(image);

        if (mPermissions) {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                    finish();
                }
            }, 1000);
        }
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (toastPermissions(permissions, grantResults)) {
                    startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                }
                finish();
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(Manifest.permission.USE_FINGERPRINT);
            }
        }
        if (permissionsList.size() > 0) {
            mPermissions = false;
            String[] permissionsArray = new String[permissionsList.size()];
            for (int i = 0; i < permissionsList.size(); i++) {
                permissionsArray[i] = permissionsList.get(i);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(this, permissionsArray, PERMISSION_REQUEST_CODE);
            }
        } else {
            mPermissions = true;
        }
    }

    private boolean toastPermissions(@NonNull String[] permissions, @NonNull int[] grantResults) {
        // TODO: 2016/5/27 权限问题
        ZYJUtils.logD(TAG, "Size1: " + permissions.length + ", Size2: " + grantResults.length);
        for (int i = 0; i < permissions.length; i++) {
            if (permissions[i].equals(Manifest.permission.READ_PHONE_STATE)) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    // 没有读取手机信息权限
                    toastString("没有手机信息读取权限");
                    return false;
                }
            }
            if (permissions[i].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE) || permissions[i].equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    // 没有读取手机信息权限
                    toastString("没有手机SD卡读取权限");
                    return false;
                }
            }
        }
        return true;
    }

    private void toastString(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
