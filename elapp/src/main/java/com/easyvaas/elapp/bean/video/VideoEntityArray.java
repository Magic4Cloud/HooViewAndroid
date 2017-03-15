/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.bean.video;

import java.util.List;

import com.easyvaas.elapp.bean.BaseEntityArray;

public class VideoEntityArray extends BaseEntityArray {
    public static final String TYPE_ALL = "all";
    public static final String TYPE_VIDEO = "video";
    public static final String TYPE_AUDIO = "audio";

    private List<VideoEntity> videos;

    public void setVideos(List<VideoEntity> videos) {
        this.videos = videos;
    }

    public List<VideoEntity> getVideos() {
        return videos;
    }

}
