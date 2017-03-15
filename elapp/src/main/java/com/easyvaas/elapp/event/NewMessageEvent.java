package com.easyvaas.elapp.event;


public class NewMessageEvent {
    public boolean hasNewMessage;

    public NewMessageEvent(boolean hasNewMessage) {
        this.hasNewMessage = hasNewMessage;
    }
}
