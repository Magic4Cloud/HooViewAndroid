/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.wxapi;

import android.os.Bundle;

import com.hooview.app.utils.Logger;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;

import com.easyvaas.common.sharelogin.wechat.WechatHandlerActivity;

public class WXPayEntryActivity extends WechatHandlerActivity {
    private static final String TAG = "WXPayEntryActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onReq(BaseReq req) {
        super.onReq(req);
    }

    @Override
    public void onResp(BaseResp resp) {
        super.onResp(resp);
        Logger.d(TAG, "Response code: " + resp.errCode);
    }
}
