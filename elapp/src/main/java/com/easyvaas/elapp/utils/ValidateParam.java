/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.easyvaas.elapp.net.ApiConstant;

/**
 * 参数校验
 */
public class ValidateParam {

    /**
     * 校验用户名判断：头部尾部不能使空格，不能是中文，不能使全角，不能有星号，长度6-16
     * 其实 中文 和 全角 的 new String(charStr.getBytes("UTF-8"),"8859_1").getBytes().length=6
     * 字母、数字、特殊符号new String(charStr.getBytes("UTF-8"),"8859_1").getBytes().length=1
     */
    public static boolean validateUserName(String username) {
        /**
         * 长度6-16
         */
        if (!((username.length() >= 6) && (username.length() <= 16))) {
            return false;
        }
        /**
         * 头部不能有空格
         */
        if (username.substring(0, 1).equals(" ")) {//头部有空格
            System.out.println(33);
            return false;
        }
        /**
         * 未部不能有空格
         */
        if (username.endsWith(" ")) {//未部有空格
            return false;
        }
        if (username.contains(" ")) {//不能含有
            return false;
        }
        /**
         * 不能有中文
         */
        Pattern pattern1 = Pattern.compile("[^\\u4E00-\\u9FA5]*"); //不是中文
        Matcher m1 = pattern1.matcher(username);
        if (!m1.matches()) {
            return false;
        }
        /**
         * 不能有*号
         */
        pattern1 = Pattern.compile("[^\\*]*"); //不是*号
        m1 = pattern1.matcher(username);
        if (!m1.matches()) {
            return false;
        }

        /**
         * 不能是纯数字
         */
        pattern1 = Pattern.compile("^[1-9]\\d*$");
        m1 = pattern1.matcher(username);
        return !m1.matches();
    }

    /**
     * 校验用户密码：字母，数字，特殊字符，至少两种，6-16个字符
     */
    public static boolean validateUserPassword(String passport) {
        Pattern pattern1 = Pattern.compile("[0-9]*"); //数字[0-9]+?
        Matcher m1 = pattern1.matcher(passport);
        pattern1 = Pattern.compile(".*[a-zA-Z].*"); //字母
        Matcher m2 = pattern1.matcher(passport);
        pattern1 = Pattern.compile(".*[^a-zA-Z0-9].*"); //非字母和数字
        Matcher m3 = pattern1.matcher(passport);
        pattern1 = Pattern.compile("[^\\u4E00-\\u9FA5]*"); //不是中文
        Matcher m4 = pattern1.matcher(passport);
        //长度6-16
        if (!((passport.length() >= 6) && (passport.length() <= 16))) {
            return true;
        }
        /**
         纯数字
         */
        if (m1.matches()) {
            return true;
        }
        /**
         必须是：字母和数字
         **/
        if (m1.matches() && m2.matches() && m4.matches()) {
            return true;
        }
        /**
         必须是：数字和特殊符号
         **/
        if (m1.matches() && m3.matches() && m4.matches()) {
            return true;
        }
        /**
         必须是：字母和特殊符号
         **/
        return m2.matches() && m3.matches() && m4.matches() ;
    }

    public static int validateUserPasswords(String passport){
        passport=new String(passport.getBytes());
        java.util.regex.Pattern pattern2= java.util.regex.Pattern.compile("[\u4e00-\u9fa5]+");
        java.util.regex.Matcher match2 =pattern2.matcher(passport);
        if (match2.find() == true) {
            return 1;
        }

        java.util.regex.Pattern pattern3 = java.util.regex.Pattern.compile("[\\s\u4e00-\u9fa5]");
        java.util.regex.Matcher match3 = pattern3.matcher(passport);
        if (match3.find() == true) {
            return 2;
        }

        java.util.regex.Pattern pattern4 = java.util.regex.Pattern.compile("\\*");
        java.util.regex.Matcher match4 = pattern4.matcher(passport);
        if (match4.find()) {
            return 3;
        }

        return 0;
    }

    /**
     * 校验手机
     */
    public static boolean validatePhone(String phone) {
        if (!ApiConstant.VALUE_COUNTRY_CODE.equals(ApiConstant.VALUE_COUNTRY_CODE_CHINA)) {
            return true;
        }
        Pattern pattern1 = Pattern.compile("^1[34578]\\d{9}$");
        Matcher m1 = pattern1.matcher(phone);
        return m1.matches();
    }
}
