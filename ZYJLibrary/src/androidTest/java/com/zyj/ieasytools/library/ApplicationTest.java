package com.zyj.ieasytools.library;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    public void testEncrypt() {
    }

    private void sys(String string) {
        Log.d("zyj", string != null ? string : "is null");
    }

}