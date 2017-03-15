/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.bean.chat;

public class ChatGiftEntity extends ChatUserEntity {
    private long gid;  // gift id
    private int gtp;    // gift type
    private String gnm; // gift name
    private String glg; // gift log
    private int gct;   // gift count
    private String goodsPicUrl;
    private String goodsAniUrl;
    private int anitype;

    public int getAnitype() {
        return anitype;
    }

    public void setAnitype(int anitype) {
        this.anitype = anitype;
    }

    public String getGoodsAniUrl() {
        return goodsAniUrl;
    }

    public void setGoodsAniUrl(String goodsAniUrl) {
        this.goodsAniUrl = goodsAniUrl;
    }

    public String getGoodsPicUrl() {
        return goodsPicUrl;
    }

    public void setGoodsPicUrl(String goodsPicUrl) {
        this.goodsPicUrl = goodsPicUrl;
    }

    public int getGtp() {
        return gtp;
    }

    public void setGtp(int gtp) {
        this.gtp = gtp;
    }

    public String getGnm() {
        return gnm;
    }

    public void setGnm(String gnm) {
        this.gnm = gnm;
    }

    public long getGid() {
        return gid;
    }

    public void setGid(long gid) {
        this.gid = gid;
    }

    public String getGlg() {
        return glg;
    }

    public void setGlg(String glg) {
        this.glg = glg;
    }

    public int getGct() {
        return gct;
    }

    public void setGct(int gct) {
        this.gct = gct;
    }
}
