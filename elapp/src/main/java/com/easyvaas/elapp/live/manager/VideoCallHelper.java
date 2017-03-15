package com.easyvaas.elapp.live.manager;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.hooview.app.R;
import com.easyvaas.elapp.adapter.oldRecycler.VCallUserAdapter;
import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.bean.user.BaseUserEntity;
import com.easyvaas.elapp.bean.video.VideoCallEntity;
import com.easyvaas.elapp.net.ApiHelper;
import com.easyvaas.elapp.net.MyRequestCallBack;
import com.easyvaas.elapp.utils.Logger;
import com.easyvaas.elapp.utils.SingleToast;
import com.easyvaas.sdk.live.base.interactive.OnInteractiveLiveListener;
import com.easyvaas.sdk.live.wrapper.EVLive;

import java.util.ArrayList;
import java.util.List;

public class VideoCallHelper {
    private static final String TAG = "VideoCallHelper";

    private static VideoCallHelper mInstance;
    private EVLive mEVVCallLive;

    private List<BaseUserEntity> mRequestUsers;
    private VCallUserAdapter mAdapter;
    private View mRequestUserView;

    private String mVid;
    private String mCallId;
    private String mRequester;
    private boolean mIsAnchor;
    private boolean mIsRequested;
    private boolean mIsInVideoCall;

    private VideoCallHelper() {
        mRequestUsers = new ArrayList<>();
    }

    public static VideoCallHelper getInstance() {
        if (mInstance == null) {
            mInstance = new VideoCallHelper();
        }
        return mInstance;
    }

//    public void setRequestUserView(View requestUserView) {
//        mRequestUserView = requestUserView;
//        MyRecyclerView recyclerView = (MyRecyclerView) requestUserView.findViewById(R.id.v_call_user_rv);
//        requestUserView.findViewById(R.id.close_iv).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                toggleRequestView();
//            }
//        });
//
//        GridLayoutManager linearLayoutManager = new GridLayoutManager(requestUserView.getContext(), 3);
//        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        recyclerView.setLayoutManager(linearLayoutManager);
//
//        mAdapter = new VCallUserAdapter(mRequestUsers);
//        mAdapter.setHasStableIds(true);
//        mAdapter.setOnItemClickListener(new CommonRcvAdapter.OnItemClickListener() {
//            private TextView lastNicknameTv;
//
//            @Override
//            public void onItemClick(View view, int position) {
//                TextView nicknameTv = (TextView) view.findViewById(R.id.v_call_user_nickname_tv);
//                if (nicknameTv == null) {
//                    Logger.w(TAG, "Not found request user nickname text view !!!");
//                    return;
//                }
//
//                nicknameTv.setBackgroundResource(R.drawable.btn_v_call_connect);
//                nicknameTv.setTextColor(view.getContext().getResources()
//                        .getColor(R.color.btn_color_three_level));
//
//                if (nicknameTv == lastNicknameTv) {
//                    nicknameTv.setText(R.string.loading_video_connect);
//                    videoCallAccept(mRequestUsers.get(position).getName());
//                    toggleRequestView();
//                } else if (lastNicknameTv != null) {
//                    lastNicknameTv.setText(mRequestUsers.get(position).getNickname());
//                    lastNicknameTv.setBackgroundResource(android.R.color.transparent);
//                    lastNicknameTv.setTextColor(view.getResources().getColor(R.color.text_black));
//                }
//                lastNicknameTv = nicknameTv;
//            }
//        });
//        recyclerView.setAdapter(mAdapter);
//    }

    public void onConnectButtonClick(String ownerName, String vid) {
        mVid = vid;
        if (mIsInVideoCall) {
            SingleToast.show(EVApplication.getApp(), R.string.msg_disconnect_video_call);
            endInteractiveLive();
        } else if (mIsAnchor) {
            toggleRequestView();
        } else if (!TextUtils.isEmpty(ownerName)) {
            if (mIsRequested) {
                SingleToast.show(EVApplication.getApp(), R.string.msg_no_need_request_twice);
            } else {
                videoCallRequest(ownerName);
            }
        }
    }

    private void toggleRequestView() {
        if (mRequestUserView == null) {
            Logger.w(TAG, "VCallUserView is null !");
            return;
        }
        if (mRequestUsers.size() == 0) {
            SingleToast.show(mRequestUserView.getContext(), R.string.msg_no_v_call_request_user);
            return;
        }
        if (!mRequestUserView.isShown()) {
            TextView titleTv = (TextView) mRequestUserView.findViewById(R.id.v_call_user_title_tv);
            titleTv.setText(titleTv.getContext().getString(R.string.title_v_call_users_count,
                    mRequestUsers.size()));
            mAdapter.notifyDataSetChanged();
            mRequestUserView.getRootView().findViewById(R.id.live_options_right_ll)
                    .setVisibility(View.GONE);
            mRequestUserView.setVisibility(View.VISIBLE);
        } else {
            mRequestUserView.setVisibility(View.GONE);
            mRequestUserView.getRootView().findViewById(R.id.live_options_right_ll)
                    .setVisibility(View.VISIBLE);
        }
    }

    /****************** Init *************************************************************************/

    public void initAssistant(Context context, FrameLayout remoteContainer, FrameLayout localContainer,
            final OnVideoCallListener listener) {
        mIsAnchor = false;
        mEVVCallLive = new EVLive(context);
        mEVVCallLive.initInteractiveLiveConfig(context, false);
        mEVVCallLive.setRemoteVideoViewContainer(remoteContainer);
        mEVVCallLive.setLocalVideoViewContainer(localContainer);
        mEVVCallLive.setOnInteractiveLiveListener(new OnInteractiveLiveListener() {
            @Override
            public void onJoinChannelResult(boolean b) {
                Logger.d(TAG, "onJoinChannelResult ... " + b);
                mIsInVideoCall = b;
            }

            @Override
            public void onLeaveChannelSuccess() {
                Logger.d(TAG, "onLeaveChannelSuccess ... ");
                videoCallEnd();
                listener.onLeave();
                mIsInVideoCall = false;
                mIsRequested = false;
            }

            @Override
            public void onFirstRemoteVideoDecoded() {
                Logger.d(TAG, "onFirstRemoteVideoDecoded ... ");
                listener.onJoin();
            }

            @Override
            public void onFirstLocalVideoFrame() {
                Logger.d(TAG, "onFirstLocalVideoFrame ... ");
            }

            @Override
            public void onUserOffline(int userId, int reason) {
                Logger.d(TAG, "onUserOffline ... ");
                listener.onLeave();
                release();
            }

            @Override
            public void onError(int code, String message) {
                Logger.d(TAG, "onError ... " + message);
                //videoCallCancel();
            }
        });
    }

    public void initAnchor(Context context, FrameLayout remoteContainer, EVLive evLive) {
        mIsAnchor = true;
        mEVVCallLive = evLive;
        mEVVCallLive.initInteractiveLiveConfig(context, true);
        mEVVCallLive.setRemoteVideoViewContainer(remoteContainer);
        mEVVCallLive.setOnInteractiveLiveListener(new OnInteractiveLiveListener() {
            @Override
            public void onJoinChannelResult(boolean b) {
                Logger.d(TAG, "onJoinChannelResult ... " + b);
                mIsInVideoCall = b;
            }

            @Override
            public void onLeaveChannelSuccess() {
                Logger.d(TAG, "onLeaveChannelSuccess ... ");
                videoCallEnd();
                mIsInVideoCall = false;
                mRequestUsers.clear();
            }

            @Override
            public void onFirstRemoteVideoDecoded() {
                Logger.d(TAG, "onFirstRemoteVideoDecoded ... ");
            }

            @Override
            public void onFirstLocalVideoFrame() {
                Logger.d(TAG, "onFirstLocalVideoFrame ... ");
            }

            @Override
            public void onUserOffline(int userId, int reason) {
                Logger.d(TAG, "onUserOffline ... ");
                release();
            }

            @Override
            public void onError(int code, String message) {
                Logger.d(TAG, "onError ... " + message);
                //videoCallCancel();
            }
        });
    }

    private void endInteractiveLive() {
        if (mIsInVideoCall) {
            mEVVCallLive.endInteractiveLive();
        }
    }

    /****************** API Request ******************************************************************/

    private void videoCallRequest(String ownerName) {
        mIsRequested = true;
        ApiHelper.getInstance().videoCallRequest(ownerName,
                new MyRequestCallBack<VideoCallEntity>() {
                    @Override
                    public void onSuccess(VideoCallEntity result) {
                        if (!result.isVc_enabled()) {
                            Logger.w(TAG, "Video call disabled !");
                        }
                    }

                    @Override
                    public void onError(String errorInfo) {
                        super.onError(errorInfo);
                        Logger.d(TAG, "Video call request error info: " + errorInfo);
                        mIsRequested = false;
                    }

                    @Override
                    public void onFailure(String msg) {
                        mIsRequested = false;
                    }
                });
    }

    private void videoCallAccept(final String requestName) {
        ApiHelper.getInstance().videoCallAccept(requestName, new MyRequestCallBack<VideoCallEntity>() {
            @Override
            public void onSuccess(VideoCallEntity result) {
                Logger.d(TAG, "Request Call Accept .. callId: " + result.getCallid());
                mCallId = result.getCallid();
                mRequester = requestName;
            }

            @Override
            public void onFailure(String msg) {

            }
        });
    }

    private void videoCallCancel() {
        if (TextUtils.isEmpty(mVid)) {
            Logger.e(TAG, "call cancel ... vid must valued !");
        } else {
            ApiHelper.getInstance().videoCallCancel(mCallId, mVid, null);
            release();
        }
    }

    private void videoCallEnd() {
        if (mIsInVideoCall) {
            ApiHelper.getInstance().videoCallEnd(mCallId, mVid, null);
        }
    }

    /****************** Message Callback *************************************************************/

    public void onCallRequest(String name, String nickname, String logoUrl) {
        Logger.d(TAG, "Call Request..");
        boolean isContained = false;
        for (BaseUserEntity u : mRequestUsers) {
            if (u.getName().equals(name)) {
                isContained = true;
                break;
            }
        }
        if (!isContained && mIsAnchor) {
            BaseUserEntity user = new BaseUserEntity();
            user.setName(name);
            user.setNickname(nickname);
            user.setLogourl(logoUrl);
            mRequestUsers.add(user);
        }
    }

    public void onCallAccept(String callId) {
        Logger.d(TAG, "Call Accept ..");
        mEVVCallLive.startInteractiveLive(callId);
    }

    public void onCallCancel(String name) {
        Logger.d(TAG, "Call Cancel ..");
        if (mIsAnchor) {
            for (BaseUserEntity u : mRequestUsers) {
                if (u.getName().equals(name)) {
                    mRequestUsers.remove(u);
                    break;
                }
            }
            if (mIsInVideoCall && name.equals(mRequester)) {
                endInteractiveLive();
            }
        } else {
            release();
        }
    }

    public void onCallEnd() {
        Logger.d(TAG, "Call End ..");
        release();
    }

    public void release() {
        Logger.d(TAG, "release() ...");
        if (mIsInVideoCall) {
            endInteractiveLive();
        } else if (mIsRequested) {
            mIsRequested = false;
            videoCallCancel();
        } else {
            mIsRequested = false;
            mRequester = "";
            mIsInVideoCall = false;
            mVid = "";
            mCallId = "";
            mRequestUsers.clear();

            if (mEVVCallLive != null) {
                mEVVCallLive.onDestroy();
                mEVVCallLive = null;
            }
        }
    }

    public interface OnVideoCallListener {
        void onJoin();
        void onLeave();
    }
}
