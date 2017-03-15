package com.easyvaas.common.gift.action;

import com.easyvaas.common.gift.action.type.AnimType;
import com.easyvaas.common.gift.action.type.FromType;
import com.easyvaas.common.gift.action.type.Type;
import com.easyvaas.common.gift.animator.GiftAnimator;
import com.easyvaas.common.gift.bean.GiftEntity;

/**
 * 通知动画Action
 *
 * Created by LiFZhe on 4/19/16.
 */
public class NotificationAction extends Action {
    private Image senderIcon;
    private String giftName;
    private Image giftIcon;
    private Image giftPicture;
    private boolean isEndAlignment;
    private int index;

    public NotificationAction(GiftEntity entity, boolean isEndAlignment) {
        this.fromType = FromType.LOCAL;
        setAnimType(entity);
        this.senderName = entity.getNickname();
        this.senderIcon = new Image(entity.getUserLogo(), GiftAnimator.prefix, GiftAnimator.suffix);
        this.giftName = entity.getGiftName();
        this.giftIcon = new Image(entity.getGiftPicUrl(), GiftAnimator.prefix, GiftAnimator.suffix);
        this.giftPicture = new Image(entity.getGiftAniUrl(), GiftAnimator.prefix, GiftAnimator.suffix);
        this.isEndAlignment = isEndAlignment;
        this.type = Type.NOTIFICATION;
        this.senderID = entity.getName();
    }

    public NotificationAction(GiftEntity entity, int index, boolean isEndAlignment) {
        this.fromType = FromType.REMOTE;
        setAnimType(entity);
        this.senderName = entity.getNickname();
        this.senderIcon = new Image(entity.getUserLogo(), GiftAnimator.prefix, GiftAnimator.suffix);
        this.giftName = entity.getGiftName();
        this.giftIcon = new Image(entity.getGiftPicUrl(), GiftAnimator.prefix, GiftAnimator.suffix);
        this.giftPicture = new Image(entity.getGiftAniUrl(), GiftAnimator.prefix, GiftAnimator.suffix);
        this.index = index;
        this.isEndAlignment = isEndAlignment;
        this.type = Type.NOTIFICATION;
        this.senderID = entity.getName();
    }

    public void setAnimType(GiftEntity entity) {
        if (entity.getGiftType() == GiftEntity.GIFT_TYPE_GIFT) {
            animType = AnimType.NOTIFICATION;
        } else if (entity.getGiftType() == GiftEntity.GIFT_TYPE_EXPRESSION) {
            animType = AnimType.EMOJI;
        }
    }

    public Image getSenderIcon() {
        return senderIcon;
    }

    public String getGiftName() {
        return giftName;
    }

    public Image getGiftIcon() {
        return giftIcon;
    }

    public Image getGiftPicture() {
        return giftPicture;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isEndAlignment() {
        return isEndAlignment;
    }

    public boolean isStartAlignment() {
        return index == 0;
    }

    public void setEndAlignment(boolean endAlignment) {
        isEndAlignment = endAlignment;
    }
}
