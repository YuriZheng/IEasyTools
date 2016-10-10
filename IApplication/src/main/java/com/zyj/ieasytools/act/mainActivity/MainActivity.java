package com.zyj.ieasytools.act.mainActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
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
import android.widget.Toast;

import com.zyj.ieasytools.BuildConfig;
import com.zyj.ieasytools.R;
import com.zyj.ieasytools.act.BaseActivity;
import com.zyj.ieasytools.act.aboutActivity.AboutActivity;
import com.zyj.ieasytools.act.addActivity.AddEntryActivity;
import com.zyj.ieasytools.act.feedbackActivity.FeedbackActivity;
import com.zyj.ieasytools.act.helpActivity.HelpActivity;
import com.zyj.ieasytools.act.mainActivity.childViews.BaseMainView;
import com.zyj.ieasytools.act.mainActivity.childViews.appView.AppPresenter;
import com.zyj.ieasytools.act.mainActivity.childViews.appView.AppView;
import com.zyj.ieasytools.act.mainActivity.childViews.emailView.EmailPresenter;
import com.zyj.ieasytools.act.mainActivity.childViews.emailView.EmailView;
import com.zyj.ieasytools.act.mainActivity.childViews.gameView.GamePresenter;
import com.zyj.ieasytools.act.mainActivity.childViews.gameView.GameView;
import com.zyj.ieasytools.act.mainActivity.childViews.otherView.OtherPresenter;
import com.zyj.ieasytools.act.mainActivity.childViews.otherView.OtherView;
import com.zyj.ieasytools.act.mainActivity.childViews.walletView.WalletPresenter;
import com.zyj.ieasytools.act.mainActivity.childViews.walletView.WalletView;
import com.zyj.ieasytools.act.mainActivity.childViews.webView.WebPresenter;
import com.zyj.ieasytools.act.mainActivity.childViews.webView.WebView;
import com.zyj.ieasytools.act.myServer.MyServer;
import com.zyj.ieasytools.act.otherDatabaseActivity.OtherDBActivity;
import com.zyj.ieasytools.act.seachActivity.SeachActivity;
import com.zyj.ieasytools.act.settingActivity.SettingActivity;
import com.zyj.ieasytools.data.SettingsConstant;
import com.zyj.ieasytools.library.encrypt.PasswordEntry;
import com.zyj.ieasytools.library.utils.ZYJPreferencesUtils;
import com.zyj.ieasytools.library.utils.ZYJUtils;
import com.zyj.ieasytools.library.views.MenuRevealView;

import java.io.File;

import static com.zyj.ieasytools.act.otherDatabaseActivity.OtherDBActivity.CLOASE_DIALOG;

/**
 * Author: Yuri.zheng<br>
 * Date: 8/21/16<br>
 * Email: 497393102@qq.com<br>
 */
public class MainActivity extends BaseActivity implements DrawerLayout.DrawerListener, IMainContract.View {

    public static final String ACTION_SWITCH_BROADCAST = "_s_db";
    public static final String ACTION_SWITCH_PATH = "_s_path";
    public static final String ACTION_SWITCH_NAME = "_s_name";

    private final int REQUEST_OTHER_DB_CODE = 100;

    /**
     * The content main layout
     */
    private ViewGroup mMainViewLayout;

    private WebView mGroupWebView;
    private EmailView mGroupEmailView;
    private WalletView mGroupWalletView;
    private AppView mGroupAppView;
    private GameView mGroupGameView;
    private OtherView mGroupOtherView;
    private BaseMainView mCurrentView;

    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private TextView mTitleTextView;
    private ProgressBar mProgressBar;
    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationView mNavigationView;

    private FloatingActionButton mFab;

    private View mMenuMore;
    private View mMenuAdd;
    private View mMenuSeach;
    private MenuRevealView mMenuLayout;

    private TextView mDebug;

    private MyServer mServer;

    private IMainContract.Presenter mPresenter;

    private Handler mHandler;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mServer = ((MyServer.MyBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    private BroadcastReceiver mSwitchDatabaseBroadcast = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                    switchDatabase(intent.getStringExtra(ACTION_SWITCH_NAME), intent.getStringExtra(ACTION_SWITCH_PATH));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHandler = new Handler();

        bindService(new Intent(getApplicationContext(), MyServer.class), mConnection, Context.BIND_AUTO_CREATE);

        mToolbar = getViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.password_catrgory_web);
        setSupportActionBar(mToolbar);
        View view = getToolbarChildView(mToolbar, "mTitleTextView", View.class);
        if (view != null && view instanceof TextView) {
            mTitleTextView = (TextView) view;
        }
        if (mTitleTextView != null) {
            ZYJPreferencesUtils.putFloat(this, SettingsConstant.TOOLBAR_TITLE_SIZE, mTitleTextView.getTextSize());
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

        DrawerLayout.LayoutParams lp = (DrawerLayout.LayoutParams) mNavigationView.getLayoutParams();
        lp.width = getResources().getDisplayMetrics().widthPixels * 2 / 3;
        mNavigationView.setLayoutParams(lp);

        mFab = getViewById(R.id.fab);

        mFab.setOnClickListener((v) -> {
            showSnackbarToast();
        });

        initContentViews();

        if (BuildConfig.FUNCTION_DEBUG) {
            mDebug = getViewById(R.id.debug);
            Object[] versions = ZYJUtils.getVersion(this);
            mDebug.setText("Name: " + versions[0].toString() + "\nCode: " + versions[1].toString());
            mDebug.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.mipmap.debug);
            mDebug.setVisibility(View.VISIBLE);
        }
        new MainPresenter(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(mSwitchDatabaseBroadcast, new IntentFilter(ACTION_SWITCH_BROADCAST));

        if (!mPresenter.hasOtherDatabase()) {
            mNavigationView.getMenu().removeItem(R.id.settings_view_other);
        }
    }

    @Override
    public void setPresenter(IMainContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    protected void onResume() {
        super.onResume();
        MenuItem otherItem = mNavigationView.getMenu().findItem(R.id.settings_view_other);
        if (mPresenter.hasOtherDatabase()) {
            if (otherItem == null) {
                mNavigationView.getMenu().add(R.id.setting_menu, R.id.settings_view_other, 3, R.string.settings_view_other);
            }
        } else {
            if (otherItem != null) {
                mNavigationView.getMenu().removeItem(R.id.settings_view_other);
            }
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
        // Only call once
        mPresenter.destory();
        // Destory views
        mGroupWebView.destory();
        mGroupEmailView.destory();
        mGroupWalletView.destory();
        mGroupAppView.destory();
        mGroupGameView.destory();
        mGroupOtherView.destory();

        unbindService(mConnection);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mSwitchDatabaseBroadcast);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    protected void verifyEnterPasswordSuccess() {
        super.verifyEnterPasswordSuccess();
        if (mCurrentView != null) {
            mCurrentView.verifyEnterPasswordSuccess();
        }
    }

    @Override
    public void enableAddButton(boolean enable) {
        mMenuAdd.setEnabled(enable);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void switchDatabase(final String name, final String path) {
        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(path) && new File(path).canRead()) {
            ZYJUtils.logD(getClass(), "-----Name：" + name);
            ZYJUtils.logD(getClass(), "-----Path：" + path);
            mPresenter.switchDatabase(name, path);
        } else {
            Toast.makeText(this, "Error name: " + name + "\nError path: " + path, Toast.LENGTH_LONG).show();
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(CLOASE_DIALOG));
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

    private boolean addSwitchView(final View clickView, final BaseMainView view) {
        final View addView = view.getView();
        mCurrentView = view;
        if (clickView == null) {
            view.onReload();
            mMainViewLayout.addView(addView, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            return true;
        }
        final View[] views = new View[mMainViewLayout.getChildCount()];
        for (int i = 0; i < views.length; i++) {
            views[i] = mMainViewLayout.getChildAt(i);
            if (views[i].equals(addView)) {
                ZYJUtils.logW(getClass(), "Duplicate add view");
                return false;
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
        return true;
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
                mHandler.postDelayed(() -> {
                    Intent intent = new Intent(getApplicationContext(), AddEntryActivity.class);
                    intent.putExtra(AddEntryActivity.PASSWORD_ENTRY, mPresenter.getCategory());
//                        if (mTitleTextView != null) {
//                            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this
//                                    , Pair.create(mTitleTextView, "share")).toBundle());
//                        } else {
//                            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
//                        }
                    startActivity(intent);
                }, 300);
                break;
            case R.id.menu_seach:
                startActivity(new Intent(getApplicationContext(), SeachActivity.class));
                hideSildeDrawer();
                break;
        }
    }

    private NavigationView.OnNavigationItemSelectedListener mNavigationClick = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(final MenuItem item) {
            hideSildeDrawer();
            mHandler.postDelayed(() -> {
                switch (item.getItemId()) {
                    case R.id.settings_settings:
                        startActivity(new Intent(getApplicationContext(), SettingActivity.class));
                        break;
                    case R.id.settings_view_other:
                        Intent intent = new Intent(getApplicationContext(), OtherDBActivity.class);
                        startActivityForResult(intent, REQUEST_OTHER_DB_CODE);
                        break;
                    case R.id.settings_feedback:
                        startActivity(new Intent(getApplicationContext(), FeedbackActivity.class));
                        break;
                    case R.id.settings_about:
                        startActivity(new Intent(getApplicationContext(), AboutActivity.class));
                        break;
                    case R.id.settings_help:
                        startActivity(new Intent(getApplicationContext(), HelpActivity.class));
                        break;
                }
            }, 300);
            return true;
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
                if (addSwitchView(view, mGroupWebView)) {
                    animatorTitle(R.string.password_catrgory_web);
                }
                mCurrentView = mGroupEmailView;
                mPresenter.setCategory(PasswordEntry.CATEGORY_WEB);
                break;
            case R.id.group_email:
                if (addSwitchView(view, mGroupEmailView)) {
                    animatorTitle(R.string.password_catrgory_email);
                }
                mCurrentView = mGroupEmailView;
                mPresenter.setCategory(PasswordEntry.CATEGORY_EMAIL);
                break;
            case R.id.group_wallet:
                if (addSwitchView(view, mGroupWalletView)) {
                    animatorTitle(R.string.password_catrgory_wallet);
                }
                mCurrentView = mGroupWalletView;
                mPresenter.setCategory(PasswordEntry.CATEGORY_WALLET);
                break;
            case R.id.group_app:
                if (addSwitchView(view, mGroupAppView)) {
                    animatorTitle(R.string.password_catrgory_app);
                }
                mCurrentView = mGroupAppView;
                mPresenter.setCategory(PasswordEntry.CATEGORY_APP);
                break;
            case R.id.group_game:
                if (addSwitchView(view, mGroupGameView)) {
                    animatorTitle(R.string.password_catrgory_game);
                }
                mCurrentView = mGroupGameView;
                mPresenter.setCategory(PasswordEntry.CATEGORY_GAME);
                break;
            case R.id.group_other:
                if (addSwitchView(view, mGroupOtherView)) {
                    animatorTitle(R.string.password_catrgory_other);
                }
                mCurrentView = mGroupOtherView;
                mPresenter.setCategory(PasswordEntry.CATEGORY_OTHER);
                break;
        }
        mMenuLayout.hideMenu();
    }

    private void animatorTitle(final int resId) {
        if (mTitleTextView != null) {
            PropertyValuesHolder translation = PropertyValuesHolder.ofFloat("translationX", 0, mTitleTextView.getWidth());
            PropertyValuesHolder aplha = PropertyValuesHolder.ofFloat("alpha", 1f, 0f);
            ObjectAnimator dis = ObjectAnimator.ofPropertyValuesHolder(mTitleTextView, translation, aplha);
            dis.setDuration(150);
            dis.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    mTitleTextView.setText(resId);
                    PropertyValuesHolder translation = PropertyValuesHolder.ofFloat("translationX", -mTitleTextView.getWidth(), 0);
                    PropertyValuesHolder aplha = PropertyValuesHolder.ofFloat("alpha", 0f, 1f);
                    ObjectAnimator show = ObjectAnimator.ofPropertyValuesHolder(mTitleTextView, translation, aplha);
                    show.setDuration(150);
                    show.start();
                }
            });
            dis.start();
        }
    }

    public void onViewClick(View view) {
        actionProgressBar(false);
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

    @Override
    public void actionProgressBar(boolean show) {
        int visibility = show ? View.VISIBLE : View.INVISIBLE;
        if (mProgressBar.getVisibility() != visibility) {
            mProgressBar.setVisibility(visibility);
        }
    }

    @Override
    public void showSnackbarToast() {
        Snackbar.make(mFab, "Replace with your own action", Snackbar.LENGTH_LONG)
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
