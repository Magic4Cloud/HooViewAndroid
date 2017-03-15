package com.easyvaas.elapp.event;


import com.hyphenate.chat.EMMessage;

import java.util.List;

public class ImageTextLiveMessageEvent {
    public static final int MSG_TYPE_MESSAGE = 1;
    public static final int MSG_TYPE_CMD = 2;

    public List<EMMessage> mEMMessageList;
    public int type = MSG_TYPE_MESSAGE;

    public ImageTextLiveMessageEvent(List<EMMessage> EMMessageList) {
        mEMMessageList = EMMessageList;
        type = MSG_TYPE_MESSAGE;
    }

    public ImageTextLiveMessageEvent(List<EMMessage> EMMessageList, int type) {
        mEMMessageList = EMMessageList;
        this.type = type;
    }
}
