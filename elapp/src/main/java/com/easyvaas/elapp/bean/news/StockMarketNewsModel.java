package com.easyvaas.elapp.bean.news;

import com.easyvaas.elapp.bean.BaseListBean;
import com.easyvaas.elapp.bean.news.NewsListModel.ChannelEntity;
import com.easyvaas.elapp.bean.news.TopRatedModel.HomeNewsBean;

import java.util.List;

/**
 * Date   2017/5/4
 * Editor  Misuzu
 * 股市资讯
 */

public class StockMarketNewsModel extends BaseListBean {

    private List<HomeNewsBean> news;
    private ChannelEntity channel;

    public List<HomeNewsBean> getNews() {
        return news;
    }

    public void setNews(List<HomeNewsBean> news) {
        this.news = news;
    }

    public ChannelEntity getChannel() {
        return channel;
    }

    public void setChannel(ChannelEntity channel) {
        this.channel = channel;
    }
}
