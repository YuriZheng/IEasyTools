package com.zyj.ieasytools.act.settingActivity;

import android.content.Context;

import com.zyj.ieasytools.act.IBasePresenter;
import com.zyj.ieasytools.act.IBaseView;

/**
 * Author: Yuri.zheng<br>
 * Date: 8/21/16<br>
 * Email: 497393102@qq.com<br>
 */
public interface ISettingContract {

    interface View extends IBaseView<Presenter> {

        Context getContext();

    }

    interface Presenter extends IBasePresenter {

        long getTimeOut();

        void setTimeOut(long time);

    }

}
