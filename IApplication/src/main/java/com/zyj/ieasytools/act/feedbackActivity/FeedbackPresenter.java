package com.zyj.ieasytools.act.feedbackActivity;

/**
 * 作者：yuri.zheng<br>
 * 时间：2016/8/20<br>
 */
public class FeedbackPresenter implements IFeedbackContract.Presenter {

    private final IFeedbackContract.View mView;

    public FeedbackPresenter(IFeedbackContract.View mView) {
        this.mView = mView;
        mView.setPresenter(this);
    }
}
