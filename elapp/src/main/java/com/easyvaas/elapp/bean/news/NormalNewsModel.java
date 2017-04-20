package com.easyvaas.elapp.bean.news;

import com.easyvaas.elapp.bean.BaseListBean;
import com.easyvaas.elapp.bean.news.TopRatedModel.HomeNewsBean;

import java.util.ArrayList;

/**
 * Date   2017/4/20
 * Editor  Misuzu
 * 新闻列表通用model
 */

public class NormalNewsModel extends BaseListBean {

    private ArrayList<HomeNewsBean> news;

    public ArrayList<HomeNewsBean> getNews() {
        return news;
    }

    public void setNews(ArrayList<HomeNewsBean> news) {
        this.news = news;
    }
}
