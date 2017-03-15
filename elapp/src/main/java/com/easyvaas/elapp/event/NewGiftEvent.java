package com.easyvaas.elapp.event;


import com.easyvaas.common.gift.bean.GiftEntity;

public class NewGiftEvent {
    public GiftEntity giftEntity;

    public NewGiftEvent(GiftEntity giftEntity) {
        this.giftEntity = giftEntity;
    }
}
