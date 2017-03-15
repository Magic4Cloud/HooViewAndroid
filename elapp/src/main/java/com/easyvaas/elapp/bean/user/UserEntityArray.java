/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.bean.user;

import java.util.List;

import com.easyvaas.elapp.bean.BaseEntityArray;

public class UserEntityArray extends BaseEntityArray {

    private List<UserEntity> users;

    public void setUsers(List<UserEntity> users) {
        this.users = users;
    }

    public List<UserEntity> getUsers() {
        return users;
    }
}
