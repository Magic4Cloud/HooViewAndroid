package com.easyvaas.elapp.chat;


import android.content.Context;

import com.hooview.app.BuildConfig;
import com.easyvaas.elapp.chat.model.HxSdkModel;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;

public abstract class AbstractHxHelper {
    protected HxSdkModel hxModel;
    protected String hxId;
    protected String password;

    public AbstractHxHelper() {
        hxModel = createModel();
    }

    public void initHxOptions(Context context) {
        EMOptions options = new EMOptions();
        options.setAcceptInvitationAlways(false);
        options.setAutoLogin(true);
        if (BuildConfig.DEBUG) {
            options.setAppKey("1150160929178497#hooviewtest");
        } else {
            options.setAppKey("1150160929178497#hooview");
        }
        EMClient.getInstance().init(context, options);
        EMClient.getInstance().addConnectionListener(new MyConnectionListener());
        EMClient.getInstance().setDebugMode(BuildConfig.DEBUG);
    }

    public void login(String hxId, String hxPwd, EMCallBack callBack) {
        EMClient.getInstance().login(hxId, hxPwd, callBack);
    }

    public void logout(boolean unbindToken, EMCallBack callBack) {
        EMClient.getInstance().logout(unbindToken, callBack);
    }

    public boolean isLogined() {
        return EMClient.getInstance().isLoggedInBefore();
    }

    public HxSdkModel getModel() {
        return hxModel;
    }

    public String getHXId() {
        if (hxId == null) {
            hxId = hxModel.getHXId();
        }
        return hxId;
    }

    public String getPassword() {
        if (password == null) {
            password = hxModel.getPwd();
        }
        return password;
    }

    public void setHXId(String hxId) {
        if (hxId != null) {
            if (hxModel.saveHXId(hxId)) {
                this.hxId = hxId;
            }
        }
    }

    public void setPassword(String password) {
        if (hxModel.savePassword(password)) {
            this.password = password;
        }
    }

    abstract protected HxSdkModel createModel();

    public void joinChatRoom() {

    }

    public void leaveChatRoom() {

    }


    private class MyConnectionListener implements EMConnectionListener {
        @Override
        public void onConnected() {

        }

        @Override
        public void onDisconnected(final int error) {
//            runOnUiThread(new Runnable() {
//
//                @Override
//                public void run() {
//                    if(error == EMError.USER_REMOVED){
//                        // 显示帐号已经被移除
//                    }else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
//                        // 显示帐号在其他设备登录
//                    } else {
//                        if (NetUtils.hasNetwork(MainActivity.this))
//                        //连接不到聊天服务器
//                        else
//                        //当前网络不可用，请检查网络设置
//                    }
//                }
//            });
        }
    }

}
