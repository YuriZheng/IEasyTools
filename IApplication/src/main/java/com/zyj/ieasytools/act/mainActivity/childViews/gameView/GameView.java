package com.zyj.ieasytools.act.mainActivity.childViews.gameView;

import com.zyj.ieasytools.R;
import com.zyj.ieasytools.act.mainActivity.childViews.BaseMainView;
import com.zyj.ieasytools.act.mainActivity.MainActivity;
import com.zyj.ieasytools.library.encrypt.PasswordEntry;

/**
 * Created by yuri.zheng on 2016/5/24.
 */
public class GameView extends BaseMainView<IGameContract.Presenter> implements IGameContract.View {

    public GameView(MainActivity context) {
        super(context, R.layout.group_game_layout);
        mCategory = PasswordEntry.CATEGORY_GAME;
    }
}
