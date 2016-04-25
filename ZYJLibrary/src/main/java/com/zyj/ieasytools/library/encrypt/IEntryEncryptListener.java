package com.zyj.ieasytools.library.encrypt;

public interface IEntryEncryptListener {

    /**
     * Befour Encrypt or Decrypt a entry action, call this method
     */
    void beforeEntryAction();

    /**
     * After Encrypt or Decrypt a entry action, call this method
     *
     * @param success if action success,return true,others return false
     */
    void endEntryAction(PasswordEntry entry, boolean success);

}
