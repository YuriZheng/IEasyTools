package com.zyj.ieasytools.act;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.zyj.ieasytools.R;

public class MainActivity extends BaseActivity {

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

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void onViewClick(View view) {
        if (view.getId() == R.id.encrypt) {
        } else if (view.getId() == R.id.decrypt) {
        }
    }
}
