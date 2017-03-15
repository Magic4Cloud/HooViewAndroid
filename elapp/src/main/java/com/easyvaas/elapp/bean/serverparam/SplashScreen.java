/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.bean.serverparam;

public class SplashScreen {
    public static final int TYPE_SPLASH_IMAGE = 0;
    public static final int TYPE_SPLASH_AD = 1;

    private int id;
    private String start_time;
    private String end_time;
    private int duration;  // unit seconds
    private String url;
    private int type;
    private String ad_url;

    public void setId(int id) {
        this.id = id;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public String getStart_time() {
        return start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public int getDuration() {
        return duration;
    }

    public String getUrl() {
        return url;
    }

    public int getType() {
        return type;
    }

    public String getAd_url() {
        return ad_url;
    }

    public void setAd_url(String ad_url) {
        this.ad_url = ad_url;
    }
}

