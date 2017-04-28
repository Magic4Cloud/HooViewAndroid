package com.easyvaas.elapp.utils;

import java.text.DecimalFormat;

/**
 * Date    2017/4/21
 * Author  xiaomao
 */

public class NumberUtil {

    /**
     * 方法待定...
     *
     * @param number
     * @return
     */
    public static String format(long number) {
        DecimalFormat df = new DecimalFormat("###,###,###,###");
        return df.format(number);
    }
}
