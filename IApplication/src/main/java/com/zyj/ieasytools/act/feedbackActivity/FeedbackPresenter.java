package com.zyj.ieasytools.act.feedbackActivity;

/**
 * Author: Yuri.zheng<br>
 * Date: 8/21/16<br>
 * Email: 497393102@qq.com<br>
 */
public class FeedbackPresenter implements IFeedbackContract.Presenter {

    private final IFeedbackContract.View mView;

    public FeedbackPresenter(IFeedbackContract.View mView) {
        this.mView = mView;
        mView.setPresenter(this);
    }
}
