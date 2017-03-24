package com.easyvaas.elapp.event;


public class LiveSearchStockEvent {
    public static final int TYPE_KEYBORDER_HIDE = 3001;
    public String keyword;
    public int type;

    public LiveSearchStockEvent(String keyword) {
        this.keyword = keyword;
    }

    public LiveSearchStockEvent(int type, String keyword) {
        this.type = type;
        this.keyword = keyword;
    }
}
