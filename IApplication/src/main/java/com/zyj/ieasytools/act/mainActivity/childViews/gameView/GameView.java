package com.zyj.ieasytools.act.mainActivity.childViews.gameView;

import com.zyj.ieasytools.R;
import com.zyj.ieasytools.act.mainActivity.childViews.BaseMainView;
import com.zyj.ieasytools.act.mainActivity.MainActivity;
import com.zyj.ieasytools.library.encrypt.PasswordEntry;

/**
 * Author: Yuri.zheng<br>
 * Date: 8/21/16<br>
 * Email: 497393102@qq.com<br>
 */
public class GameView extends BaseMainView<IGameContract.Presenter> implements IGameContract.View {

    public GameView(MainActivity context) {
        super(context, R.layout.group_game_layout);
        mCategory = PasswordEntry.CATEGORY_GAME;
        new GamePresenter(this);
    }
}
