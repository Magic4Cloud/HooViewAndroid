/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.net;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.easyvaas.common.gift.GiftManager;
import com.easyvaas.common.gift.bean.GoodsEntityArray;
import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.bean.BannerModel;
import com.easyvaas.elapp.bean.TopicEntityArray;
import com.easyvaas.elapp.bean.pay.MyAssetEntity;
import com.easyvaas.elapp.bean.serverparam.ServerParam;
import com.easyvaas.elapp.bean.serverparam.WatermarkEntity;
import com.easyvaas.elapp.bean.user.User;
import com.easyvaas.elapp.db.Preferences;
import com.easyvaas.elapp.event.NewMessageEvent;
import com.easyvaas.elapp.ui.user.LoginActivity;
import com.easyvaas.elapp.utils.Constants;
import com.easyvaas.elapp.utils.Logger;
import com.easyvaas.elapp.utils.SingleToast;
import com.easyvaas.elapp.utils.UserUtil;
import com.easyvaas.elapp.utils.Utils;
import com.google.gson.Gson;
import com.hooview.app.R;

import org.greenrobot.eventbus.EventBus;

public class ApiUtil {
    private static final String TAG = "ApiUtil";

    public static void userFollow(Context ctx, String userId, final boolean isChecked, final View clickedView) {
        userFollow(ctx, userId, isChecked, clickedView, null);
    }

    public static void userFollow(final Context ctx, final String userId, final boolean isChecked,
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
                    Preferences.getInstance(ctx).putBoolean(userId, true);
                } else {
                    user.setFollow_count(user.getFollow_count() - 1);
                    Preferences.getInstance(ctx).putBoolean(userId, false);
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

    // 登录过后得到火眼豆
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
        HooviewApiHelper.getInstance().getBannerInfo(new MyRequestCallBack<BannerModel>() {
            @Override
            public void onSuccess(BannerModel result) {
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
        if (ctx == null) return;
        if (EVApplication.isLogin() && Preferences.getInstance(ctx).isLogin()) {
            ApiHelper.getInstance().getMessageUnreadCount(new MyRequestCallBack<String>() {
                @Override
                public void onSuccess(String result) {
                    if (JsonParserUtil.getInt(result, "unread") > 0) {
                        ctx.sendBroadcast(new Intent(Constants.ACTION_SHOW_NEW_MESSAGE_ICON));
//                    ChatManager.getInstance().setHaveUnreadMsg(true);
                        EventBus.getDefault().post(new NewMessageEvent(true));
                    } else {
                        ctx.sendBroadcast(new Intent(Constants.ACTION_HIDE_NEW_MESSAGE_ICON));
//                    ChatManager.getInstance().setHaveUnreadMsg(false);
//                    ChatManager.getInstance().checkUnreadChatGroupMessage(ctx);
                        EventBus.getDefault().post(new NewMessageEvent(false));

                    }
                }

                @Override
                public void onFailure(String msg) {

                }
            });
        }
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

    public static void checkSession(final Context context) {
        if (context == null) return;
        if (EVApplication.isLogin() && Preferences.getInstance(context).isLogin()) {
            ApiHelper.getInstance().userSessionCheck(new MyRequestCallBack<String>() {
                @Override
                public void onSuccess(String result) {
                    UserUtil.handleAfterLoginBySession(context);
                }

                @Override
                public void onError(String errorInfo) {
                    super.onError(errorInfo);
                    Preferences.getInstance(context).logout(false);
//              LoginActivity.start(context);
                }

                @Override
                public void onFailure(String msg) {
                    LoginActivity.start(context);
                }
            });
        }
    }
}
