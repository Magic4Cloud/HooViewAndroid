package com.easyvaas.common.chat.bean;

public class UserEntity extends BaseUser {
    private String phone;
    private String contact_name;
    private int faned;
    private int recommend_type;
    private String recommend_reason;
    private int live_time;
    private int live_count;
    private int like_count;
    private int watch_count;
    private int distance;
    private boolean selected;
    private int score;
    private String pinyin;
    private String sortLetter;
    private int pinned;

    public int getPinned() {
        return pinned;
    }

    public void setPinned(int pinned) {
        this.pinned = pinned;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getSortLetter() {
        return sortLetter;
    }

    public void setSortLetter(String sortLetters) {
        this.sortLetter = sortLetters;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getContact_name() {
        return contact_name;
    }

    public void setContact_name(String contact_name) {
        this.contact_name = contact_name;
    }

    public int getFaned() {
        return faned;
    }

    public void setFaned(int faned) {
        this.faned = faned;
    }

    public int getRecommend_type() {
        return recommend_type;
    }

    public void setRecommend_type(int recommend_type) {
        this.recommend_type = recommend_type;
    }

    public String getRecommend_reason() {
        return recommend_reason;
    }

    public void setRecommend_reason(String recommend_reason) {
        this.recommend_reason = recommend_reason;
    }

    public int getLive_time() {
        return live_time;
    }

    public void setLive_time(int live_time) {
        this.live_time = live_time;
    }

    public int getLive_count() {
        return live_count;
    }

    public void setLive_count(int live_count) {
        this.live_count = live_count;
    }

    public int getLike_count() {
        return like_count;
    }

    public void setLike_count(int like_count) {
        this.like_count = like_count;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getWatch_count() {
        return watch_count;
    }

    public void setWatch_count(int watch_count) {
        this.watch_count = watch_count;
    }
}
