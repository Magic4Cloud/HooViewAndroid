package com.easyvaas.common.sharelogin.model;

import com.easyvaas.common.sharelogin.data.ShareConstants;

public class ShareContentWebpage extends ShareContent {
    private String title;
    private String content;
    private String url;
    private String imageUrl;

    public ShareContentWebpage(String title, String content,
            String url, String imageUrl) {
        this.title = title;
        this.content = content;
        this.url = url;
        this.imageUrl = imageUrl;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getURL() {
        return url;
    }

    @Override
    public int getShareWay() {
        return ShareConstants.SHARE_WAY_WEBPAGE;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    @Override
    public String getMusicUrl() {
        return null;
    }
}
