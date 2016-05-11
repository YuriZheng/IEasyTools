package com.zyj.ieasytools.act;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.zyj.ieasytools.library.db.ZYJContentProvider;
import com.zyj.ieasytools.library.utils.ZYJUtils;

/**
 * Created by yuri.zheng on 2016/5/11.
 */
public class BaseActivity extends AppCompatActivity {

    private ContentObserver mListener = null;

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
        getContentResolver().registerContentObserver(ZYJContentProvider.SEETINGS_URI, true, mListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getContentResolver().unregisterContentObserver(mListener);
    }

    /**
     * Listen for setting<br>
     * {@link ContentObserver#onChange(boolean, Uri)}
     */
    public void onChange(boolean selfChange, Uri uri) {
        // Subclass override method
    }
}
