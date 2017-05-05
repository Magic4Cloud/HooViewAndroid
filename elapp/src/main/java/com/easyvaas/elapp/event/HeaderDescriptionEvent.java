package com.easyvaas.elapp.event;

/**
 * Date   2017/5/5
 * Editor  Misuzu
 * 简介信息事件
 */

public class HeaderDescriptionEvent {

    private String description;

    public HeaderDescriptionEvent(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
