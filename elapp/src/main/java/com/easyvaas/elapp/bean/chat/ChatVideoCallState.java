package com.easyvaas.elapp.bean.chat;

public class ChatVideoCallState {
    public static final int STATE_REQUEST = 0;
    public static final int STATE_ACCEPT = 1;
    public static final int STATE_CANCEL = 2;
    public static final int STATE_END = 3;

    private int st;     // State
    private String cid; // callid
    private String nk;  // nickname
    private String lg;  // logourl

    public int getSt() {
        return st;
    }

    public void setSt(int st) {
        this.st = st;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
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
}
