package com.easyvaas.common.sharelogin.model;

public abstract class ShareContent {

    /**
     * 分享的方式
     */
    public abstract int getShareWay();

    /**
     * 分享的描述信息
     */
    public abstract String getContent();

    /**
     * 分享的标题
     */
    public abstract String getTitle();

    /**
     * 获取跳转的链接
     */
    public abstract String getURL();

    /**
     * 分享的本地图片路径
     */
    public abstract String getImageUrl();

    /**
     * 音频url
     */
    public abstract String getMusicUrl();

}
