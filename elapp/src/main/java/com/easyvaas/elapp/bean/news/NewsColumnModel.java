package com.easyvaas.elapp.bean.news;

import com.easyvaas.elapp.bean.BaseListBean;

import java.util.List;

/**
 * Date    2017/4/12
 * Author  xiaomao
 * 咨询---专栏bean
 */

public class NewsColumnModel extends BaseListBean {

    private List<ColumnModel> news;

    public List<ColumnModel> getNews() {
        return news;
    }

    public void setNews(List<ColumnModel> news) {
        this.news = news;
    }

    public static class ColumnModel {

        /**
         * author: object 作者信息
         * id: string 文章ID
         * title: string 文章标题
         * introduce: string 文章介绍
         * cover: string 文章封面
         */

        private Author author;
        private String id;
        private String title;
        private String introduce;
        private String cover;

        public Author getAuthor() {
            return author;
        }

        public void setAuthor(Author author) {
            this.author = author;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getIntroduce() {
            return introduce;
        }

        public void setIntroduce(String introduce) {
            this.introduce = introduce;
        }

        public String getCover() {
            return cover;
        }

        public void setCover(String cover) {
            this.cover = cover;
        }

    }

    public static class Author {

        /**
         * id: string 作者ID
         * nickname: string 作者昵称
         * avatar: string 作者头像
         * introduce: string 作者介绍
         */

        private String id;
        private String nickname;
        private String avatar;
        private String introduce;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getIntroduce() {
            return introduce;
        }

        public void setIntroduce(String introduce) {
            this.introduce = introduce;
        }
    }
}
