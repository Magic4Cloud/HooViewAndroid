package com.easyvaas.elapp.chat;


import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.chat.model.ChatHXSDKModel;
import com.easyvaas.elapp.chat.model.HxSdkModel;
import com.easyvaas.elapp.utils.Logger;
import com.hyphenate.EMCallBack;


public class LiveHxHelper extends AbstractHxHelper {
    private static final String TAG = "LiveHxHelper";
    private static LiveHxHelper sLiveHxHelper;

    private LiveHxHelper() {

    }

    public static synchronized LiveHxHelper getInstance() {
        if (sLiveHxHelper == null) {
            sLiveHxHelper = new LiveHxHelper();
        }
        return sLiveHxHelper;
    }

    public void login(String hxID, String pwd) {
        Logger.d(TAG, "login: hxID=" + hxID + "  pwd=" + pwd);
        super.login(hxID, pwd, new EMCallBack() {
            @Override
            public void onSuccess() {
                Logger.i(TAG, "onSuccess: im login onSuccess");

            }

            @Override
            public void onError(int i, String s) {
                Logger.e(TAG, "onError: code=" + i + "   msg=" + s);

            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
    }

    public void logout() {
        super.logout(true, new EMCallBack() {
            @Override
            public void onSuccess() {
                Logger.i(TAG, "onSuccess: im logout onSuccess");
            }

            @Override
            public void onError(int i, String s) {
                Logger.e(TAG, "onSuccess: im logout onError");

            }

            @Override
            public void onProgress(int i, String s) {
                Logger.i(TAG, "onSuccess: im logout onProgress");

            }
        });
    }


    @Override
    protected HxSdkModel createModel() {
        return new ChatHXSDKModel(EVApplication.getApp());
    }
}
