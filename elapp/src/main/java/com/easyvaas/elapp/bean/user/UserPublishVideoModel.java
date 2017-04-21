package com.easyvaas.elapp.bean.user;

import com.easyvaas.elapp.bean.BaseListBean;
import com.easyvaas.elapp.bean.imageTextLive.ImageTextLiveRoomModel;
import com.easyvaas.elapp.bean.video.VideoEntity;

import java.util.List;

/**
 * Date    2017/4/21
 * Author  xiaomao
 */

public class UserPublishVideoModel extends BaseListBean {

    private List<VideoEntity> videolive;
    private ImageTextLiveRoomModel textlive;

    public List<VideoEntity> getVideolive() {
        return videolive;
    }

    public void setVideolive(List<VideoEntity> videolive) {
        this.videolive = videolive;
    }

    public ImageTextLiveRoomModel getTextlive() {
        return textlive;
    }

    public void setTextlive(ImageTextLiveRoomModel textlive) {
        this.textlive = textlive;
    }
}
