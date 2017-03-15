package com.easyvaas.common.sharelogin.wechat;

import android.content.Context;

import com.easyvaas.common.sharelogin.model.PlatformActionListener;
import com.tencent.mm.sdk.modelpay.PayReq;

public class WechatPayManager extends WechatBaseManager {
    private static PlatformActionListener platformActionListener;

    public WechatPayManager(Context context) {
        super(context);
    }

    public static PlatformActionListener getPlatformActionListener() {
        return platformActionListener;
    }

    public void pay(String appId, String partnerId, String prepayId, String packageValue, String nonceStr,
            String timeStamp, String sign, PlatformActionListener listener) {
        PayReq payReq = new PayReq();
        payReq.appId = appId;
        payReq.partnerId = partnerId;
        payReq.prepayId = prepayId;
        payReq.packageValue = packageValue;
        payReq.nonceStr = nonceStr;
        payReq.timeStamp = timeStamp;
        payReq.sign = sign;
        mIWXAPI.sendReq(payReq);
        platformActionListener = listener;
    }
}
