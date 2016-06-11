package com.zyj.ieasytools.act;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;

import com.zyj.ieasytools.R;

/**
 * Created by ZYJ on 6/11/16.
 */
public class AddEntryActivity extends BaseActivity {

    private Toolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setEnterTransition(new Explode());
        getWindow().setExitTransition(new Explode());

        setContentView(R.layout.add_entry_layout);

        mToolbar = getViewById(R.id.toolbar);
        mToolbar.setTitle("111111");
        setSupportActionBar(mToolbar);

        mToolbar.setNavigationIcon(R.mipmap.back);

    }
}
