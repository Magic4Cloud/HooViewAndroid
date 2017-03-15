package com.easyvaas.common.chat.bean;

import com.easemob.chat.EMContact;

public class ChatUser extends EMContact {
    private int unreadMsgCount;
    private String header;
    private String avatar;
    private String userId;

    public ChatUser() {
    }

    public ChatUser(String username) {
        this.username = username;
    }

    public ChatUser(String username, String avatar, String nickName) {
        this.username = username;
        this.avatar = avatar;
        this.nick = nickName;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public int getUnreadMsgCount() {
        return unreadMsgCount;
    }

    public void setUnreadMsgCount(int unreadMsgCount) {
        this.unreadMsgCount = unreadMsgCount;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public int hashCode() {
        return 17 * getUsername().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof ChatUser)) {
            return false;
        }
        return getUsername().equals(((ChatUser) o).getUsername());
    }

    @Override
    public String toString() {
        return nick == null ? username : nick;
    }
}
