package com.easyvaas.elapp.bean.market;

import java.util.List;

/**
 * 涨跌榜
 */
public class UpsAndDownsDataModel {
    private DataModel data;

    public DataModel getData() {
        return data;
    }

    public void setData(DataModel data) {
        this.data = data;
    }

    public class DataModel {

        private List<StockModel> head;
        private List<StockModel> tail;

        public List<StockModel> getHead() {
            return head;
        }

        public void setHead(List<StockModel> head) {
            this.head = head;
        }

        public List<StockModel> getTail() {
            return tail;
        }

        public void setTail(List<StockModel> tail) {
            this.tail = tail;
        }
    }

}
