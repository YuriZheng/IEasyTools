package com.zyj.ieasytools.act.fileManagerActivity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.zyj.ieasytools.R;
import com.zyj.ieasytools.act.BaseActivity;
import com.zyj.ieasytools.views.DividerItemDecoration;

import java.io.File;

import static android.content.Intent.EXTRA_TITLE;

/**
 * Author: Yuri.zheng<br>
 * Date: 2016/10/8<br>
 * Email: 497393102@qq.com<br>
 */
public class FileManagerActivity extends BaseActivity implements IFileContract.View {

    /**
     * The path record
     */
    public static final String RECORD_HOSTORY = "_record";
    /**
     * The file type, if null mean dir
     */
    public static final String TYPE = "_type";

    private TextView mTitleView;
    private RecyclerView mRecyclerView;

    private IFileContract.Presenter mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_manager_layout);
        String title = getIntent().getStringExtra(EXTRA_TITLE);
        if (TextUtils.isEmpty(title)) {
            title = getString(R.string.file_manager_title);
        }
        String hostoryPath = getIntent().getStringExtra(RECORD_HOSTORY);
        if (TextUtils.isEmpty(hostoryPath)) {
            hostoryPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        if (!new File(hostoryPath).canRead()) {
            Toast.makeText(this, R.string.file_manager_can_not_read, Toast.LENGTH_LONG).show();
            finish();
        } else {
            mTitleView = (TextView) findViewById(R.id.file_title);
            mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);

            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
            mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

            mTitleView.setText(title);
            new FilePresenter(this);
        }
    }

    @Override
    public void setPresenter(IFileContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        super.onBackPressed();
    }
}
