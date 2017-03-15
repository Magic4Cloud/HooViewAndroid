/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.bean.pay;

public class MyAssetEntity {
    private long ecoin;
    private long riceroll;
    private int cash;
    private int feerate;

    public int getFeerate() {
        return feerate;
    }

    public void setFeerate(int feerate) {
        this.feerate = feerate;
    }

    public long getEcoin() {
        return ecoin;
    }

    public void setEcoin(long ecoin) {
        this.ecoin = ecoin;
    }

    public long getRiceroll() {
        return riceroll;
    }

    public void setRiceroll(long riceroll) {
        this.riceroll = riceroll;
    }

    public int getCash() {
        return cash;
    }

    public void setCash(int cash) {
        this.cash = cash;
    }
}
