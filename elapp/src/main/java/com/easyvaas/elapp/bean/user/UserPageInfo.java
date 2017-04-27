package com.easyvaas.elapp.bean.user;

import java.util.List;

/**
 * Date   2017/4/23
 * Editor  Misuzu
 * 他人用户主页信息
 */

public class UserPageInfo {

    /**
     * name : 18271157
     * nickname : 旋转の八音盒
     * logourl : http://wx.qlogo.cn/mmopen/Q3auHgzwzM4ZTMZwGJqaXXIwQwiblVfdSPC59arBDpFXRGUrictRXCLA7tbmQ5L2LO1GuIibTFvTiahKibFyJ0ydsYw/0
     * location : beijing
     * birthday : 1989-07-04
     * signature : 火眼助你成为财经大师
     * level : 0
     * vip : 1
     * gender : male
     * followed : 1
     * fans_count : 0
     * follow_count : 1
     * video_count : 0
     * tags : []
     * credentials :
     * introduce : 个人详情介绍
     */

    private String name;
    private String nickname;
    private String logourl;
    private String location;
    private String birthday;
    private String signature;
    private int level;
    private int vip;
    private String gender;
    private int followed;
    private int fans_count;
    private int follow_count;
    private int video_count;
    private String credentials;
    private String introduce;
    private List<TagBean> tags;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getLogourl() {
        return logourl;
    }

    public void setLogourl(String logourl) {
        this.logourl = logourl;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getVip() {
        return vip;
    }

    public void setVip(int vip) {
        this.vip = vip;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getFollowed() {
        return followed;
    }

    public void setFollowed(int followed) {
        this.followed = followed;
    }

    public int getFans_count() {
        return fans_count;
    }

    public void setFans_count(int fans_count) {
        this.fans_count = fans_count;
    }

    public int getFollow_count() {
        return follow_count;
    }

    public void setFollow_count(int follow_count) {
        this.follow_count = follow_count;
    }

    public int getVideo_count() {
        return video_count;
    }

    public void setVideo_count(int video_count) {
        this.video_count = video_count;
    }

    public String getCredentials() {
        return credentials;
    }

    public void setCredentials(String credentials) {
        this.credentials = credentials;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public List<TagBean> getTags() {
        return tags;
    }

    public void setTags(List<TagBean> tags) {
        this.tags = tags;
    }

    public static class TagBean
    {
        private int id;
        private String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
