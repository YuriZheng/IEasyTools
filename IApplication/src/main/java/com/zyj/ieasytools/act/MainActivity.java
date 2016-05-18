package com.zyj.ieasytools.act;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zyj.ieasytools.R;
import com.zyj.ieasytools.library.db.ZYJSettings;
import com.zyj.ieasytools.library.utils.ZYJUtils;
import com.zyj.ieasytools.library.views.MenuRevealView;
import com.zyj.ieasytools.utils.SettingsConstant;

public class MainActivity extends BaseActivity implements DrawerLayout.DrawerListener {

    private ViewGroup mMainViewLayout;

    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private ProgressBar mProgressBar;
    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationView mNavigationView;

    private View mMenuMore;
    private View mMenuAdd;
    private View mMenuSeach;
    private MenuRevealView mMenuLayout;

    private TextView mDebug;

    private ZYJSettings mSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSettings = ZYJSettings.getInstance(this);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);

        mProgressBar = (ProgressBar) findViewById(R.id.progress);
        mProgressBar.getIndeterminateDrawable().setTint(getResources().getColor(android.R.color.white));

        mMainViewLayout = (ViewGroup) findViewById(R.id.main_view_layout);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_toggle_text, R.string.drawer_toggle_text);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerLayout.addDrawerListener(this);

        mMenuMore = findViewById(R.id.menu_more);
        mMenuAdd = findViewById(R.id.menu_add);
        mMenuSeach = findViewById(R.id.menu_seach);

        mMenuLayout = (MenuRevealView) findViewById(R.id.menu_layout);

        mNavigationView = (NavigationView) findViewById(R.id.navigationView);
        mNavigationView.setNavigationItemSelectedListener(mNavigationClick);

        if (TextUtils.isEmpty(mSettings.getStringProperties(SettingsConstant.SETTINGS_VIEW_OTHER_DATABASE, null))) {
            mNavigationView.getMenu().removeItem(R.id.settings_view_other);
        }
        DrawerLayout.LayoutParams lp = (DrawerLayout.LayoutParams) mNavigationView.getLayoutParams();
        lp.width = ZYJUtils.getDisplayMetrics(this)[0] * 2 / 3;
        mNavigationView.setLayoutParams(lp);

        if (ZYJUtils.isFunctionDebug) {
            mDebug = (TextView) findViewById(R.id.debug);
            Object[] versions = ZYJUtils.getVersion(this);
            mDebug.setText("Name: " + versions[0].toString() + "\nCode: " + versions[1].toString());
            mDebug.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.mipmap.debug);
            mDebug.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void onMenuViewClick(View view) {
        switch (view.getId()) {
            case R.id.menu_more:
                if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                }
                if (mMenuLayout.isShowMenu()) {
                    mMenuLayout.hideMenu();
                } else {
                    mMenuLayout.showMenu();
                }
                break;
            case R.id.menu_add:
                break;
            case R.id.menu_seach:
                break;
            case R.id.main_test:
                break;
        }
    }

    private NavigationView.OnNavigationItemSelectedListener mNavigationClick = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.settings_settings:
                    break;
                case R.id.settings_view_other:
                    break;
                case R.id.settings_feedback:
                    break;
                case R.id.settings_about:
                    break;
                case R.id.settings_help:
                    break;
            }
            return false;
        }
    };

    public void onGroupMenuClick(View view) {
        switch (view.getId()) {
            case R.id.group_web:
                break;
            case R.id.group_email:
                break;
            case R.id.group_wallet:
                break;
            case R.id.group_app:
                break;
            case R.id.group_game:
                break;
            case R.id.group_other:
                break;
        }
    }

    private void showToast(View view) {
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Menu distance between two bt
     */
    private float mMenuDistance = 0.0f;

    private void menuTransform(final float offset) {
        if (mMenuDistance <= 0) {
            int[] addLocation = new int[2];
            int[] moreLocation = new int[2];
            mMenuAdd.getLocationInWindow(addLocation);
            mMenuMore.getLocationInWindow(moreLocation);
            mMenuDistance = moreLocation[0] - addLocation[0];
        }
        mMenuSeach.setTranslationX(mMenuDistance * offset);

        // y = 4x*x - 4*x + 1
        mMenuMore.setScaleX((float) (4 * Math.pow(offset, 2) - 4 * offset + 1));
        mMenuMore.setScaleY((float) (4 * Math.pow(offset, 2) - 4 * offset + 1));

        mMenuAdd.setAlpha(1 - offset);

        if (mMenuLayout.isShowMenu()) {
            mMenuLayout.hideMenu();
        }
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {
        menuTransform(slideOffset);
    }

    @Override
    public void onDrawerOpened(View drawerView) {
    }

    @Override
    public void onDrawerClosed(View drawerView) {
    }

    @Override
    public void onDrawerStateChanged(int newState) {
    }
}
