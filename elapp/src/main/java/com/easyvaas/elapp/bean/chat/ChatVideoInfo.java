/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.bean.chat;

public class ChatVideoInfo {
    private int comment_count;
    private int like_count;
    private int watch_count;
    private int watching_count;
    private long riceRoll_count;

    public void setComment_count(int comment_count) {
        this.comment_count = comment_count;
    }

    public void setLike_count(int like_count) {
        this.like_count = like_count;
    }

    public void setWatch_count(int watch_count) {
        this.watch_count = watch_count;
    }

    public void setWatching_count(int watching_count) {
        this.watching_count = watching_count;
    }

    public int getComment_count() {
        return comment_count;
    }

    public int getLike_count() {
        return like_count;
    }

    public int getWatch_count() {
        return watch_count;
    }

    public int getWatching_count() {
        return watching_count;
    }

    public long getRiceRoll_count() {
        return riceRoll_count;
    }

    public void setRiceRoll_count(long riceRoll_count) {
        this.riceRoll_count = riceRoll_count;
    }

    @Override
    public String toString() {
        return "ChatVideoInfo{" +
                "comment_count=" + comment_count +
                ", like_count=" + like_count +
                ", watch_count=" + watch_count +
                ", watching_count=" + watching_count +
                ", riceRoll_count=" + riceRoll_count +
                '}';
    }
}
