/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.bean.user;

public class UserSettingEntity {
    public static final int IS_NOTICE_ALL = 1;
    public static final int IS_NOTICE_FOLLOWED = 1;
    public static final int IS_NOTICE_LIVING = 1;
    /**
     * disturb : 1
     * follow : 0
     * live : 1
     */
    private int disturb;
    private int follow;
    private int live;

    public void setDisturb(int disturb) {
        this.disturb = disturb;
    }

    public void setFollow(int follow) {
        this.follow = follow;
    }

    public void setLive(int live) {
        this.live = live;
    }

    public int getDisturb() {
        return disturb;
    }

    public int getFollow() {
        return follow;
    }

    public int getLive() {
        return live;
    }
}
