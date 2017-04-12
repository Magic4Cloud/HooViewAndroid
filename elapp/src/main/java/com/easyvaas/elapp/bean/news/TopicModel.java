package com.easyvaas.elapp.bean.news;

import com.easyvaas.elapp.bean.BaseListBean;
import com.easyvaas.elapp.bean.news.TopRatedModel.HomeNewsBean;

import java.util.List;

/**
 * Date   2017/4/12
 * Editor  Misuzu
 * 专题界面数据
 */

public class TopicModel extends BaseListBean {

    /**
     * id : 12345
     * title : 火眼财经培训
     * introduce : 专题介绍
     * cover : http://image-cdn.hooview.com/hooviewportal/upload_ddfaf5a599a50cfb019c6625c920e908.png
     * viewCount : 1234
     */

    private int id;
    private String title;
    private String introduce;
    private String cover;
    private int viewCount;
    private List<HomeNewsBean> news;

    public List<HomeNewsBean> getNews() {
        return news;
    }

    public void setNews(List<HomeNewsBean> news) {
        this.news = news;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }
}
