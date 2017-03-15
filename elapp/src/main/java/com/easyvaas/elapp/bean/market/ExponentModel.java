package com.easyvaas.elapp.bean.market;

import java.io.Serializable;

import io.realm.RealmObject;

public class ExponentModel extends RealmObject implements Serializable {
//    code: String, 指数代码
//    name: String, 指数名称
//    change: Number, 涨跌幅
//    open: Number, 开盘点位
//    preclose: Number, 昨日收盘点位
//    close: Number, 收盘点位
//    high: Number, 最高点位
//    low: Number, 最低点位
//    volume: Number, 成交量（手）
//    amount: Number, 成交金额(亿元)

    private double amount;
    private double change;
    private double close;
    private String code;
    private double high;
    private double low;
    private String name;
    private double open;
    private double preclose;
    private double volume;
    private String datetime;
    private double changepercent;
    private String symbol;
    private boolean isSelect;
    private boolean addOrDelete;
    String id;
    String time;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isAddOrDelete() {
        return addOrDelete;
    }

    public void setAddOrDelete(boolean addOrDelete) {
        this.addOrDelete = addOrDelete;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getChange() {
        return change;
    }

    public void setChange(double change) {
        this.change = change;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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
}
