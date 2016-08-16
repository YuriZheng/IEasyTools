package com.zyj.ieasytools.data;

import com.zyj.ieasytools.library.db.ZYJEncrypts;
import com.zyj.ieasytools.library.encrypt.PasswordEntry;

import java.util.List;

/**
 * 作者：yuri.zheng<br>
 * 时间：2016/8/16<br>
 */
public interface IEntrypt {

    /**
     * {@link ZYJEncrypts#setEncryptListener(ZYJEncrypts.EncryptListener)}
     */
    void setEncryptListener(ZYJEncrypts.EncryptListener l);

    /**
     * {@link ZYJEncrypts#isCurrentDatabase()}
     */
    boolean isCurrentDatabase();

    /**
     * {@link ZYJEncrypts#isDestory()}
     */
    boolean isDestory();

    /**
     * {@link ZYJEncrypts#validDatabase()}
     */
    boolean validDatabase();

    /**
     * {@link ZYJEncrypts#insertEntry(PasswordEntry, String)}
     */
    long insertEntry(PasswordEntry entry, String password);

    /**
     * {@link ZYJEncrypts#deleteEntry(PasswordEntry, String)}
     */
    int deleteEntry(PasswordEntry entry, String password);

    /**
     * {@link ZYJEncrypts#updateEntry(PasswordEntry, String)}
     */
    long updateEntry(PasswordEntry entry, String password);

    /**
     * {@link ZYJEncrypts#queryEntry(String, String[], String, String)}
     */
    List<PasswordEntry> queryEntry(String selection, String[] selectionArgs, String groupBy, String password);

    /**
     * {@link ZYJEncrypts#getAllRecord()}
     */
    int getAllRecord();

}
