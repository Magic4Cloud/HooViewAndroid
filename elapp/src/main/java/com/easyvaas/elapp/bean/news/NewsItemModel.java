package com.easyvaas.elapp.bean.news;


public class NewsItemModel {
    /**
     * id : 26
     * cover : https://wpimg.wallstcn.com/68/24/56/4a44Hw.png
     * title : 一文读懂：欧洲央行QE还能买多久？
     * time : 2016-06-30T16:49:43.000Z
     * viewCount : 0
     */

    private int id;
    private String cover;
    private String title;
    private String time;
    private int viewCount;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }
}

