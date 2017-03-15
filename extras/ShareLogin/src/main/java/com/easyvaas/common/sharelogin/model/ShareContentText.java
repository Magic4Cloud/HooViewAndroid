package com.easyvaas.common.sharelogin.model;

import com.easyvaas.common.sharelogin.data.ShareConstants;

public class ShareContentText extends ShareContent {
    private String content;

    public ShareContentText(String content) {
        this.content = content;
    }

    @Override
    public String getContent() {
        return content;
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
        return null;
    }

    @Override
    public String getMusicUrl() {
        return null;
    }

    @Override
    public int getShareWay() {
        return ShareConstants.SHARE_WAY_TEXT;
    }

}