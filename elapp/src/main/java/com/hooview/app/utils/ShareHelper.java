/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.utils;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;

import com.easyvaas.common.bottomsheet.BottomSheet;
import com.easyvaas.common.sharelogin.data.ShareConstants;
import com.easyvaas.common.sharelogin.model.ShareContent;
import com.easyvaas.common.sharelogin.model.ShareContentText;
import com.easyvaas.common.sharelogin.model.ShareContentWebpage;
import com.easyvaas.common.sharelogin.qq.QQShareManager;
import com.easyvaas.common.sharelogin.qq.QQZoneShareManager;
import com.easyvaas.common.sharelogin.wechat.WechatShareManager;
import com.easyvaas.common.sharelogin.weibo.WeiboShareManager;

import com.hooview.app.net.MyRequestCallBack;
import com.hooview.app.net.RequestHelper;

public class ShareHelper {
    private  static final String TAG = "ShareHelper";

    private static ShareHelper mInstance;
    private static Activity mActivity;
    private ShareContent mShareContent;
    private BottomSheet mShareBottomSheet;

    private ShareHelper(Activity activity) {
        if (mActivity != null && mActivity != activity) {
            mActivity = null;
        }
        mActivity = activity;
        initShareMenu();
    }

    public static ShareHelper getInstance(Activity activity) {
        if (mInstance == null || mActivity != activity) {
            mInstance = new ShareHelper(activity);
        }
        return mInstance;
    }

    private void initShareMenu() {
        mShareBottomSheet = new BottomSheet.Builder(mActivity).grid()
                .sheet(com.hooview.app.R.menu.share)
                .title(com.hooview.app.R.string.share)
                .listener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case com.hooview.app.R.id.menu_share_qq:
                                shareContent(Constants.SHARE_BY_QQ, mShareContent,
                                        Constants.SHARE_TYPE_NOTICE);
                                break;
                            case com.hooview.app.R.id.menu_share_qq_zone:
                                shareContent(Constants.SHARE_BY_QQ_ZONE, mShareContent,
                                        Constants.SHARE_TYPE_NOTICE);
                                break;
                            case com.hooview.app.R.id.menu_share_weibo:
                                shareContent(Constants.SHARE_BY_WEIBO, mShareContent,
                                        Constants.SHARE_TYPE_NOTICE);
                                break;
                            case com.hooview.app.R.id.menu_share_weixin:
                                shareContent(Constants.SHARE_BY_WEIXIN, mShareContent,
                                        Constants.SHARE_TYPE_NOTICE);
                                break;
                            case com.hooview.app.R.id.menu_share_weixin_circle:
                                shareContent(Constants.SHARE_BY_WEIXIN_CIRCLE, mShareContent,
                                        Constants.SHARE_TYPE_NOTICE);
                                break;
                            case com.hooview.app.R.id.menu_share_copy_url:
                                copyToClipboard(mShareContent.getURL());
                                break;
                        }
                    }
                }).build();
    }

    public void showShareBottomPanel(ShareContent shareContent) {
        mShareContent = shareContent;
        mShareBottomSheet.show();
    }

    /**
     * @param shareMenuId One of {R.id.menu_share_qq}, {R.id.menu_share_qq_zone}.
     */
    public void shareContent(int shareMenuId, ShareContent shareContent, String type) {
        switch (shareMenuId) {
            case com.hooview.app.R.id.menu_share_qq:
                shareContent(Constants.SHARE_BY_QQ, shareContent, type);
                break;
            case com.hooview.app.R.id.menu_share_qq_zone: shareContent(Constants.SHARE_BY_QQ_ZONE, shareContent, type);
                break;
            case com.hooview.app.R.id.menu_share_weibo:
                shareContent(Constants.SHARE_BY_WEIBO, shareContent, type);
                break;
            case com.hooview.app.R.id.menu_share_weixin:
                shareContent(Constants.SHARE_BY_WEIXIN, shareContent, type);
                break;
            case com.hooview.app.R.id.menu_share_weixin_circle:
                shareContent(Constants.SHARE_BY_WEIXIN_CIRCLE, shareContent, type);
                break;
            case com.hooview.app.R.id.menu_share_copy_url:
                copyToClipboard(shareContent.getURL());
                break;
        }
    }

    private void shareContent(String shareBy, ShareContent shareContent, String type) {
        if (TextUtils.isEmpty(shareContent.getImageUrl())) {
            Logger.w(TAG, "shareContent ImageUrl is empty!");
        }
        if (!TextUtils.isEmpty(shareContent.getImageUrl())
                && (shareContent.getImageUrl().startsWith("http://")
                || shareContent.getImageUrl().startsWith("https://"))) {
            loadImageAndShare(shareBy, shareContent, type);
            return;
        }
        if (Constants.SHARE_BY_WEIBO.equals(shareBy)) {
            WeiboShareManager weiboShareManager = new WeiboShareManager(mActivity);
            if (Constants.SHARE_TYPE_VIDEO.equals(type)) {
                shareContent = new ShareContentText(shareContent.getContent() + " " + shareContent.getURL());
            }
            weiboShareManager.share(shareContent, ShareConstants.SHARE_WAY_WEBPAGE);
        } else if (Constants.SHARE_BY_WEIXIN.equals(shareBy)) {
            WechatShareManager wechatShareManager = new WechatShareManager(mActivity);
            wechatShareManager.share(shareContent, WechatShareManager.WEIXIN_SHARE_TYPE_TALK);
        } else if (Constants.SHARE_BY_WEIXIN_CIRCLE.equals(shareBy)) {
            WechatShareManager wechatCircleShare = new WechatShareManager(mActivity);
            wechatCircleShare.share(shareContent, WechatShareManager.WEIXIN_SHARE_TYPE_FRENDS);
        } else if (Constants.SHARE_BY_QQ.equals(shareBy)) {
            QQShareManager qqShareManager = new QQShareManager(mActivity);
            qqShareManager.share(shareContent, ShareConstants.SHARE_WAY_WEBPAGE);
        } else if (Constants.SHARE_BY_QQ_ZONE.equals(shareBy)) {
            QQZoneShareManager qqShareManager = new QQZoneShareManager(mActivity);
            qqShareManager.share(shareContent, ShareConstants.SHARE_WAY_WEBPAGE);
        }
    }

    private boolean loadImageAndShare(final String shareBy, final ShareContent shareContent, final String type) {
        RequestHelper.getInstance(mActivity).downloadFile(shareContent.getImageUrl(),
                FileUtil.CACHE_SHARE_DIR,
                new MyRequestCallBack<String>() {
                    @Override
                    public void onSuccess(String result) {
                        if (shareContent instanceof ShareContentWebpage) {
                            mShareContent = new ShareContentWebpage(shareContent.getTitle(),
                                    shareContent.getContent(), shareContent.getURL(), result);
                            shareContent(shareBy, mShareContent, type);
                        } else {
                            Logger.w(TAG, "Wrong share content type ! Need to correct !");
                        }
                    }

                    @Override
                    public void onFailure(String msg) {
                        Logger.w(TAG, "Loading image failed when sharing !");
                    }
                });
        return false;
    }

    private void copyToClipboard(String content) {
        ClipboardManager cmb = (ClipboardManager) mActivity.getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setPrimaryClip(ClipData.newPlainText(null, content));
        SingleToast.show(mActivity, com.hooview.app.R.string.msg_copy_to_clipboard_success);
    }
}
