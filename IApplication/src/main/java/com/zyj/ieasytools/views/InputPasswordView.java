package com.zyj.ieasytools.views;

import android.content.Context;
import android.support.design.widget.TextInputEditText;
import android.util.AttributeSet;
import android.view.ViewGroup;

/**
 * Created by yuri.zheng on 2016/5/18.
 */
public class InputPasswordView extends ViewGroup {

    private TextInputEditText mInputText;

    public InputPasswordView(Context context) {
        this(context, null);
    }

    public InputPasswordView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public InputPasswordView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, -1);
    }

    public InputPasswordView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }
}
