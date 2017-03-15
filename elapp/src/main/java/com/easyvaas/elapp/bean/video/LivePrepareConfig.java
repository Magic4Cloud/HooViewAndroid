/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.bean.video;

import java.io.Serializable;

public class LivePrepareConfig implements Serializable {
    private String vid;
    private String uri;
    private String liveTitle;
    private String liveShareUrl;
    private boolean isContinueRecord;
    private boolean isShowLocation;
    private boolean isUseFrontCamera;
    private boolean isBeauty;
    private int videoLimitType;
    private int shareType;
    private String customThumbPath = "";
    private String chatImUsername = "";
    private String chatUserId = "";
    private String videoPassword = "";
    private int videoPrice;
    private int chatType;
    private String topicId;

    public int getChatType() {
        return chatType;
    }

    public void setChatType(int chatType) {
        this.chatType = chatType;
    }

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getLiveTitle() {
        return liveTitle;
    }

    public void setLiveTitle(String liveTitle) {
        this.liveTitle = liveTitle;
    }

    public String getLiveShareUrl() {
        return liveShareUrl;
    }

    public void setLiveShareUrl(String liveShareUrl) {
        this.liveShareUrl = liveShareUrl;
    }

    public boolean isContinueRecord() {
        return isContinueRecord;
    }

    public void setIsContinueRecord(boolean isContinueRecord) {
        this.isContinueRecord = isContinueRecord;
    }

    public boolean isShowLocation() {
        return isShowLocation;
    }

    public void setIsShowLocation(boolean isShowLocation) {
        this.isShowLocation = isShowLocation;
    }

    public int getVideoLimitType() {
        return videoLimitType;
    }

    public void setVideoLimitType(int videoLimitType) {
        this.videoLimitType = videoLimitType;
    }

    public String getCustomThumbPath() {
        return customThumbPath;
    }

    public void setCustomThumbPath(String customThumbPath) {
        this.customThumbPath = customThumbPath;
    }

    public String getChatImUsername() {
        return chatImUsername;
    }

    public void setChatImUsername(String chatImUsername) {
        this.chatImUsername = chatImUsername;
    }

    public String getChatUserId() {
        return chatUserId;
    }

    public void setChatUserId(String chatUserId) {
        this.chatUserId = chatUserId;
    }

    public int getShareType() {
        return shareType;
    }

    public void setShareType(int shareType) {
        this.shareType = shareType;
    }

    public boolean isUseFrontCamera() {
        return isUseFrontCamera;
    }

    public void setIsUseFrontCamera(boolean isUseFrontCamera) {
        this.isUseFrontCamera = isUseFrontCamera;
    }

    public String getVideoPassword() {
        return videoPassword;
    }

    public void setVideoPassword(String videoPassword) {
        this.videoPassword = videoPassword;
    }

    public int getVideoPrice() {
        return videoPrice;
    }

    public void setVideoPrice(int videoPrice) {
        this.videoPrice = videoPrice;
    }

    public boolean isBeauty() {
        return isBeauty;
    }

    public void setBeauty(boolean beauty) {
        isBeauty = beauty;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }
}
