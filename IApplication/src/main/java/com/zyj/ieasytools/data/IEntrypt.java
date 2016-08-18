package com.zyj.ieasytools.data;

import com.zyj.ieasytools.library.db.ZYJDatabaseEncrypts;
import com.zyj.ieasytools.library.encrypt.PasswordEntry;

import java.util.List;

/**
 * 作者：yuri.zheng<br>
 * 时间：2016/8/16<br>
 */
public interface IEntrypt extends IData {

    /**
     * {@link ZYJDatabaseEncrypts#setEncryptListener(ZYJDatabaseEncrypts.EncryptListener)}
     */
    void setEncryptListener(ZYJDatabaseEncrypts.EncryptListener l);

    /**
     * {@link ZYJDatabaseEncrypts#isCurrentDatabase()}
     */
    boolean isCurrentDatabase();

    /**
     * {@link ZYJDatabaseEncrypts#isDestory()}
     */
    boolean isDestory();

    /**
     * {@link ZYJDatabaseEncrypts#validDatabase()}
     */
    boolean validDatabase();

    /**
     * {@link ZYJDatabaseEncrypts#insertEntry(PasswordEntry, String)}
     */
    long insertEntry(PasswordEntry entry, String password);

    /**
     * {@link ZYJDatabaseEncrypts#deleteEntry(PasswordEntry, String)}
     */
    int deleteEntry(PasswordEntry entry, String password);

    /**
     * {@link ZYJDatabaseEncrypts#updateEntry(PasswordEntry, String)}
     */
    long updateEntry(PasswordEntry entry, String password);

    /**
     * {@link ZYJDatabaseEncrypts#queryEntry(String[], String, String[], String, String)}
     */
    List<PasswordEntry> queryEntry(String[] columns, String selection, String[] selectionArgs, String groupBy, String password);

}
