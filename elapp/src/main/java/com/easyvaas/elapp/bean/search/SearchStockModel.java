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
        private boolean isSelected; // 选中状态

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean selected) {
            isSelected = selected;
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
