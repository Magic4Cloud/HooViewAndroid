package com.easyvaas.common.sharelogin.wechat;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.easyvaas.common.sharelogin.R;
import com.easyvaas.common.sharelogin.ShareBlock;
import com.easyvaas.common.sharelogin.model.ILoginManager;
import com.easyvaas.common.sharelogin.model.PlatformActionListener;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WechatLoginManager implements ILoginManager {
    private static final String SCOPE = "snsapi_userinfo";
    private static final String STATE = "lls_engzo_wechat_login";
    //    private static WechatLoginManager mInstance;

    private static IWXAPI mIWXAPI;
    private static PlatformActionListener mPlatformActionListener;

    //    public static WechatLoginManager getInstance(Context context) {
    //        if (mInstance == null) {
    //            mInstance = new WechatLoginManager(context);
    //        }
    //        return mInstance;
    //    }

    public WechatLoginManager(Context context) {
        String wechatAppId = ShareBlock.getInstance().getWechatAppId();
        if (!TextUtils.isEmpty(wechatAppId)) {
            mIWXAPI = WXAPIFactory.createWXAPI(context, wechatAppId, true);
            if (!mIWXAPI.isWXAppInstalled()) {
                Toast.makeText(context, context.getString(R.string.share_install_wechat_tips),
                        Toast.LENGTH_SHORT).show();
            } else {
                mIWXAPI.registerApp(wechatAppId);
            }
        }
    }

    public static IWXAPI getIWXAPI() {
        return mIWXAPI;
    }

    public static PlatformActionListener getPlatformActionListener() {
        return mPlatformActionListener;
    }

    @Override
    public void login(PlatformActionListener platformActionListener) {
        if (mIWXAPI != null) {
            final SendAuth.Req req = new SendAuth.Req();
            req.scope = SCOPE;
            req.state = STATE;
            mIWXAPI.sendReq(req);
            mPlatformActionListener = platformActionListener;
        }
    }
}
