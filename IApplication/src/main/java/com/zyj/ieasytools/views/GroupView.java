package com.zyj.ieasytools.views;

import android.content.Context;
import android.os.Handler;
import android.view.View;

/**
 * Created by yuri.zheng on 2016/5/24.
 */
public abstract class GroupView {

    protected Handler mHandler;

    protected View mView;

    protected Context mContext;

    public GroupView(Context context) {
        mContext = context;
        mView = init(context);
        mHandler = new Handler(context.getMainLooper());
    }

    protected abstract View init(Context context);

    public View getMainView() {
        return mView;
    }

}
