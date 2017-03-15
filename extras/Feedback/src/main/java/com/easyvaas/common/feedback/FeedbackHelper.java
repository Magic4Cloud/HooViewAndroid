package com.easyvaas.common.feedback;

import java.util.HashMap;
import java.util.Map;

import android.app.Application;
import android.content.Context;

import org.json.JSONObject;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.alibaba.sdk.android.feedback.util.IWxCallback;

public class FeedbackHelper {
    private static final String APP_KEY = "";
    private static final String UID = "";
    private static final String PWD = "";
    private static FeedbackHelper mInstance;
    private final Context mContext;

    public static FeedbackHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new FeedbackHelper(context);
        }
        return mInstance;
    }

    private FeedbackHelper(Context context) {
        this.mContext = context;
    }

    public void init(Application application, String appKey) {
        FeedbackAPI.initAnnoy(application, appKey);
        //FeedbackAPI.initOpenImAccount(mContext.getap, APP_KEY, UID, PWD);
    }

    public void showFeedbackUI() {
        FeedbackAPI.openFeedbackActivity(mContext);
    }

    public void getUnreadCount(String uid) {
        FeedbackAPI.getFeedbackUnreadCount(mContext, uid, new IWxCallback() {
            @Override
            public void onSuccess(Object... objects) {

            }

            @Override
            public void onError(int i, String s) {

            }

            @Override
            public void onProgress(int i) {

            }
        });
    }

    public void customUI(String title, String phoneNumber, String avatar, String toAvatar) {
        //可以设置UI自定义参数，如主题色等,map的key值具体为：
        //enableAudio(是否开启语音 1：开启 0：关闭)
        //bgColor(消息气泡背景色 "#ffffff")，
        //color(消息内容文字颜色 "#ffffff")，
        //avatar(当前登录账号的头像)，string，为http url
        //toAvatar(客服账号的头像),string，为http url
        //themeColor(标题栏自定义颜色 "#ffffff")
        //profilePlaceholder: (顶部联系方式)，string
        //profileTitle: （顶部联系方式左侧提示内容）, String
        //chatInputPlaceholder: (输入框里面的内容),string
        //profileUpdateTitle:(更新联系方式标题), string
        //profileUpdateDesc:(更新联系方式文字描述), string
        //profileUpdatePlaceholder:(更新联系方式), string
        //profileUpdateCancelBtnText: (取消更新), string
        //profileUpdateConfirmBtnText: (确定更新),string
        //sendBtnText: (发消息),string
        //sendBtnTextColor: ("white"),string
        //sendBtnBgColor: ('red'),string
        //hideLoginSuccess: true  隐藏登录成功的toast
        //pageTitle: （Web容器标题）, string
        //photoFromCamera: (拍摄一张照片),String
        //photoFromAlbum: (从相册选取), String
        //voiceContent:(点击这里录制语音), String
        //voiceCancelContent: (滑到这里取消录音), String
        //voiceReleaseContent: (松开取消录音), String
        Map<String, String> map = new HashMap<>();
        map.put("enableAudio", "1");
        map.put("hideLoginSuccess", "true");
        map.put("pageTitle", title);
        map.put("avatar", avatar);
        map.put("toAvatar", toAvatar);
        FeedbackAPI. setUICustomInfo(map);

        setCustomContact(phoneNumber);
    }

    public void setAppExtInfo(JSONObject extInfo) {
        //可以设置反馈消息自定义参数，方便在反馈后台查看自定义数据，参数是json对象，里面所有的数据都可以由开发者自定义
        FeedbackAPI. setAppExtInfo(extInfo);
    }

    private void setCustomContact(String phoneNumber) {
        //设置自定义联系方式
        //@param customContact  自定义联系方式
        //@param hideContactView 是否隐藏联系人设置界面
        FeedbackAPI.setCustomContact(phoneNumber, false);
    }
}
