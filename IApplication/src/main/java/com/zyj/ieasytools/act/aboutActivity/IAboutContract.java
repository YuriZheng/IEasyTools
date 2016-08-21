package com.zyj.ieasytools.act.aboutActivity;

import com.zyj.ieasytools.act.IBasePresenter;
import com.zyj.ieasytools.act.IBaseView;

/**
 * Author: Yuri.zheng<br>
 * Date: 8/21/16<br>
 * Email: 497393102@qq.com<br>
 */
public interface IAboutContract {

    interface View extends IBaseView<Presenter> {

        void setToolbarTitle(String title);

        void setToolbarSubtitle(String title);

    }

    interface Presenter extends IBasePresenter {


    }

}
