/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.bean.video;

public class VideoEntity {
    public static final int IS_LIVING = 1;
    public static final int STATUS_GENERATING = 2;
    public static final int MODE_VIDEO = 0;
    public static final int MODE_AUDIO = 1;
    public static final int IS_HORIZONTAL = 1;
    public static final int IS_PINNED_LIST_GIRL = 130;  // Special value for list custom header item type
    public static final int IS_PINNED_LIST_TITLE_BAR = 140;
    public static final int IS_PINNED_LIST_SLIDER_BAR = 150;
    public static final int IS_PINNED_HEADER = 2;
    public static final int IS_RECOMMEND = 1;


    private String vid;
    private String title;
    private String thumb;
    private String name;
    private String nickname;
    private String logourl;

    //live 0--->录播 ，，，，，1---->直播
    private int living;
    private String location;
    private int watch_count;//watch
    private int comment_count;//comment
    private int watching_count;//watching
    private int like_count;//like
    private int time_span;
    private int duration;
    private String share_url;
    private String share_thumb_url;
    private String play_url;
    private int followed;
    private String live_start_time;
    private String live_stop_time;
    private long live_stop_time_span;
    private long live_start_time_span;
    private String living_device;
    private String network_type;
    private String uri;

    private int permission;
    private float gps_latitude;
    private float gps_longitude;

    private int watchid;
    private int gps;
    private int living_status;
    private int status;
    private int pinned;
    private int vip;

    private String imuser;
    private int horizontal;
    private int mode;
    private String password;
    private int price;
    private int recommend;
    private String extra;
    private long sentTimeLength;
    private String start_time;

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public int getTime_span() {
        return time_span;
    }

    public void setTime_span(int time_span) {
        this.time_span = time_span;
    }

    public long getSentTimeLength() {
        return sentTimeLength;
    }

    public void setSentTimeLength(long sentTimeLength) {
        this.sentTimeLength = sentTimeLength;
    }

    public String getImuser() {
        return imuser;
    }

    public void setImuser(String imuser) {
        this.imuser = imuser;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getShare_url() {
        return share_url;
    }

    public void setShare_url(String share_url) {
        this.share_url = share_url;
    }

    public String getShare_thumb_url() {
        return share_thumb_url;
    }

    public void setShare_thumb_url(String share_thumb_url) {
        this.share_thumb_url = share_thumb_url;
    }

    public int getPermission() {
        return permission;
    }

    public void setPermission(int permission) {
        this.permission = permission;
    }

    public void setGps_latitude(float gps_latitude) {
        this.gps_latitude = gps_latitude;
    }

    public void setWatch_count(int watch_count) {
        this.watch_count = watch_count;
    }

    public void setComment_count(int comment_count) {
        this.comment_count = comment_count;
    }

    public void setWatching_count(int watching_count) {
        this.watching_count = watching_count;
    }

    public void setLive_stop_time(String live_stop_time) {
        this.live_stop_time = live_stop_time;
    }

    public void setPlay_url(String play_url) {
        this.play_url = play_url;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setGps_longitude(float gps_longitude) {
        this.gps_longitude = gps_longitude;
    }

    public void setLive_stop_time_span(long live_stop_time_span) {
        this.live_stop_time_span = live_stop_time_span;
    }

    public void setLive_start_time(String live_start_time) {
        this.live_start_time = live_start_time;
    }

    public void setLiving(int living) {
        this.living = living;
    }

    public void setFollowed(int followed) {
        this.followed = followed;
    }

    public void setLive_start_time_span(long live_start_time_span) {
        this.live_start_time_span = live_start_time_span;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLogourl(String logourl) {
        this.logourl = logourl;
    }

    public void setWatchid(int watchid) {
        this.watchid = watchid;
    }

    public void setLike_count(int like_count) {
        this.like_count = like_count;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGps(int gps) {
        this.gps = gps;
    }

    public void setLiving_status(int living_status) {
        this.living_status = living_status;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public void setVip(int vip) {
        this.vip = vip;
    }

    public float getGps_latitude() {
        return gps_latitude;
    }

    public int getWatch_count() {
        return watch_count;
    }

    public int getComment_count() {
        return comment_count;
    }

    public int getWatching_count() {
        return watching_count;
    }

    public String getLive_stop_time() {
        return live_stop_time;
    }

    public String getPlay_url() {
        return play_url;
    }

    public String getLocation() {
        return location;
    }

    public String getNickname() {
        return nickname;
    }

    public float getGps_longitude() {
        return gps_longitude;
    }

    public long getLive_stop_time_span() {
        return live_stop_time_span;
    }

    public String getLive_start_time() {
        return live_start_time;
    }

    public int getLiving() {
        return living;
    }

    public int isFollowed() {
        return followed;
    }

    public long getLive_start_time_span() {
        return live_start_time_span;
    }

    public String getTitle() {
        return title;
    }

    public String getLogourl() {
        return logourl;
    }

    public int getWatchid() {
        return watchid;
    }

    public int getLike_count() {
        return like_count;
    }

    public String getName() {
        return name;
    }

    public int getGps() {
        return gps;
    }

    public int getLiving_status() {
        return living_status;
    }

    public String getVid() {
        return vid;
    }

    public String getThumb() {
        return thumb;
    }

    public int getVip() {
        return vip;
    }

    public int getPinned() {
        return pinned;
    }

    public void setPinned(int pinned) {
        this.pinned = pinned;
    }

    public String getLiving_device() {
        return living_device;
    }

    public void setLiving_device(String living_device) {
        this.living_device = living_device;
    }

    public String getNetwork_type() {
        return network_type;
    }

    public void setNetwork_type(String network_type) {
        this.network_type = network_type;
    }

    public int getHorizontal() {
        return horizontal;
    }

    public void setHorizontal(int horizontal) {
        this.horizontal = horizontal;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getRecommend() {
        return recommend;
    }

    public void setRecommend(int recommend) {
        this.recommend = recommend;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
