package com.easyvaas.elapp.bean.user;

import com.easyvaas.elapp.bean.BaseListBean;
import com.easyvaas.elapp.bean.video.VideoEntity;

import java.util.List;

/**
 * Date    2017/4/20
 * Author  xiaomao
 */

public class UserHistoryTestModel extends BaseListBean {

    private List<VideoEntity> videolive;

    public List<VideoEntity> getVideolive() {
        return videolive;
    }

    public void setVideolive(List<VideoEntity> videolive) {
        this.videolive = videolive;
    }
}
