package com.zyj.ieasytools.act.mainActivity.webView;

import com.zyj.ieasytools.R;
import com.zyj.ieasytools.act.mainActivity.BaseMainView;
import com.zyj.ieasytools.act.mainActivity.MainActivity;

/**
 * Created by yuri.zheng on 2016/5/24.
 */
public class WebView extends BaseMainView<IWebContract.Presenter> implements IWebContract.View {

    public WebView(MainActivity context) {
        super(context, R.layout.group_web_layout);
    }


}
