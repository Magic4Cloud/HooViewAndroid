/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.bean;

public class BaseSortModel {

    private String pinyin;
    private String sortLetter;

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getSortLetter() {
        return sortLetter;
    }

    public void setSortLetter(String sortLetters) {
        this.sortLetter = sortLetters;
    }
}
