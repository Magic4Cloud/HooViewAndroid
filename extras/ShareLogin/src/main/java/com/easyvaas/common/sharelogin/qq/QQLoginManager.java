package com.easyvaas.common.sharelogin.qq;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import com.easyvaas.common.sharelogin.ShareBlock;
import com.easyvaas.common.sharelogin.data.ShareConstants;
import com.easyvaas.common.sharelogin.model.ILoginManager;
import com.easyvaas.common.sharelogin.model.PlatformActionListener;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

public class QQLoginManager implements ILoginManager {
    private Context mContext;
    private Tencent mTencent;
    protected PlatformActionListener mPlatformActionListener;

    public QQLoginManager(Context context) {
        mContext = context;
        String appId = ShareBlock.getInstance().getQQAppId();
        if (!TextUtils.isEmpty(appId)) {
            mTencent = Tencent.createInstance(appId, context);
        }
    }

    private void initOpenidAndToken(JSONObject jsonObject) {
        try {
            String token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
            String expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN);
            String openId = jsonObject.getString(Constants.PARAM_OPEN_ID);
            if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires)
                    && !TextUtils.isEmpty(openId)) {
                mTencent.setAccessToken(token, expires);
                mTencent.setOpenId(openId);
            }
        } catch (Exception e) {
        }
    }


    @Override
    public void login(PlatformActionListener platformActionListener) {
        if (!mTencent.isSessionValid()) {
            mPlatformActionListener = platformActionListener;
            mTencent.login((Activity) mContext, "all", new IUiListener() {
                @Override
                public void onComplete(Object object) {
                    JSONObject jsonObject = (JSONObject) object;
                    initOpenidAndToken(jsonObject);
                    doLogin();
                }

                @Override
                public void onError(UiError uiError) {
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
            });
        } else {
            mTencent.logout(mContext);
        }
    }

    private void doLogin() {
        UserInfo info = new UserInfo(mContext, mTencent.getQQToken());
        info.getUserInfo(new IUiListener() {
            @Override
            public void onComplete(Object object) {
                try {
                    JSONObject jsonObject = (JSONObject) object;
                    Bundle bundle = new Bundle();
                    String gender = jsonObject.getString("gender");
                    bundle.putString(ShareConstants.PARAMS_SEX, gender.equals("女") ? "female" : "male");
                    bundle.putString(ShareConstants.PARAMS_IMAGEURL, jsonObject.getString("figureurl_qq_2"));
                    bundle.putString(ShareConstants.PARAMS_OPENID, mTencent.getOpenId());
                    bundle.putString(ShareConstants.PARAMS_ACCESS_TOKEN, mTencent.getAccessToken());
                    bundle.putLong(ShareConstants.PARAMS_EXPIRES_IN, mTencent.getExpiresIn());
                    bundle.putString(ShareConstants.PARAMS_REFRESH_TOKEN, "");
                    bundle.putString(ShareConstants.PARAMS_NICK_NAME, jsonObject.getString("nickname"));
                    bundle.putString(ShareConstants.USER_CITY, jsonObject.getString("city"));
                    bundle.putString(ShareConstants.AUTHTYPE, "qq");
                    if (mPlatformActionListener != null) {
                        mPlatformActionListener .onComplete(bundle);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (mPlatformActionListener != null) {
                        mPlatformActionListener.onError();
                    }
                }
            }

            @Override
            public void onError(UiError uiError) {
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
        });
    }
}


