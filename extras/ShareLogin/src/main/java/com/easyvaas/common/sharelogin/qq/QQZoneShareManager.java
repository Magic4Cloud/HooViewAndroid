package com.easyvaas.common.sharelogin.qq;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.easyvaas.common.sharelogin.R;
import com.easyvaas.common.sharelogin.ShareBlock;
import com.easyvaas.common.sharelogin.data.ShareConstants;
import com.easyvaas.common.sharelogin.model.IShareManager;
import com.easyvaas.common.sharelogin.model.ShareContent;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.open.utils.ThreadManager;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import java.util.ArrayList;

public class QQZoneShareManager implements IShareManager {
    private QzoneShare mQQShare;
    private QQShare mQQShare2;
    private Context mContext;

    public QQZoneShareManager(Context context) {
        String appId = ShareBlock.getInstance().getQQAppId();
        mContext = context;
        if (!TextUtils.isEmpty(appId)) {
            Tencent tencent = Tencent.createInstance(appId, context);
            mQQShare = new QzoneShare(context, tencent.getQQToken());
            mQQShare2 = new QQShare(context, tencent.getQQToken());
        }
    }

    private void shareWebPage(Activity activity, ShareContent shareContent) {
        Bundle params = new Bundle();
        shareWebPageQzone(activity, shareContent, params);
    }

    private void shareWebPageQzone(Activity activity, ShareContent shareContent, Bundle params) {
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, shareContent.getTitle());
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, shareContent.getContent());
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, shareContent.getURL());
        ArrayList<String> imageUrls = new ArrayList<String>();
        imageUrls.add(shareContent.getImageUrl());
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);
        doShareToQzone(activity, params);
    }

    private void doShareToQzone(final Activity activity, final Bundle params) {
        ThreadManager.getMainHandler().post(new Runnable() {
            @Override
            public void run() {
                if (mQQShare != null) {
                    mQQShare.shareToQzone(activity, params, iUiListener);
                }
            }
        });
    }

    private final IUiListener iUiListener = new IUiListener() {
        @Override
        public void onCancel() {
            Toast.makeText(mContext, mContext.getString(R.string.share_cancel), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onComplete(Object response) {
            Toast.makeText(mContext, mContext.getString(R.string.share_success), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(UiError e) {
            Toast.makeText(mContext, mContext.getString(R.string.share_failed) + e.errorDetail + e.errorMessage, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void share(ShareContent shareContent, int shareType) {
        switch (shareContent.getShareWay()) {
            case ShareConstants.SHARE_WAY_TEXT:
            case ShareConstants.SHARE_WAY_WEBPAGE:
            case ShareConstants.SHARE_WAY_MUSIC:
                shareWebPage((Activity) mContext, shareContent);
                break;
            case ShareConstants.SHARE_WAY_PIC:
                sharePicture((Activity) mContext, shareContent);
        }
    }

    private void sharePicture(Activity activity, ShareContent shareContent) {
        Bundle params = new Bundle();
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, shareContent.getImageUrl());
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, activity.getString(R.string.app_name));
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
        doShareToQQ(activity, params);
    }

    /**
     * 用异步方式启动分享
     */
    private void doShareToQQ(final Activity activity, final Bundle params) {
        ThreadManager.getMainHandler().post(new Runnable() {
            @Override
            public void run() {
                if (mQQShare != null) {
                    mQQShare2.shareToQQ(activity, params, iUiListener);
                }
            }
        });
    }
}
