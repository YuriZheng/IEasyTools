package com.zyj.ieasytools.act.otherDatabaseActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zyj.ieasytools.R;
import com.zyj.ieasytools.act.BaseActivity;
import com.zyj.ieasytools.data.DatabaseUtils;
import com.zyj.ieasytools.data.SettingsConstant;
import com.zyj.ieasytools.library.utils.ZYJPreferencesUtils;
import com.zyj.ieasytools.library.utils.ZYJUtils;
import com.zyj.ieasytools.library.utils.ZYJVersion;
import com.zyj.ieasytools.views.DividerItemDecoration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.zyj.ieasytools.act.mainActivity.MainActivity.ACTION_SWITCH_BROADCAST;
import static com.zyj.ieasytools.act.mainActivity.MainActivity.ACTION_SWITCH_NAME;
import static com.zyj.ieasytools.act.mainActivity.MainActivity.ACTION_SWITCH_PATH;
import static com.zyj.ieasytools.library.db.DatabaseColumns.DATABASE_FILE_SUFFIX;

/**
 * Author: Yuri.zheng<br>
 * Date: 09/10/2016<br>
 * Email: 497393102@qq.com<br>
 */

public class OtherDBActivity extends BaseActivity implements IOtherDBContract.View {

    public static final String CLOASE_DIALOG = "_c_d";

    private TextView mTitleView;
    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;

    private ProgressDialog mProgressBar;

    private IOtherDBContract.Presenter mPresenter;

    private BroadcastReceiver mCloseProgressDialog = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (mProgressBar != null && mProgressBar.isShowing()) {
                mProgressBar.dismiss();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_db_layout);
        new OtherDBPresenter(this);

        mTitleView = (TextView) findViewById(R.id.title);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.setAdapter(mAdapter = new MyAdapter());

        mTitleView.setText(R.string.other_db_title);
        float size = ZYJPreferencesUtils.getFloat(this, SettingsConstant.TOOLBAR_TITLE_SIZE);
        if (size > 0) {
            mTitleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
        }
        findViewById(R.id.back).setOnClickListener((v) -> {
            onBackPressed();
        });

        mAdapter.setDatas(mPresenter.getDatabasePathsBesidesCurrent(this));
        LocalBroadcastManager.getInstance(this).registerReceiver(mCloseProgressDialog, new IntentFilter(CLOASE_DIALOG));
    }

    @Override
    public void setPresenter(IOtherDBContract.Presenter presenter) {
        mPresenter = presenter;
    }

    private void showConfirmDialog(final String name, final String path) {
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(path) || !path.contains("/")) {
            ZYJUtils.logW(getClass(), "Path is error!!!");
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle(R.string.other_db_dialog_switch)
                    .setMessage(R.string.other_db_dialog_message)
                    .setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                        showSwitchProgressDialog(name, path);
                        dialog.dismiss();
                    });
            builder.create().show();
        }
    }

    private void showSwitchProgressDialog(final String name, final String path) {
        mProgressBar = new ProgressDialog(this);
        mProgressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressBar.setCancelable(false);
        mProgressBar.setTitle(R.string.other_db_dialog_switch);
        mProgressBar.setMessage(getString(R.string.other_db_dialog_switching));
        mProgressBar.show();

        Intent intent = new Intent(ACTION_SWITCH_BROADCAST);
        intent.putExtra(ACTION_SWITCH_PATH, path);
        intent.putExtra(ACTION_SWITCH_NAME, name);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mCloseProgressDialog);
    }

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
        public MyAdapter.VHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyAdapter.VHolder(new TextView(OtherDBActivity.this));
        }

        @Override
        public void onBindViewHolder(MyAdapter.VHolder holder, int position) {
            String path = datas.get(position);
            File file = new File(path);
            String fileName = file.getName();
            fileName = fileName.substring(0, fileName.lastIndexOf("."));
            fileName = DatabaseUtils.getDefaultEncryptEntry(OtherDBActivity.this).decrypt(fileName, ZYJVersion.getCurrentVersion());
            holder.text.setTag(R.id.other_list_item_path_tag, path);
            holder.text.setTag(R.id.other_list_item_name_tag, fileName + "." + DATABASE_FILE_SUFFIX);
            holder.text.setText(fileName + "." + DATABASE_FILE_SUFFIX);
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
                Resources r = OtherDBActivity.this.getResources();
                int l_r = r.getDimensionPixelOffset(R.dimen.other_db_item_left_right);
                int t_b = r.getDimensionPixelOffset(R.dimen.other_db_item_top_bottom);
                text.setPadding(l_r, t_b, l_r, t_b);
                text.setOnClickListener((v) -> {
                    String name = text.getTag(R.id.other_list_item_name_tag).toString();
                    String path = text.getTag(R.id.other_list_item_path_tag).toString();
                    if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(path)) {
                        showConfirmDialog(name, path);
                    } else {
                        ZYJUtils.logW(getClass(), "The tag is null");
                    }
                });
            }
        }

    }

}