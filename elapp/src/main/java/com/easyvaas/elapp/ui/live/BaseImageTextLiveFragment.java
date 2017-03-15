package com.easyvaas.elapp.ui.live;


import com.easyvaas.elapp.chat.model.EMMessageWrapper;
import com.easyvaas.elapp.net.HooviewApiHelper;
import com.easyvaas.elapp.net.MyRequestCallBack;
import com.easyvaas.elapp.ui.base.BaseFragment;

/**
 * 图文直播和聊天
 */
public abstract class BaseImageTextLiveFragment extends BaseFragment {
    public static final String EXTRA_CHAT_ROOM_ID = "roomId";
    public static final String EXTRA_IS_ANCHOR = "isAnchor";
    public static final String EXTRA_WATCH_COUNT = "watchcount";
    public static final String EXTRA_STREEM = "extra_streem";

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    protected void uploadChatMsg(EMMessageWrapper emMessageWrapper) {
        HooviewApiHelper.getInstance().uploadChatMessage(emMessageWrapper, new MyRequestCallBack() {
            @Override
            public void onSuccess(Object result) {

            }

            @Override
            public void onFailure(String msg) {

            }
        });
    }
}
