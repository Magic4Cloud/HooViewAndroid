package com.easyvaas.elapp.bean.news;


import com.easyvaas.elapp.bean.BaseListModel;

import java.util.List;

public class MyStockNewsModel extends BaseListModel {

    private List<NewsEntity> news;

    public List<NewsEntity> getNews() {
        return news;
    }

    public void setNews(List<NewsEntity> news) {
        this.news = news;
    }

    public static class NewsEntity {

        /**
         * id : 1628
         * title : 【见智精译】流动性危机启示录：回顾贝尔斯登、LTCM与AIG引发的市场风暴
         * time : 2017-01-04 15:16:00
         * stocks : [{"symbol":"600228","name":"昌九生化","persent":"-1.33%"}]
         */
        private boolean isHeader;
        private int id;

        public boolean isHeader() {
            return isHeader;
        }

        public void setHeader(boolean header) {
            isHeader = header;
        }

        public NewsEntity() {

        }

        public NewsEntity(boolean isHeader) {

            this.isHeader = isHeader;
        }

        private String title;
        private String time;
        private List<StocksEntity> stocks;

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

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public List<StocksEntity> getStocks() {
            return stocks;
        }

        public void setStocks(List<StocksEntity> stocks) {
            this.stocks = stocks;
        }

    }

    public static class StocksEntity {
        /**
         * symbol : 600228
         * name : 昌九生化
         * persent : -1.33%
         */

        private String symbol;
        private String name;
        private String percent;

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPercent() {
            return percent;
        }

        public void setPercent(String percent) {
            this.percent = percent;
        }
    }
}
