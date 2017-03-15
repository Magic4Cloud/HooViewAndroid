/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.utils;

import java.util.Comparator;

import com.easyvaas.elapp.bean.CountryCodeEntity;

/**
 * @author xiaanming
 */
public class PinyinComparatorCountry implements Comparator<CountryCodeEntity> {

    public int compare(CountryCodeEntity o1, CountryCodeEntity o2) {
        if (o1.getSortLetter().equals("@")
                || o2.getSortLetter().equals("#")) {
            return -1;
        } else if (o1.getSortLetter().equals("#")
                || o2.getSortLetter().equals("@")) {
            return 1;
        } else {
            return o1.getSortLetter().compareTo(o2.getSortLetter());
        }
    }

}
