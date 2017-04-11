package com.easyvaas.elapp.bean.market;

import java.util.List;

/**
 * Date    2017/4/11
 * Author  xiaomao
 */
public class MarketGlobalModel {

    /**
     * name: string 分类名称
     * index: array[object] 指数列表
     */

    private String name;
    private List<StockModel> index;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<StockModel> getIndex() {
        return index;
    }

    public void setIndex(List<StockModel> index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return "MarketGlobalModel{" +
                "name='" + name + '\'' +
                ", index=" + index +
                '}';
    }
}
