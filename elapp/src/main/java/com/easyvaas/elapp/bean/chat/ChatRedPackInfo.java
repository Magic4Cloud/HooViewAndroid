/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.bean.chat;

import java.util.ArrayList;
import java.util.List;

public class ChatRedPackInfo {
    public final static int TYPE_SYSTEM = 0;
    public final static int TYPE_WATCHER = 1;
    public final static int TYPE_OWNER = 2;

    private String id;
    private String name;        // name
    private String logo;        // logo
    private String senderNickName;
    private int duration;       // show duration, unit second
    private int currentUserAmount;
    private int type;
    private boolean isNewRedPack;
    private boolean isGetRedPack;
    private List<ChatRedPackUserEntity> users;

    public ChatRedPackInfo() {
        users = new ArrayList<>();
    }

    public boolean isGetRedPack() {
        return isGetRedPack;
    }

    public void setGetRedPack(boolean getRedPack) {
        isGetRedPack = getRedPack;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public List<ChatRedPackUserEntity> getUsers() {
        return users;
    }

    public void setUsers(List<ChatRedPackUserEntity> users) {
        this.users = users;
    }

    public int getCurrentUserAmount() {
        return currentUserAmount;
    }

    public void setCurrentUserAmount(int currentUserAmount) {
        this.currentUserAmount = currentUserAmount;
    }

    public boolean isNewRedPack() {
        return isNewRedPack;
    }

    public void setNewRedPack(boolean newRedPack) {
        isNewRedPack = newRedPack;
    }

    public int getType() {
        return type;
    }

    public String getSenderNickName() {
        return senderNickName;
    }

    public void setSenderNickName(String senderNickName) {
        this.senderNickName = senderNickName;
    }

    public void setType(int type) {
        this.type = type;
    }
}
