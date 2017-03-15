package com.easyvaas.elapp.bean.video;

import com.easyvaas.elapp.bean.BaseListModel;

import java.util.List;


public class LiveCommentModel extends BaseListModel {


    private List<PostsEntity> posts;

    public List<PostsEntity> getPosts() {
        return posts;
    }

    public void setPosts(List<PostsEntity> posts) {
        this.posts = posts;
    }

    public static class PostsEntity {
        /**
         * id : 7
         * time : 2017-01-18 19:12:26
         * heats : 0
         * user_id : 14416909
         * user_name : Lcrnice
         * user_avatar : http://wx.qlogo.cn/mmopen/uGGksuzn7bgib6fRp4CDyIbp3LSKWzxg61nJUbKE1udxDnBEDTxYfjc7ZxibTw9w4khxPnM75FoLeZTAHhF5stelMlo6woMyvR/0
         * content : ddd
         */

        private int id;
        private String time;
        private int heats;
        private String user_id;
        private String user_name;
        private String user_avatar;
        private String content;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public int getHeats() {
            return heats;
        }

        public void setHeats(int heats) {
            this.heats = heats;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getUser_name() {
            return user_name;
        }

        public void setUser_name(String user_name) {
            this.user_name = user_name;
        }

        public String getUser_avatar() {
            return user_avatar;
        }

        public void setUser_avatar(String user_avatar) {
            this.user_avatar = user_avatar;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
