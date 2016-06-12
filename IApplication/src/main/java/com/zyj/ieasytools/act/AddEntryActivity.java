package com.zyj.ieasytools.act;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.transition.Explode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zyj.ieasytools.R;
import com.zyj.ieasytools.library.encrypt.PasswordEntry;
import com.zyj.ieasytools.library.utils.ZYJUtils;
import com.zyj.ieasytools.views.PagerAdapter;
import com.zyj.ieasytools.views.VerticalViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZYJ on 6/11/16.
 */
public class AddEntryActivity extends BaseActivity {

    public static final String PASSWORD_ENTRY = "entry_style";

    private Toolbar mToolbar;
    private View mTitleView;

    private VerticalViewPager mViewPager;

    // entry category
    private String mCategory;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setEnterTransition(new Explode());
        getWindow().setExitTransition(new Explode());

        setContentView(R.layout.add_entry_layout);

        mToolbar = getViewById(R.id.toolbar);

        mCategory = getIntent().getStringExtra(PASSWORD_ENTRY);
        if (TextUtils.isEmpty(mCategory)) {
            ZYJUtils.logD(TAG, "Category is null");
            finish();
        }
        setTitle();
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.mipmap.back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mTitleView = getToolbarChildView(mToolbar, "mTitleTextView", View.class);
        if (mTitleView != null) {
            mTitleView.setTransitionName("share");
        }

        initViewPager();

    }

    private void initViewPager() {
        mViewPager = getViewById(R.id.vertical_viewpager);
        List<View> datas = new ArrayList<View>();
        datas.add(LayoutInflater.from(this).inflate(R.layout.add_one_layout, null));
        datas.add(LayoutInflater.from(this).inflate(R.layout.add_second_layout, null));
        datas.add(LayoutInflater.from(this).inflate(R.layout.add_third_layout, null));
        MyAdapter adapter = new MyAdapter(datas);
        mViewPager.setAdapter(adapter);
        mViewPager.setOnPageChangeListener(mPageListener);
    }

    private void setTitle() {
        if (mCategory.equals(PasswordEntry.CATEGORY_WEB)) {
            mToolbar.setTitle(R.string.password_catrgory_web);
        } else if (mCategory.equals(PasswordEntry.CATEGORY_EMAIL)) {
            mToolbar.setTitle(R.string.password_catrgory_email);
        } else if (mCategory.equals(PasswordEntry.CATEGORY_WALLET)) {
            mToolbar.setTitle(R.string.password_catrgory_wallet);
        } else if (mCategory.equals(PasswordEntry.CATEGORY_APP)) {
            mToolbar.setTitle(R.string.password_catrgory_app);
        } else if (mCategory.equals(PasswordEntry.CATEGORY_GAME)) {
            mToolbar.setTitle(R.string.password_catrgory_game);
        } else if (mCategory.equals(PasswordEntry.CATEGORY_OTHER)) {
            mToolbar.setTitle(R.string.password_catrgory_other);
        }
    }

    private VerticalViewPager.OnPageChangeListener mPageListener = new VerticalViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    public void onViewClick(View v) {
        switch (v.getId()) {
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public class MyAdapter extends PagerAdapter {

        List<View> datas = new ArrayList<View>();

        public MyAdapter(List<View> datas) {
            this.datas = datas;
        }

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = datas.get(position);
            container.addView(view);
            return view;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
