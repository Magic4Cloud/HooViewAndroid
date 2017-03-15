package com.easyvaas.common.sharelogin.model;

import android.os.Bundle;

public interface PlatformActionListener {

    /**
     * 登录成功
     * @param userInfo
     */
    //void onComplete(HashMap<String, Object> userInfo);
    void onComplete(Bundle userInfo);
    /**
     * 登录失败
     */
    void onError();

    /**
     * 取消登录
     */
    void onCancel();

}
