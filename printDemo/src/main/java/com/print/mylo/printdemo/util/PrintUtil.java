package com.print.mylo.printdemo.util;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mylo on 2017/8/9.
 * 打印相关的util
 */

public class PrintUtil {


    public static int getPrintLength(String value) {
        int valueLength = 0;
        for (int i = 0; i < value.length(); i++) {
            String temp = value.substring(i, i + 1);
            if (isChinese(temp)) {
                valueLength += 2;
            } else {
                valueLength += 1;
            }
        }
        return valueLength;
    }

    /**
     * 可能会返回null
     *
     * @param value
     * @param splitLength
     * @return
     */
    public static String[] splitStringByPrintLength(String value, int splitLength) {
        int valueLength = 0;
        for (int i = 0; i < value.length(); i++) {
            String temp = value.substring(i, i + 1);
            if (isChinese(temp)) {
                valueLength += 2;
            } else {
                valueLength += 1;
            }
            if (valueLength >= splitLength) {
                String[] strs = new String[2];
                strs[0] = value.substring(0, i + 1);
                strs[1] = value.equals(strs[0]) ? "" : value.substring(i + 1);
                return strs;
            }
        }
        return null;
    }

    public static boolean isChinese(String temp) {
        String chinese = "[\u4e00-\u9fa5]";
        return temp.matches(chinese);
    }

    public static int getChineseCharCount(String string) {
        int count = 0;
        String regex = "[\u4e00-\u9fa5]";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(string);
        while (m.find()) {
            count++;
            System.out.print(m.group() + " ");
        }
        return count;
    }

    public static String getDecimalString(Double d) {
        BigDecimal b = new BigDecimal(d);
        return String.valueOf(b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
    }
}
