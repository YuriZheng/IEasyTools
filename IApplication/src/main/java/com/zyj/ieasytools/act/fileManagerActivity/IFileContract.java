package com.zyj.ieasytools.act.fileManagerActivity;

import com.zyj.ieasytools.act.IBasePresenter;
import com.zyj.ieasytools.act.IBaseView;

import java.util.List;

/**
 * Author: Yuri.zheng<br>
 * Date: 8/21/16<br>
 * Email: 497393102@qq.com<br>
 */
public interface IFileContract {

    interface View extends IBaseView<Presenter> {

        void notifyPathChanged();

    }

    interface Presenter extends IBasePresenter {

        List<String> getFileNameByPath(String rootPath, String suffix);

        String getCurrentPath();

    }

}
