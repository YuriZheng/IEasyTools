package com.zyj.ieasytools.act.mainActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ActivityOptions;
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
import android.util.Pair;
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
import com.zyj.ieasytools.act.BaseActivity;
import com.zyj.ieasytools.act.addActivity.AddEntryActivity;
import com.zyj.ieasytools.act.mainActivity.appView.AppPresenter;
import com.zyj.ieasytools.act.mainActivity.appView.AppView;
import com.zyj.ieasytools.act.mainActivity.emailView.EmailPresenter;
import com.zyj.ieasytools.act.mainActivity.emailView.EmailView;
import com.zyj.ieasytools.act.mainActivity.gameView.GamePresenter;
import com.zyj.ieasytools.act.mainActivity.gameView.GameView;
import com.zyj.ieasytools.act.mainActivity.otherView.OtherPresenter;
import com.zyj.ieasytools.act.mainActivity.otherView.OtherView;
import com.zyj.ieasytools.act.mainActivity.walletView.WalletPresenter;
import com.zyj.ieasytools.act.mainActivity.walletView.WalletView;
import com.zyj.ieasytools.act.mainActivity.webView.WebPresenter;
import com.zyj.ieasytools.act.mainActivity.webView.WebView;
import com.zyj.ieasytools.act.myServer.MyServer;
import com.zyj.ieasytools.data.EntryptImple;
import com.zyj.ieasytools.library.encrypt.PasswordEntry;
import com.zyj.ieasytools.library.utils.ZYJUtils;
import com.zyj.ieasytools.library.views.MenuRevealView;

public class MainActivity extends BaseActivity implements DrawerLayout.DrawerListener {

    /**
     * The content main layout
     */
    private ViewGroup mMainViewLayout;

    /**
     * Record toolbar text size:{@link com.zyj.ieasytools.dialog.InputEnterPasswordDialog}
     */
    public static float mToolbarTextSize = -1;

    private WebView mGroupWebView;
    private EmailView mGroupEmailView;
    private WalletView mGroupWalletView;
    private AppView mGroupAppView;
    private GameView mGroupGameView;
    private OtherView mGroupOtherView;
    private BaseMainView mCurrentView;

    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private View mToolNavigationView;
    private View mTitleTextView;
    private ProgressBar mProgressBar;
    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationView mNavigationView;

    private View mMenuMore;
    private View mMenuAdd;
    private View mMenuSeach;
    private MenuRevealView mMenuLayout;

    /**
     * Record the category
     * <li>{@link PasswordEntry#CATEGORY_WEB}</li>
     * <li>{@link PasswordEntry#CATEGORY_EMAIL}</li>
     * <li>{@link PasswordEntry#CATEGORY_WALLET}</li>
     * <li>{@link PasswordEntry#CATEGORY_APP}</li>
     * <li>{@link PasswordEntry#CATEGORY_GAME}</li>
     * <li>{@link PasswordEntry#CATEGORY_OTHER}</li>
     */
    private String mCategory = PasswordEntry.CATEGORY_WEB;

    private TextView mDebug;

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

        mToolbar = getViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.password_catrgory_web);
        setSupportActionBar(mToolbar);
        mToolNavigationView = getToolbarChildView(mToolbar, "mNavButtonView", View.class);
        mTitleTextView = getToolbarChildView(mToolbar, "mTitleTextView", View.class);
        if (mTitleTextView != null) {
            mToolbarTextSize = ((TextView) mTitleTextView).getTextSize();
        }

        mProgressBar = getViewById(R.id.progress);
        mProgressBar.getIndeterminateDrawable().setTint(getResources().getColor(android.R.color.white));
        actionProgressBar(true);

        mMainViewLayout = getViewById(R.id.main_view_layout);
        mDrawerLayout = getViewById(R.id.drawer_layout);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_toggle_text, R.string.drawer_toggle_text);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerLayout.addDrawerListener(this);

        mMenuMore = getViewById(R.id.menu_more);
        mMenuAdd = getViewById(R.id.menu_add);
        mMenuSeach = getViewById(R.id.menu_seach);

        mMenuLayout = getViewById(R.id.menu_layout);

        mNavigationView = getViewById(R.id.navigationView);
        mNavigationView.setNavigationItemSelectedListener(mNavigationClick);

        if (EntryptImple.getDatabasePathsBesidesCurrent(this).size() <= 0) {
            mNavigationView.getMenu().removeItem(R.id.settings_view_other);
        }
        DrawerLayout.LayoutParams lp = (DrawerLayout.LayoutParams) mNavigationView.getLayoutParams();
        lp.width = ZYJUtils.getDisplayMetrics(this).widthPixels * 2 / 3;
        mNavigationView.setLayoutParams(lp);

        FloatingActionButton fab = getViewById(R.id.fab);

        initContentViews();

        if (ZYJUtils.isFunctionDebug) {
            mDebug = getViewById(R.id.debug);
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
        EntryptImple.destoryEntrypt();
    }

    @Override
    protected void verifyEnterPasswordSuccess() {
        super.verifyEnterPasswordSuccess();
        if (mCurrentView != null) {
            mCurrentView.verifyEnterPasswordSuccess();
        }
    }

    /**
     * Init the child view and add the default view
     */
    private void initContentViews() {
        new Thread() {
            public void run() {
                mGroupWebView = new WebView(MainActivity.this);
                new WebPresenter(mGroupWebView);
                mGroupEmailView = new EmailView(MainActivity.this);
                new EmailPresenter(mGroupEmailView);
                mGroupWalletView = new WalletView(MainActivity.this);
                new WalletPresenter(mGroupWalletView);
                mGroupAppView = new AppView(MainActivity.this);
                new AppPresenter(mGroupAppView);
                mGroupGameView = new GameView(MainActivity.this);
                new GamePresenter(mGroupGameView);
                mGroupOtherView = new OtherView(MainActivity.this);
                new OtherPresenter(mGroupOtherView);
                mHandler.post(new Runnable() {
                    public void run() {
                        addSwitchView(null, mGroupWebView);
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
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                for (int i = 0; i < views.length; i++) {
                    mMainViewLayout.removeView(views[i]);
                }
            }
        });
        return anim;
    }

    /**
     * Add centent view by animation
     *
     * @param clickView the click view
     * @param view      added view
     */
    private void addSwitchView(final View clickView, final BaseMainView view) {
        final View addView = view.getView();
        mCurrentView = view;
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
        if (mCurrentView != null) {
            mCurrentView.onReload();
        }
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
                mHandler.postDelayed(new Runnable() {
                    public void run() {
                        Intent intent = new Intent(getApplicationContext(), AddEntryActivity.class);
                        intent.putExtra(AddEntryActivity.PASSWORD_ENTRY, mCategory);
                        if (mTitleTextView != null) {
                            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this
                                    , Pair.create(mTitleTextView, "share")).toBundle());
                        } else {
                            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this
                            ).toBundle());
                        }

                    }
                }, 300);
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
                addSwitchView(view, mGroupWebView);
                mCategory = PasswordEntry.CATEGORY_WEB;
                break;
            case R.id.group_email:
                mToolbar.setTitle(R.string.password_catrgory_email);
                addSwitchView(view, mGroupEmailView);
                mCurrentView = mGroupEmailView;
                mCategory = PasswordEntry.CATEGORY_EMAIL;
                break;
            case R.id.group_wallet:
                mToolbar.setTitle(R.string.password_catrgory_wallet);
                addSwitchView(view, mGroupWalletView);
                mCurrentView = mGroupWalletView;
                mCategory = PasswordEntry.CATEGORY_WALLET;
                break;
            case R.id.group_app:
                mToolbar.setTitle(R.string.password_catrgory_app);
                addSwitchView(view, mGroupAppView);
                mCurrentView = mGroupAppView;
                mCategory = PasswordEntry.CATEGORY_APP;
                break;
            case R.id.group_game:
                mToolbar.setTitle(R.string.password_catrgory_game);
                addSwitchView(view, mGroupGameView);
                mCurrentView = mGroupGameView;
                mCategory = PasswordEntry.CATEGORY_GAME;
                break;
            case R.id.group_other:
                mToolbar.setTitle(R.string.password_catrgory_other);
                addSwitchView(view, mGroupOtherView);
                mCurrentView = mGroupOtherView;
                mCategory = PasswordEntry.CATEGORY_OTHER;
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

    public void actionProgressBar(boolean show) {
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
