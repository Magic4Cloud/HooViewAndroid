/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.bean.user;

import java.util.List;

import com.easyvaas.elapp.bean.BaseEntityArray;

public class RiceRollContributorEntityArray extends BaseEntityArray {
    private List<RankUserEntity> users;

    public List<RankUserEntity> getUsers() {
        return users;
    }

    public void setUsers(List<RankUserEntity> users) {
        this.users = users;
    }
}
