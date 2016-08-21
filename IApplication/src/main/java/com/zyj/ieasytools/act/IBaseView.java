package com.zyj.ieasytools.act;

/**
 * Author: Yuri.zheng<br>
 * Date: 8/21/16<br>
 * Email: 497393102@qq.com<br>
 */
public interface IBaseView<T extends IBasePresenter> {

    void setPresenter(T presenter);

}
