package com.easyvaas.elapp.bean.imageTextLive;


import com.easyvaas.elapp.bean.user.UserInfoModel;

import java.io.Serializable;
import java.util.List;

public class ImageTextLiveRoomModel implements Serializable {

    private String id;
    private String ownerid;
    private String name;
    private int state;
    private int viewcount;
    private List<UserInfoModel.TagsEntity> tags;

    public List<UserInfoModel.TagsEntity> getTags() {
        return tags;
    }

    public void setTags(List<UserInfoModel.TagsEntity> tags) {
        this.tags = tags;
    }

    public UserInfoModel getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfoModel userInfo) {
        this.userInfo = userInfo;
    }

    private UserInfoModel userInfo;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwnerid() {
        return ownerid;
    }

    public void setOwnerid(String ownerid) {
        this.ownerid = ownerid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getViewcount() {
        return viewcount;
    }

    public void setViewcount(int viewcount) {
        this.viewcount = viewcount;
    }
}
