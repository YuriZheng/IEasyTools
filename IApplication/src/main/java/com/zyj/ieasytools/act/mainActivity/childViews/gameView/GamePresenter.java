package com.zyj.ieasytools.act.mainActivity.childViews.gameView;

import com.zyj.ieasytools.act.mainActivity.childViews.BaseMainPresenter;

/**
 * Author: Yuri.zheng<br>
 * Date: 8/21/16<br>
 * Email: 497393102@qq.com<br>
 */
public class GamePresenter extends BaseMainPresenter<IGameContract.View> implements IGameContract.Presenter {

    public GamePresenter(IGameContract.View view) {
        super(view);
        view.setPresenter(this);
    }
}
