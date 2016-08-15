package com.zyj.ieasytools.act.mainActivity.gameView;

import com.zyj.ieasytools.R;
import com.zyj.ieasytools.act.mainActivity.BaseMainView;
import com.zyj.ieasytools.act.mainActivity.MainActivity;

/**
 * Created by yuri.zheng on 2016/5/24.
 */
public class GameView extends BaseMainView<IGameContract.Presenter> {

    public GameView(MainActivity context) {
        super(context, R.layout.group_game_layout);
    }

    @Override
    public void setPresenter(IGameContract.Presenter presenter) {

    }
}
