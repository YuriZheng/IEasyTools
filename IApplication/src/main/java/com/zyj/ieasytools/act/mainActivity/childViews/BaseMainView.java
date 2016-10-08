package com.zyj.ieasytools.act.mainActivity.childViews;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zyj.ieasytools.R;
import com.zyj.ieasytools.act.mainActivity.MainActivity;
import com.zyj.ieasytools.library.encrypt.PasswordEntry;
import com.zyj.ieasytools.library.utils.ZYJUtils;
import com.zyj.ieasytools.views.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Yuri.zheng<br>
 * Date: 8/21/16<br>
 * Email: 497393102@qq.com<br>
 */
public abstract class BaseMainView<P extends IViewsPresenter> {

    protected ViewGroup mViewGroup;
    protected MainActivity mContext;
    protected RecyclerView mRecyclerView;
    protected DataAdapter mAdapter;

    protected String mCategory;

    protected P mPresenter;

    public BaseMainView(MainActivity context, int layoutId) {
        this.mContext = context;
        mViewGroup = (ViewGroup) LayoutInflater.from(context).inflate(layoutId, null);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.setAdapter(mAdapter = new DataAdapter());
    }

    /**
     * {@link IViewsView#getView()}
     */
    public View getView() {
        return mViewGroup;
    }

    /**
     * {@link IViewsView#destory()}
     */
    public void destory() {
        mPresenter.destory();
    }

    /**
     * {@link IViewsView#setDatas(List)}
     */
    public void setDatas(List<PasswordEntry> list) {
        mAdapter.setList(list);
        mAdapter.notifyDataSetChanged();
    }

    protected View findViewById(int id) {
        return mViewGroup.findViewById(id);
    }


    /**
     * {@link IViewsView#getContext()}
     */
    public Context getContext() {
        return mContext;
    }

    /**
     * {@link com.zyj.ieasytools.act.IBaseView#setPresenter(com.zyj.ieasytools.act.IBasePresenter)}
     */
    public void setPresenter(P presenter) {
        mPresenter = presenter;
    }

    /**
     * In the main activity called
     */
    public void verifyEnterPasswordSuccess() {
        mPresenter.requestEntryByCategory(mCategory);
    }

    /**
     * {@link IViewsView#onReload()}
     */
    public void onReload() {
        mPresenter.requestEntryByCategory(mCategory);
    }


    class DataAdapter extends RecyclerView.Adapter<DataAdapter.VHolder> {

        List<PasswordEntry> list = new ArrayList<>();

        public void setList(List<PasswordEntry> l) {
            this.list = l;
        }

        @Override
        public VHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new VHolder(LayoutInflater.from(mContext).inflate(R.layout.group_main_item_layout, parent, false));
        }

        @Override
        public void onBindViewHolder(VHolder holder, int position) {
            PasswordEntry entry = list.get(position);
            holder.title.setText(entry.p_title);
            holder.user.setText(entry.p_username);
            ZYJUtils.logD(getClass(), entry.toString());
            if (!TextUtils.isEmpty(entry.p_description)) {
                holder.description.setText(entry.p_description);
            } else {
                holder.description.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(entry.p_remarks)) {
                holder.mark.setText(entry.p_remarks);
            } else {
                holder.mark.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return list != null ? list.size() : 0;
        }

        class VHolder extends RecyclerView.ViewHolder {

            TextView title;
            TextView user;
            TextView description;
            TextView mark;

            public VHolder(View itemView) {
                super(itemView);
                title = (TextView) itemView.findViewById(R.id.title);
                user = (TextView) itemView.findViewById(R.id.user);
                description = (TextView) itemView.findViewById(R.id.description);
                mark = (TextView) itemView.findViewById(R.id.mark);
            }
        }
    }

}
