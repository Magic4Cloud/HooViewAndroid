package com.easyvaas.common.chat.bean;

import java.util.List;


public class FriendsArray extends BaseEntityArray {

    private List<UserEntity> friends;

    public void setFriends(List<UserEntity> friends) {
        this.friends = friends;
    }

    public List<UserEntity> getFriends() {
        return friends;
    }
}
