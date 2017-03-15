/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.bean.message;

public class MessageGroupEntity {
    public static final int TYPE_OFFICIAL_MESSAGE = 0;
    public static final int TYPE_FOLLOWER_MESSAGE = 1;
    public static final int TYPE_CHAT_GROUP = 100000;
    public static final int GROUP_ID_OFFICIAL = 0;
    public static final int GROUP_ID_FOLLOWER = 1;
    public static final int GROUP_ID_CHAT_GROUP = 100000;

    private long groupid;
    private String title;
    private String desc;
    private String icon;
    private int type;
    private int unread;
    private int total;
    private String update_time;
    private LatestContentEntity lastest_content;

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public long getGroupid() {
        return groupid;
    }

    public void setGroupid(long groupid) {
        this.groupid = groupid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getUnread() {
        return unread;
    }

    public void setUnread(int unread) {
        this.unread = unread;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public String getIcon() {
        return icon;
    }

    public String getTitle() {
        return title;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setLastest_content(LatestContentEntity lastest_content) {
        this.lastest_content = lastest_content;
    }

    public LatestContentEntity getLastest_content() {
        return lastest_content;
    }

    public class LatestContentEntity {
        private int type;
        private MessageFollowUser data;

        public void setType(int type) {
            this.type = type;
        }

        public void setData(MessageFollowUser data) {
            this.data = data;
        }

        public int getType() {
            return type;
        }

        public MessageFollowUser getData() {
            return data;
        }
    }
}
