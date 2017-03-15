package com.easyvaas.elapp.bean.video;


import com.easyvaas.elapp.bean.BaseListModel;
import com.easyvaas.elapp.bean.user.UserInfoModel;

import java.io.Serializable;
import java.util.List;

public class TextLiveListModel extends BaseListModel implements Serializable {

    private List<StreamsEntity> hotstreams;
    private List<StreamsEntity> streams;

    public List<StreamsEntity> getHotstreams() {
        return hotstreams;
    }

    public void setHotstreams(List<StreamsEntity> hotstreams) {
        this.hotstreams = hotstreams;
    }

    public List<StreamsEntity> getStreams() {
        return streams;
    }

    public void setStreams(List<StreamsEntity> streams) {
        this.streams = streams;
    }


    public static class StreamsEntity implements Serializable {
        private String id;
        private String ownerid;
        private String name;
        private int viewcount;
        private UserInfoModel userEntity;

        public UserInfoModel getUserEntity() {
            return userEntity;
        }

        public void setUserEntity(UserInfoModel userEntity) {
            this.userEntity = userEntity;
        }

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

        public int getViewcount() {
            return viewcount;
        }

        public void setViewcount(int viewcount) {
            this.viewcount = viewcount;
        }
    }
}
