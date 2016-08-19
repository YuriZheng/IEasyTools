package com.zyj.ieasytools.act.mainActivity.childViews.webView;

import com.zyj.ieasytools.R;
import com.zyj.ieasytools.act.mainActivity.MainActivity;
import com.zyj.ieasytools.act.mainActivity.childViews.BaseMainView;
import com.zyj.ieasytools.library.encrypt.PasswordEntry;

/**
 * Created by yuri.zheng on 2016/5/24.
 */
public class WebView extends BaseMainView<IWebContract.Presenter> implements IWebContract.View {

    public WebView(MainActivity context) {
        super(context, R.layout.group_web_layout);
        mCategory = PasswordEntry.CATEGORY_WEB;
    }
}
