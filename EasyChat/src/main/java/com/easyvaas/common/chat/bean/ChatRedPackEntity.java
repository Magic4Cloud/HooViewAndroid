package com.easyvaas.common.chat.bean;

import java.io.Serializable;

public class ChatRedPackEntity implements Serializable {
    /*
    redpack_code：红包码，string
    redpack_value：红包价值，int
    redpack_count：红包个数，int
    redpack_name：红包名称，string
    redpack_logo：红包头像，string
     */
    private String code;
    private int value;
    private int count;
    private String name;
    private String logo;

    private String msgId;
    private String from;
    private boolean isOpened = false;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public boolean isOpened() {
        return isOpened;
    }

    public void setOpened(boolean opened) {
        isOpened = opened;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
