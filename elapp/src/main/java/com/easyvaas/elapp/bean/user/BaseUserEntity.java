/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.bean.user;

import android.text.TextUtils;

import com.easyvaas.elapp.bean.chat.ChatUser;
import com.easyvaas.elapp.utils.UserUtil;

import java.io.Serializable;
import java.util.List;

public class BaseUserEntity extends BaseUer implements Serializable {
    public static final int FOLLOWED = 1;
    public static final int UN_FOLLOWED = 0;

    public static final String GENDER_MALE = "male";
    public static final String GENDER_FEMALE = "female";

    private String signature;
    private String birthday;
    private String location;
    private int fans_count;
    private int follow_count;
    private int followed;
    private int living_count;// video_count
    private int video_count;
    private long costecoin;
    private String credentials;

    public String getCredentials() {
        return credentials;
    }

    public void setCredentials(String credentials) {
        this.credentials = credentials;
    }

    private List<TagsEntity> tags;

    private String imuser;

    public int getVideo_count() {
        return video_count;
    }

    public void setVideo_count(int video_count) {
        this.video_count = video_count;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImuser() {
        return imuser;
    }

    public void setImuser(String imuser) {
        this.imuser = imuser;
    }

    public int getLiving_count() {
        return living_count;
    }

    public void setLiving_count(int living_count) {
        this.living_count = living_count;
    }

    public String getBirthday() {
        return (!TextUtils.isEmpty(birthday) && birthday.startsWith("0"))
                ? UserUtil.DEFAULT_BIRTHDAY : birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public int getFollowed() {
        return followed;
    }

    public void setFollowed(int followed) {
        this.followed = followed;
    }

    public int getFollow_count() {
        return follow_count;
    }

    public void setFollow_count(int follow_count) {
        this.follow_count = follow_count;
    }

    public int getFans_count() {
        return fans_count;
    }

    public void setFans_count(int fans_count) {
        this.fans_count = fans_count;
    }

    public long getCostecoin() {
        return costecoin;
    }

    public void setCostecoin(long costecoin) {
        this.costecoin = costecoin;
    }

    public List<TagsEntity> getTags() {
        return tags;
    }

    public void setTags(List<TagsEntity> tags) {
        this.tags = tags;
    }

    public ChatUser convertChatUser() {
        ChatUser chatUser = new ChatUser();
        chatUser.setLogourl(getLogourl());
        chatUser.setName(getName());
        chatUser.setNickname(getNickname());
        return chatUser;
    }

    @Override
    public String toString() {
        return "BaseUserEntity{" +
                "signature='" + signature + '\'' +
                ", birthday='" + birthday + '\'' +
                ", location='" + location + '\'' +
                ", fans_count=" + fans_count +
                ", follow_count=" + follow_count +
                ", followed=" + followed +
                ", living_count=" + living_count +
                ", video_count=" + video_count +
                ", costecoin=" + costecoin +
                ", credentials='" + credentials + '\'' +
                ", tags=" + tags +
                ", imuser='" + imuser + '\'' +
                '}' + super.toString();
    }

    public static class TagsEntity implements Serializable {
        private int id;
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

    }
}
