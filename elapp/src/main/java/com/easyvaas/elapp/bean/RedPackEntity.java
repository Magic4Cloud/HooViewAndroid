/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.bean;

import java.util.List;

import com.easyvaas.elapp.bean.chat.ChatRedPackUserEntity;

public class RedPackEntity {
    private String redpackid;
    private int amount;
    private int type;
    private String message;
    private int count;
    private int open;

    private List<ChatRedPackUserEntity> users;

    public String getRedpackid() {
        return redpackid;
    }

    public void setRedpackid(String redpackid) {
        this.redpackid = redpackid;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getOpen() {
        return open;
    }

    public void setOpen(int open) {
        this.open = open;
    }

    public List<ChatRedPackUserEntity> getUsers() {
        return users;
    }

    public void setUsers(List<ChatRedPackUserEntity> users) {
        this.users = users;
    }
}
