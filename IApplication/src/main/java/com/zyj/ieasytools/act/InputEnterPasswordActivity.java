package com.zyj.ieasytools.act;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.zyj.ieasytools.R;

/**
 * Created by yuri.zheng on 2016/5/26.
 */
public class InputEnterPasswordActivity extends AppCompatActivity {

    /**
     * Request code
     */
    public static final int REQUEST_CODE = 88;
    /**
     * Result success code
     */
    public static final int RESULT_CODE_SUCCESS = 89;
    /**
     * Result faile code
     */
    public static final int RESULT_CODE_FAILE = 90;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTranslucentStatus();
        setContentView(R.layout.password_input_layout);
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
                finishByCode(RESULT_CODE_SUCCESS);
                break;
            case R.id.switch_id:
                finishByCode(RESULT_CODE_FAILE);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finishByCode(RESULT_CODE_FAILE);
        super.onBackPressed();
    }

    private void finishByCode(int code) {
        setResult(code);
        finish();
    }
}
