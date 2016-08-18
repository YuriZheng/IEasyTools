package com.zyj.ieasytools.act.mainActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zyj.ieasytools.act.IBasePresenter;

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

    /**
     * Get the root View
     *
     * @return return root view subclass {@link ViewGroup}
     */
    public View getView() {
        return mViewGroup;
    }

    public View findViewById(int id) {
        return mViewGroup.findViewById(id);
    }

    /**
     * {@link com.zyj.ieasytools.act.IBaseView#setPresenter(IBasePresenter)}
     */
    public void setPresenter(P presenter) {
        mPresenter = presenter;
    }

    public abstract void verifyEnterPasswordSuccess();

    /**
     * Call this method When the main activity switch view
     */
    public abstract void onReload();

}
