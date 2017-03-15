package com.easyvaas.common.sharelogin.model;

import com.easyvaas.common.sharelogin.data.ShareConstants;

public class ShareContentPic extends ShareContent {
    private String imageUrl;

    public ShareContentPic(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String getContent() {
        return null;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public String getURL() {
        return null;
    }

    @Override
    public String getImageUrl() {
        return imageUrl;
    }

    @Override
    public String getMusicUrl() {
        return null;
    }

    @Override
    public int getShareWay() {
        return ShareConstants.SHARE_WAY_PIC;
    }
}
