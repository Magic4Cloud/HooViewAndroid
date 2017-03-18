package com.easyvaas.elapp.bean.market;


import java.io.Serializable;

public class StockModel implements Serializable {

    /**
     * symbol: String, 代码
     * name: String, 名称
     * changepercent: Number, 涨跌幅
     * close: Number, 现价
     * open: Number, 开盘价
     * high: Number, 最高价
     * low: Number, 最低价
     * pre_close: Number, 昨日收盘价
     * volume: Number, 成交量（手）
     * turnoverratio: Number, 换手率
     * amount: Number, 成交金额(亿元)
     * per: Number, 市盈率
     * pb: Number, 市净率
     * mktcap: Number, 总市值
     * nmc: Number, 流通市值
     */

    private long amount;
    private double changepercent;
    private String symbol;
    private double high;
    private double low;
    private double mktcap;
    private String name;
    private double nmc;
    private double open;
    private double pb;
    private double per;
    private double pre_close;
    private double close;
    private double turnoverratio;
    private long volume;
    private int rank = -1;


    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
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

    public double getMktcap() {
        return mktcap;
    }

    public void setMktcap(double mktcap) {
        this.mktcap = mktcap;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getNmc() {
        return nmc;
    }

    public void setNmc(double nmc) {
        this.nmc = nmc;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public double getPb() {
        return pb;
    }

    public void setPb(double pb) {
        this.pb = pb;
    }

    public double getPer() {
        return per;
    }

    public void setPer(double per) {
        this.per = per;
    }

    public double getPre_close() {
        return pre_close;
    }

    public void setPre_close(double pre_close) {
        this.pre_close = pre_close;
    }

    public double getTurnoverratio() {
        return turnoverratio;
    }

    public void setTurnoverratio(double turnoverratio) {
        this.turnoverratio = turnoverratio;
    }

    public long getVolume() {
        return volume;
    }

    public void setVolume(long volume) {
        this.volume = volume;
    }
}
