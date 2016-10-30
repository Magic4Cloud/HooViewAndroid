package com.easyvaas.common.chat.net;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.text.TextUtils;

import com.easyvaas.common.chat.ChatHXSDKHelper;
import com.easyvaas.common.chat.bean.BaseUser;
import com.easyvaas.common.chat.bean.FriendsArray;
import com.easyvaas.common.chat.bean.UserArray;
import com.easyvaas.common.chat.db.ChatDB;
import com.easyvaas.common.chat.utils.ChatLogger;

public class AppChatHelper {
    private static final String TAG = "AppStatHelper";

    private static AppChatHelper mInstance;
    private Context mContext;
    private RequestHelper mRequestHelper;
    private ChatDB mPref;

    private AppChatHelper(Context context) {
        mRequestHelper = RequestHelper.getInstance(context);
        mContext = context;
        mPref = ChatDB.getInstance(context);
    }

    public static AppChatHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new AppChatHelper(context);
        }
        return mInstance;
    }

    public void cancelRequest() {
        mRequestHelper.cancelRequest();
    }

    /**
     * Follow each other
     */
    public void getUserFriendList(int startIndex, int count, MyRequestCallBack<FriendsArray> callBack) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("sessionid", mPref.getSessionId(mContext));
        param.put("start", startIndex + "");
        param.put("count", count + "");
        mRequestHelper.getAsGson(ApiConstant.USER_FRIEND_LIST, param, FriendsArray.class, callBack);
    }

    public void getUserInfos(List<String> names, MyRequestCallBack<UserArray> callBack) {
        if (names == null || names.size() == 0) {
            ChatLogger.w(TAG, "Not find valid name !");
            return;
        }
        String namelist = "";
        for (int i = 0, n = names.size(); i < n; i++) {
            if (i != n - 1) {
                namelist = namelist + names.get(i) + ",";
            } else {
                namelist = namelist + names.get(i);
            }
        }
        Map<String, String> map = new HashMap<>();
        map.put("sessionid", mPref.getSessionId(mContext));
        map.put("namelist", namelist);
        mRequestHelper.getAsGson(ApiConstant.USER_BASE_INFO, map, UserArray.class, callBack);
    }

    // ImUserID is same as UserId
    public void getUserInfo(String name, final MyRequestCallBack<BaseUser> callBack) {
        List<String> names = new ArrayList<>();
        names.add(name);
        getUserInfos(names, new MyRequestCallBack<UserArray>() {
            @Override
            public void onSuccess(UserArray result) {
                if (result != null && result.getUsers() != null && result.getUsers().size() > 0) {
                    callBack.onSuccess(result.getUsers().get(0));
                }
            }

            @Override
            public void onError(String errorInfo) {
                super.onError(errorInfo);
                callBack.onError(errorInfo);
            }

            @Override
            public void onFailure(String msg) {
                callBack.onFailure(msg);
            }
        });
    }
}
