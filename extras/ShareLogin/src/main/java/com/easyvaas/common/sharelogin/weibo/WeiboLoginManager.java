package com.easyvaas.common.sharelogin.weibo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.easyvaas.common.sharelogin.ShareBlock;
import com.easyvaas.common.sharelogin.data.ShareConstants;
import com.easyvaas.common.sharelogin.model.ILoginManager;
import com.easyvaas.common.sharelogin.model.PlatformActionListener;
import com.easyvaas.common.sharelogin.weibo.model.User;
import com.easyvaas.common.sharelogin.weibo.model.UsersAPI;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;

public class WeiboLoginManager implements ILoginManager{
    private static final String REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";
    //private static final String SCOPE =
    //        "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
    //                + "follow_app_official_microblog";
    private static final String SCOPE = "follow_app_official_microblog";

    private static final  String MALES = "male";
    private static final String FEMALE = "female";

    private Context mContext;
    private static String mSinaAppKey;
    private Oauth2AccessToken mAccessToken;
    private PlatformActionListener mPlatformActionListener;

    /**
     * 注意：SsoHandler 仅当 SDK 支持 SSO 时有效
     */
    private static SsoHandler mSsoHandler;

    public WeiboLoginManager(Context context) {
        mContext = context;
        mSinaAppKey = ShareBlock.getInstance().getWeiboAppId();
    }

    public static SsoHandler getSsoHandler() {
        return mSsoHandler;
    }

    @Override
    public void login(PlatformActionListener platformActionListener) {
        mPlatformActionListener = platformActionListener;
        AccessTokenKeeper.clear(mContext);
        AuthInfo mAuthInfo = new AuthInfo(mContext, mSinaAppKey, REDIRECT_URL, SCOPE);
        mSsoHandler = new SsoHandler((Activity) mContext, mAuthInfo);
        mSsoHandler.authorize(new AuthListener());
    }

    /**
     * * 1. SSO 授权时，需要在 onActivityResult 中调用 {@link SsoHandler#authorizeCallBack} 后，
     * 该回调才会被执行。
     * 2. 非SSO 授权时，当授权结束后，该回调就会被执行
     *
     */
    private class AuthListener implements WeiboAuthListener {
        @Override
        public void onComplete(Bundle values) {
            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            if (mAccessToken != null && mAccessToken.isSessionValid()) {
                AccessTokenKeeper.writeAccessToken(mContext, mAccessToken);
                UsersAPI userAPI = new UsersAPI(mContext, mSinaAppKey, mAccessToken);
                userAPI.show(Long.parseLong(mAccessToken.getUid()), mListener);
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            if (mPlatformActionListener != null) {
                mPlatformActionListener.onError();
            }
        }

        @Override
        public void onCancel() {
            if (mPlatformActionListener != null) {
                mPlatformActionListener.onCancel();
            }
        }
    }

    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                User user = User.parse(response);
                if (user != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString(ShareConstants.PARAMS_NICK_NAME, user.name);
                    bundle.putString(ShareConstants.PARAMS_SEX, user.gender.equals("f") ? FEMALE : MALES);
                    bundle.putString(ShareConstants.PARAMS_IMAGEURL, user.avatar_large);
                    bundle.putString(ShareConstants.PARAMS_OPENID, user.id);
                    bundle.putString(ShareConstants.USER_CITY, user.location);
                    bundle.putString(ShareConstants.DESCRIPTION, user.description);
                    bundle.putString(ShareConstants.AUTHTYPE, "sina");
                    if (mAccessToken != null) {
                        bundle.putString(ShareConstants.PARAMS_ACCESS_TOKEN, mAccessToken.getToken());
                        bundle.putString(ShareConstants.PARAMS_REFRESH_TOKEN, mAccessToken.getRefreshToken());
                        bundle.putLong(ShareConstants.PARAMS_EXPIRES_IN,
                                mAccessToken.getExpiresTime() - System.currentTimeMillis());
                    } else {
                        bundle.putString(ShareConstants.PARAMS_ACCESS_TOKEN, "");
                        bundle.putString(ShareConstants.PARAMS_REFRESH_TOKEN, "");
                        bundle.putInt(ShareConstants.PARAMS_EXPIRES_IN, -1);
                    }

                    if (mPlatformActionListener != null) {
                        mPlatformActionListener.onComplete(bundle);
                    }
                }
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            if (mPlatformActionListener != null) {
                mPlatformActionListener.onError();
            }
        }
    };


}
