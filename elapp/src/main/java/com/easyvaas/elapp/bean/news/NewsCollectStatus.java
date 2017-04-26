package com.easyvaas.elapp.bean.news;

import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.view.live.SwitchButton;

/**
 * Date   2017/4/26
 * Editor  Misuzu
 * 旧版收藏状态
 */

public class NewsCollectStatus {

    /**
     * code : 4389
     * exist : 1    1存在 0不存在
     */

    private String code;
    private int exist;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        SwitchButton switchButton = new SwitchButton(EVApplication.getApp());
        switchButton.toggle();
        this.code = code;
    }

    public int getExist() {
        return exist;
    }

    public void setExist(int exist) {
        this.exist = exist;
    }
}
