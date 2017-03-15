package com.easyvaas.common.chat.bean;

import com.easemob.chat.EMMessage;

public class GroupMessage {
    private String groupId;
    private long lastModifyTime;
    private String groupIcon;
    private EMMessage lastMessage;
    private int unReadMsg;

    public int getUnReadMsg() {
        return unReadMsg;
    }

    public void setUnReadMsg(int unReadMsg) {
        this.unReadMsg = unReadMsg;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public long getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(long lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public String getGroupIcon() {
        return groupIcon;
    }

    public void setGroupIcon(String groupIcon) {
        this.groupIcon = groupIcon;
    }

    public EMMessage getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(EMMessage lastMessage) {
        this.lastMessage = lastMessage;
    }
}
