package com.zyj.ieasytools.act.mainActivity.emailView;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zyj.ieasytools.R;
import com.zyj.ieasytools.act.mainActivity.BaseMainView;

/**
 * Created by yuri.zheng on 2016/5/24.
 */
public class EmailView extends BaseMainView {

    public EmailView() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.group_email_layout, null);
        return view;
    }
}
