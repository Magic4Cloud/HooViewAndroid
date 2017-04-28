package com.easyvaas.elapp.event;

/**
 * Date   2017/4/28
 * Editor  Misuzu
 * 清空历史记录事件 0 直播 1 新闻
 */

public class UserHistoryCleanEvent {

    private int type;

    public UserHistoryCleanEvent(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
