package com.easyvaas.elapp.bean.news;


import java.io.Serializable;
import java.util.List;

public class ImportantNewsModel implements Serializable{


    private int start;
    private int count;
    private int next;
    private HuoyanEntity huoyan;
    private List<NewsItemModel> home_news;

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
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

    public HuoyanEntity getHuoyan() {
        return huoyan;
    }

    public void setHuoyan(HuoyanEntity huoyan) {
        this.huoyan = huoyan;
    }

    public List<NewsItemModel> getHome_news() {
        return home_news;
    }

    public void setHome_news(List<NewsItemModel> home_news) {
        this.home_news = home_news;
    }

    public static class HuoyanEntity implements Serializable{
        /**
         * channels : {"id":5,"name":"火眼金睛","Programs":[{"id":4,"name":"晚间公告"},{"id":3,"name":"看大盘"},{"id":2,"name":"机构调研"}]}
         * news : [{"id":65,"cover":"http://hooviewimg.oss-cn-shanghai.aliyuncs.com/shenjianshou/43355-7fe826cfe7563180fb8b9971b1353f2b","title":"拿地狂人孙宏斌：融创中国已暂停拿地","time":"2016-06-29 14:49:13","viewCount":0},{"id":64,"cover":"http://hooviewimg.oss-cn-shanghai.aliyuncs.com/shenjianshou/43355-15e3c4cc36dad2c7e910b4ec3efdfa49","title":"接受低利率\u2014\u2014全球和中国都在进入\u201c类滞胀\u201d","time":"2016-06-29 13:50:56","viewCount":0},{"id":62,"cover":"http://hooviewimg.oss-cn-shanghai.aliyuncs.com/shenjianshou/43355-4e0c7fadc63a000eca076c4517e19b98","title":"花旗恐怖的\u201c杠杆时钟\u201d：市场距离天翻地覆近在咫尺","time":"2017-01-09 14:58:25","viewCount":0}]
         */

        private ChannelModel channels;
        private List<NewsItemModel> news;

        public ChannelModel getChannels() {
            return channels;
        }

        public void setChannels(ChannelModel channels) {
            this.channels = channels;
        }

        public List<NewsItemModel> getNews() {
            return news;
        }

        public void setNews(List<NewsItemModel> news) {
            this.news = news;
        }


    }

    public static class Programs implements Serializable{

        /**
         * id : 4
         * name : 晚间公告
         */

        private int id;
        private String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

}
