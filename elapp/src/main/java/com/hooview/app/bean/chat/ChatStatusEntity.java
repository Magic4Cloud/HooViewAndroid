/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.bean.chat;

import com.easyvaas.common.gift.bean.GiftEntity;

public class ChatStatusEntity {
    private ChatCommentEntity exct;     // Comment's Extra Info
    private ChatBarrageEntity exbr;     // Barrage's Extra Info
    private ChatGiftEntity exgf;        // Gift's Extra Info
    private ChatRedPackInfoEntity exrp; // RedPack's Extra Info

    public ChatCommentEntity getExct() {
        return exct;
    }

    public void setExct(ChatCommentEntity exct) {
        this.exct = exct;
    }

    public ChatBarrageEntity getExbr() {
        return exbr;
    }

    public void setExbr(ChatBarrageEntity exbr) {
        this.exbr = exbr;
    }

    public void setExgf(ChatGiftEntity exgf) {
        this.exgf = exgf;
    }

    public ChatRedPackInfoEntity getExrp() {
        return exrp;
    }

    public void setExrp(ChatRedPackInfoEntity exrp) {
        this.exrp = exrp;
    }

    //============= Assemble Custom Comment by type & extra ==========

    public ChatComment getComment(String name, String content) {
        if (exct != null) {
            ChatComment comment = new ChatComment();
            comment.setNickname(exct.getNk());
            comment.setName(name);
            comment.setContent(content);
            comment.setReply_name(exct.getRnm());
            comment.setReply_nickname(exct.getRnk());
            return comment;
        }
        return null;
    }

    public ChatBarrage getBarrage(String name, String content) {
        if (exbr != null) {
            ChatBarrage barrage = new ChatBarrage();
            barrage.setName(name);
            barrage.setContent(content);
            barrage.setLogo(exbr.getLg());
            barrage.setNickname(exbr.getNk());
            return barrage;
        }
        return null;
    }

    public GiftEntity getGiftEntity(String name) {
        if (exgf != null) {
            GiftEntity giftEntity = new GiftEntity();
            giftEntity.setName(name);
            giftEntity.setNickname(exgf.getNk());
            giftEntity.setUserLogo(exgf.getLg());
            giftEntity.setGiftId(exgf.getGid());
            giftEntity.setGiftName(exgf.getGnm());
            giftEntity.setGiftPicUrl(exgf.getGlg());
            giftEntity.setGiftAniUrl(exgf.getGlg());
            giftEntity.setGiftCount(exgf.getGct());
            giftEntity.setAnimationType(GiftEntity.ANI_TYPE_STATIC);
            giftEntity.setGiftType(exgf.getGtp());
            return giftEntity;
        }
        return null;
    }

    public ChatRedPackInfo getRedPack(String name) {
        if (exrp != null) {
            ChatRedPackInfo redPackInfo = new ChatRedPackInfo();
            redPackInfo.setId(exrp.getHid());
            redPackInfo.setLogo(exrp.getHlg());
            redPackInfo.setName(exrp.getHnm());
            redPackInfo.setSenderNickName(exrp.getNk());
            redPackInfo.setNewRedPack(true);
            redPackInfo.setType(ChatRedPackInfo.TYPE_WATCHER);
            return redPackInfo;
        }
        return null;
    }
}
