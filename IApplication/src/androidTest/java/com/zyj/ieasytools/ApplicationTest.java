package com.zyj.ieasytools;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.zyj.ieasytools.library.utils.ZYJUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    public void testSDWrite() {
        String path = ZYJUtils.getExternalRootPath() + "/111.txt";
        File file = new File(path);
        try {
            FileWriter w = new FileWriter(file);
            w.write("11111111112222222233333333344444444555555555555");
            w.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testSDRead() {
        String path = ZYJUtils.getExternalRootPath() + "/111.txt";
        File file = new File(path);
        try {
            BufferedReader r = new BufferedReader(new FileReader(file));
            ZYJUtils.logD(getClass(),"Get: " + r.readLine());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}