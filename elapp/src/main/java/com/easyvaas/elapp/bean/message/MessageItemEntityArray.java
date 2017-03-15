/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.bean.message;

import java.util.List;

import com.easyvaas.elapp.bean.BaseEntityArray;

public class MessageItemEntityArray extends BaseEntityArray {

    private List<MessageItemEntity> items;

    public void setItems(List<MessageItemEntity> items) {
        this.items = items;
    }

    public List<MessageItemEntity> getItems() {
        return items;
    }

}
