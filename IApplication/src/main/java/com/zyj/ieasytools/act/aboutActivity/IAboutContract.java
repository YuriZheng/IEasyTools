package com.zyj.ieasytools.act.aboutActivity;

import com.zyj.ieasytools.act.IBasePresenter;
import com.zyj.ieasytools.act.IBaseView;

/**
 * 作者：yuri.zheng<br>
 * 时间：2016/8/20<br>
 */
public interface IAboutContract {

    interface View extends IBaseView<Presenter> {

        void setToolbarTitle(String title);

        void setToolbarSubtitle(String title);

    }

    interface Presenter extends IBasePresenter {


    }

}
