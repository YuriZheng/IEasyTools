package com.zyj.ieasytools.act;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.zyj.ieasytools.library.db.ZYJContentProvider;
import com.zyj.ieasytools.library.db.ZYJSettings;
import com.zyj.ieasytools.library.utils.ZYJUtils;
import com.zyj.ieasytools.utils.SettingsConstant;

/**
 * Created by yuri.zheng on 2016/5/11.
 */
public class BaseActivity extends AppCompatActivity {

    private ContentObserver mListener;

    protected ZYJSettings mSettings;
    protected Handler mHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHandler = new Handler(getMainLooper());
        mListener = new ContentObserver(mHandler) {
            public void onChange(boolean selfChange, Uri uri) {
                ZYJUtils.logD(getClass(), uri.toString());
                BaseActivity.this.onChange(selfChange, uri);
            }
        };
        mSettings = ZYJSettings.getInstance(this);
        getContentResolver().registerContentObserver(ZYJContentProvider.SEETINGS_URI, true, mListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getContentResolver().unregisterContentObserver(mListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSettings.putLongProperties(SettingsConstant.SETTINGS_PAUSE_TIME, System.currentTimeMillis());
    }

    /**
     * Listen for setting<br>
     * {@link ContentObserver#onChange(boolean, Uri)}
     */
    public void onChange(boolean selfChange, Uri uri) {
        // Subclass override method
    }
}
