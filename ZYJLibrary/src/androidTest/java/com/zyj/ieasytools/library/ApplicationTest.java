package com.zyj.ieasytools.library;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.zyj.ieasytools.library.encrypt.AESEncrypt;
import com.zyj.ieasytools.library.encrypt.BaseEncrypt;
import com.zyj.ieasytools.library.encrypt.EncryptFactory;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    public void testEncrypt() {
        BaseEncrypt encrypt = EncryptFactory.getInstance().getInstance(AESEncrypt.class, "497393102333333");

        String string = "111";

        String encryptString = encrypt.encrypt(string);
        String decryptString = encrypt.decrypt(encryptString);

        sys(encryptString != null ? encryptString : "null");

        sys(decryptString != null ? decryptString : "null");
    }

    private void sys(String string) {
        Log.d("zyj", string != null ? string : "is null");
    }

}