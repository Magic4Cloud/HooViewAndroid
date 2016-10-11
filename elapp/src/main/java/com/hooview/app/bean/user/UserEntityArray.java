/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.bean.user;

import java.util.List;

import com.hooview.app.bean.BaseEntityArray;

public class UserEntityArray extends BaseEntityArray {

    private List<UserEntity> users;

    public void setUsers(List<UserEntity> users) {
        this.users = users;
    }

    public List<UserEntity> getUsers() {
        return users;
    }
}
