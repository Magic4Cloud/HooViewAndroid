package com.easyvaas.elapp.event;


public class ChatLiveInputEvent {
    public static final int ACTION_SHOW_INPUT = 0;
    public int action;

    public ChatLiveInputEvent(int action) {
        this.action = action;
    }
}
