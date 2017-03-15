/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.utils;

import android.content.Context;
import android.text.TextUtils;

import com.hooview.app.R;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.regex.Pattern;

public class StringUtil {

    /**
     * 字符串为“0” 或 “” 或 null 返回true
     *
     * @param str
     * @return
     */
    public static boolean isEmptyZero(String str) {
        return str == null || "".equals(str) || "0".equals(str);
    }


    public static String getStockPercent(double percent) {
        return (percent > 0 ? "+" + getBigDecimal(percent) : getBigDecimal(percent)) + "%";
    }

    /**
     * 小数点保留两位
     *
     * @param d 要保留的小数点
     * @return 格式完后的小数
     */
    public static String getBigDecimal(double d) {
        if (Double.isNaN(d)) {
            return "0.00";
        } else {
            BigDecimal bd = new BigDecimal(d);
            bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
            return bd.toString();
        }
    }

    /**
     * 截取字符串
     *
     * @param number 字符串
     * @return 截掉点后面的数字
     */
    public static String getString(String number) {
        String[] strs = null;
        if (!"".equals(number)) {
            strs = number.split("[.]");
        }
        return strs == null ? "" : strs[0];
    }

    /**
     * 截取单独的零，有是小数
     *
     * @param number
     * @return
     */
    public static String cutString(String number) {
        if (!"".equals(number) && number.contains(".0")) {
            String[] strs = number.split("[.]");
            return strs[0];
        } else {
            return number;
        }
    }

    /**
     * 对get请求文字编码
     *
     * @param text
     * @return
     */
    public static String URLEncoders(String text) {
        String encoderStr = null;
        if (!TextUtils.isEmpty(text)) {
            try {
                encoderStr = URLEncoder.encode(text, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return encoderStr;
    }

    /**
     * 截取时间，保留年月日
     */
    public static String getSplit(String text) {
        String split = null;
        if (!TextUtils.isEmpty(text) && text.length() <= 19) {
            split = text.substring(0, 10);
        }
        return split;
    }

    /**
     * 匹配是否纯数字
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    public static String splitString(String phone) {
        String[] str = null;
        if (!TextUtils.isEmpty(phone)) {
            str = phone.split("_");
        }
        return str == null || str.length == 0 ? "" : str[1];
    }

    public static String fmtMicrometer(String text) {
        DecimalFormat df = null;
        if (text.indexOf(".") > 0) {
            if (text.length() - text.indexOf(".") - 1 == 0) {
                df = new DecimalFormat("###,##0.");
            } else if (text.length() - text.indexOf(".") - 1 == 1) {
                df = new DecimalFormat("###,##0.0");
            } else {
                df = new DecimalFormat("###,##0.00");
            }
        } else {
            df = new DecimalFormat("###,##0");
        }
        double number = 0.0;
        try {
            number = Double.parseDouble(text);
        } catch (Exception e) {
            number = 0.0;
        }
        return df.format(number);
    }

    public static String getShortCount(Context context, int count) {

        if (count <= 0) {
            return "0";
        } else if (count < 100000) {
            return count + "";
            //} else if (count < 1000) {
            //    return count + "";
            //} else if (count >= 1000 && count < 10000) {
            //    return context.getString(R.string.unit_k, count / 1000.0f);
        } else if (count >= 100000) {
            return context.getString(R.string.unit_100k, count / 10000.0f);
        }

        return "0";
    }

    public static String getShortDistance(Context context, float distance) {
        if (distance <= 0) {
            return "";
        } else if (distance < 1000) {
            return context.getString(R.string.unit_m, distance);
        } else if (distance < 10000000) {
            return context.getString(R.string.unit_km, distance / 1000.0f);
        } else {
            return context.getString(R.string.unit_10k_km, distance / 10000000.0f);
        }
    }

    public static String[] parseFullPhoneNumber(String fullPhoneNumber) {
        if (TextUtils.isEmpty(fullPhoneNumber)) {
            return new String[]{};
        } else if (!fullPhoneNumber.contains("_")) {
            return new String[]{fullPhoneNumber};
        }
        return fullPhoneNumber.split("_");
    }

    public static String formatThousand(int srcNumber) {
        return new DecimalFormat(",###").format(srcNumber);
    }

    public static String formatTenThousand(float srcNumber) {
        float f = srcNumber / 10000f;
        return new DecimalFormat("##0.0").format(f);
    }
}
