package com.zyj.ieasytools.act.mainActivity.childViews.gameView;

import com.zyj.ieasytools.act.mainActivity.childViews.BaseMainPresenter;

/**
 * Created by ZYJ on 8/13/16.
 */
public class GamePresenter extends BaseMainPresenter<IGameContract.View> implements IGameContract.Presenter {

    public GamePresenter(IGameContract.View view) {
        super(view);
        view.setPresenter(this);
    }
}
