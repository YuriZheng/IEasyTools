package com.zyj.ieasytools.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.zyj.ieasytools.R;

/**
 * Created by yuri.zheng on 2016/5/24.
 */
public class GroupEmailView extends GroupView {

    public GroupEmailView(Context context) {
        super(context);
    }

    @Override
    protected View init(Context context) {
        return LayoutInflater.from(context).inflate(R.layout.group_email_layout, null);
    }
}
