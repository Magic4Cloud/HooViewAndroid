package com.easyvaas.elapp.bean.news;

import com.easyvaas.elapp.bean.BannerModel;
import com.easyvaas.elapp.bean.BaseListBean;

import java.io.Serializable;
import java.util.List;

/**
 * Date   2017/4/10
 * Editor  Misuzu
 * 首页新闻bean
 */

public class TopRatedModel extends BaseListBean {


    private HooviewBean hooview;
    private List<RecommendBean> recommend;
    private List<IndexBean> index;
    private List<HomeNewsBean> homeNews; // （0，普通文章；1，专题；2，牛人推荐）
    private BannerModel mBannerModel; // banner数据  现阶段手动set

    public BannerModel getBannerModel() {
        return mBannerModel;
    }

    public void setBannerModel(BannerModel bannerModel) {
        mBannerModel = bannerModel;
    }
    public HooviewBean getHooview() {
        return hooview;
    }

    public void setHooview(HooviewBean hooview) {
        this.hooview = hooview;
    }

    public List<RecommendBean> getRecommend() {
        return recommend;
    }

    public void setRecommend(List<RecommendBean> recommend) {
        this.recommend = recommend;
    }

    public List<IndexBean> getIndex() {
        return index;
    }

    public void setIndex(List<IndexBean> index) {
        this.index = index;
    }

    public List<HomeNewsBean> getHomeNews() {
        return homeNews;
    }

    public void setHomeNews(List<HomeNewsBean> homeNews) {
        this.homeNews = homeNews;
    }

    public static class HooviewBean {
        /**
         * channels : {"id":2,"name":"火眼金睛","Programs":[{"id":1,"name":"机构调研","priority":31},{"id":2,"name":"火眼看盘","priority":12},{"id":3,"name":"晚间公告","priority":1}]}
         * news : [{"id":8189,"type":1,"cover":[],"title":"商业新力量 迈向新未来","time":"2017-04-05 11:05:49","viewCount":6428},{"id":8189,"type":1,"cover":[],"title":"商业新力量 迈向新未来","time":"2017-04-05 11:05:49","viewCount":6428},{"id":8189,"type":1,"cover":[],"title":"商业新力量 迈向新未来","time":"2017-04-05 11:05:49","viewCount":6428}]
         */

        private ChannelsBean channels;
        private List<NewsBean> news;

        public ChannelsBean getChannels() {
            return channels;
        }

        public void setChannels(ChannelsBean channels) {
            this.channels = channels;
        }

        public List<NewsBean> getNews() {
            return news;
        }

        public void setNews(List<NewsBean> news) {
            this.news = news;
        }

        public static class ChannelsBean implements Serializable {
            /**
             * id : 2
             * name : 火眼金睛
             * Programs : [{"id":1,"name":"机构调研","priority":31},{"id":2,"name":"火眼看盘","priority":12},{"id":3,"name":"晚间公告","priority":1}]
             */

            private String id;
            private String name;
            private List<ProgramsBean> Programs;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public List<ProgramsBean> getPrograms() {
                return Programs;
            }

            public void setPrograms(List<ProgramsBean> Programs) {
                this.Programs = Programs;
            }

            public static class ProgramsBean {
                /**
                 * id : 1
                 * name : 机构调研
                 * priority : 31
                 */

                private String id;
                private String name;
                private int priority;

                public String getId() {
                    return id;
                }

                public void setId(String id) {
                    this.id = id;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public int getPriority() {
                    return priority;
                }

                public void setPriority(int priority) {
                    this.priority = priority;
                }
            }
        }

        public static class NewsBean {
            /**
             * id : 8189
             * type : 1
             * cover : []
             * title : 商业新力量 迈向新未来
             * time : 2017-04-05 11:05:49
             * viewCount : 6428
             */

            private String id;
            private int type;
            private String title;
            private String time;
            private int viewCount;
            private List<String> cover;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public int getType() {
                return type;
            }

            public void setType(int type) {
                this.type = type;
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

            public List<String> getCover() {
                return cover;
            }

            public void setCover(List<String> cover) {
                this.cover = cover;
            }
        }
    }

    public static class RecommendBean {
        /**
         * id : 9521
         * nickname : 火眼财经小助手
         * avatar : https://wpimg.wallstcn.com/a87314bf-9182-4af4-b4df-4f3e94b51887
         * fellow : 1234
         */

        private String id;
        private String nickname;
        private String avatar;
        private int fellow;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public int getFellow() {
            return fellow;
        }

        public void setFellow(int fellow) {
            this.fellow = fellow;
        }
    }

    public static class IndexBean {
        /**
         * name : 上证
         * symbol : SH000001
         * close : 3242.35
         * open : 3200.16
         * changePercent : 0.17
         */

        private String name;
        private String symbol;
        private double close;
        private double open;
        private double changePercent;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public double getClose() {
            return close;
        }

        public void setClose(double close) {
            this.close = close;
        }

        public double getOpen() {
            return open;
        }

        public void setOpen(double open) {
            this.open = open;
        }

        public double getChangePercent() {
            return changePercent;
        }

        public void setChangePercent(double changePercent) {
            this.changePercent = changePercent;
        }
    }

    public static class HomeNewsBean {
        /**
         * id : 8189
         * type : 1
         * cover : []
         * title : 商业新力量 迈向新未来
         * time : 2017-04-05 11:05:49
         * viewCount : 6428
         */

        private String id;
        private int type;
        private String title;
        private String time;
        private int viewCount;
        private List<String> cover;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
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

        public List<String> getCover() {
            return cover;
        }

        public void setCover(List<String> cover) {
            this.cover = cover;
        }
    }
}
