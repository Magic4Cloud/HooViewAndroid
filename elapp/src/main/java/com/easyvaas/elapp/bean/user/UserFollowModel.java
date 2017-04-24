package com.easyvaas.elapp.bean.user;

import com.easyvaas.elapp.bean.BaseListBean;

import java.util.ArrayList;

/**
 * Date   2017/4/21
 * Editor  Misuzu
 */

public class UserFollowModel extends BaseListBean {

    private ArrayList<UserFollow> users;

    public ArrayList<UserFollow> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<UserFollow> users) {
        this.users = users;
    }
}
