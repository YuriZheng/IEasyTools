package com.zyj.ieasytools.act.mainActivity;

import com.zyj.ieasytools.data.EntryptImple;
import com.zyj.ieasytools.data.IEntrypt;
import com.zyj.ieasytools.library.encrypt.PasswordEntry;

/**
 * Created by ZYJ on 8/18/16.
 */
public class MainPresenter implements IMainContract.Presenter {

    private final IEntrypt mModel;

    private final IMainContract.View mView;

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

    public MainPresenter(IMainContract.View view) {
        this.mView = view;
        mModel = EntryptImple.getEntryptImple(view.getContext());
        mView.setPresenter(this);
    }

    @Override
    public void setCategory(String mCategory) {
        this.mCategory = mCategory;
    }

    @Override
    public String getCategory() {
        return mCategory;
    }

    @Override
    public boolean hasOtherDatabase() {
        return EntryptImple.getDatabasePathsBesidesCurrent(mView.getContext()).size() > 0;
    }
}
