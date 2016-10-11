/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.bean.serverparam;

import java.util.List;

public class ServerParam {
    private List<SplashScreen> screenlist;
    private H5Entity webchatinfo;
    private WatermarkEntity watermark;
    private H5Entity h5url;

    public H5Entity getH5url() {
        return h5url;
    }

    public void setH5url(H5Entity h5url) {
        this.h5url = h5url;
    }

    public WatermarkEntity getWatermark() {
        return watermark;
    }

    public void setWatermark(WatermarkEntity watermarkinfo) {
        this.watermark = watermarkinfo;
    }

    public H5Entity getWebchatinfo() { return webchatinfo; }

    public void setWebchatinfo(H5Entity webchatinfo) { this.webchatinfo = webchatinfo; }

    public List<SplashScreen> getScreenlist() {
        return screenlist;
    }

    public void setScreenlist(List<SplashScreen> screenlist) {
        this.screenlist = screenlist;
    }
}
