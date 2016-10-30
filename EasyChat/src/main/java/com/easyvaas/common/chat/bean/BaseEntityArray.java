package com.easyvaas.common.chat.bean;

public class BaseEntityArray {
    private int count;
    private String start;
    private int next;
    private int total;

    public void setCount(int count) {
        this.count = count;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public void setNext(int next) {
        this.next = next;
    }

    public int getCount() {
        return count;
    }

    public String getStart() {
        return start;
    }

    public int getNext() {
        return next;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
