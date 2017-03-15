package com.easyvaas.elapp.bean.news;


import com.easyvaas.elapp.bean.BaseListModel;

import java.util.List;

public class NewsListModel extends BaseListModel {

    /**
     * channel : {"id":"频道ID","name":"首页","program":[{"id":"栏目ID","name":"栏目名称"}]}
     * news : [{"id":"新闻ID","cover":"新闻封面","title":"新闻标题","time":"发布时间","viewCount":"阅读数"},{"id":"新闻ID","cover":"新闻封面","title":"新闻标题","time":"发布时间","viewCount":"阅读数"}]
     */

    private ChannelEntity channel;
    private List<NewsItemModel> news;

    public ChannelEntity getChannel() {
        return channel;
    }

    public void setChannel(ChannelEntity channel) {
        this.channel = channel;
    }

    public List<NewsItemModel> getNews() {
        return news;
    }

    public void setNews(List<NewsItemModel> news) {
        this.news = news;
    }

    public static class ChannelEntity {
        /**
         * id : 频道ID
         * name : 首页
         * program : [{"id":"栏目ID","name":"栏目名称"}]
         */

        private String id;
        private String name;
        private List<ProgramEntity> program;

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

        public List<ProgramEntity> getProgram() {
            return program;
        }

        public void setProgram(List<ProgramEntity> program) {
            this.program = program;
        }

        public static class ProgramEntity {
            /**
             * id : 栏目ID
             * name : 栏目名称
             */

            private String id;
            private String name;

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
        }
    }

}
