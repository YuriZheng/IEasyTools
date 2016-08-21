package com.zyj.ieasytools.act.mainActivity.childViews.webView;

import com.zyj.ieasytools.R;
import com.zyj.ieasytools.act.mainActivity.MainActivity;
import com.zyj.ieasytools.act.mainActivity.childViews.BaseMainView;
import com.zyj.ieasytools.library.encrypt.PasswordEntry;

/**
 * Author: Yuri.zheng<br>
 * Date: 8/21/16<br>
 * Email: 497393102@qq.com<br>
 */
public class WebView extends BaseMainView<IWebContract.Presenter> implements IWebContract.View {

    public WebView(MainActivity context) {
        super(context, R.layout.group_web_layout);
        mCategory = PasswordEntry.CATEGORY_WEB;
    }
}
