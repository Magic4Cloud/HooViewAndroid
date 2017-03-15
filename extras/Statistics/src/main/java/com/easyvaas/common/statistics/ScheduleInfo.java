package com.easyvaas.common.statistics;

public class ScheduleInfo {
    private String url;
    private String ip;
    private boolean use_best_ip;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public boolean isUse_best_ip() {
        return use_best_ip;
    }

    public void setUse_best_ip(boolean use_best_ip) {
        this.use_best_ip = use_best_ip;
    }
}
