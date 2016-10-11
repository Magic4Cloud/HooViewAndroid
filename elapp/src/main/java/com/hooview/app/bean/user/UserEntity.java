/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.bean.user;

import java.io.Serializable;

public class UserEntity extends BaseUserEntity implements Serializable {
    public static final int FANED = 1;
    public static final int UN_FANED = 0;

    public static final int SUBSCRIBED = 1;
    public static final int UN_SUBSCRIBED = 0;

    private int faned;

    private int subscribed;

    private boolean selected;
    private String pinyin;
    private String sortLetter;
    private int pinned;

    public static final int IS_PINNED = 1;
    public static final int IS_PINNED_LIST_HEADER = 110;  // Special value for list custom header item type

    public int getPinned() {
        return pinned;
    }

    public void setPinned(int pinned) {
        this.pinned = pinned;
    }

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

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void setSubscribed(int subscribed) {
        this.subscribed = subscribed;
    }

    public int getSubscribed() {
        return subscribed;
    }

    public int getFaned() {
        return faned;
    }

    public void setFaned(int faned) {
        this.faned = faned;
    }
}
