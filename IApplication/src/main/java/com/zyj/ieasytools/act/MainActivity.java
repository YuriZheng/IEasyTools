package com.zyj.ieasytools.act;

import android.animation.Animator;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
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
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zyj.ieasytools.R;
import com.zyj.ieasytools.library.db.ZYJEncrypts;
import com.zyj.ieasytools.library.encrypt.BaseEncrypt;
import com.zyj.ieasytools.library.encrypt.PasswordEntry;
import com.zyj.ieasytools.library.utils.ZYJDBEntryptUtils;
import com.zyj.ieasytools.library.utils.ZYJUtils;
import com.zyj.ieasytools.library.views.MenuRevealView;
import com.zyj.ieasytools.utils.SettingsConstant;
import com.zyj.ieasytools.views.GroupAppView;
import com.zyj.ieasytools.views.GroupEmailView;
import com.zyj.ieasytools.views.GroupGameView;
import com.zyj.ieasytools.views.GroupOtherView;
import com.zyj.ieasytools.views.GroupWalletView;
import com.zyj.ieasytools.views.GroupWebView;

import java.lang.reflect.Field;
import java.util.UUID;

public class MainActivity extends BaseActivity implements DrawerLayout.DrawerListener {

    /**
     * The content main layout
     */
    private ViewGroup mMainViewLayout;

    private GroupWebView mGroupWebView;
    private GroupEmailView mGroupEmailView;
    private GroupWalletView mGroupWalletView;
    private GroupAppView mGroupAppView;
    private GroupGameView mGroupGameView;
    private GroupOtherView mGroupOtherView;

    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private View mToolNavigationView;
    private ProgressBar mProgressBar;
    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationView mNavigationView;

    private View mMenuMore;
    private View mMenuAdd;
    private View mMenuSeach;
    private MenuRevealView mMenuLayout;

    private TextView mDebug;

    private ZYJEncrypts mEncrypt;

    private MyServer mServer;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mServer = ((MyServer.MyBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindService(new Intent(getApplicationContext(), MyServer.class), mConnection, Context.BIND_AUTO_CREATE);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.password_catrgory_web);
        setSupportActionBar(mToolbar);
        try {
            Field colorFid = Toolbar.class.getDeclaredField("mNavButtonView");
            colorFid.setAccessible(true);
            mToolNavigationView = (View) colorFid.get(mToolbar);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mProgressBar = (ProgressBar) findViewById(R.id.progress);
        mProgressBar.getIndeterminateDrawable().setTint(getResources().getColor(android.R.color.white));
        actionProgressBar(true);

        mMainViewLayout = (ViewGroup) findViewById(R.id.main_view_layout);

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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        initContentViews();

        if (ZYJUtils.isFunctionDebug) {
            mDebug = (TextView) findViewById(R.id.debug);
            Object[] versions = ZYJUtils.getVersion(this);
            mDebug.setText("Name: " + versions[0].toString() + "\nCode: " + versions[1].toString());
            mDebug.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.mipmap.debug);
            mDebug.setVisibility(View.VISIBLE);
        }

        mEncrypt = ZYJDBEntryptUtils.getCurrentEncryptDatabase(this, "12345678");
        PasswordEntry e = new PasswordEntry(UUID.randomUUID().toString(), "497393102", BaseEncrypt.ENCRYPT_AES);
        if (mEncrypt != null) {
            mEncrypt.insertEntry(e, "497393102");
            ZYJUtils.logD(TAG, "" + mEncrypt.getAllRecord());
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ZYJUtils.logD(TAG, "onSaveInstanceState");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        ZYJUtils.logD(TAG, "onRestoreInstanceState");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
        // Only call once
        ZYJEncrypts.destory();
    }

    /**
     * Init the child view and add the default view
     */
    private void initContentViews() {
        new Thread() {
            public void run() {
                mGroupWebView = new GroupWebView(MainActivity.this);
                mGroupEmailView = new GroupEmailView(MainActivity.this);
                mGroupWalletView = new GroupWalletView(MainActivity.this);
                mGroupAppView = new GroupAppView(MainActivity.this);
                mGroupGameView = new GroupGameView(MainActivity.this);
                mGroupOtherView = new GroupOtherView(MainActivity.this);
                mHandler.post(new Runnable() {
                    public void run() {
                        addSwitchView(null, mGroupWebView.getMainView());
                        actionProgressBar(false);
                    }
                });
            }
        }.start();
    }

    /**
     * Record the content view width
     */
    private int mGroupWidth = 0;
    /**
     * Record the content view height
     */
    private int mGroupHeight = 0;
    /**
     * Record the anim end of radius
     */
    private float mEndRadius = 0;

    /**
     * Remove all child view
     *
     * @param views   the child view array
     * @param addView the added view
     * @param y       the click coordinate y
     * @return the animator
     */
    private Animator removeAllViewBesidesAddView(final View[] views, final View addView, final int y) {
        // Modified by 50 value to correction the center of circular
        final Animator anim = ViewAnimationUtils.createCircularReveal(addView, mGroupWidth + 50, y, 0, mEndRadius);
        anim.setDuration(600);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.addListener(new Animator.AnimatorListener() {
            public void onAnimationStart(Animator animation) {
            }

            public void onAnimationEnd(Animator animation) {
                for (int i = 0; i < views.length; i++) {
                    mMainViewLayout.removeView(views[i]);
                }
            }

            public void onAnimationCancel(Animator animation) {
            }

            public void onAnimationRepeat(Animator animation) {
            }
        });
        return anim;
    }

    /**
     * Add centent view by animation
     *
     * @param clickView the click view
     * @param addView   added view
     */
    private void addSwitchView(final View clickView, final View addView) {
        if (clickView == null) {
            mMainViewLayout.addView(addView, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            return;
        }
        final View[] views = new View[mMainViewLayout.getChildCount()];
        for (int i = 0; i < views.length; i++) {
            views[i] = mMainViewLayout.getChildAt(i);
            if (views[i].equals(addView)) {
                ZYJUtils.logW(getClass(), "Duplicate add view");
                return;
            }
        }
        if (mGroupWidth <= 0) {
            mGroupWidth = mMainViewLayout.getWidth();
        }
        if (mGroupHeight <= 0) {
            mGroupHeight = mMainViewLayout.getHeight();
        }
        if (mEndRadius <= 0) {
            mEndRadius = (float) Math.hypot(mGroupWidth, mGroupHeight);
        }
        mMainViewLayout.setOnHierarchyChangeListener(new ViewGroup.OnHierarchyChangeListener() {
            @Override
            public void onChildViewAdded(View parent, View child) {
                if (child.equals(addView)) {
                    int[] location = new int[2];
                    clickView.getLocationInWindow(location);
                    removeAllViewBesidesAddView(views, addView, location[1]).start();
                }
            }

            @Override
            public void onChildViewRemoved(View parent, View child) {
            }
        });
        mMainViewLayout.addView(addView, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    /**
     * Menu click, used in activity_main.xml
     *
     * @param view
     */
    @SuppressWarnings("unused")
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
                ZYJUtils.logD(TAG, "menu_add");
                hideSildeDrawer();
                if (mMenuAdd.getAlpha() != 1) {
                    // Show the left drawer
                } else {

                }
                break;
            case R.id.menu_seach:
                ZYJUtils.logD(TAG, "menu_seach");
                hideSildeDrawer();
                break;
        }
    }

    private NavigationView.OnNavigationItemSelectedListener mNavigationClick = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.settings_settings:
                    actionProgressBar(true);
                    break;
                case R.id.settings_view_other:
                    break;
                case R.id.settings_feedback:
                    actionProgressBar(false);
                    break;
                case R.id.settings_about:
                    break;
                case R.id.settings_help:
                    break;
            }
            return false;
        }
    };

    /**
     * Menu click, used in activity_main.xml
     *
     * @param view
     */
    @SuppressWarnings("unused")
    public void onGroupMenuClick(View view) {
        // The view not ready yet
        if (mGroupOtherView == null) {
            return;
        }
        switch (view.getId()) {
            case R.id.group_web:
                mToolbar.setTitle(R.string.password_catrgory_web);
                addSwitchView(view, mGroupWebView.getMainView());
                break;
            case R.id.group_email:
                mToolbar.setTitle(R.string.password_catrgory_email);
                addSwitchView(view, mGroupEmailView.getMainView());
                break;
            case R.id.group_wallet:
                mToolbar.setTitle(R.string.password_catrgory_wallet);
                addSwitchView(view, mGroupWalletView.getMainView());
                break;
            case R.id.group_app:
                mToolbar.setTitle(R.string.password_catrgory_app);
                addSwitchView(view, mGroupAppView.getMainView());
                break;
            case R.id.group_game:
                mToolbar.setTitle(R.string.password_catrgory_game);
                addSwitchView(view, mGroupGameView.getMainView());
                break;
            case R.id.group_other:
                mToolbar.setTitle(R.string.password_catrgory_other);
                addSwitchView(view, mGroupOtherView.getMainView());
                break;
        }
        mMenuLayout.hideMenu();
    }

    public void onViewClick(View view) {
        actionProgressBar(false);
        showToast(view);
        switch (view.getId()) {
            case R.id.ok_id:
                break;
            case R.id.switch_id:
                break;
        }
    }

    private void hideSildeDrawer() {
        if (mMenuLayout.isShowMenu()) {
            mMenuLayout.hideMenu();
        }
        if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        }
    }

    private void actionProgressBar(boolean show) {
        int visibility = show ? View.VISIBLE : View.INVISIBLE;
        if (mProgressBar.getVisibility() != visibility) {
            mProgressBar.setVisibility(visibility);
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
            mMenuSeach.getLocationInWindow(addLocation);
            mMenuMore.getLocationInWindow(moreLocation);
            mMenuDistance = moreLocation[0] - addLocation[0];
        }
        mMenuAdd.setTranslationX(mMenuDistance * offset);

        // y = 4x*x - 4*x + 1
        mMenuMore.setScaleX((float) (4 * Math.pow(offset, 2) - 4 * offset + 1));
        mMenuMore.setScaleY((float) (4 * Math.pow(offset, 2) - 4 * offset + 1));

        mMenuSeach.setAlpha(1 - offset);
        mMenuSeach.setVisibility(offset == 1 ? View.INVISIBLE : View.VISIBLE);

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
