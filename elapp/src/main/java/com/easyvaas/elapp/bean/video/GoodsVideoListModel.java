package com.easyvaas.elapp.bean.video;

import com.easyvaas.elapp.bean.BaseListModel;

import java.util.List;


public class GoodsVideoListModel extends BaseListModel {
    private List<VideoEntity> videos;

    public List<VideoEntity> getVideos() {
        return videos;
    }

    public void setVideos(List<VideoEntity> videos) {
        this.videos = videos;
    }
}
