package com.zyj.ieasytools.act.addActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.Explode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.zyj.ieasytools.R;
import com.zyj.ieasytools.act.BaseActivity;
import com.zyj.ieasytools.act.helpActivity.HelpActivity;
import com.zyj.ieasytools.data.EntryptImple;
import com.zyj.ieasytools.library.db.ZYJDatabaseEncrypts;
import com.zyj.ieasytools.library.encrypt.BaseEncrypt;
import com.zyj.ieasytools.library.encrypt.PasswordEntry;
import com.zyj.ieasytools.library.utils.ZYJUtils;

/**
 * Author: Yuri.zheng<br>
 * Date: 8/21/16<br>
 * Email: 497393102@qq.com<br>
 */
public class AddEntryActivity extends BaseActivity implements IAddContract.View {

    public static final String PASSWORD_ENTRY = "entry_style";

    private Toolbar mToolbar;
    private View mTitleView;

    private RelativeLayout mMainLayout;
    private ImageButton mSaveButton;
    private View mFirstView;
    private EditText mTitleInput;
    private EditText mUserInput;
    private EditText mPasswordInput;
    private EditText mMethodInput;
    private Button mNextButton;

    private View mSecondView;
    private EditText mEmailInput;
    private EditText mPhoneInput;
    private EditText mAddressInput;
    private EditText mDescriptionInput;

    private View mThirdView;
    private EditText mQInput1;
    private EditText mAInput1;
    private EditText mQInput2;
    private EditText mAInput2;
    private EditText mQInput3;
    private EditText mAInput3;
    private EditText mRemark;

    // entry category
    private String mCategory;

    private ZYJDatabaseEncrypts mEncrypt;

    private IAddContract.Presenter mPresenter;

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
            return;
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

//        mTitleView = getToolbarChildView(mToolbar, "mTitleTextView", View.class);
//        if (mTitleView != null) {
//            mTitleView.setTransitionName("share");
//        }

        initChildViews();

        new AddPresenter(EntryptImple.getEntryptImple(this), this);
    }

    @Override
    public void setPresenter(IAddContract.Presenter presenter) {
        mPresenter = presenter;
    }

    private void initChildViews() {

        mMainLayout = getViewById(R.id.add_main_layout);
        mSaveButton = getViewById(R.id.save);
        mSaveButton.setEnabled(false);
        mSaveButton.setImageResource(R.mipmap.add_finish_disable);

        mFirstView = LayoutInflater.from(this).inflate(R.layout.add_one_layout, null);
        mTitleInput = (EditText) mFirstView.findViewById(R.id.title_input);
        mTitleInput.addTextChangedListener(mFirstTextWatcher);
        mUserInput = (EditText) mFirstView.findViewById(R.id.user_input);
        mUserInput.addTextChangedListener(mFirstTextWatcher);
        mPasswordInput = (EditText) mFirstView.findViewById(R.id.password_input);
        mPasswordInput.addTextChangedListener(mFirstTextWatcher);
        mMethodInput = (EditText) mFirstView.findViewById(R.id.method_input);
        mMethodInput.setKeyListener(null);
        mMethodInput.setOnClickListener(mMethod);
        mMethodInput.setText(BaseEncrypt.ENCRYPT_AES);
        mMethodInput.addTextChangedListener(mFirstTextWatcher);
        mNextButton = (Button) mFirstView.findViewById(R.id.next1);
        mNextButton.setEnabled(false);
        mMainLayout.addView(mFirstView, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        mSecondView = LayoutInflater.from(this).inflate(R.layout.add_second_layout, null);
        mEmailInput = (EditText) mSecondView.findViewById(R.id.email_input);
        mPhoneInput = (EditText) mSecondView.findViewById(R.id.phone_input);
        mAddressInput = (EditText) mSecondView.findViewById(R.id.address_input);
        mDescriptionInput = (EditText) mSecondView.findViewById(R.id.description_input);

        mThirdView = LayoutInflater.from(this).inflate(R.layout.add_third_layout, null);
        mQInput1 = (EditText) mThirdView.findViewById(R.id.q_1_input);
        mAInput1 = (EditText) mThirdView.findViewById(R.id.a_1_input);
        mQInput2 = (EditText) mThirdView.findViewById(R.id.q_2_input);
        mAInput2 = (EditText) mThirdView.findViewById(R.id.a_2_input);
        mQInput3 = (EditText) mThirdView.findViewById(R.id.q_3_input);
        mAInput3 = (EditText) mThirdView.findViewById(R.id.a_3_input);
        mRemark = (EditText) mThirdView.findViewById(R.id.remark_input);

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

    private TextWatcher mFirstTextWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void afterTextChanged(Editable s) {
            enableSave();
        }
    };

    private View.OnClickListener mMethod = new View.OnClickListener() {
        public void onClick(View v) {
            final String[] choice = new String[]{BaseEncrypt.ENCRYPT_AES, BaseEncrypt.ENCRYPT_DES, BaseEncrypt.ENCRYPT_RC4, BaseEncrypt.ENCRYPT_BASE_64, BaseEncrypt.ENCRYPT_BLOWFISH};
            int index = 0;
            for (int i = 0; i < choice.length; i++) {
                if (mMethodInput.getText().toString().equals(choice[i])) {
                    index = i;
                    break;
                }
            }
            new AlertDialog.Builder(AddEntryActivity.this).setTitle(R.string.add_method).setSingleChoiceItems(choice, index, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    mMethodInput.setText(choice[which]);
                    dialog.dismiss();
                }
            }).setPositiveButton(R.string.add_encrypt_help, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(getApplicationContext(), HelpActivity.class);
                    startActivity(intent);
                }
            }).create().show();
        }
    };

    private void enableSave() {
        boolean enable = !TextUtils.isEmpty(mTitleInput.getText()) && !TextUtils.isEmpty(mUserInput.getText())
                && !TextUtils.isEmpty(mPasswordInput.getText()) && !TextUtils.isEmpty(mMethodInput.getText());
        mSaveButton.setImageResource(enable ? R.mipmap.add_finish : R.mipmap.add_finish_disable);
        mSaveButton.setEnabled(enable);
        mNextButton.setEnabled(enable);
    }

    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.save:
                final EditText et = new EditText(this);
                et.setSingleLine(true);
                new AlertDialog.Builder(this).setMessage(R.string.add_input_see_password).setView(et).setPositiveButton(R.string.add_save, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mPresenter.setEntryAttributes(mEmailInput.getText().toString(),
                                mPhoneInput.getText().toString(),
                                mAddressInput.getText().toString(),
                                mDescriptionInput.getText().toString(),
                                mQInput1.getText().toString(),
                                mAInput1.getText().toString(),
                                mQInput2.getText().toString(),
                                mAInput2.getText().toString(),
                                mQInput3.getText().toString(),
                                mAInput3.getText().toString(),
                                mRemark.getText().toString());
                        mPresenter.saveEntry(
                                mTitleInput.getText().toString(),
                                mUserInput.getText().toString(),
                                mPasswordInput.getText().toString(),
                                mCategory,
                                et.getText().toString().trim(), mMethodInput.getText().toString().trim());
                    }
                }).setNegativeButton(R.string.add_see_password_help, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(), HelpActivity.class);
                        startActivity(intent);
                    }
                }).create().show();
                break;
            case R.id.next1:
                switchContentView(mSecondView);
                break;
            case R.id.next2:
                switchContentView(mThirdView);
                break;
            case R.id.previous1:
                switchContentView(mSecondView);
                break;
            case R.id.previous2:
                switchContentView(mFirstView);
                break;
        }
    }

    private void switchContentView(final View view) {
        for (int i = 0; i < mMainLayout.getChildCount(); i++) {
            if (mMainLayout.getChildAt(i).equals(view)) {
                return;
            }
        }
        mMainLayout.addView(view, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        view.measure(0, 0);
        int w = view.getMeasuredWidth();
        int h = view.getMeasuredHeight();
        final Animator anim = ViewAnimationUtils.createCircularReveal(view, w / 2, h / 2, 0, (float) Math.hypot(w, h));
        anim.setDuration(600);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                for (int i = 0; i < mMainLayout.getChildCount(); i++) {
                    View v = mMainLayout.getChildAt(i);
                    if (!v.equals(view)) {
                        mMainLayout.removeView(v);
                    }
                }
            }
        });
        anim.start();
    }

    @Override
    public void finishView() {
        this.finish();
    }

    @Override
    public void showToast(int resId) {
        Toast.makeText(this, getResources().getString(resId), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void verifyEnterPasswordSuccess() {
        super.verifyEnterPasswordSuccess();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
