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
import android.widget.Toast;

import com.zyj.ieasytools.R;
import com.zyj.ieasytools.act.BaseActivity;
import com.zyj.ieasytools.data.DatabaseUtils;
import com.zyj.ieasytools.data.SettingsConstant;
import com.zyj.ieasytools.library.db.DatabaseColumns;
import com.zyj.ieasytools.library.utils.ZYJPreferencesUtils;
import com.zyj.ieasytools.library.utils.ZYJUtils;
import com.zyj.ieasytools.library.utils.ZYJVersion;
import com.zyj.ieasytools.views.DividerItemDecoration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.zyj.ieasytools.act.mainActivity.MainPresenter.BROADCAST_SWITCH_DATABASE;
import static com.zyj.ieasytools.act.mainActivity.MainPresenter.BROADCAST_SWITCH_DATABASE_NAME;
import static com.zyj.ieasytools.act.mainActivity.MainPresenter.BROADCAST_SWITCH_DATABASE_PATH;
import static com.zyj.ieasytools.act.mainActivity.MainPresenter.BROADCAST_SWITCH_DATABASE_PD;
import static com.zyj.ieasytools.library.db.DatabaseColumns.DATABASE_FILE_SUFFIX;

/**
 * Author: Yuri.zheng<br>
 * Date: 09/10/2016<br>
 * Email: 497393102@qq.com<br>
 */

public class OtherDBActivity extends BaseActivity implements IOtherDBContract.View {

    /**
     * Switch success
     */
    public static final int SWITCH_RESULT_SUCCESS = 0xa;
    /**
     * The database name or path or password is null
     */
    public static final int SWITCH_RESULT_NULL = 0xb;
    /**
     * Current database is the same as tager database
     */
    public static final int SWITCH_RESULT_RECOPY = 0xc;
    /**
     * The database open exception
     */
    public static final int SWITCH_RESULT_EXCEPTION = 0xd;
    /**
     * The database password is wrong
     */
    public static final int SWITCH_RESULT_PASSWORD = 0xe;
    /**
     * Unkonw error
     */
    public static final int SWITCH_RESULT_UNKNOW = 0xff;

    /**
     * Cloase the progress dialog
     */
    public static final String CLOASE_DIALOG = "_c_d";
    /**
     * The switch result, ture mean success
     */
    public static final String SWITCH_RESULT = "_s_r";

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
            int result = intent.getIntExtra(SWITCH_RESULT, SWITCH_RESULT_UNKNOW);
            int message = -1;
            switch (result) {
                case SWITCH_RESULT_SUCCESS:
                    message = R.string.other_db_switch_success;
                    break;
                case SWITCH_RESULT_NULL:
                    message = R.string.other_db_null_error;
                    break;
                case SWITCH_RESULT_RECOPY:
                    message = R.string.other_db_switch_same_db;
                    break;
                case SWITCH_RESULT_EXCEPTION:
                    message = R.string.database_open_file_exception;
                    break;
                case SWITCH_RESULT_PASSWORD:
                    message = R.string.database_open_password_wrong;
                    break;
                default:
                    message = R.string.database_open_open_unknow;
                    break;
            }
            if (message > 0) {
                Toast.makeText(OtherDBActivity.this, message, Toast.LENGTH_LONG).show();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_db_layout);
        new OtherDBPresenter(this);
        getPackageName();
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

        List<String> list = new ArrayList<>();
        String our = mPresenter.getOurDatabasePath(this);
        if (!TextUtils.isEmpty(our)) {
            list.add(our);
        }
        list.addAll(mPresenter.getDatabasePathsBesidesCurrent(this));
        mAdapter.setDatas(list);
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

        Intent intent = new Intent(BROADCAST_SWITCH_DATABASE);
        intent.putExtra(BROADCAST_SWITCH_DATABASE_PATH, path);
        intent.putExtra(BROADCAST_SWITCH_DATABASE_NAME, name);
        // TODO: 10/10/2016 需要密码的 
        intent.putExtra(BROADCAST_SWITCH_DATABASE_PD, "123");
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
            holder.text.setTag(R.id.other_list_item_path_tag, path);
            if (!fileName.equals(DatabaseColumns.EncryptColumns.DATABASE_NAME)) {
                fileName = fileName.substring(0, fileName.lastIndexOf("."));
                fileName = DatabaseUtils.getDefaultEncryptEntry(OtherDBActivity.this).decrypt(fileName, ZYJVersion.getCurrentVersion());

                holder.text.setTag(R.id.other_list_item_name_tag, fileName + "." + DATABASE_FILE_SUFFIX);
                holder.text.setText(fileName + "." + DATABASE_FILE_SUFFIX);
            } else {
                holder.text.setTag(R.id.other_list_item_name_tag, fileName);
                holder.text.setText(R.string.app_name);
            }
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