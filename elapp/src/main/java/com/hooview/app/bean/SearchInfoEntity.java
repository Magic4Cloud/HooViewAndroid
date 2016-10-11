/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.bean;

import java.util.List;

import com.hooview.app.bean.user.UserEntity;
import com.hooview.app.bean.video.VideoEntity;

public class SearchInfoEntity {

    private int user_start;
    private int user_count;
    private int user_next;
    private List<UserEntity> users;

    private int live_start;
    private int live_count;
    private int live_next;
    private List<VideoEntity> lives;

    private int video_start;
    private int video_count;
    private int video_next;
    private List<VideoEntity> videos;

    public int getUser_start() {
        return user_start;
    }

    public void setUser_start(int user_start) {
        this.user_start = user_start;
    }

    public int getUser_count() {
        return user_count;
    }

    public void setUser_count(int user_count) {
        this.user_count = user_count;
    }

    public int getUser_next() {
        return user_next;
    }

    public void setUser_next(int user_next) {
        this.user_next = user_next;
    }

    public List<UserEntity> getUsers() {
        return users;
    }

    public void setUsers(List<UserEntity> users) {
        this.users = users;
    }

    public int getLive_start() {
        return live_start;
    }

    public void setLive_start(int live_start) {
        this.live_start = live_start;
    }

    public int getLive_count() {
        return live_count;
    }

    public void setLive_count(int live_count) {
        this.live_count = live_count;
    }

    public int getLive_next() {
        return live_next;
    }

    public void setLive_next(int live_next) {
        this.live_next = live_next;
    }

    public List<VideoEntity> getLives() {
        return lives;
    }

    public void setLives(List<VideoEntity> lives) {
        this.lives = lives;
    }

    public int getVideo_start() {
        return video_start;
    }

    public void setVideo_start(int video_start) {
        this.video_start = video_start;
    }

    public int getVideo_count() {
        return video_count;
    }

    public void setVideo_count(int video_count) {
        this.video_count = video_count;
    }

    public int getVideo_next() {
        return video_next;
    }

    public void setVideo_next(int video_next) {
        this.video_next = video_next;
    }

    public List<VideoEntity> getVideos() {
        return videos;
    }

    public void setVideos(List<VideoEntity> videos) {
        this.videos = videos;
    }
}
