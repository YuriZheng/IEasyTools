package com.zyj.ieasytools.act.fileManagerActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.zyj.ieasytools.R;
import com.zyj.ieasytools.act.BaseActivity;
import com.zyj.ieasytools.data.SettingsConstant;
import com.zyj.ieasytools.library.utils.ZYJPreferencesUtils;
import com.zyj.ieasytools.library.utils.ZYJUtils;
import com.zyj.ieasytools.views.DividerItemDecoration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.content.Intent.EXTRA_TITLE;

/**
 * Author: Yuri.zheng<br>
 * Date: 2016/10/8<br>
 * Email: 497393102@qq.com<br>
 * 暂时不使用该文件管理
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

    private final String ROOT_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();

    private TextView mTitleView;
    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;
    private DefaultItemAnimator mItemAnimator;

    private IFileContract.Presenter mPresenter;
    private Handler mHandler;

    private String mSuffix;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_manager_layout);
        new FilePresenter(this);
        mHandler = new Handler(getMainLooper());

        String title = getIntent().getStringExtra(EXTRA_TITLE);
        if (TextUtils.isEmpty(title)) {
            title = getString(R.string.file_manager_title);
        }
        String hostoryPath = getIntent().getStringExtra(RECORD_HOSTORY);
        if (TextUtils.isEmpty(hostoryPath)) {
            hostoryPath = ROOT_PATH;
        }
        if (!new File(hostoryPath).canRead()) {
            Toast.makeText(this, R.string.file_manager_can_not_read, Toast.LENGTH_LONG).show();
            finish();
        } else {
            mSuffix = getIntent().getStringExtra(TYPE);

            mTitleView = (TextView) findViewById(R.id.title);
            mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);

            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            mRecyclerView.setItemAnimator(mItemAnimator = new DefaultItemAnimator());
            mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
            mRecyclerView.setAdapter(mAdapter = new MyAdapter());
            mRecyclerView.setOnTouchListener(mRecyclerTouch);

            float size = ZYJPreferencesUtils.getFloat(this, SettingsConstant.TOOLBAR_TITLE_SIZE);
            if (size > 0) {
                mTitleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
            }
            mTitleView.setText(title);
            findViewById(R.id.back).setOnClickListener((v) -> {
                onBackPressed();
            });

            mAdapter.setDatas(mPresenter.getFileNameByPath(hostoryPath, mSuffix));
        }
    }

    @Override
    public void setPresenter(IFileContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onBackPressed() {
        boolean finish = true;
        String currentPath = mPresenter.getCurrentPath();
        ZYJUtils.logD(getClass(), "Current: " + currentPath);
        if (TextUtils.isEmpty(currentPath) || ROOT_PATH.contains(currentPath)) {
            finish = true;
        } else {
            try {
                String prePath = currentPath.substring(0, currentPath.lastIndexOf("/"));
                ZYJUtils.logD(getClass(), "Pre Path: " + prePath);
                mAdapter.setDatas(mPresenter.getFileNameByPath(prePath, mSuffix));
                finish = false;
            } catch (Exception e) {
                ZYJUtils.logW(getClass(), e.getLocalizedMessage());
                finish = true;
            }
        }
        if (finish) {
            setResult(Activity.RESULT_CANCELED);
            super.onBackPressed();
        }
    }

    @Override
    public void notifyPathChanged() {

    }

    private View.OnTouchListener mRecyclerTouch = (v, event) -> {
        return false;
    };


    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.VHolder> {

        /**
         * The file's absolute path
         */
        private List<String> datas = new ArrayList<>();

        public List<String> getDatas() {
            return datas;
        }

        public void setDatas(List<String> datas) {
            if (datas != null) {
                this.datas = datas;
                notifyDataSetChanged();
            }
        }

        @Override
        public VHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new VHolder(new TextView(FileManagerActivity.this));
        }

        @Override
        public void onBindViewHolder(VHolder holder, int position) {
            String path = datas.get(position);
            String fileName = path;
            if (path.contains("/")) {
                fileName = path.substring(path.lastIndexOf("/") + 1, path.length());
            }
            holder.text.setTag(path);
            holder.text.setText(fileName);
        }

        @Override
        public int getItemCount() {
            return datas != null ? datas.size() : 0;
        }

        class VHolder extends RecyclerView.ViewHolder {

            TextView text;

            public VHolder(View itemView) {
                super(itemView);
                text = (TextView) itemView;
                text.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                Resources r = FileManagerActivity.this.getResources();
                int l_r = r.getDimensionPixelOffset(R.dimen.file_manager_item_left_right);
                int t_b = r.getDimensionPixelOffset(R.dimen.file_manager_item_top_bottom);
                text.setPadding(l_r, t_b, l_r, t_b);
                text.setOnClickListener((v) -> {
                    List<String> datas = mPresenter.getFileNameByPath(v.getTag().toString(), mSuffix);
                    if (datas == null) {
                        if (!TextUtils.isEmpty(mSuffix)) {
                            Intent data = new Intent();
                            data.setData(Uri.parse(v.getTag().toString()));
                            setResult(Activity.RESULT_OK, data);
                            finish();
                        }
                    } else {
                        mAdapter.setDatas(datas);
                    }
                });
            }
        }

    }
}
