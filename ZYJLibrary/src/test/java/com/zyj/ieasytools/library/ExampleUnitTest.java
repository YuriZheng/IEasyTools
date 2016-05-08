package com.zyj.ieasytools.library;

import com.zyj.ieasytools.library.encrypt.BaseEncrypt;
import com.zyj.ieasytools.library.encrypt.EncryptFactory;
import com.zyj.ieasytools.library.encrypt.RC4Encrypt;

import org.junit.Test;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        BaseEncrypt encrypt = EncryptFactory.getInstance().getInstance(RC4Encrypt.class, "497393102333333");

        BaseEncrypt encrypt2 = EncryptFactory.getInstance().getInstance(RC4Encrypt.class, "asgaerhaeth");

        String string = "12345678";

        String encryptString = encrypt.encrypt(string);
        String decryptString = encrypt.decrypt(encryptString);

        String decryptString2 = encrypt2.decrypt(encryptString);

        sys(encryptString != null ? encryptString : "null");

        sys(decryptString != null ? decryptString : "null");

        sys(decryptString2 != null ? decryptString2 : "null");
    }

    private void sys(String string) {
        System.out.println(string != null ? string : "is null");
    }
}