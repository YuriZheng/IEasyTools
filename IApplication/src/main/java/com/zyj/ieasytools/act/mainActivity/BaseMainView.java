package com.zyj.ieasytools.act.mainActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by yuri.zheng on 2016/5/24.
 */
public abstract class BaseMainView<T extends IMainPresenter> implements IMainView<T> {

    protected ViewGroup mViewGroup;
    protected MainActivity mContext;

    public BaseMainView(MainActivity context, int layoutId) {
        this.mContext = context;
        mViewGroup = (ViewGroup) LayoutInflater.from(context).inflate(layoutId, null);
    }

    @Override
    public View getView() {
        return mViewGroup;
    }

}
