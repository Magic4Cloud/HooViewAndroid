package com.easyvaas.elapp.bean.search;


import com.easyvaas.elapp.bean.BaseListModel;
import com.easyvaas.elapp.bean.video.VideoEntity;

import java.util.List;

public class SearchLiveModel extends BaseListModel {
    List<VideoEntity> videos;

    public List<VideoEntity> getVideos() {
        return videos;
    }

    public void setVideos(List<VideoEntity> videos) {
        this.videos = videos;
    }
}
