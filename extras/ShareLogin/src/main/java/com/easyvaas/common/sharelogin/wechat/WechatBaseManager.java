package com.easyvaas.common.sharelogin.wechat;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.easyvaas.common.sharelogin.R;
import com.easyvaas.common.sharelogin.ShareBlock;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WechatBaseManager {
    protected Context mContext;
    protected IWXAPI mIWXAPI;
    protected String mWeChatAppId;

    public WechatBaseManager(Context context) {
        mContext = context;
        mWeChatAppId = ShareBlock.getInstance().getWechatAppId();
        if (!TextUtils.isEmpty(mWeChatAppId)) {
            registerApp(context);
        }
    }

    private void registerApp(Context context) {
        mIWXAPI = WXAPIFactory.createWXAPI(context, mWeChatAppId, true);
        if (!mIWXAPI.isWXAppInstalled()) {
            Toast.makeText(context, context.getString(R.string.share_install_wechat_tips), Toast.LENGTH_SHORT)
                    .show();
        } else {
            mIWXAPI.registerApp(mWeChatAppId);
        }
    }
}
