package com.easyvaas.common.statistics.qualitymonitor;

public class QualityStatisticsEntity {
    private String vid;
    private int speed_dev;
    private int speed_avg;
    private int buffer_duration;
    private float cpuTotal;
    private float cpuAM;
    private float memUsed;
    private float memAM;
    private int memTotal;

    public QualityStatisticsEntity(String vid, int speed_dev, int speed_avg, int buffer_duration, float cpuTotal,
            float cpuAM, float memUsed, float memAM, int memTotal) {
        this.vid = vid;
        this.speed_dev = speed_dev;
        this.speed_avg = speed_avg;
        this.buffer_duration = buffer_duration;
        this.cpuTotal = cpuTotal;
        this.cpuAM = cpuAM;
        this.memUsed = memUsed;
        this.memAM = memAM;
        this.memTotal = memTotal;
    }

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public int getSpeed_dev() {
        return speed_dev;
    }

    public void setSpeed_dev(int speed_dev) {
        this.speed_dev = speed_dev;
    }

    public int getSpeed_avg() {
        return speed_avg;
    }

    public void setSpeed_avg(int speed_avg) {
        this.speed_avg = speed_avg;
    }

    public int getBuffer_duration() {
        return buffer_duration;
    }

    public void setBuffer_duration(int buffer_duration) {
        this.buffer_duration = buffer_duration;
    }

    public float getCpuTotal() {
        return cpuTotal;
    }

    public void setCpuTotal(float cpuTotal) {
        this.cpuTotal = cpuTotal;
    }

    public float getCpuAM() {
        return cpuAM;
    }

    public void setCpuAM(float cpuAM) {
        this.cpuAM = cpuAM;
    }

    public float getMemUsed() {
        return memUsed;
    }

    public void setMemUsed(float memUsed) {
        this.memUsed = memUsed;
    }

    public float getMemAM() {
        return memAM;
    }

    public void setMemAM(float memAM) {
        this.memAM = memAM;
    }

    public int getMemTotal() {
        return memTotal;
    }

    public void setMemTotal(int memTotal) {
        this.memTotal = memTotal;
    }
}
