package com.easyvaas.common.gift.bean;

public class GiftEntity {
    public static final int GIFT_TYPE_EXPRESSION = GoodsEntity.TYPE_EMOJI;
    public static final int GIFT_TYPE_GIFT = GoodsEntity.TYPE_GIFT;

    public static final int ANI_TYPE_NO = GoodsEntity.ANI_TYPE_NO;
    public static final int ANI_TYPE_STATIC = GoodsEntity.ANI_TYPE_STATIC;
    public static final int ANI_TYPE_GIF = GoodsEntity.ANI_TYPE_GIF;
    public static final int ANI_TYPE_ZIP = GoodsEntity.ANI_TYPE_ZIP;
    public static final int ANI_TYPE_RED_PACK = GoodsEntity.ANI_TYPE_RED_PACK;

    private String name;  // name (sender)
    private String nickname;  // nickname (sender)
    private String userLogo; // user logo
    private long giftId;  // gift id
    private int giftType;    // gift type
    private String giftName; // gift name
    private int giftCount;   // gift count
    private String giftPicUrl;
    private String giftAniUrl;
    private int animationType;
    private boolean isDisplayed;

    public GiftEntity() {
    }

    public GiftEntity(int giftCount, String giftName) {
        this.giftCount = giftCount;
        this.giftName = giftName;
    }

    public boolean isDisplayed() {
        return isDisplayed;
    }

    public void setDisplayed(boolean displayed) {
        isDisplayed = displayed;
    }

    public int getAnimationType() {
        return animationType;
    }

    public void setAnimationType(int animationType) {
        this.animationType = animationType;
    }

    public String getGiftAniUrl() {
        return giftAniUrl;
    }

    public void setGiftAniUrl(String giftAniUrl) {
        this.giftAniUrl = giftAniUrl;
    }

    public String getGiftPicUrl() {
        return giftPicUrl;
    }

    public void setGiftPicUrl(String giftPicUrl) {
        this.giftPicUrl = giftPicUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getUserLogo() {
        return userLogo;
    }

    public void setUserLogo(String userLogo) {
        this.userLogo = userLogo;
    }

    public long getGiftId() {
        return giftId;
    }

    public void setGiftId(long giftId) {
        this.giftId = giftId;
    }

    public int getGiftType() {
        return giftType;
    }

    public void setGiftType(int giftType) {
        this.giftType = giftType;
    }

    public String getGiftName() {
        return giftName;
    }

    public void setGiftName(String giftName) {
        this.giftName = giftName;
    }

    public int getGiftCount() {
        return giftCount;
    }

    public void setGiftCount(int giftCount) {
        this.giftCount = giftCount;
    }

    @Override
    public String toString() {
        return "GiftEntity{" +
                "animationType=" + animationType +
                ", name='" + name + '\'' +
                ", nickname='" + nickname + '\'' +
                ", userLogo='" + userLogo + '\'' +
                ", giftId=" + giftId +
                ", giftType=" + giftType +
                ", giftName='" + giftName + '\'' +
                ", giftCount=" + giftCount +
                ", giftPicUrl='" + giftPicUrl + '\'' +
                ", giftAniUrl='" + giftAniUrl + '\'' +
                '}';
    }
}
