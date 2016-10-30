/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.net;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.text.TextUtils;

import com.easyvaas.common.gift.bean.GoodsEntityArray;
import com.hooview.app.BuildConfig;
import com.hooview.app.app.EVApplication;
import com.hooview.app.bean.CarouselInfoEntityArray;
import com.hooview.app.bean.RedPackEntity;
import com.hooview.app.bean.SearchInfoEntity;
import com.hooview.app.bean.TopicEntityArray;
import com.hooview.app.bean.UpdateInfoEntity;
import com.hooview.app.bean.message.MessageGroupEntityArray;
import com.hooview.app.bean.message.MessageItemEntityArray;
import com.hooview.app.bean.pay.CashInOptionEntityArray;
import com.hooview.app.bean.pay.MyAssetEntity;
import com.hooview.app.bean.pay.PayCommonRecordEntityArray;
import com.hooview.app.bean.pay.PayOrderEntity;
import com.hooview.app.bean.serverparam.ServerParam;
import com.hooview.app.bean.user.AssetsRankEntityArray;
import com.hooview.app.bean.user.BaseUserEntityArray;
import com.hooview.app.bean.user.RiceRollContributorEntityArray;
import com.hooview.app.bean.user.User;
import com.hooview.app.bean.user.UserEntityArray;
import com.hooview.app.bean.user.UserSettingEntity;
import com.hooview.app.bean.video.LiveInfoEntity;
import com.hooview.app.bean.video.VideoEntity;
import com.hooview.app.bean.video.VideoEntityArray;
import com.hooview.app.db.Preferences;
import com.hooview.app.push.PushHelper;
import com.hooview.app.utils.Logger;
import com.hooview.app.utils.Utils;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class ApiHelper {
    private static final String TAG = "ApiConstant";

    private static ApiHelper mInstance;
    private static RequestHelper sRequestHelper;
    private Preferences mPref;
    private final Context mContext;

    private ApiHelper() {
        mContext = EVApplication.getApp();
        sRequestHelper = RequestHelper.getInstance(mContext);
        mPref = Preferences.getInstance(mContext);
    }

    public static ApiHelper getInstance() {
        if (mInstance == null) {
            mInstance = new ApiHelper();
        }
        return mInstance;
    }

    public void cancelRequest() {
        sRequestHelper.cancelRequest();
    }

    private Map<String, String> getExtPushInfo() {
        Map<String, String> map = getLocationInfo();
        String deviceId = Utils.getDeviceId(mContext);
        map.put("device", "android");
        map.put("dev_id", deviceId);
        map.put("channel_id", PushHelper.getInstance(mContext).getChannelID());
        map.put("dev_token", deviceId);
        map.put("imei", deviceId);
        return map;
    }

    private Map<String, String> getLocationInfo() {
        Map<String, String> stb = new HashMap<>();
        Location location = Utils.getLocation(mContext);
        if (location != null) {
            stb.put("gps_latitude", location.getLatitude() + "");
            stb.put("gps_longitude", location.getLongitude() + "");
            mPref.putString(Preferences.KEY_CACHE_LOCATION,
                    location.getLatitude() + "," + location.getLongitude());
            Utils.removeLocationListener(mContext);
        } else {
            String cache_location = mPref.getString(Preferences.KEY_CACHE_LOCATION);
            if (TextUtils.isEmpty(cache_location)) {
                stb.put("gps_latitude", "0");
                stb.put("gps_longitude", "0");
            } else {
                String[] locationArr = cache_location.split(",");
                stb.put("gps_latitude", locationArr[0]);
                stb.put("gps_longitude", locationArr[1]);
            }
        }
        return stb;
    }

    //======================= EasyVass 1.0 Start =============================================

    public void deviceRegister(MyRequestCallBack<String> callBack) {
        Map<String, String> params = getExtPushInfo();
        sRequestHelper.getAsString(ApiConstant.DEVICE_ONLINE, params, callBack);
    }

    /**
     * @param params token qq/weixin's openid, weibo's user.id
     *               unionid weixin's unionid, only weixin auth need, for
     */
    private void userRegister(Map<String, String> params, MyRequestCallBack<User> callBack) {
        Map<String, String> map = getExtPushInfo();
        map.putAll(params);
        sRequestHelper.postAsGson(ApiConstant.USER_REGISTER, map, User.class, callBack);
    }

    public void registerByPhone(String nickname, String phone, String gender, String password,
            String birthday, String location, String signature, String authType,
            MyRequestCallBack<User> callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("nickname", nickname);
        map.put("token", phone);
        map.put("authtype", authType);
        map.put("password", password);
        map.put("gender", gender);
        map.put("birthday", birthday);
        map.put("location", location);
        map.put("signature", signature);
        userRegister(map, callBack);
    }

    public void registerByAuth(String nickname, String openid, String gender, String accessToken,
            String birthday, String location, String signature, String authType, String expires_in,
            String unionid, String refreshToken, String logoUrl, MyRequestCallBack<User> callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("nickname", nickname);
        map.put("birthday", birthday);
        map.put("token", openid);
        map.put("gender", gender);
        map.put("access_token", accessToken);
        map.put("refresh_token", refreshToken);
        map.put("expires_in", expires_in);
        map.put("location", location);
        map.put("signature", signature);
        map.put("authtype", authType);
        if (!TextUtils.isEmpty(unionid)) {
            map.put("unionid", unionid);
        }
        if (!TextUtils.isEmpty(logoUrl)) {
            map.put("logourl", logoUrl);
        }
        userRegister(map, callBack);
    }

    public void loginByPhone(String phoneNumber, String password, MyRequestCallBack<User> callBack) {
        String md5Pwd = "";
        try {
            md5Pwd = Utils.getMD5(password);
        } catch (NoSuchAlgorithmException e) {
            Logger.e(TAG, "getMD5 string failed !", e);
        }
        if (md5Pwd.isEmpty()) {
            Logger.e(TAG, "user register failed, md5 password is empty!");
            callBack.onFailure("Can not get md5");
            return;
        }

        Map<String, String> map = getExtPushInfo();
        map.put("password", md5Pwd);
        String phone = ApiConstant.VALUE_COUNTRY_CODE + phoneNumber;
        String url = ApiConstant.USER_LOGIN + "authtype=" + User.AUTH_TYPE_PHONE + "&token=" + phone;
        sRequestHelper.postAsGson(url, map, User.class, callBack);
    }

    /**
     * @param token   qq/weixin's openid, weibo's user.id
     * @param unionid weixin's unionid, only weixin auth need, for
     */
    public void loginBySNSAccount(String authType, String nickName, String logoUrl, String gender,
            String token, String unionid, String accessToken, String refreshToken, long expiresIn,
            MyRequestCallBack<User> callBack) {
        Map<String, String> map = getExtPushInfo();
        map.put("nickname", nickName);
        map.put("authtype", authType);
        map.put("token", token);
        map.put("unionid", unionid);
        map.put("logourl", logoUrl);
        map.put("access_token", accessToken);
        if (authType.equals(User.AUTH_TYPE_SINA)) {
            map.put("refresh_token", refreshToken);
        }
        map.put("expires_in", (expiresIn - System.currentTimeMillis() / 1000) + "");
        map.put("gender", gender);
        sRequestHelper.getAsGson(ApiConstant.USER_LOGIN, map, User.class, callBack);
    }

    public void userLogout(MyRequestCallBack<String> callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("sessionid", mPref.getSessionId());
        sRequestHelper.getAsString(ApiConstant.USER_LOGOUT, map, callBack);
    }

    public void resetPassword(String phoneNumber, String password, MyRequestCallBack<String> callBack) {
        String md5Pwd = "";
        try {
            md5Pwd = Utils.getMD5(password);
        } catch (NoSuchAlgorithmException e) {
            Logger.e(TAG, "getMD5 string failed !", e);
        }
        if (md5Pwd.isEmpty()) {
            Logger.e(TAG, "user register failed, md5 password is empty!");
            callBack.onFailure("Can not get md5");
            return;
        }

        Map<String, String> map = new HashMap<>();
        map.put("phone", ApiConstant.VALUE_COUNTRY_CODE + phoneNumber);
        map.put("password", md5Pwd);

        sRequestHelper.postAsString(ApiConstant.USER_RESET_PASSWORD, map, callBack);
    }

    /**
     * @param token   qq/weixin's openid, weibo's user.id
     * @param unionid weixin's unionid, only weixin auth need, for
     */
    private void userAuthBind(String type, String token, String password, String access_token,
            String refreshToken, long expiresIn, String unionid, MyRequestCallBack<String> callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("type", type);
        map.put("token", token);
        map.put("unionid", unionid);
        map.put("access_token", access_token);
        map.put("refresh_token", refreshToken);
        if (expiresIn > 0) {
            map.put("expires_in", expiresIn + "");
        }
        try {
            map.put("password", Utils.getMD5(password));
        } catch (NoSuchAlgorithmException e) {
            Logger.e(TAG, "getMD5 string failed !", e);
        }
        map.put("sessionid", mPref.getSessionId());
        sRequestHelper.postAsString(ApiConstant.USER_AUTH_BIND, map, callBack);
    }

    public void bindByAuth(String type, String token, String access_token,
            String refreshToken, long expiresIn, String unionid, MyRequestCallBack<String> callBack) {
        userAuthBind(type, token, "", access_token, refreshToken, expiresIn, unionid, callBack);
    }

    public void bindPhone(String phoneNumber, String pwd, MyRequestCallBack<String> callBack) {
        phoneNumber = ApiConstant.VALUE_COUNTRY_CODE + phoneNumber;
        userAuthBind(User.AUTH_TYPE_PHONE, phoneNumber, pwd, "", "", -1, "", callBack);
    }

    public void userAuthUnbind(String authType, MyRequestCallBack<String> callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("sessionid", mPref.getSessionId());
        map.put("type", authType);
        sRequestHelper.getAsString(ApiConstant.USER_AUTH_UNBIND, map, callBack);
    }

    public static int SMS_TYPE_REGISTER = 0;
    public static int SMS_TYPE_RESET_PWD = 1;

    /**
     * @param smsType One of {@link #SMS_TYPE_REGISTER}, {@link #SMS_TYPE_RESET_PWD}
     */
    public void smsSend(String phoneNumber, int smsType, final MyRequestCallBack<String> callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("phone", ApiConstant.VALUE_COUNTRY_CODE + phoneNumber);
        map.put("type", smsType + "");

        sRequestHelper.getAsString(ApiConstant.SMS_SEND, map, callBack);
    }

    public void smsVerify(String smsId, String smsCode, String authType,
            final MyRequestCallBack<String> callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("sms_id", smsId);
        map.put("sms_code", smsCode);
        map.put("authtype", authType);

        sRequestHelper.getAsString(ApiConstant.SMS_VERIFY, map, callBack);
    }

    private void getUserInfo(String name, String imUserId, boolean isBase, MyRequestCallBack<User> callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("sessionid", mPref.getSessionId());
        if (!TextUtils.isEmpty(name)) {
            map.put("name", name);
        }
        if (!TextUtils.isEmpty(imUserId)) {
            map.put("imuser", imUserId);
        }
        if (isBase) {
            sRequestHelper.getAsGson(ApiConstant.USER_BASE_INFO, map, User.class, callBack);
        } else {
            sRequestHelper.getAsGson(ApiConstant.USER_INFO, map, User.class, callBack);
        }
    }

    public void getUserInfoByImuser(String imuser, MyRequestCallBack<User> callBack) {
        getUserInfo("", imuser, false, callBack);
    }

    public void getUserInfo(String name, MyRequestCallBack<User> callBack) {
        getUserInfo(name, "", false, callBack);
    }

    public void getUserInfos(List<String> names, MyRequestCallBack<BaseUserEntityArray> callBack) {
        Map<String, String> map = new HashMap<>();
        if (names == null || names.size() == 0) {
            return;
        }
        String nameList = "";
        for (int i = 0, n = names.size(); i < n; i++) {
            if (i != n - 1) {
                nameList = nameList + names.get(i) + ",";
            } else {
                nameList = nameList + names.get(i);
            }
        }
        map.put("sessionid", mPref.getSessionId());
        map.put("namelist", nameList);
        sRequestHelper.getAsGson(ApiConstant.USER_INFOS, map, BaseUserEntityArray.class, callBack);
    }

    public void userEditInfo(String nickname, String birthday, String location,
            String gender, String signature, MyRequestCallBack<String> callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("sessionid", mPref.getSessionId());
        map.put("nickname", nickname);
        map.put("birthday", birthday);
        map.put("gender", gender);
        map.put("signature", signature);
        if (TextUtils.isEmpty(location)) {
            map.put("location", mContext.getString(com.hooview.app.R.string.default_user_location));
        } else {
            map.put("location", location);
        }
        sRequestHelper.getAsString(ApiConstant.USER_EDIT_INFO, map, callBack);
    }

    public void userEditSetting(boolean isLive, boolean isFollow, boolean isDisturb,
            MyRequestCallBack<String> callBack) {
        Map<String, String> map = new Hashtable<String, String>();
        map.put("sessionid", mPref.getSessionId());
        map.put("live", isLive ? "1" : "0");
        map.put("follow", isFollow ? "1" : "0");
        map.put("disturb", isDisturb ? "1" : "0");
        sRequestHelper.getAsString(ApiConstant.USER_SETTING_EDIT, map, callBack);
    }

    public void userSettingInfo(MyRequestCallBack<UserSettingEntity> callBack) {
        Map<String, String> map = new Hashtable<String, String>();
        map.put("sessionid", mPref.getSessionId());
        sRequestHelper.getAsGson(ApiConstant.USER_SETTING_INFO, map, UserSettingEntity.class, callBack);
    }

    public void userFollow(String name, boolean follow, MyRequestCallBack<String> callBack) {
        userFollow(name, follow, "", callBack);
    }

    public void userFollow(String name, boolean follow, String vid, MyRequestCallBack<String> callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("sessionid", mPref.getSessionId());
        map.put("name", name);
        if (!TextUtils.isEmpty(vid)) {
            map.put("vid", vid);
        }
        map.put("action", follow ? "1" : "0");
        sRequestHelper.getAsString(ApiConstant.USER_FOLLOW, map, callBack);
    }

    public void getFollowerList(String name, int start, int count,
            MyRequestCallBack<UserEntityArray> callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("sessionid", mPref.getSessionId());
        map.put("name", name);
        map.put("start", start + "");
        map.put("count", count + "");
        sRequestHelper.getAsGson(ApiConstant.USER_FOLLOWER_LIST, map, UserEntityArray.class, callBack);
    }

    public void getFansList(String name, int start, int count, MyRequestCallBack<UserEntityArray> callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("sessionid", mPref.getSessionId());
        map.put("name", name);
        map.put("start", start + "");
        map.put("count", count + "");
        sRequestHelper.getAsGson(ApiConstant.USER_FANS_LIST, map, UserEntityArray.class, callBack);
    }

    public void searchInfos(String type, String keyword, int startIndex, int count,
            MyRequestCallBack<SearchInfoEntity> callBack) {
        Map<String, String> param = new HashMap<>();
        param.put("sessionid", mPref.getSessionId());
        param.put("type", type);
        param.put("keyword", keyword);
        param.put("start", startIndex + "");
        param.put("count", count + "");
        sRequestHelper.getAsGson(ApiConstant.SEARCH_INFO, param, SearchInfoEntity.class, callBack);
    }

    public static final String VIDEO_QUALITY_NORMAL = "normal";
    public static final String VIDEO_QUALITY_STANDARD = "standard";
    public static final String VIDEO_QUALITY_HIGH = "high";
    public static final String VIDEO_QUALITY_SUPER = "super";

    public void liveStart(String title, boolean showGps, MyRequestCallBack<LiveInfoEntity> callBack) {
        Map<String, String> param = getLocationInfo();
        param.put("sessionid", mPref.getSessionId());
        param.put("title", title);
        param.put("quality", VIDEO_QUALITY_NORMAL);
        param.put("gps", showGps ? "1" : "0");
        sRequestHelper.getAsGson(ApiConstant.LIVE_START, param, LiveInfoEntity.class, callBack);
    }

    public void liveStop(String vid, boolean save, MyRequestCallBack<VideoEntity> callBack) {
        Map<String, String> param = new HashMap<>();
        param.put("sessionid", mPref.getSessionId());
        param.put("vid", vid);
        param.put("save", save ? "1" : "0");
        sRequestHelper.getAsGson(ApiConstant.LIVE_STOP, param, VideoEntity.class, callBack);
    }

    public void getWatchVideo(String vid, MyRequestCallBack<VideoEntity> callBack) {
        getWatchVideo(vid, "", callBack);
    }

    public void videoSetTitle(String vid, String title, MyRequestCallBack<String> callBack) {
        Map<String, String> param = new HashMap<>();
        param.put("sessionid", mPref.getSessionId());
        param.put("vid", vid);
        param.put("title", title);
        sRequestHelper.getAsString(ApiConstant.VIDEO_SET_TITLE, param, callBack);
    }

    public void getVideoInfo(String vid, MyRequestCallBack<VideoEntity> callBack) {
        Map<String, String> param = new HashMap<>();
        param.put("sessionid", mPref.getSessionId());
        param.put("vid", vid);
        sRequestHelper.getAsGson(ApiConstant.VIDEO_INFO, param, VideoEntity.class, callBack);
    }

    public void getVideoInfos(String[] vids, MyRequestCallBack<VideoEntityArray> callBack) {
        Map<String, String> param = new HashMap<>();
        String vidlist = "";
        for (int i = 0, n = vids.length; i < n; i++) {
            if (i != n - 1) {
                vidlist = vids[i] + ",";
            } else {
                vidlist = vids[i];
            }
        }
        param.put("sessionid", mPref.getSessionId());
        param.put("vidlist", vidlist);
        sRequestHelper.getAsGson(ApiConstant.VIDEO_INFOS, param, VideoEntityArray.class, callBack);
    }

    public void getHotVideoList(int startIndex, int count, MyRequestCallBack<VideoEntityArray> callBack) {
        Map<String, String> param = new HashMap<>();
        param.put("sessionid", mPref.getSessionId());
        param.put("start", startIndex + "");
        param.put("count", count + "");
        sRequestHelper.getAsGson(ApiConstant.HOT_LIVE_LIST, param, VideoEntityArray.class, callBack);
    }

    public void getFriendVideoList(int startIndex, int count, MyRequestCallBack<VideoEntityArray> callBack) {
        Map<String, String> param = new HashMap<>();
        param.put("sessionid", mPref.getSessionId());
        param.put("start", startIndex + "");
        param.put("count", count + "");
        sRequestHelper.getAsGson(ApiConstant.VIDEO_FRIEND, param, VideoEntityArray.class, callBack);
    }

    void checkServerParamNew(MyRequestCallBack<ServerParam> callBack) {
        Map<String, String> map = new Hashtable<>();
        map.put("sessionid", mPref.getSessionId());
        map.put("devtype", "android");
        sRequestHelper.getAsGson(ApiConstant.SYS_SETTINGS, map, ServerParam.class, callBack);
    }

    public void checkUpdate(MyRequestCallBack<UpdateInfoEntity> callBack) {
        Map<String, String> param = new HashMap<>();
        param.put("version", BuildConfig.VERSION_NAME);
        param.put("device", "android");
        param.put("versioncode", BuildConfig.VERSION_CODE + "");
        sRequestHelper.getAsGson(ApiConstant.CHECK_UPDATE, param, UpdateInfoEntity.class, callBack);
    }

    public void uploadThumb(String url, Map<String, String> params, Bitmap bitmap, String fileName,
            MyRequestCallBack<String> callBack) {
        sRequestHelper.uploadFile(url, params, bitmap, fileName, callBack);
    }

    public void getFriends(int startIndex, int count, MyRequestCallBack<UserEntityArray> callBack) {
        Map<String, String> param = new HashMap<>();
        param.put("sessionid", mPref.getSessionId());
        param.put("start", startIndex + "");
        param.put("count", count + "");
        sRequestHelper.getAsGson(ApiConstant.USER_FRIENDS, param, UserEntityArray.class, callBack);
    }

    //======================= EasyVass 1.0 End =============================================


    public void getWatchVideo(String vid, String password, MyRequestCallBack<VideoEntity> callBack) {
        Map<String, String> param = new HashMap<>();
        param.put("sessionid", mPref.getSessionId());
        param.put("vid", vid);
        param.put("password", password);
        sRequestHelper.getAsGson(ApiConstant.WATCH_START, param, VideoEntity.class, callBack);
    }

    public void liveStart(String title, boolean showGps, int permission, String password,
            int price, MyRequestCallBack<LiveInfoEntity> callBack) {
        Map<String, String> param = getLocationInfo();
        param.put("sessionid", mPref.getSessionId());
        param.put("title", title);
        param.put("quality", VIDEO_QUALITY_NORMAL);
        param.put("gps", showGps ? "1" : "0");
        param.put("permission", permission + "");
        if (permission == ApiConstant.VALUE_LIVE_PERMISSION_PASSWORD) {
            param.put("password", password + "");
        }
        if (permission == ApiConstant.VALUE_LIVE_PERMISSION_PAY) {
            param.put("price", price + "");
        }
        sRequestHelper.getAsGson(ApiConstant.LIVE_START, param, LiveInfoEntity.class, callBack);
    }

    public void getTopicList(int startIndex, int count, MyRequestCallBack<TopicEntityArray> callBack) {
        Map<String, String> param = new HashMap<>();
        param.put("sessionid", mPref.getSessionId());
        param.put("start", startIndex + "");
        param.put("count", count + "");
        sRequestHelper.getAsGson(ApiConstant.VIDEO_TOPIC_LIST, param, TopicEntityArray.class, callBack);
    }

    public void getTopicVideoList(String topicId, boolean containLive, int startIndex, int count,
            MyRequestCallBack<VideoEntityArray> callBack) {
        Map<String, String> param = new HashMap<>();
        param.put("sessionid", mPref.getSessionId());
        param.put("topicid", topicId);
        param.put("start", startIndex + "");
        param.put("count", count + "");
        param.put("live", containLive ? "1" : "0");
        sRequestHelper.getAsGson(ApiConstant.VIDEO_TOPIC_VIDEO_LIST, param, VideoEntityArray.class, callBack);
    }

    public void setTopic(String vid, String topicId, MyRequestCallBack<String> callBack) {
        Map<String, String> param = new HashMap<>();
        param.put("sessionid", mPref.getSessionId());
        param.put("vid", vid);
        param.put("topicid", topicId);
        sRequestHelper.getAsString(ApiConstant.VIDEO_SET_TOPIC, param, callBack);
    }

    public void videoPay(String vid, MyRequestCallBack<String> callBack) {
        Map<String, String> param = new HashMap<>();
        param.put("sessionid", mPref.getSessionId());
        param.put("vid", vid);
        sRequestHelper.getAsString(ApiConstant.VIDEO_PAY, param, callBack);
    }

    public void videoRemove(String vid, MyRequestCallBack<String> callBack) {
        Map<String, String> param = new HashMap<>();
        param.put("sessionid", mPref.getSessionId());
        param.put("vid", vid);
        sRequestHelper.getAsString(ApiConstant.VIDEO_REMOVE, param, callBack);
    }

    public void liveShutUp(String vid, String name, boolean shutUp, MyRequestCallBack<String> callBack) {
        Map<String, String> param = new HashMap<>();
        param.put("sessionid", mPref.getSessionId());
        param.put("vid", vid);
        param.put("name", name);
        param.put("shutup", shutUp ? "1" : "0");
        param.put("time", "0");
        param.put("reason", "");
        sRequestHelper.getAsString(ApiConstant.INTERACT_SHUT_UP, param, callBack);
    }

    public void liveSetManager(String vid, String name, boolean manager, int durationSeconds, String reason,
            MyRequestCallBack<String> callBack) {
        Map<String, String> param = new HashMap<>();
        param.put("sessionid", mPref.getSessionId());
        param.put("vid", vid);
        param.put("name", name);
        param.put("shutup", manager ? "1" : "0");
        param.put("time", durationSeconds + "");
        param.put("reason", reason);
        sRequestHelper.getAsString(ApiConstant.INTERACT_SET_MANAGER, param, callBack);
    }

    public void liveKickUser(String vid, String name, boolean kick, int durationSeconds, String reason,
            MyRequestCallBack<String> callBack) {
        Map<String, String> param = new HashMap<>();
        param.put("sessionid", mPref.getSessionId());
        param.put("vid", vid);
        param.put("name", name);
        param.put("shutup", kick ? "1" : "0");
        param.put("time", durationSeconds + "");
        param.put("reason", reason);
        sRequestHelper.getAsString(ApiConstant.INTERACT_SET_MANAGER, param, callBack);
    }

    public void getMessageGroupList(MyRequestCallBack<MessageGroupEntityArray> callBack) {
        Map<String, String> param = new HashMap<>();
        param.put("sessionid", mPref.getSessionId());
        sRequestHelper.getAsGson(ApiConstant.MESSAGE_GROUP_LIST,
                param, MessageGroupEntityArray.class, callBack);
    }

    public void getMessageItemList(int startIndex, int count, long groupId,
            MyRequestCallBack<MessageItemEntityArray> callBack) {
        Map<String, String> param = new HashMap<>();
        param.put("sessionid", mPref.getSessionId());
        param.put("start", startIndex + "");
        param.put("count", count + "");
        param.put("groupid", groupId + "");
        sRequestHelper.getAsGson(ApiConstant.MESSAGE_ITEM_LIST, param, MessageItemEntityArray.class,
                callBack);
    }

    public void getMessageUnreadCount(MyRequestCallBack<String> callBack) {
        Map<String, String> param = new HashMap<>();
        param.put("sessionid", mPref.getSessionId());
        sRequestHelper.getAsString(ApiConstant.MESSAGE_UNREAD_COUNT, param, callBack);
    }

    public void getAssetInfo(final MyRequestCallBack<MyAssetEntity> callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("sessionid", mPref.getSessionId());
        sRequestHelper.getAsGson(ApiConstant.PAY_ASSET, map, MyAssetEntity.class, callBack);
    }

    public void getCashInOptions(final MyRequestCallBack<CashInOptionEntityArray> callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("sessionid", mPref.getSessionId());
        map.put("device", "android");
        sRequestHelper.postAsGson(ApiConstant.PAY_CASH_IN_OPTION, map, CashInOptionEntityArray.class, callBack);
    }

    public void cashOutByWeixin(int amount, final MyRequestCallBack<MyAssetEntity> callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("sessionid", mPref.getSessionId());
        map.put("rmb", amount + "");
        sRequestHelper.postAsGson(ApiConstant.PAY_CASH_OUT, map, MyAssetEntity.class, callBack);
    }

    public void getCashOutRecords(int startIndex, int count,
            final MyRequestCallBack<PayCommonRecordEntityArray> callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("sessionid", mPref.getSessionId());
        map.put("start", startIndex + "");
        map.put("count", count + "");
        sRequestHelper
                .getAsGson(ApiConstant.PAY_CASH_OUT_RECORD, map, PayCommonRecordEntityArray.class, callBack);
    }

    private void cashIn(int amount, String platform, String activity, int free,
            final MyRequestCallBack<PayOrderEntity> callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("sessionid", mPref.getSessionId());
        //DecimalFormat decimalFormat=new DecimalFormat(".00");
        map.put("amount", amount + "");
        map.put("platform", platform);
        map.put("activity", "");
        map.put("free", free + "");
        sRequestHelper.postAsGson(ApiConstant.PAY_RECHARGE, map, PayOrderEntity.class, callBack);
    }

    public void cashInByWeixin(int amount, final MyRequestCallBack<PayOrderEntity> callBack) {
        cashIn(amount, "weixin", "", 0, callBack);
    }

    public void cashInByAlipay(int amount, final MyRequestCallBack<PayOrderEntity> callBack) {
        cashIn(amount, "ali", "", 0, callBack);
    }

    public void getCashInRecords(int startIndex, int count,
            final MyRequestCallBack<PayCommonRecordEntityArray> callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("sessionid", mPref.getSessionId());
        map.put("start", startIndex + "");
        map.put("count", count + "");
        sRequestHelper.postAsGson(ApiConstant.PAY_RECHARGE_RECORD, map, PayCommonRecordEntityArray.class,
                callBack);
    }

    public void getGiftList(MyRequestCallBack<GoodsEntityArray> callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("sessionid", mPref.getSessionId());
        sRequestHelper.getAsGson(ApiConstant.PAY_GIFT_LIST, map, GoodsEntityArray.class, callBack);
    }

    public void sendGift(String vid, long goodsId, int count, boolean isContinue, String toName,
            MyRequestCallBack<MyAssetEntity> callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("sessionid", mPref.getSessionId());
        map.put("vid", vid);
        map.put("goodsid", goodsId + "");
        map.put("count", count + "");
        map.put("continue", isContinue ? "yes" : "no");
        map.put("touser", toName);
        sRequestHelper.getAsGson(ApiConstant.PAY_SEND_GIFT, map, MyAssetEntity.class, callBack);
    }

    public void sendRedPack(String vid, String amount, String count, String title,
            MyRequestCallBack<String> callBack) {
        sendRedPack(vid, amount, count, title, "", callBack);
    }

    public void sendRedPack(String vid, String amount, String count, String title, String toName,
            MyRequestCallBack<String> callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("sessionid", mPref.getSessionId());
        map.put("vid", vid);
        map.put("amount", amount);
        map.put("count", count);  // count <= 200
        map.put("type", "0");
        map.put("message", title);
        map.put("touser", toName);
        sRequestHelper.getAsString(ApiConstant.PAY_SEND_RED_PACK, map, callBack);
    }

    public void openRedPack(String redPackId, MyRequestCallBack<RedPackEntity> callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("sessionid", mPref.getSessionId());
        map.put("redpackid", redPackId);
        sRequestHelper.getAsGson(ApiConstant.PAY_OPEN_RED_PACK, map, RedPackEntity.class, callBack);
    }

    public void getContributor(String name, int startIndex,
            int count, final MyRequestCallBack<RiceRollContributorEntityArray> callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("sessionid", mPref.getSessionId());
        map.put("name", name);
        map.put("start", startIndex + "");
        map.put("count", count + "");
        sRequestHelper.getAsGson(ApiConstant.PAY_CONTRIBUTOR_LIST, map, RiceRollContributorEntityArray.class,
                callBack);
    }

    public void getAssetsRankList(String type, int startIndex,
            int count, final MyRequestCallBack<AssetsRankEntityArray> callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("sessionid", mPref.getSessionId());
        map.put("type", type);
        map.put("start", startIndex + "");
        map.put("count", count + "");
        sRequestHelper
                .getAsGson(ApiConstant.PAY_ASSETS_RANK_LIST, map, AssetsRankEntityArray.class, callBack);
    }

    public void getCarouseInfo(MyRequestCallBack<CarouselInfoEntityArray> callBack) {
        Map<String, String> param = new HashMap<>();
        param.put("sessionid", mPref.getSessionId());
        sRequestHelper.getAsGson(ApiConstant.VIDEO_CAROUSEL_INFO, param,
                CarouselInfoEntityArray.class, callBack);
    }

    public void userUpdatePassword(String oldPassword, String newPassword,
            MyRequestCallBack<String> callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("sessionid", mPref.getSessionId());
        String md5oldPassword = "", md5newPassword = "";
        try {
            md5oldPassword = Utils.getMD5(oldPassword);
            md5newPassword = Utils.getMD5(newPassword);
        } catch (NoSuchAlgorithmException e) {
            Logger.e(TAG, "getMD5 string failed !", e);
        }
        if (md5oldPassword.isEmpty() || md5newPassword.isEmpty()) {
            Logger.e(TAG, "user update password failed, md5 password is empty!");
            callBack.onFailure("Can not get md5");
            return;
        }
        map.put("old", md5oldPassword);
        map.put("new", md5newPassword);
        sRequestHelper.postAsString(ApiConstant.USER_UPDATE_PASSWORD, map, callBack);
    }

    public void changeBindPhone(String token, final MyRequestCallBack<String> callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("sessionid", mPref.getSessionId());
        map.put("token", ApiConstant.VALUE_COUNTRY_CODE + token);
        sRequestHelper.getAsString(ApiConstant.USER_AUTH_PHONE_CHANGE, map, callBack);
    }

    //检查用户的sessionId是不是过期了
    public void userSessionCheck(MyRequestCallBack<String> callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("sessionid", mPref.getSessionId());
        sRequestHelper.getAsString(ApiConstant.USER_SESSION_CHECK, map, callBack);
    }

    public void getUserVideoList(String name, int startIndex, int count,
            MyRequestCallBack<VideoEntityArray> callBack) {
        Map<String, String> param = new HashMap<>();
        param.put("sessionid", mPref.getSessionId());
        param.put("name", name);
        param.put("start", startIndex + "");
        param.put("count", count + "");
        sRequestHelper.getAsGson(ApiConstant.USER_VIDEO_LIST, param, VideoEntityArray.class, callBack);
    }

    public static final int REPORT_TYPE_VIDEO = 1;
    public static final int REPORT_TYPE_USER = 2;
    public static final int REPORT_TYPE_GROUP = 3;

    /**
     * @param name
     * @param type One of {@link #REPORT_TYPE_VIDEO}, {@link #REPORT_TYPE_USER}, or {@link #REPORT_TYPE_GROUP}.
     */
    public void commonReport(String name, String description, int type, MyRequestCallBack<String> callBack) {
        Map<String, String> param = new HashMap<>();
        String reportType;
        if (type == REPORT_TYPE_VIDEO) {
            reportType = "video: ";
        } else if (type == REPORT_TYPE_GROUP) {
            reportType = "group: ";
        } else {
            reportType = "user:";
        }
        param.put("sessionid", mPref.getSessionId());
        param.put("description", reportType + description);
        param.put("name", name);
        sRequestHelper.getAsString(ApiConstant.USER_INFORM, param, callBack);
    }
    //======================= EasyVass 1.0.5 End =============================================

    public void userSubscribe(String name, String subscribe, String deviceId,
            MyRequestCallBack<String> callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("sessionid", mPref.getSessionId());
        map.put("name", name);
        map.put("subscribe", subscribe);
        map.put("device", deviceId);
    }

    public void getCashOutTotal(final MyRequestCallBack callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("sessionid", mPref.getSessionId());
    }

    public void getRechargeTotal(final MyRequestCallBack callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("sessionid", mPref.getSessionId());
    }
}
