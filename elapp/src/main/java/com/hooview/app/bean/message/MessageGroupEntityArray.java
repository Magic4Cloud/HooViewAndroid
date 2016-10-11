/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.bean.message;

import java.util.List;

import com.hooview.app.bean.BaseEntityArray;

public class MessageGroupEntityArray extends BaseEntityArray {

    private List<MessageGroupEntity> groups;
    private List<MessageGroupEntity> messages;

    public List<MessageGroupEntity> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageGroupEntity> messages) {
        this.messages = messages;
    }

    public List<MessageGroupEntity> getGroups() {
        return groups;
    }

    public void setGroups(List<MessageGroupEntity> groups) {
        this.groups = groups;
    }
}
