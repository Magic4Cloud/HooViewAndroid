package com.easyvaas.common.gift.action;

public class Image {
    private String url;

    public Image(String url, String urlPrefix, String urlSuffix) {
        /*if (!TextUtils.isEmpty(url) && !url.startsWith("http")) {
            this.url = urlPrefix + url + urlSuffix;
        } else {
            this.url = url;
        }*/
        this.url = url;
    }

    public String url() {
        return url;
    }

    @Override
    public String toString() {
        return "Image{" +
                "url='" + url + '\'' +
                '}';
    }
}
