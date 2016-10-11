/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.bean.user;

import java.util.ArrayList;
import java.util.List;

import com.hooview.app.bean.BaseEntityArray;
import com.hooview.app.bean.chat.ChatUser;

public class BaseUserEntityArray extends BaseEntityArray {

    private List<BaseUserEntity> users;

    public void setUsers(List<BaseUserEntity> users) {
        this.users = users;
    }

    public List<BaseUserEntity> getUsers() {
        return users;
    }

    public List<ChatUser> convertChatUsers() {
        List<ChatUser> chatUsers = new ArrayList<>(users != null ? users.size() : 0);
        for (BaseUserEntity user : users) {
            chatUsers.add(user.convertChatUser());
        }
        return chatUsers;
    }
}
