package com.zyj.ieasytools.act.otherDatabaseActivity;

import android.content.Context;

import com.zyj.ieasytools.act.IBasePresenter;
import com.zyj.ieasytools.act.IBaseView;

import java.util.List;

/**
 * Author: Yuri.zheng<br>
 * Date: 09/10/2016<br>
 * Email: 497393102@qq.com<br>
 */

public interface IOtherDBContract {

    interface View extends IBaseView<Presenter> {


    }

    interface Presenter extends IBasePresenter {

        List<String> getDatabasePathsBesidesCurrent(Context context);

    }

}
