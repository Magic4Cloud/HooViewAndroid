/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.bean.chat;

public class ChatCommentEntity {
    private String nk;   // Content
    private String rnm;  // Reply name
    private String rnk;  // Reply nickname
    private String avatar;
    private int vip;
    private String userId;
    private String tp;

    public String getNk() {
        return nk;
    }

    public void setNk(String nk) {
        this.nk= nk;
    }

    public String getRnm() {
        return rnm;
    }

    public void setRnm(String rnm) {
        this.rnm = rnm;
    }

    public String getRnk() {
        return rnk;
    }

    public void setRnk(String rnk) {
        this.rnk = rnk;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getVip() {
        return vip;
    }

    public void setVip(int vip) {
        this.vip = vip;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getType() {
        return tp;
    }

    public void setType(String type) {
        this.tp = type;
    }
}
