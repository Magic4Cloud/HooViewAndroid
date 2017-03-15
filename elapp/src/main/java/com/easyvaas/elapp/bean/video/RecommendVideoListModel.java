package com.easyvaas.elapp.bean.video;


import java.util.List;

public class RecommendVideoListModel {
    private String start;
    private int count;
    private int next;
    private List<VideoEntity> hotrecommend;
    private List<VideoEntity> recommend;

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getNext() {
        return next;
    }

    public void setNext(int next) {
        this.next = next;
    }

    public List<VideoEntity> getHotrecommend() {
        return hotrecommend;
    }

    public void setHotrecommend(List<VideoEntity> hotrecommend) {
        this.hotrecommend = hotrecommend;
    }

    public List<VideoEntity> getRecommend() {
        return recommend;
    }

    public void setRecommend(List<VideoEntity> recommend) {
        this.recommend = recommend;
    }

}
