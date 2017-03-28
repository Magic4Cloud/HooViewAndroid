package com.easyvaas.elapp.bean.market;

import java.io.Serializable;
import java.util.List;

/**
 * Created by guoliuya on 2017/2/28.
 */

public class StockListModel implements Serializable {

    private List<StockModel> data;

    public List<StockModel> getData() {
        return data;
    }

    public void setData(List<StockModel> data) {
        this.data = data;
    }

    public static class StockModel implements Serializable{
        /**
         * a1p : 0
         * a2p : 0
         * a3p : 0
         * a4p : 0
         * a5p : 0
         * a1v : 0
         * a2v : 0
         * a3v : 0
         * a4v : 0
         * a5v : 0
         * amount : 465735
         * ask : 0
         * b1p : 10.3
         * b2p : 10.29
         * b3p : 10.28
         * b4p : 10.27
         * b5p : 10.26
         * b1v : 100571
         * b2v : 885
         * b3v : 173
         * b4v : 46
         * b5v : 267
         * bid : 10.3
         * datetime : 2017-02-28 14:59:57
         * changepercent : 10.04
         * symbol : SH603817
         * high : 10.3
         * low : 10.3
         * name : 海峡环保
         * open : 10.3
         * close : 10.3
         * preclose : 9.36
         * volume : 45200
         */

        private double a1p;
        private double a2p;
        private double a3p;
        private double a4p;
        private double a5p;
        private double a1v;
        private double a2v;
        private double a3v;
        private double a4v;
        private double a5v;
        private double amount;
        private double ask;
        private double b1p;
        private double b2p;
        private double b3p;
        private double b4p;
        private double b5p;
        private double b1v;
        private double b2v;
        private double b3v;
        private double b4v;
        private double b5v;
        private double bid;
        private String datetime;
        private double changepercent;
        private double high;
        private double low;
        private String name;
        private double open;
        private double close;
        private double preclose;
        private double price;
        private String time;
        private double volume;
        private String symbol;
        private String percent;

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public int getRank() {
            return rank;
        }

        public void setRank(int rank) {
            this.rank = rank;
        }

        private int rank = -1;

        public double getA1p() {
            return a1p;
        }

        public void setA1p(double a1p) {
            this.a1p = a1p;
        }

        public double getA2p() {
            return a2p;
        }

        public void setA2p(double a2p) {
            this.a2p = a2p;
        }

        public double getA3p() {
            return a3p;
        }

        public void setA3p(double a3p) {
            this.a3p = a3p;
        }

        public double getA4p() {
            return a4p;
        }

        public void setA4p(double a4p) {
            this.a4p = a4p;
        }

        public double getA5p() {
            return a5p;
        }

        public void setA5p(double a5p) {
            this.a5p = a5p;
        }

        public double getA1v() {
            return a1v;
        }

        public void setA1v(double a1v) {
            this.a1v = a1v;
        }

        public double getA2v() {
            return a2v;
        }

        public void setA2v(double a2v) {
            this.a2v = a2v;
        }

        public double getA3v() {
            return a3v;
        }

        public void setA3v(double a3v) {
            this.a3v = a3v;
        }

        public double getA4v() {
            return a4v;
        }

        public void setA4v(double a4v) {
            this.a4v = a4v;
        }

        public double getA5v() {
            return a5v;
        }

        public void setA5v(double a5v) {
            this.a5v = a5v;
        }

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        public double getAsk() {
            return ask;
        }

        public void setAsk(double ask) {
            this.ask = ask;
        }

        public double getB1p() {
            return b1p;
        }

        public void setB1p(double b1p) {
            this.b1p = b1p;
        }

        public double getB2p() {
            return b2p;
        }

        public void setB2p(double b2p) {
            this.b2p = b2p;
        }

        public double getB3p() {
            return b3p;
        }

        public void setB3p(double b3p) {
            this.b3p = b3p;
        }

        public double getB4p() {
            return b4p;
        }

        public void setB4p(double b4p) {
            this.b4p = b4p;
        }

        public double getB5p() {
            return b5p;
        }

        public void setB5p(double b5p) {
            this.b5p = b5p;
        }

        public double getB1v() {
            return b1v;
        }

        public void setB1v(double b1v) {
            this.b1v = b1v;
        }

        public double getB2v() {
            return b2v;
        }

        public void setB2v(double b2v) {
            this.b2v = b2v;
        }

        public double getB3v() {
            return b3v;
        }

        public void setB3v(double b3v) {
            this.b3v = b3v;
        }

        public double getB4v() {
            return b4v;
        }

        public void setB4v(double b4v) {
            this.b4v = b4v;
        }

        public double getB5v() {
            return b5v;
        }

        public void setB5v(double b5v) {
            this.b5v = b5v;
        }

        public double getBid() {
            return bid;
        }

        public void setBid(double bid) {
            this.bid = bid;
        }

        public String getDatetime() {
            return datetime;
        }

        public void setDatetime(String datetime) {
            this.datetime = datetime;
        }

        public double getChangepercent() {
            return changepercent;
        }

        public void setChangepercent(double changepercent) {
            this.changepercent = changepercent;
        }

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public double getHigh() {
            return high;
        }

        public void setHigh(double high) {
            this.high = high;
        }

        public double getLow() {
            return low;
        }

        public void setLow(double low) {
            this.low = low;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getOpen() {
            return open;
        }

        public void setOpen(double open) {
            this.open = open;
        }

        public double getClose() {
            return close;
        }

        public void setClose(double close) {
            this.close = close;
        }

        public double getPreclose() {
            return preclose;
        }

        public void setPreclose(double preclose) {
            this.preclose = preclose;
        }

        public double getVolume() {
            return volume;
        }

        public void setVolume(double volume) {
            this.volume = volume;
        }

        public String getPercent() {
            return percent;
        }

        public void setPercent(String percent) {
            this.percent = percent;
        }
    }
}
