package com.easyvaas.common.statistics.qualitymonitor;

class PingResultEntity {
    private String ip;
    private float loss_rate;
    private float rtt_avg;
    private float rtt_std_dev;

    public PingResultEntity(String ip, float loss_rate, float rtt_avg, float rtt_std_dev) {
        this.ip = ip;
        this.loss_rate = loss_rate;
        this.rtt_avg = rtt_avg;
        this.rtt_std_dev = rtt_std_dev;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public float getLoss_rate() {
        return loss_rate;
    }

    public void setLoss_rate(float loss_rate) {
        this.loss_rate = loss_rate;
    }

    public float getRtt_avg() {
        return rtt_avg;
    }

    public void setRtt_avg(float rtt_avg) {
        this.rtt_avg = rtt_avg;
    }

    public float getRtt_std_dev() {
        return rtt_std_dev;
    }

    public void setRtt_std_dev(float rtt_std_dev) {
        this.rtt_std_dev = rtt_std_dev;
    }
}
