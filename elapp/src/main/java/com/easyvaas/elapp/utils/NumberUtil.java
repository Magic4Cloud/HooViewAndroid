package com.easyvaas.elapp.utils;

import java.text.DecimalFormat;

/**
 * Date    2017/4/21
 * Author  xiaomao
 */

public class NumberUtil {

    /**
     * 方法待定...
     * @param number
     * @return
     */
    public static String format(long number) {
        String result = "";
        if (number < 1000) {
            result = String.valueOf(number);
        } else if (number >= 1000 && number < 10000) {
            DecimalFormat df = new DecimalFormat("#,###");
            result = df.format(number);
        } else {
            result = String.valueOf(number);
        }
        return result;
    }
}
