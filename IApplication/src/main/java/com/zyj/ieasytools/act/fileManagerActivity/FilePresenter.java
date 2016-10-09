package com.zyj.ieasytools.act.fileManagerActivity;

import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Author: Yuri.zheng<br>
 * Date: 2016/10/8<br>
 * Email: 497393102@qq.com<br>
 */
public class FilePresenter implements IFileContract.Presenter {

    private final IFileContract.View mView;

    private String mCurrentPath;

    public FilePresenter(IFileContract.View mView) {
        this.mView = mView;
        mView.setPresenter(this);
    }

    @Override
    public List<String> getFileNameByPath(String rootPath, String suffix) {
        mCurrentPath = rootPath;
        if (TextUtils.isEmpty(rootPath)) {
            return new ArrayList<>();
        }
        List<String> list = new ArrayList<>();
        List<String> files = new ArrayList<>();
        File[] names = new File(rootPath).listFiles();
        if (names == null) {
            return null;
        }
        if (TextUtils.isEmpty(suffix)) {
            // dir
            for (File name : names) {
                if (name.isDirectory()) {
                    list.add(name.getAbsolutePath());
                }
            }
        } else {
            // Only show the suffix file
            for (File name : names) {
                if (name.isDirectory()) {
                    list.add(name.getAbsolutePath());
                }
                if (name.getName().endsWith(suffix)) {
                    files.add(name.getAbsolutePath());
                }
            }
            Collections.sort(files);
        }
        Collections.sort(list);
        list.addAll(files);
        return list;
    }

    @Override
    public String getCurrentPath() {
        return mCurrentPath;
    }
}
