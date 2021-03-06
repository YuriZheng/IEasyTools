package com.zyj.ieasytools.act.addActivity;

import android.text.TextUtils;

import com.zyj.ieasytools.R;
import com.zyj.ieasytools.data.IEntrypt;
import com.zyj.ieasytools.library.encrypt.BaseEncrypt;
import com.zyj.ieasytools.library.encrypt.PasswordEntry;

import java.util.UUID;

/**
 * Author: Yuri.zheng<br>
 * Date: 8/21/16<br>
 * Email: 497393102@qq.com<br>
 */
public class AddPresenter implements IAddContract.Presenter {

    private final IEntrypt mModel;
    private final IAddContract.View mView;

    private String mEmail;
    private String mPhone;
    private String mAddress;
    private String mDescription;
    private String mQ1;
    private String mA1;
    private String mQ2;
    private String mA2;
    private String mQ3;
    private String mA3;
    private String mMark;

    public AddPresenter(IEntrypt model, IAddContract.View view) {
        this.mModel = model;
        this.mView = view;

        mView.setPresenter(this);
    }

    @Override
    public void saveEntry(final String title, final String username, final String userPassword, final String category, final String password, final String method) {
        if (password.length() < BaseEncrypt.ENCRYPT_PRIVATE_KEY_LENGTH_MIN) {
            mView.showToast(R.string.password_short);
            return;
        }
        if (password.length() > BaseEncrypt.ENCRYPT_PRIVATE_KEY_LENGTH_MAX) {
            mView.showToast(R.string.password_long);
            return;
        }
        if (TextUtils.isEmpty(title)) {
            mView.showToast(R.string.add_save_title_null);
            return;
        }
        if (TextUtils.isEmpty(username)) {
            mView.showToast(R.string.add_save_user_null);
            return;
        }
        if (TextUtils.isEmpty(userPassword)) {
            mView.showToast(R.string.add_save_password_null);
            return;
        }
        PasswordEntry entry = new PasswordEntry(UUID.randomUUID().toString(), password, method);
        entry.p_title = title;
        entry.p_username = username;
        entry.p_password = userPassword;
        entry.p_category = category;
        entry.p_email = mEmail;
        entry.p_phone = mPhone;
        entry.p_address = mAddress;
        entry.p_description = mDescription;
        entry.p_q_1 = mQ1;
        entry.p_q_a_1 = mA1;
        entry.p_q_2 = mQ2;
        entry.p_q_a_2 = mA2;
        entry.p_q_3 = mQ3;
        entry.p_q_a_3 = mA3;
        entry.p_remarks = mMark;
        long result = mModel.insertEntry(entry, password);
        if (result > 0) {
            mView.showToast(R.string.add_success);
            mView.finishView();
        } else {
            mView.showToast(R.string.add_fail);
        }
    }

    @Override
    public void setEntryAttributes(String email, String phone, String address, String description, String q1, String a1, String q2, String a2, String q3, String a3, String mark) {
        mEmail = email;
        mPhone = phone;
        mAddress = address;
        mDescription = description;
        mQ1 = q1;
        mA1 = a1;
        mQ2 = q2;
        mA2 = a2;
        mQ3 = q3;
        mA3 = a3;
        mMark = mark;
    }
}
