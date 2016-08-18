package com.zyj.ieasytools.act.mainActivity.webView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zyj.ieasytools.R;
import com.zyj.ieasytools.act.mainActivity.BaseMainView;
import com.zyj.ieasytools.act.mainActivity.MainActivity;
import com.zyj.ieasytools.library.encrypt.PasswordEntry;
import com.zyj.ieasytools.library.utils.ZYJUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuri.zheng on 2016/5/24.
 */
public class WebView extends BaseMainView<IWebContract.Presenter> implements IWebContract.View {

    private RecyclerView mRecyclerView;
    private DataAdapter mAdapter;

    public WebView(MainActivity context) {
        super(context, R.layout.group_web_layout);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.setAdapter(mAdapter = new DataAdapter());
    }

    @Override
    public void setDatas(List<PasswordEntry> list) {
        mAdapter.setList(list);
        mAdapter.notifyDataSetChanged();
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

    @Override
    public Context getContext() {
        return mContext;
    }

    @Override
    public void verifyEnterPasswordSuccess() {
        mPresenter.requestEntryByCategory();
    }

    @Override
    public void onReload() {
        mPresenter.requestEntryByCategory();
    }


    class DividerItemDecoration extends RecyclerView.ItemDecoration {

        private final int[] ATTRS = new int[]{
                android.R.attr.listDivider
        };

        public static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;

        public static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;

        private Drawable mDivider;

        private int mOrientation;

        public DividerItemDecoration(Context context, int orientation) {
            final TypedArray a = context.obtainStyledAttributes(ATTRS);
            mDivider = a.getDrawable(0);
            a.recycle();
            setOrientation(orientation);
        }

        public void setOrientation(int orientation) {
            if (orientation != HORIZONTAL_LIST && orientation != VERTICAL_LIST) {
                throw new IllegalArgumentException("invalid orientation");
            }
            mOrientation = orientation;
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent) {
            if (mOrientation == VERTICAL_LIST) {
                drawVertical(c, parent);
            } else {
                drawHorizontal(c, parent);
            }
        }


        public void drawVertical(Canvas c, RecyclerView parent) {
            final int left = parent.getPaddingLeft();
            final int right = parent.getWidth() - parent.getPaddingRight();

            final int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = parent.getChildAt(i);
                android.support.v7.widget.RecyclerView v = new android.support.v7.widget.RecyclerView(parent.getContext());
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                        .getLayoutParams();
                final int top = child.getBottom() + params.bottomMargin;
                final int bottom = top + mDivider.getIntrinsicHeight();
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }

        public void drawHorizontal(Canvas c, RecyclerView parent) {
            final int top = parent.getPaddingTop();
            final int bottom = parent.getHeight() - parent.getPaddingBottom();

            final int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = parent.getChildAt(i);
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                        .getLayoutParams();
                final int left = child.getRight() + params.rightMargin;
                final int right = left + mDivider.getIntrinsicHeight();
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }

        @Override
        public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {
            if (mOrientation == VERTICAL_LIST) {
                outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
            } else {
                outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
            }
        }
    }
}
