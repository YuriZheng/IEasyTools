package com.zyj.ieasytools.act.mainActivity.childViews.emailView;

import com.zyj.ieasytools.act.mainActivity.childViews.BaseMainPresenter;

/**
 * Author: Yuri.zheng<br>
 * Date: 8/21/16<br>
 * Email: 497393102@qq.com<br>
 */
public class EmailPresenter extends BaseMainPresenter<IEmailContract.View> implements IEmailContract.Presenter {

    public EmailPresenter(IEmailContract.View view) {
        super(view);
        view.setPresenter(this);
    }
}
