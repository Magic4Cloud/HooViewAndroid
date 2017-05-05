package com.easyvaas.elapp.chat.model;

import java.io.Serializable;

import io.realm.RealmObject;

public class ChatRecord extends RealmObject implements Serializable {

    public String avatar;
    public String nickname;
    String id;

    public ChatRecord() {
    }

    public ChatRecord(String avatar, String nickname, String id) {
        this.avatar = avatar;
        this.nickname = nickname;
        this.id = id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

