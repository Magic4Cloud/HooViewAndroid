/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.bean.chat;

public class ChatRedPackUserEntity {
    public static final int IS_BEST = 1;

    private String name;     // getter name
    private String nickname; // getter nickname
    private String logourl;      // getter logo
    private int ecoin;       // getter get money amount
    private int isbest;      // the maximum amount flag

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getLogourl() {
        return logourl;
    }

    public void setLogourl(String logourl) {
        this.logourl = logourl;
    }

    public int getEcoin() {
        return ecoin;
    }

    public void setEcoin(int ecoin) {
        this.ecoin = ecoin;
    }

    public int getIsbest() {
        return isbest;
    }

    public void setIsbest(int isbest) {
        this.isbest = isbest;
    }
}
