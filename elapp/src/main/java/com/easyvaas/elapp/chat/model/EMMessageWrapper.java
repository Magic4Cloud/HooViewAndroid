package com.easyvaas.elapp.chat.model;


import android.text.TextUtils;

import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;

public class EMMessageWrapper {
    public static final String MSG_TYPE_IMPORTANT = "hl";
    public static final String MSG_TYPE_STICK = "st";
    public static final String MSG_TYPE_NORMAL = "nor";
    public static final String MSG_TYPE_REPLY = "rp";
    public static final String MSG_TYPE_IMAGE = "image";

    public static final String EXTRA_MSG_TYPE = "tp";
    public static final String EXTRA_MSG_STICK = "st";
    public static final String EXTRA_MSG_HIGH_LIGHT = "hl";
    public static final String EXTRA_MSG_NORMAL = "nor";
    public static final String EXTRA_MSG_NICKNAME = "nk";
    public static final String EXTRA_MSG_REPLY_NICKNAME = "rnk";
    public static final String EXTRA_MSG_REPLY_CONTENT = "rct";
    public static final String EXTRA_MSG_AVATAR = "avatar";
    public static final String EXTRA_MSG_USER_ID = "userid";
    public static final String EXTRA_MSG_VIP = "vip";
    public String type;
    public String nickname;
    public String replyNickname;
    public String replyContent;
    public EMMessage emaMessage;
    public boolean isStickMessage;
    public String content;
    public String imageUrl;
    public boolean isAnchor;
    public boolean isSelf;
    public String avatar;
    public String userId;

    public EMMessageWrapper(EMMessage emaMessage) {
        this.emaMessage = emaMessage;
        type = emaMessage.getStringAttribute(EXTRA_MSG_TYPE, MSG_TYPE_NORMAL);
        isStickMessage = type.equals(EXTRA_MSG_STICK);
        replyNickname = emaMessage.getStringAttribute(EXTRA_MSG_REPLY_NICKNAME, "");
        replyContent = emaMessage.getStringAttribute(EXTRA_MSG_REPLY_CONTENT, "");
        nickname = emaMessage.getStringAttribute(EXTRA_MSG_NICKNAME, "");
        if (!TextUtils.isEmpty(replyNickname)) {
            type = MSG_TYPE_REPLY;
        }
        if (emaMessage.getType() == EMMessage.Type.IMAGE) {
            type = MSG_TYPE_IMAGE;
            imageUrl = ((EMImageMessageBody) emaMessage.getBody()).getRemoteUrl();
        } else {
            content = ((EMTextMessageBody) emaMessage.getBody()).getMessage();
        }
        avatar = emaMessage.getStringAttribute(EXTRA_MSG_AVATAR, "");
        userId = emaMessage.getStringAttribute(EXTRA_MSG_USER_ID, "");
        String vip = emaMessage.getStringAttribute(EXTRA_MSG_VIP, "0");
        if ("1".equals(vip)) {
            isAnchor = true;
        } else {
            isAnchor = false;
        }
    }

    @Override
    public String toString() {
        return "EMMessageWrapper{" +
                "type='" + type + '\'' +
                ", nickname='" + nickname + '\'' +
                ", replyNickname='" + replyNickname + '\'' +
                ", replyContent='" + replyContent + '\'' +
                ", emaMessage=" + emaMessage +
                ", isStickMessage=" + isStickMessage +
                ", content='" + content + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", isAnchor=" + isAnchor +
                ", isSelf=" + isSelf +
                '}';
    }
}
