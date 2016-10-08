package com.zyj.ieasytools.act.fileManagerActivity;

/**
 * Author: Yuri.zheng<br>
 * Date: 2016/10/8<br>
 * Email: 497393102@qq.com<br>
 */
public class FilePresenter implements IFileContract.Presenter {

    private final IFileContract.View mView;

    public FilePresenter(IFileContract.View mView) {
        this.mView = mView;
        mView.setPresenter(this);
    }
}
