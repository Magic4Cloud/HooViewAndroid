package com.easyvaas.elapp.event;

/**
 * Date    2017/5/10
 * Author  xiaomao
 * 刷新第一次fragment
 */

public class MainRefreshEvent {

    public static final String TYPE_NEWS = "main_type_news";
    public static final String TYPE_LIVE = "main_type_live";
    public static final String TYPE_MARKET = "main_type_market";

    public String type;

    public MainRefreshEvent(String type) {
        this.type = type;
    }
}
