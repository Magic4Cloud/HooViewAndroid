/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.bean.pay;

public class PayRecordListEntity {

    private String time;
    private int barley;
    private int goodsid;
    private String goodsname;
    private int goodstype;
    private String description;

    private int rmb;
    private long riceroll;
    private int ecoin;

    public int getEcoin() {
        return ecoin;
    }

    public void setEcoin(int ecoin) {
        this.ecoin = ecoin;
    }

    public int getRmb() {
        return rmb;
    }

    public void setRmb(int rmb) {
        this.rmb = rmb;
    }

    public long getRiceroll() {
        return riceroll;
    }

    public void setRiceroll(long riceroll) {
        this.riceroll = riceroll;
    }
    public void setTime(String time) {
        this.time = time;
    }

    public void setBarley(int barley) {
        this.barley = barley;
    }

    public void setGoodsid(int goodsid) {
        this.goodsid = goodsid;
    }

    public void setGoodsname(String goodsname) {
        this.goodsname = goodsname;
    }

    public void setGoodstype(int goodstype) {
        this.goodstype = goodstype;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTime() {
        return time;
    }

    public int getBarley() {
        return barley;
    }

    public int getGoodsid() {
        return goodsid;
    }

    public String getGoodsname() {
        return goodsname;
    }

    public int getGoodstype() {
        return goodstype;
    }

    public String getDescription() {
        return description;
    }
}
