/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.bean.user;

public class RankUserEntity extends BaseUer {
    public static final int IS_HEADER = 1;
    public static final int IS_FOOTER = 2;
    public static final int IS_TITLE_PINNED = 4;

    public final static String ASSETS_RANK_TYPE_ALL = "all";
    public final static String ASSETS_RANK_TYPE_SEND = "send";
    public final static String ASSETS_RANK_TYPE_RECEIVE = "receive";
    public final static String ASSETS_RANK_TYPE_WEEK_ALL = "weekall";
    public final static String ASSETS_RANK_TYPE_WEEK_SEND = "weeksend";
    public final static String ASSETS_RANK_TYPE_WEEK_RECEIVE = "weekreceive";
    public final static String ASSETS_RANK_TYPE_MONTH_ALL = "monthall";
    public final static String ASSETS_RANK_TYPE_MONTH_SEND = "monthsend";
    public final static String ASSETS_RANK_TYPE_MONTH_RECEIVE = "monthreceive";

    private long costecoin;
    private String type;
    private int pinned;
    private int rank;

    // Rice Roll Contributor
    private int itemType;
    private long riceroll;

    public int getPinned() {
        return pinned;
    }

    public void setPinned(int pinned) {
        this.pinned = pinned;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setCostecoin(long costecoin) {
        this.costecoin = costecoin;
    }

    public long getCostecoin() {
        return costecoin;
    }

    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public long getRiceroll() {
        return riceroll;
    }

    public void setRiceroll(long riceroll) {
        this.riceroll = riceroll;
    }
}
