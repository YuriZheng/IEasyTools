package com.zyj.ieasytools.act;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.zyj.ieasytools.R;
import com.zyj.ieasytools.library.encrypt.AESEncrypt;
import com.zyj.ieasytools.library.encrypt.BaseEncrypt;
import com.zyj.ieasytools.library.encrypt.EncryptFactory;
import com.zyj.ieasytools.library.encrypt.RC4Encrypt;
import com.zyj.ieasytools.library.utils.ZYJUtils;

public class MainActivity extends AppCompatActivity {

    private TextView mTextView;

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

        getSharedPreferences("keyFile", MODE_PRIVATE).edit().putString("key", "497393102").commit();

        BaseEncrypt a = EncryptFactory.getInstance().getInstance(AESEncrypt.class,"","");
    }

    public void onViewClick(View view) {
        String key = getSharedPreferences("keyFile", MODE_PRIVATE).getString("key", "");
        if (TextUtils.isEmpty(key)) {
            ZYJUtils.logD(getClass(), "key is null");
            return;
        }
        BaseEncrypt encrypt = EncryptFactory.getInstance().getInstance(RC4Encrypt.class, key, "");
        String string = "111111111111111111";
        if (view.getId() == R.id.encrypt) {
            String s = encrypt.encrypt(string);
            if (TextUtils.isEmpty(s)) {
                ZYJUtils.logD(getClass(), "Encrypt is null");
            } else {
                getSharedPreferences("File", MODE_PRIVATE).edit().putString("encrypt", s).commit();
                mTextView.setText(s);
            }
        } else if (view.getId() == R.id.decrypt) {
            String e = getSharedPreferences("File", MODE_PRIVATE).getString("encrypt", "");
            if (TextUtils.isEmpty(e)) {
                ZYJUtils.logD(getClass(), "Decrypt string is null");
            } else {
                String str = encrypt.decrypt(e);
                if (TextUtils.isEmpty(str)) {
                    ZYJUtils.logD(getClass(), "Decrypt result is null");
                } else {
                    mTextView.setText(str);
                }
            }
        }
    }
}
