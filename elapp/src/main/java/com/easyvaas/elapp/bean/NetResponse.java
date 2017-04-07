package com.easyvaas.elapp.bean;

/**
 * Date   2017/3/28
 * Editor  Misuzu
 * 请求返回基类
 */

public class NetResponse<T> {

    private String retval;
    private String reterr;
    private int timestamp;
    private T retinfo;

    public String getRetval() {
        return retval;
    }

    public void setRetval(String retval) {
        this.retval = retval;
    }

    public String getReterr() {
        return reterr;
    }

    public void setReterr(String reterr) {
        this.reterr = reterr;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public T getRetinfo() {
        return retinfo;
    }

    public void setRetinfo(T retinfo) {
        this.retinfo = retinfo;
    }
}
