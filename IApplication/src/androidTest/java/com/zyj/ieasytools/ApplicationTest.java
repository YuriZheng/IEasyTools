package com.zyj.ieasytools;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.text.TextUtils;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);

        System.out.println("BB:::   " + isEmpty("1","2",null,"4"));
    }

    private boolean isEmpty(String... args) {
        boolean empty = false;
        for (String string : args) {
            empty = (empty || TextUtils.isEmpty(string));
        }
        return empty;
    }
}