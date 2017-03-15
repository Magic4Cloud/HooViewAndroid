/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.bean.chat;

public class ChatRedPackInfoEntity extends ChatUserEntity {
    private String hid;
    private String htp;
    private String hnm;
    private String hlg;
    private int sum;
    private int cnt;

    public String getHid() {
        return hid;
    }

    public void setHid(String hid) {
        this.hid = hid;
    }

    public String getHtp() {
        return htp;
    }

    public void setHtp(String htp) {
        this.htp = htp;
    }

    public String getHnm() {
        return hnm;
    }

    public void setHnm(String hnm) {
        this.hnm = hnm;
    }

    public String getHlg() {
        return hlg;
    }

    public void setHlg(String hlg) {
        this.hlg = hlg;
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }

    public int getCnt() {
        return cnt;
    }

    public void setCnt(int cnt) {
        this.cnt = cnt;
    }
}
