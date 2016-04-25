package com.zyj.ieasytools.library.encrypt;

public interface IItemEncryptListener {

    /**
     * Before Encrypt or Decrypt, called
     *
     * @param resourceContent source content
     */
    void beforeItemAction(String resourceContent);

    /**
     * After Encrypt or Decrypt, called
     *
     * @param resourceContent source content
     * @param encryptContent  after Encrypt or Decrypt content
     * @return Encrypt or Decrypt success will return true, others return false
     */
    void endItemAction(String resourceContent, String encryptContent);

}
