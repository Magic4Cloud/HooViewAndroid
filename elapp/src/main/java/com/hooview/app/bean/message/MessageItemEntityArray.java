/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.bean.message;

import java.util.List;

import com.hooview.app.bean.BaseEntityArray;

public class MessageItemEntityArray extends BaseEntityArray {

    private List<MessageItemEntity> items;

    public void setItems(List<MessageItemEntity> items) {
        this.items = items;
    }

    public List<MessageItemEntity> getItems() {
        return items;
    }

}
