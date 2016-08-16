package com.zyj.ieasytools.act.mainActivity.gameView;

import com.zyj.ieasytools.act.mainActivity.BaseMainPresenter;
import com.zyj.ieasytools.data.IData;

/**
 * Created by ZYJ on 8/13/16.
 */
public class GamePresenter extends BaseMainPresenter<IData, IGameContract.View> implements IGameContract.Presenter {

    public GamePresenter(IData presenter, IGameContract.View view) {
        super(presenter, view);
        view.setPresenter(this);
    }
}
