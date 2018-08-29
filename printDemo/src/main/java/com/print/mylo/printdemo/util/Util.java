package com.print.mylo.printdemo.util;

import android.text.TextUtils;
import android.widget.EditText;

/**
 * Created by mylo on 2017/7/4.
 */

public class Util {
    public static String toString(EditText et, String defaultValue) {
        try {
            if (TextUtils.isEmpty(et.getText().toString())) {
                return defaultValue;
            }
            return et.getText().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultValue;

    }

    public static int toInt(EditText et, int defaultValue) {
        try {
            return Integer.parseInt(et.getText().toString().trim());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultValue;
    }
}
