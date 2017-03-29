package com.easyvaas.elapp.bean.search;


import com.easyvaas.elapp.bean.BaseListModel;

import java.util.List;

public class SearchStockModel extends BaseListModel {

    private List<DataEntity> data;

    public List<DataEntity> getData() {
        return data;
    }

    public void setData(List<DataEntity> data) {
        this.data = data;
    }

    public static class DataEntity {
        /**
         * symbol : SZ300104
         * market : SZ
         * name : 乐视网
         */

        private String symbol;
        private String market;
        private String name;
        private int collected; // 选中状态 0未选 1选了

        public int getCollected() {
            return collected;
        }

        public void setCollected(int collected) {
            this.collected = collected;
        }

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public String getMarket() {
            return market;
        }

        public void setMarket(String market) {
            this.market = market;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
