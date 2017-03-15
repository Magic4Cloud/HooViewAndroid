package com.easyvaas.elapp.bean.news;

import java.io.Serializable;
import java.util.List;

public class ChannelModel implements Serializable {
    /**
     * id : 5
     * name : 火眼金睛
     * Programs : [{"id":4,"name":"晚间公告"},{"id":3,"name":"看大盘"},{"id":2,"name":"机构调研"}]
     */

    private int id;
    private String name;
    private List<ImportantNewsModel.Programs> Programs;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ImportantNewsModel.Programs> getPrograms() {
        return Programs;
    }

    public void setPrograms(List<ImportantNewsModel.Programs> Programs) {
        this.Programs = Programs;
    }

}
