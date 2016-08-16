package com.zyj.ieasytools.act.mainActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by yuri.zheng on 2016/5/24.
 */
public abstract class BaseMainView<P extends IMainPresenter> {

    protected ViewGroup mViewGroup;
    protected MainActivity mContext;

    protected P mPresenter;

    public BaseMainView(MainActivity context, int layoutId) {
        this.mContext = context;
        mViewGroup = (ViewGroup) LayoutInflater.from(context).inflate(layoutId, null);
    }

    public View getView() {
        return mViewGroup;
    }

    public void setPresenter(P presenter) {
        mPresenter = presenter;
    }

}
