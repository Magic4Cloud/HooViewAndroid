/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.bean.chat;

class ChatUserEntity {
    private String nm;   // Name
    private String nk;   // Nickname
    private String lg;   // User logo url
    private int v;

    public String getNm() {
        return nm;
    }

    public void setNm(String nm) {
        this.nm = nm;
    }

    public String getNk() {
        return nk;
    }

    public void setNk(String nk) {
        this.nk = nk;
    }

    public String getLg() {
        return lg;
    }

    public void setLg(String lg) {
        this.lg = lg;
    }

    public int getV() {
        return v;
    }

    public void setV(int v) {
        this.v = v;
    }
}
