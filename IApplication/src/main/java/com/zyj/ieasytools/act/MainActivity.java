package com.zyj.ieasytools.act;

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
import com.zyj.ieasytools.library.db.ZYJContentProvider;
import com.zyj.ieasytools.library.db.ZYJEncrypts;
import com.zyj.ieasytools.library.encrypt.BaseEncrypt;
import com.zyj.ieasytools.library.encrypt.PasswordEntry;
import com.zyj.ieasytools.library.utils.ZYJDBEntryptUtils;
import com.zyj.ieasytools.library.utils.ZYJUtils;
import com.zyj.ieasytools.library.utils.ZYJVersion;

import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private TextView mTextView;

    //    private ZYJSettings mSettings;
    private ZYJEncrypts mEncrypt;

    private String databasePassword = "12345678";
    private String seePasswrod = "497393102";

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

        mEncrypt = ZYJDBEntryptUtils.getCurrentEncryptDatabase(this, databasePassword);

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
        if (view.getId() == R.id.encrypt) {
            String[] testString = ZYJDBEntryptUtils.generateTestTo(this, BaseEncrypt.ENCRYPT_AES, seePasswrod, ZYJVersion.FIRST_VERSION);
            PasswordEntry entry = new PasswordEntry(UUID.randomUUID().toString(), System.currentTimeMillis(), BaseEncrypt.ENCRYPT_AES, testString[0], testString[1], ZYJVersion.FIRST_VERSION);
            entry.p_username = "username";
            entry.p_password = "497393102";
            long result = mEncrypt.insertEntry(entry, seePasswrod);
            ZYJUtils.logD(getClass(), "Result: " + result);
        } else if (view.getId() == R.id.decrypt) {
//            String string = mSettings.getStringProperties("key", "");
//            mTextView.setText("Get " + string);

            ZYJUtils.logD(getClass(), "Size: " + mEncrypt.getAllRecord());
            List<PasswordEntry> entrys = mEncrypt.queryEntry(null, null, null, seePasswrod);
            for (PasswordEntry e : entrys) {
                ZYJUtils.logD(getClass(), e.toString() + ", Password: " + e.p_password);
            }
        }
    }
}
