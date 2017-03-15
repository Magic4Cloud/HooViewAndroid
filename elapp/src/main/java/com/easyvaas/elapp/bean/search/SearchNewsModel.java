package com.easyvaas.elapp.bean.search;


import com.easyvaas.elapp.bean.BaseListModel;
import com.easyvaas.elapp.bean.news.NewsItemModel;

import java.util.List;

public class SearchNewsModel extends BaseListModel {
    private List<NewsItemModel> news;
    public List<NewsItemModel> getNews() {
        return news;
    }

    public void setNews(List<NewsItemModel> news) {
        this.news = news;
    }

}
