package com.easyvaas.common.statistics.schedule;

import java.util.ArrayList;
import java.util.List;

class PingStatusEntity {
    private int index;
    private int rttMin;
    private int rttMax;
    private int rttAvg;
    private int rttMdev;
    private boolean pinged;
    private List<Integer> listLastRtt;

    public PingStatusEntity() {
        pinged = false;
        listLastRtt = new ArrayList<>();
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getRttMin() {
        return rttMin;
    }

    public void setRttMin(int rttMin) {
        this.rttMin = rttMin;
    }

    public int getRttMax() {
        return rttMax;
    }

    public void setRttMax(int rttMax) {
        this.rttMax = rttMax;
    }

    public int getRttAvg() {
        return rttAvg;
    }

    public void setRttAvg(int rttAvg) {
        this.rttAvg = rttAvg;
    }

    public int getRttMdev() {
        return rttMdev;
    }

    public void setRttMdev(int rttMdev) {
        this.rttMdev = rttMdev;
    }

    public List<Integer> getListLastRtt() {
        return listLastRtt;
    }

    public void setListLastRtt(List<Integer> listLastRtt) {
        this.listLastRtt = listLastRtt;
    }

    public boolean isPinged() {
        return pinged;
    }

    public void setPinged(boolean pinged) {
        this.pinged = pinged;
    }
}
