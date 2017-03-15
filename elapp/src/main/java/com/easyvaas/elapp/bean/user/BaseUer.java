/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.bean.user;

import java.io.Serializable;

class BaseUer implements Serializable{
    private String name;
    private String logourl;
    private String nickname;
    private String gender;
    private int vip;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogourl() {
        return logourl;
    }

    public void setLogourl(String logourl) {
        this.logourl = logourl;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getVip() {
        return vip;
    }

    public void setVip(int vip) {
        this.vip = vip;
    }

    @Override
    public String toString() {
        return "BaseUer{" +
                "gender='" + gender + '\'' +
                ", name='" + name + '\'' +
                ", logourl='" + logourl + '\'' +
                ", nickname='" + nickname + '\'' +
                ", vip=" + vip +
                '}';
    }
}
