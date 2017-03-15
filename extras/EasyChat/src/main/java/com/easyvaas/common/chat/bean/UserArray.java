package com.easyvaas.common.chat.bean;

import java.util.List;


public class UserArray extends BaseEntityArray {
    private List<BaseUser> users;

    public List<BaseUser> getUsers() {
        return users;
    }

    public void setUsers(List<BaseUser> users) {
        this.users = users;
    }
}
