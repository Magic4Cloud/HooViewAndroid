package com.easyvaas.elapp.utils;

import java.math.BigDecimal;
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

    /**
     * 直播
     */
    public static String formatLive(long number) {
        if (number < 10000) {
            DecimalFormat df = new DecimalFormat("#,###");
            return df.format(number);
        } else {
            double d = (double) number / (double) 10000;
            BigDecimal bd = new BigDecimal(d);
            return bd.setScale(1, BigDecimal.ROUND_HALF_UP).toString() + "万";
        }
    }
}
