package com.zyj.ieasytools.act.mainActivity.emailView;

import com.zyj.ieasytools.act.mainActivity.BaseMainPresenter;
import com.zyj.ieasytools.data.IData;

/**
 * Created by ZYJ on 8/13/16.
 */
public class EmailPresenter extends BaseMainPresenter<IData, IEmailContract.View> implements IEmailContract.Presenter {

    public EmailPresenter(IData data, IEmailContract.View view) {
        super(data, view);
        view.setPresenter(this);
    }
}
