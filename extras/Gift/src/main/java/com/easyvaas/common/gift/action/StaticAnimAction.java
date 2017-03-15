package com.easyvaas.common.gift.action;

import com.easyvaas.common.gift.action.type.AnimType;
import com.easyvaas.common.gift.action.type.FromType;
import com.easyvaas.common.gift.action.type.Type;
import com.easyvaas.common.gift.animator.GiftAnimator;
import com.easyvaas.common.gift.bean.GiftEntity;

public class StaticAnimAction extends Action {
    public static StaticAnimAction lastAction = null;

    private Image giftPictureUrl;
    private int index;

    public StaticAnimAction(GiftEntity entity, boolean isAlignmentsEnd) {
        this.fromType = FromType.LOCAL;
        this.animType = AnimType.NORMAL;
        this.type = Type.ANIMATION;
        this.senderName = entity.getNickname();
        this.giftPictureUrl = new Image(entity.getGiftAniUrl(), GiftAnimator.prefix, GiftAnimator.suffix);

        if (isAlignmentsEnd) {
            lastAction = null;
        }

        this.senderID = entity.getName();
    }

    public StaticAnimAction(GiftEntity entity, int index) {
        this.fromType = FromType.REMOTE;
        this.animType = AnimType.NORMAL;
        this.type = Type.ANIMATION;
        this.senderName = entity.getNickname();
        this.index = index;
        this.giftPictureUrl = new Image(entity.getGiftAniUrl(), GiftAnimator.prefix, GiftAnimator.suffix);
        this.senderID = entity.getName();
    }

    public boolean isShow() {
        if (fromType == FromType.LOCAL) {
            boolean isShow = lastAction == null;
            lastAction = this;
            return isShow;
        } else
            return fromType == FromType.REMOTE && index == 0;
    }

    public Image getGiftPictureUrl() {
        return giftPictureUrl;
    }
}
