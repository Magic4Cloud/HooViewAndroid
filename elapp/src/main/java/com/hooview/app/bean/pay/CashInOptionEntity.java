/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.bean.pay;

public class CashInOptionEntity {
    public static final int PLATFORM_WEIXIN = 1;
    public static final int PLATFORM_ALIPAY = 7;
    
    public static final int PINNED_END = 5;

    private int rmb;
    private int ecoin;
    private int free;
    private int active;
    private int platform;
    private int device;
    private String productid;
    private boolean isChecked;
    
    private int pinned;

    public int getPinned() {
        return pinned;
    }

    public void setPinned(int pinned) {
        this.pinned = pinned;
    }

    public void setRmb(int rmb) {
        this.rmb = rmb;
    }

    public void setEcoin(int ecoin) {
        this.ecoin = ecoin;
    }

    public void setFree(int free) {
        this.free = free;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public void setPlatform(int platform) {
        this.platform = platform;
    }

    public void setDevice(int device) {
        this.device = device;
    }

    public void setProductid(String productid) {
        this.productid = productid;
    }

    public int getRmb() {
        return rmb;
    }

    public int getEcoin() {
        return ecoin;
    }

    public int getFree() {
        return free;
    }

    public int getActive() {
        return active;
    }

    public int getPlatform() {
        return platform;
    }

    public int getDevice() {
        return device;
    }

    public String getProductid() {
        return productid;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean checked) {
        isChecked = checked;
    }
}
