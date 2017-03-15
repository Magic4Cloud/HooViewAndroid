package com.easyvaas.elapp.bean.video;

public class VideoCallEntity {
    private int vc_enabled;
    private String callid;

    public boolean isVc_enabled() {
        return vc_enabled == 1;
    }

    public void setVc_enabled(int vc_enabled) {
        this.vc_enabled = vc_enabled;
    }

    public String getCallid() {
        return callid;
    }

    public void setCallid(String callid) {
        this.callid = callid;
    }
}
