package com.zyj.ieasytools.act.addActivity;

import com.zyj.ieasytools.act.IBasePresenter;
import com.zyj.ieasytools.act.IBaseView;

/**
 * Author: Yuri.zheng<br>
 * Date: 8/21/16<br>
 * Email: 497393102@qq.com<br>
 */
public class IAddContract {

    interface View extends IBaseView<Presenter> {

        void showToast(int resId);

        void finishView();

    }

    interface Presenter extends IBasePresenter {

        void saveEntry(final String title, final String username, final String userPassword,
                       final String category, final String password, final String method);

        void setEntryAttributes(String email, String phone, String address, String description,
                                String q1, String a1, String q2, String a2, String q3, String a3, String mark);

    }

}
