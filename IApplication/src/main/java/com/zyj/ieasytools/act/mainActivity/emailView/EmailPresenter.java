package com.zyj.ieasytools.act.mainActivity.emailView;

import com.zyj.ieasytools.act.mainActivity.BaseMainPresenter;

/**
 * Created by ZYJ on 8/13/16.
 */
public class EmailPresenter extends BaseMainPresenter<IEmailContract.View> implements IEmailContract.Presenter {

    public EmailPresenter(IEmailContract.View view) {
        super(view);
        view.setPresenter(this);
    }
}
