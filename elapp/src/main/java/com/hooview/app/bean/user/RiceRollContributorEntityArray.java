/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.bean.user;

import java.util.List;

import com.hooview.app.bean.BaseEntityArray;

public class RiceRollContributorEntityArray extends BaseEntityArray {
    private List<RankUserEntity> users;

    public List<RankUserEntity> getUsers() {
        return users;
    }

    public void setUsers(List<RankUserEntity> users) {
        this.users = users;
    }
}
