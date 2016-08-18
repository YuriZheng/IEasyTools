package com.zyj.ieasytools.act.mainActivity.childViews.emailView;

import com.zyj.ieasytools.act.mainActivity.childViews.BaseMainPresenter;

/**
 * Created by ZYJ on 8/13/16.
 */
public class EmailPresenter extends BaseMainPresenter<IEmailContract.View> implements IEmailContract.Presenter {

    public EmailPresenter(IEmailContract.View view) {
        super(view);
        view.setPresenter(this);
    }
}
