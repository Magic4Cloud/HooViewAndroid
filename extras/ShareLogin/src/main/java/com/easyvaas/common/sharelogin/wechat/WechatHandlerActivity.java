package com.easyvaas.common.sharelogin.wechat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import org.json.JSONObject;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import com.easyvaas.common.sharelogin.R;
import com.easyvaas.common.sharelogin.ShareBlock;
import com.easyvaas.common.sharelogin.api.WechatApiService;
import com.easyvaas.common.sharelogin.data.ShareConstants;
import com.easyvaas.common.sharelogin.model.PlatformActionListener;
import com.easyvaas.common.sharelogin.util.DummySubscriber;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * 参考文档:https://open.weixin.qq.com/cgi-bin/showdocument?action=dir_list&t=resource/res_list&verify=1&id=open1419317853&lang=zh_CN
 */
public class WechatHandlerActivity extends Activity implements IWXAPIEventHandler {
    private IWXAPI mIWXAPI;
    private PlatformActionListener mPlatformActionListener;

    private static final String API_URL = "https://api.weixin.qq.com";
    private static final String MALES = "male";
    private static final String FEMALES = "female";
    /**
     * BaseResp的getType函数获得的返回值，1:第三方授权， 2:分享
     */
    private static final int TYPE_LOGIN = ConstantsAPI.COMMAND_SENDAUTH;
    private static final int TYPE_SHARE = ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX;
    private static final int TYPE_PAY = ConstantsAPI.COMMAND_PAY_BY_WX;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = WechatHandlerActivity.this;
        mIWXAPI = WechatLoginManager.getIWXAPI();
        if (mIWXAPI == null) {
            String wechatAppId = ShareBlock.getInstance().getWechatAppId();
            mIWXAPI = WXAPIFactory.createWXAPI(mContext, wechatAppId, true);
        }
        mIWXAPI.handleIntent(getIntent(), this);
        finish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (mIWXAPI != null) {
            mIWXAPI.handleIntent(getIntent(), this);
        }
        finish();
    }

    @Override
    public void onReq(BaseReq baseReq) {
        finish();
    }

    @Override
    public void onResp(BaseResp resp) {
        if (resp.getType() == TYPE_PAY) {
            mPlatformActionListener = WechatPayManager.getPlatformActionListener();
        } else if (resp.getType() == TYPE_LOGIN) {
            mPlatformActionListener = WechatLoginManager.getPlatformActionListener();
        }

        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                if (resp.getType() == TYPE_PAY) {
                    if (mPlatformActionListener != null) {
                        mPlatformActionListener.onComplete(new Bundle());
                    }
                } else if (resp.getType() == TYPE_LOGIN) {
                    final String code = ((SendAuth.Resp) resp).code;
                    RequestInterceptor requestInterceptor = new RequestInterceptor() {
                        @Override
                        public void intercept(RequestFacade request) {
                            request.addQueryParam("appid", ShareBlock.getInstance().getWechatAppId());
                            request.addQueryParam("secret", ShareBlock.getInstance().getWechatSecret());
                            request.addQueryParam("code", code);
                            request.addQueryParam("grant_type", "authorization_code");
                        }
                    };

                    getApiService(requestInterceptor).getAccessToken()
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new DummySubscriber<Response>() {
                                @Override
                                public void onError(Throwable e) {
                                    if (mPlatformActionListener != null) {
                                        mPlatformActionListener.onError();
                                    }
                                }

                                @Override
                                public void onNext(Response response) {
                                    try {
                                        String json = new String(((TypedByteArray) response.getBody())
                                                .getBytes());

                                        JSONObject jsonObject = new JSONObject(json);
                                        final String accessToken = jsonObject.getString("access_token");
                                        final String openId = jsonObject.getString("openid");
                                        final long expiresIn = jsonObject.getLong("expires_in");
                                        RequestInterceptor requestInterceptor = new RequestInterceptor() {
                                            @Override
                                            public void intercept(RequestFacade request) {
                                                request.addQueryParam("access_token", accessToken);
                                                request.addQueryParam("openid", openId);
                                            }
                                        };
                                        getApiService(requestInterceptor).getWechatUserInfo()
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(new DummySubscriber<Response>() {
                                                    @Override
                                                    public void onError(Throwable e) {
                                                        if (mPlatformActionListener != null) {
                                                            mPlatformActionListener.onError();
                                                        }
                                                    }

                                                    @Override
                                                    public void onNext(Response response) {
                                                        try {
                                                            parseUserInfo(response, accessToken, expiresIn);
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                            onError(e);
                                                        }
                                                    }
                                                });
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        onError(e);
                                    }
                                }
                            });
                } else {
                    Toast.makeText(mContext, mContext.getString(
                            R.string.share_success), Toast.LENGTH_SHORT).show();
                }
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                if (resp.getType() == TYPE_PAY || resp.getType() == TYPE_LOGIN) {
                    if (mPlatformActionListener != null) {
                        mPlatformActionListener.onCancel();
                    }
                } else {
                    Toast.makeText(mContext, mContext.getString(
                            R.string.share_cancel), Toast.LENGTH_SHORT).show();
                }
                break;
            case BaseResp.ErrCode.ERR_SENT_FAILED:
                if (resp.getType() == TYPE_PAY || resp.getType() == TYPE_LOGIN) {
                    if (mPlatformActionListener != null) {
                        mPlatformActionListener.onError();
                    }
                } else {
                    Toast.makeText(mContext, mContext.getString(
                            R.string.share_failed), Toast.LENGTH_SHORT).show();
                }
                break;
        }
        finish();
    }

    private WechatApiService getApiService(RequestInterceptor requestInterceptor) {
        return new RestAdapter.Builder()
                .setEndpoint(API_URL)
                .setRequestInterceptor(requestInterceptor)
                .build().create(WechatApiService.class);
    }

    private void parseUserInfo(Response response, String accessToken, long expiresIn) throws Exception {
        String json = new String(((TypedByteArray) response.getBody()).getBytes());
        JSONObject jsonObject = new JSONObject(json);
        Bundle bundle = new Bundle();
        bundle.putString(ShareConstants.PARAMS_NICK_NAME, jsonObject.getString("nickname"));
        bundle.putString(ShareConstants.PARAMS_SEX, jsonObject.getInt("sex") == 1 ? MALES : FEMALES);
        bundle.putString(ShareConstants.PARAMS_IMAGEURL, jsonObject.getString("headimgurl"));
        bundle.putString(ShareConstants.PARAMS_OPENID, jsonObject.getString("openid"));
        bundle.putString(ShareConstants.PARAMS_UNIONID, jsonObject.getString("unionid"));
        bundle.putString(ShareConstants.PARAMS_ACCESS_TOKEN, accessToken);
        bundle.putString(ShareConstants.PARAMS_REFRESH_TOKEN, "");
        bundle.putLong(ShareConstants.PARAMS_EXPIRES_IN, expiresIn);
        bundle.putString(ShareConstants.AUTHTYPE, "weixin");
        if (mPlatformActionListener != null) {
            mPlatformActionListener.onComplete(bundle);
        }
    }
}
