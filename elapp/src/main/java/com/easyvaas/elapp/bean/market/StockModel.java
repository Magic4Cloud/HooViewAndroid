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
     * datetime: string 交易时间
     * preclose: number 昨日收盘价
     * nationalFlag: string 国旗地址
     */

    private double amount;
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
    private double preclose;
    private String datetime;
    private String nationalFlag;
    private int rank = -1;
    private String category;
    private int headerId;
    private boolean lastInCategory;
    private boolean up;

    public boolean isUp() {
        return up;
    }

    public void setUp(boolean up) {
        this.up = up;
    }

    public boolean isLastInCategory() {
        return lastInCategory;
    }

    public void setLastInCategory(boolean lastInCategory) {
        this.lastInCategory = lastInCategory;
    }

    public int getHeaderId() {
        return headerId;
    }

    public void setHeaderId(int headerId) {
        this.headerId = headerId;
    }

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

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
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

    public double getPreclose() {
        return preclose;
    }

    public void setPreclose(double preclose) {
        this.preclose = preclose;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getNationalFlag() {
        return nationalFlag;
    }

    public void setNationalFlag(String nationalFlag) {
        this.nationalFlag = nationalFlag;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "StockModel{" +
                "amount=" + amount +
                ", changepercent=" + changepercent +
                ", symbol='" + symbol + '\'' +
                ", high=" + high +
                ", low=" + low +
                ", mktcap=" + mktcap +
                ", name='" + name + '\'' +
                ", nmc=" + nmc +
                ", open=" + open +
                ", pb=" + pb +
                ", per=" + per +
                ", pre_close=" + pre_close +
                ", close=" + close +
                ", turnoverratio=" + turnoverratio +
                ", volume=" + volume +
                ", preclose=" + preclose +
                ", datetime='" + datetime + '\'' +
                ", nationalFlag='" + nationalFlag + '\'' +
                ", rank=" + rank +
                '}';
    }
}
