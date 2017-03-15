package com.easyvaas.elapp.event;


import com.easyvaas.elapp.bean.chat.ChatComment;

public class NewCommentEvent {
    public ChatComment chatComment;

    public NewCommentEvent(ChatComment chatComment) {
        this.chatComment = chatComment;
    }
}
