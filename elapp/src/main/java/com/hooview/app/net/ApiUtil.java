/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.net;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.google.gson.Gson;

import com.easyvaas.common.chat.ChatManager;
import com.easyvaas.common.gift.GiftManager;
import com.easyvaas.common.gift.bean.GoodsEntityArray;

import com.hooview.app.R;
import com.hooview.app.app.EVApplication;
import com.hooview.app.bean.CarouselInfoEntityArray;
import com.hooview.app.bean.TopicEntityArray;
import com.hooview.app.bean.pay.MyAssetEntity;
import com.hooview.app.bean.serverparam.ServerParam;
import com.hooview.app.bean.serverparam.WatermarkEntity;
import com.hooview.app.bean.user.User;
import com.hooview.app.db.Preferences;
import com.hooview.app.utils.Constants;
import com.hooview.app.utils.Logger;
import com.hooview.app.utils.SingleToast;
import com.hooview.app.utils.Utils;

public class ApiUtil {
    private static final String TAG = "ApiUtil";

    public static void userFollow(Context ctx, String userId, final boolean isChecked, final View clickedView) {
        userFollow(ctx, userId, isChecked, clickedView, null);
    }

    public static void userFollow(final Context ctx, String userId, final boolean isChecked,
            final View clickedView, final MyRequestCallBack<String> callBack) {
        if (clickedView != null) {
            clickedView.setEnabled(false);
        }
        final int success = isChecked ? R.string.msg_follow_success : R.string.msg_unfollow_success;
        final int failed = isChecked ? R.string.msg_follow_success : R.string.msg_unfollow_success;
        ApiHelper.getInstance().userFollow(userId, isChecked, new MyRequestCallBack<String>() {
            @Override
            public void onSuccess(String result) {
                Logger.d(TAG, "follow result : " + result);
                SingleToast.show(ctx, success);
                User user = EVApplication.getUser();
                if (isChecked) {
                    user.setFollow_count(user.getFollow_count() + 1);
                } else {
                    user.setFollow_count(user.getFollow_count() - 1);
                }
                if (clickedView != null) {
                    clickedView.setEnabled(true);
                }
                if (callBack != null) {
                    callBack.onSuccess(result);
                }
            }

            @Override
            public void onError(String errorInfo) {
                super.onError(errorInfo);
                SingleToast.show(ctx, failed);
                if (clickedView != null) {
                    clickedView.setEnabled(true);
                }
                if (callBack != null) {
                    callBack.onError(errorInfo);
                }
            }

            @Override
            public void onFailure(String msg) {
                RequestUtil.handleRequestFailed(msg);
                SingleToast.show(ctx, failed);
                if (clickedView != null) {
                    clickedView.setEnabled(true);
                }
                if (callBack != null) {
                    callBack.onFailure(msg);
                }
            }
        });
    }

    public static void getAssetInfo(final Context ctx) {
        ApiHelper.getInstance().getAssetInfo(new MyRequestCallBack<MyAssetEntity>() {
            @Override
            public void onSuccess(MyAssetEntity result) {
                if (result != null) {
                    Preferences.getInstance(ctx)
                            .putLong(Preferences.KEY_PARAM_ASSET_E_COIN_ACCOUNT, result.getEcoin());
                    GiftManager.setECoinCount(ctx, result.getEcoin());
                }
            }

            @Override
            public void onFailure(String msg) {

            }
        });
    }

    public static void checkServerParam(final Context context) {
        final Preferences pref = Preferences.getInstance(context);
        cacheCarouseInfo(context);
        cacheTopicList(context);
        cacheGoodsList(context);
        ApiHelper.getInstance().checkServerParamNew(new MyRequestCallBack<ServerParam>() {
            @Override
            public void onSuccess(ServerParam result) {
                if (result.getScreenlist() == null || result.getScreenlist().size() == 0) {
                    pref.remove(Preferences.KEY_PARAM_SCREEN_LIST_JSON);
                } else {
                    String json = new Gson().toJson(result.getScreenlist());
                    pref.putString(Preferences.KEY_PARAM_SCREEN_LIST_JSON, json);
                }
                if (result.getH5url() != null) {
                    pref.putString(Preferences.KEY_PARAM_CONTACT_US_URL,
                            result.getH5url().getContactinfo());
                }
                if (result.getH5url() != null) {
                    pref.putString(Preferences.KEY_PARAM_FREE_USER_INFO_US_URL,
                            result.getH5url().getFreeuserinfo());
                }
                if (result.getWebchatinfo() != null) {
                    pref.putString(Preferences.KEY_PARAM_WEB_CHAT_INFO_US_URL,
                            result.getWebchatinfo().getUrl());
                }
                if (result.getWatermark() != null) {
                    //cache watermark param
                    WatermarkEntity watermarkEntity = result.getWatermark();
                    final String wmUrl = watermarkEntity.getUrl();
                    if (!TextUtils.isEmpty(wmUrl)) {
                        new Thread() {
                            @Override
                            public void run() {
                                String watermarkPath = Utils.downloadWatermarkImage(wmUrl);
                                pref.putString(Preferences.KEY_PARAM_WATERMARK_IMAGE_PATH, watermarkPath);
                            }
                        }.start();
                    }
                    String wmJson = new Gson().toJson(watermarkEntity);
                    pref.putString(Preferences.KEY_PARAM_WATERMARK_JSON, wmJson);
                }
            }

            @Override
            public void onFailure(String msg) {
                RequestUtil.handleRequestFailed(msg);
            }
        });
    }

    private static void cacheCarouseInfo(final Context ctx) {
        ApiHelper.getInstance().getCarouseInfo(new MyRequestCallBack<CarouselInfoEntityArray>() {
            @Override
            public void onSuccess(CarouselInfoEntityArray result) {
                if (result != null) {
                    String json = new Gson().toJson(result);
                    Preferences.getInstance(ctx).putString(Preferences.KEY_CACHED_CAROUSEL_INFO_JSON, json);
                }
            }

            @Override
            public void onError(String errorInfo) {
                super.onError(errorInfo);
            }

            @Override
            public void onFailure(String msg) {
                RequestUtil.handleRequestFailed(msg);
            }
        });
    }

    public static void checkUnreadMessage(final Context ctx) {
        ApiHelper.getInstance().getMessageUnreadCount(new MyRequestCallBack<String>() {
            @Override
            public void onSuccess(String result) {
                if (JsonParserUtil.getInt(result, "unread") > 0) {
                    ctx.sendBroadcast(new Intent(Constants.ACTION_SHOW_NEW_MESSAGE_ICON));
                    ChatManager.getInstance().setHaveUnreadMsg(true);
                } else {
                    ctx.sendBroadcast(new Intent(Constants.ACTION_HIDE_NEW_MESSAGE_ICON));
                    ChatManager.getInstance().setHaveUnreadMsg(false);
                    ChatManager.getInstance().checkUnreadChatGroupMessage(ctx);
                }
            }

            @Override
            public void onFailure(String msg) {

            }
        });
    }

    private static void cacheTopicList(final Context context) {
        ApiHelper.getInstance().getTopicList(ApiConstant.DEFAULT_FIRST_PAGE_INDEX,
                ApiConstant.DEFAULT_PAGE_SIZE_ALL, new MyRequestCallBack<TopicEntityArray>() {
                    @Override
                    public void onSuccess(TopicEntityArray result) {
                        if (result != null && result.getTopics() != null && result.getTopics().size() > 0) {
                            String json = new Gson().toJson(result);
                            Preferences.getInstance(context)
                                    .putString(Preferences.KEY_CACHED_TOPICS_INFO_JSON, json);
                        }
                    }

                    @Override
                    public void onFailure(String msg) {

                    }
                });
    }

    private static void cacheGoodsList(final Context context) {
        ApiHelper.getInstance().getGiftList(new MyRequestCallBack<GoodsEntityArray>() {
            @Override
            public void onSuccess(GoodsEntityArray result) {
                GiftManager.setGoodsList(context, result);
            }

            @Override
            public void onFailure(String msg) {

            }
        });
    }
}
