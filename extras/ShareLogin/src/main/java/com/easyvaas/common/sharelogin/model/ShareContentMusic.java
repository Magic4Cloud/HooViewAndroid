package com.easyvaas.common.sharelogin.model;

import com.easyvaas.common.sharelogin.data.ShareConstants;

public class ShareContentMusic extends ShareContent {
    private String title;
    private String content;
    private String url;
    private String imageUrl;
    private String musicUrl;

    public ShareContentMusic(String title, String content, String url, String imageUrl, String musicUrl) {
        this.title = title;
        this.content = content;
        this.url = url;
        this.imageUrl = imageUrl;
        this.musicUrl = musicUrl;
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
    public String getImageUrl() {
        return imageUrl;
    }

    @Override
    public String getMusicUrl() {
        return musicUrl;
    }

    @Override
    public int getShareWay() {
        return ShareConstants.SHARE_WAY_MUSIC;
    }

}
