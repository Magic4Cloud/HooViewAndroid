package com.easyvaas.elapp.bean.news;


import java.util.List;

public class LastestNewsModel {
    /**
     * start : 0
     * count : 2
     * next : 2
     * newsFlash : [{"id":"快讯ID","time":"时间","body":"内容","importance":"重要程度"},{"id":"快讯ID","time":"时间","body":"内容","importance":"重要程度"}]
     */

    private int start;
    private int count;
    private int next;
    private List<NewsFlashEntity> newsFlash;

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

    public List<NewsFlashEntity> getNewsFlash() {
        return newsFlash;
    }

    public void setNewsFlash(List<NewsFlashEntity> newsFlash) {
        this.newsFlash = newsFlash;
    }

    public static class NewsFlashEntity {
        public NewsFlashEntity() {
        }

        public NewsFlashEntity(boolean isHeader) {
            this.isHeader = isHeader;
        }

        public boolean isHeader() {
            return isHeader;
        }

        public void setHeader(boolean header) {
            isHeader = header;
        }

        /**
         * id : 快讯ID
         * time : 时间
         * body : 内容
         * importance : 重要程度
         */
        private boolean isHeader;
        private String id;
        private String time;
        private String body;
        private String importance;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }

        public String getImportance() {
            return importance;
        }

        public void setImportance(String importance) {
            this.importance = importance;
        }
    }
}
