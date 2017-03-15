/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.utils;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;

import com.easyvaas.common.bottomsheet.BottomSheet;
import com.easyvaas.common.sharelogin.data.ShareConstants;
import com.easyvaas.common.sharelogin.model.ShareContent;
import com.easyvaas.common.sharelogin.model.ShareContentText;
import com.easyvaas.common.sharelogin.model.ShareContentWebpage;
import com.easyvaas.common.sharelogin.qq.QQShareManager;
import com.easyvaas.common.sharelogin.qq.QQZoneShareManager;
import com.easyvaas.common.sharelogin.wechat.WechatShareManager;
import com.easyvaas.common.sharelogin.weibo.WeiboShareManager;
import com.easyvaas.elapp.dialog.ShareRightPanelDialog;
import com.easyvaas.elapp.net.MyRequestCallBack;
import com.easyvaas.elapp.net.RequestHelper;
import com.hooview.app.R;

import java.io.File;

public class ShareHelper {
    private static final String TAG = "ShareHelper";

    private static ShareHelper mInstance;
    private static Activity mActivity;
    private ShareContent mShareContent;
    private BottomSheet mShareBottomSheet;
    private ShareRightPanelDialog mShareRightPanelDailog;
    private boolean isSharePic = false;

    private ShareHelper(Activity activity) {
        if (mActivity != null && mActivity != activity) {
            mActivity = null;
        }
        mActivity = activity;
        initShareMenu();
    }

    private ShareHelper(Activity activity, boolean isSharePic) {
        if (mActivity != null && mActivity != activity) {
            mActivity = null;
        }
        mActivity = activity;
        this.isSharePic = isSharePic;
        initShareMenu();
    }

    public static ShareHelper getInstance(Activity activity) {
        if (mInstance == null || mActivity != activity) {
            mInstance = new ShareHelper(activity);
            mInstance.isSharePic = false;
        }
        return mInstance;
    }

    public static ShareHelper getInstance(Activity activity, boolean isSharePic) {
        if (mInstance == null || mActivity != activity) {
            mInstance = new ShareHelper(activity, isSharePic);
        }
        return mInstance;
    }

    private void initShareMenu() {
        mShareBottomSheet = new BottomSheet.Builder(mActivity).grid()
                .sheet(isSharePic ? R.menu.share_pic : R.menu.share)
                .title(R.string.share)
                .listener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case R.id.menu_share_qq:
                                if (isSharePic) {
                                    sharePicture(Constants.SHARE_BY_QQ);
                                } else {
                                    shareContent(Constants.SHARE_BY_QQ, mShareContent,
                                            Constants.SHARE_TYPE_NOTICE);
                                }
                                break;
                            case R.id.menu_share_qq_zone:
                                if (isSharePic) {
                                    sharePicture(Constants.SHARE_BY_QQ_ZONE);
                                } else {
                                    shareContent(Constants.SHARE_BY_QQ_ZONE, mShareContent,
                                            Constants.SHARE_TYPE_NOTICE);
                                }
                                break;
                            case R.id.menu_share_weibo:
                                if (isSharePic) {
                                    sharePicture(Constants.SHARE_BY_WEIBO);

                                } else {
                                    shareContent(Constants.SHARE_BY_WEIBO, mShareContent,
                                            Constants.SHARE_TYPE_NOTICE);
                                }
                                break;
                            case R.id.menu_share_weixin:
                                if (isSharePic) {
                                    sharePicture(Constants.SHARE_BY_WEIXIN);

                                } else {
                                    shareContent(Constants.SHARE_BY_WEIXIN, mShareContent,
                                            Constants.SHARE_TYPE_NOTICE);
                                }
                                break;
                            case R.id.menu_share_weixin_circle:
                                if (isSharePic) {
                                    sharePicture(Constants.SHARE_BY_WEIXIN_CIRCLE);
                                } else {
                                    shareContent(Constants.SHARE_BY_WEIXIN_CIRCLE, mShareContent,
                                            Constants.SHARE_TYPE_NOTICE);
                                }
                                break;
                            case R.id.menu_share_copy_url:
                                copyToClipboard(mShareContent.getURL());
                                break;
                        }
                    }
                }).build();
    }

    public void showShareBottomPanel(final ShareContent shareContent) {
        mShareContent = shareContent;
        if (TextUtils.isEmpty(mShareContent.getImageUrl())) {
            mShareContent = new ShareContent() {
                @Override
                public int getShareWay() {
                    return shareContent.getShareWay();
                }

                @Override
                public String getContent() {
                    return shareContent.getContent();
                }

                @Override
                public String getTitle() {
                    return shareContent.getTitle();
                }

                @Override
                public String getURL() {
                    return shareContent.getURL();
                }

                @Override
                public String getImageUrl() {
                    return mActivity.getFilesDir().getAbsolutePath() + File.separator + FileUtil.LOGO_FILE_NAME;
                }

                @Override
                public String getMusicUrl() {
                    return shareContent.getMusicUrl();
                }
            };
        }
        mShareBottomSheet.show();
    }

    public void showShareRightPannel(ShareContent shareContent) {
        mShareContent = shareContent;
        if (mShareRightPanelDailog == null) {
            mShareRightPanelDailog = new ShareRightPanelDialog(mActivity, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.iv_qq:
                            shareContent(Constants.SHARE_BY_QQ, mShareContent,
                                    Constants.SHARE_TYPE_NOTICE);
                            break;
                        case R.id.iv_zone:
                            shareContent(Constants.SHARE_BY_QQ_ZONE, mShareContent,
                                    Constants.SHARE_TYPE_NOTICE);
                            break;
                        case R.id.iv_weibo:
                            shareContent(Constants.SHARE_BY_WEIBO, mShareContent,
                                    Constants.SHARE_TYPE_NOTICE);
                            break;
                        case R.id.iv_wechat:
                            shareContent(Constants.SHARE_BY_WEIXIN, mShareContent,
                                    Constants.SHARE_TYPE_NOTICE);
                            break;
                        case R.id.iv_weixin_circle:
                            shareContent(Constants.SHARE_BY_WEIXIN_CIRCLE, mShareContent,
                                    Constants.SHARE_TYPE_NOTICE);
                            break;
                    }
                    mShareRightPanelDailog.dismiss();
                }
            });
        }
        mShareRightPanelDailog.show();

    }

    /**
     * @param shareMenuId One of {R.id.menu_share_qq}, {R.id.menu_share_qq_zone}.
     */
    public void shareContent(int shareMenuId, ShareContent shareContent, String type) {
        switch (shareMenuId) {
            case R.id.menu_share_qq:
                shareContent(Constants.SHARE_BY_QQ, shareContent, type);
                break;
            case R.id.menu_share_qq_zone:
                shareContent(Constants.SHARE_BY_QQ_ZONE, shareContent, type);
                break;
            case R.id.menu_share_weibo:
                shareContent(Constants.SHARE_BY_WEIBO, shareContent, type);
                break;
            case R.id.menu_share_weixin:
                shareContent(Constants.SHARE_BY_WEIXIN, shareContent, type);
                break;
            case R.id.menu_share_weixin_circle:
                shareContent(Constants.SHARE_BY_WEIXIN_CIRCLE, shareContent, type);
                break;
            case R.id.menu_share_copy_url:
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

    public void setShareContentPic(ShareContent shareContent) {
        this.mShareContent = shareContent;
    }

    private void sharePicture(String shareBy) {
        if (mShareContent == null) {
            return;
        }
        Logger.d(TAG, "sharePicture: " + mShareContent.getImageUrl());
        if (Constants.SHARE_BY_WEIBO.equals(shareBy)) {
            WeiboShareManager weiboShareManager = new WeiboShareManager(mActivity);
            weiboShareManager.share(mShareContent, ShareConstants.SHARE_WAY_PIC);
        } else if (Constants.SHARE_BY_WEIXIN.equals(shareBy)) {
            WechatShareManager wechatShareManager = new WechatShareManager(mActivity);
            wechatShareManager.share(mShareContent, WechatShareManager.WEIXIN_SHARE_TYPE_TALK);
        } else if (Constants.SHARE_BY_WEIXIN_CIRCLE.equals(shareBy)) {
            WechatShareManager wechatCircleShare = new WechatShareManager(mActivity);
            wechatCircleShare.share(mShareContent, WechatShareManager.WEIXIN_SHARE_TYPE_FRENDS);
        } else if (Constants.SHARE_BY_QQ.equals(shareBy)) {
            QQShareManager qqShareManager = new QQShareManager(mActivity);
            qqShareManager.share(mShareContent, ShareConstants.SHARE_WAY_PIC);
        } else if (Constants.SHARE_BY_QQ_ZONE.equals(shareBy)) {
            QQZoneShareManager qqShareManager = new QQZoneShareManager(mActivity);
            qqShareManager.share(mShareContent, ShareConstants.SHARE_WAY_PIC);
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
        SingleToast.show(mActivity, R.string.msg_copy_to_clipboard_success);
    }

}
