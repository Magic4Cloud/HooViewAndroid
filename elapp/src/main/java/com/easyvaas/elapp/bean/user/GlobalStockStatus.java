package com.easyvaas.elapp.bean.user;

import java.io.Serializable;

import io.realm.RealmObject;

/**
 * Created by guoliuya on 2017/3/7.
 */
public class GlobalStockStatus extends RealmObject implements Serializable {
    public String title;
    public String pic;
    public int living;
    public int mode;
    public int count;
    String id;
    long time;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getLiving() {
        return living;
    }

    public void setLiving(int living) {
        this.living = living;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}


