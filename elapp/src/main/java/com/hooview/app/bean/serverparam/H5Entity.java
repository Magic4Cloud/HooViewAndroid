/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.bean.serverparam;

public class H5Entity {
    private String url;
    private String title;
    private String description;

    private String contactinfo;
    private String freeuserinfo;

    public String getContactinfo() {
        return contactinfo;
    }

    public void setContactinfo(String contactinfo) {
        this.contactinfo = contactinfo;
    }

    public String getFreeuserinfo() {
        return freeuserinfo;
    }

    public void setFreeuserinfo(String freeuserinfo) {
        this.freeuserinfo = freeuserinfo;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
