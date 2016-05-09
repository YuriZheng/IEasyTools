package com.zyj.ieasytools.act;

import android.app.Activity;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.zyj.ieasytools.R;
import com.zyj.ieasytools.library.db.DatabaseColumns;
import com.zyj.ieasytools.library.db.ZYJContentProvider;
import com.zyj.ieasytools.library.db.ZYJEncrypts;
import com.zyj.ieasytools.library.utils.ZYJDBEntryptUtils;
import com.zyj.ieasytools.library.utils.ZYJUtils;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private TextView mTextView;

//    private ZYJSettings mSettings;
    private ZYJEncrypts mEncrypt;

    private ContentObserver mListener = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        mTextView = (TextView) findViewById(R.id.content);

//        mSettings = ZYJSettings.getInstance(this);

        mListener = new ContentObserver(new Handler()) {
            public void onChange(boolean selfChange, Uri uri) {
                ZYJUtils.logD(getClass(), uri.toString());
            }
        };

        getContentResolver().registerContentObserver(ZYJContentProvider.SEETINGS_URI, true, mListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getContentResolver().unregisterContentObserver(mListener);
//        mSettings.onDestroy();
        ZYJEncrypts.destory();
    }

    public void onViewClick(View view) {
        File file = new File(getDir(DatabaseColumns.EncryptColumns.DATABASE_NAME, Activity.MODE_PRIVATE).getPath());
        ZYJUtils.logD(getClass(),"File: " + file.exists());
        mEncrypt = ZYJDBEntryptUtils.getCurrentEncryptDatabase(this,"123");
        if (view.getId() == R.id.encrypt) {
            String string = ((int) (Math.random() * 1000)) + "";
//            mSettings.putStringProperties("key", string);
            mTextView.setText("Put " + string);
        } else if (view.getId() == R.id.decrypt) {
//            String string = mSettings.getStringProperties("key", "");
//            mTextView.setText("Get " + string);
        }
    }
}
