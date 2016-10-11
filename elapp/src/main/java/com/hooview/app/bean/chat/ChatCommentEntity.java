/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.bean.chat;

public class ChatCommentEntity {
    private String nk;   // Content
    private String rnm;  // Reply name
    private String rnk;  // Reply nickname

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
}
