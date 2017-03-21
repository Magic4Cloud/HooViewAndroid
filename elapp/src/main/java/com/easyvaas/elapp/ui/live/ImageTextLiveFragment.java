package com.easyvaas.elapp.ui.live;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easyvaas.common.bottomsheet.BottomSheet;
import com.easyvaas.common.gift.bean.GiftEntity;
import com.easyvaas.common.gift.view.GiftPagerView;
import com.easyvaas.elapp.adapter.ImageTextLiveMsgAdapter;
import com.easyvaas.elapp.adapter.ImageTextLiveMsgListAdapter;
import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.bean.imageTextLive.ImageTextLiveHistoryModel;
import com.easyvaas.elapp.bean.imageTextLive.ImageTextLiveHistoryModel.MsgsBean;
import com.easyvaas.elapp.bean.user.User;
import com.easyvaas.elapp.bean.video.TextLiveListModel;
import com.easyvaas.elapp.chat.model.EMMessageWrapper;
import com.easyvaas.elapp.db.Preferences;
import com.easyvaas.elapp.dialog.CommonPromptDialog;
import com.easyvaas.elapp.event.AppBarLayoutOffsetChangeEvent;
import com.easyvaas.elapp.event.HideGiftViewEvent;
import com.easyvaas.elapp.event.ImageTextLiveMessageEvent;
import com.easyvaas.elapp.event.JoinRoomSuccessEvent;
import com.easyvaas.elapp.net.ApiHelper;
import com.easyvaas.elapp.net.HooviewApiHelper;
import com.easyvaas.elapp.net.MyRequestCallBack;
import com.easyvaas.elapp.ui.pay.CashInActivity;
import com.easyvaas.elapp.utils.DateTimeUtil;
import com.easyvaas.elapp.utils.Logger;
import com.easyvaas.elapp.utils.SingleToast;
import com.easyvaas.elapp.utils.Utils;
import com.easyvaas.elapp.utils.ViewUtil;
import com.easyvaas.elapp.view.AutoLoadRecyclerView;
import com.easyvaas.elapp.view.ImageTextLiveInputView;
import com.easyvaas.elapp.view.LoadMoreListener;
import com.easyvaas.elapp.view.gift.GiftViewContainer;
import com.hooview.app.R;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


public class ImageTextLiveFragment extends BaseImageTextLiveFragment implements View.OnClickListener,LoadMoreListener {
    private static final String TAG = "ImageTextLiveFragment";
    private static final int REQUEST_CODE_IMAGE = 0;
    private static final int REQUEST_CODE_CAMERA = 1;
    private static final int REQUEST_CODE_RESULT = 2;
    private static final String IMAGE_FILE_NAME = "faceImage.jpg";
    private static final int HEAD_PORTRAIT_WIDTH = 320;
    private static final int HEAD_PORTRAIT_HEIGHT = 320;
    protected int start = 0;
    protected int count = 20;
    protected int next;
    private ImageTextLiveInputView mImageTextLiveInputView;
    private ImageTextLiveMsgAdapter mMsgAdapter;
    private ImageTextLiveMsgListAdapter mImageTextLiveMsgListAdapter; // 新adapter
    private TextView mTvWatchCount;
    private AutoLoadRecyclerView mAutoLoadRecyclerView;
    private String mRoomId;
    private EMConversation mEMConversation;
    protected int pagesize = 20;
    private User mUser;
    private LinkedList<EMMessageWrapper> mEMMessageList;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private BottomSheet mSetThumbPanel;
    private GiftViewContainer mGiftViewContainer;
    private boolean isAnchor;
    private RelativeLayout mRlOpertation;
    private File mTempLogoPic;
    private int mWatchCount;
    private TextLiveListModel.StreamsEntity mStreamsEntity;
    private Preferences mPref;
    protected GiftPagerView mExpressionGiftLayout;
    private FrameLayout mFlGiftContainer;
    private LinearLayout mLlEmpty;
    private LinkedList<MsgsBean> mDatas;
    private String ownerId;
    private TextView liveEmptyTv;

    public static ImageTextLiveFragment newInstance(String roomId, boolean isAnchor, int watcherCount) {
        Bundle args = new Bundle();
        args.putString(EXTRA_CHAT_ROOM_ID, roomId);
        args.putBoolean(EXTRA_IS_ANCHOR, isAnchor);
        args.putInt(EXTRA_WATCH_COUNT, watcherCount);
        args.putString(EXTRA_OWENERID,EVApplication.getUser().getName());
        ImageTextLiveFragment fragment = new ImageTextLiveFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static ImageTextLiveFragment newInstance(TextLiveListModel.StreamsEntity streamsEntity) {
        if (streamsEntity == null) {
            return null;
        }
        Bundle args = new Bundle();
        args.putString(EXTRA_CHAT_ROOM_ID, streamsEntity.getId());
        args.putBoolean(EXTRA_IS_ANCHOR, false);
        args.putInt(EXTRA_WATCH_COUNT, streamsEntity.getViewcount());
        args.putSerializable(EXTRA_STREEM, streamsEntity);
        args.putString(EXTRA_OWENERID,streamsEntity.getOwnerid());
        ImageTextLiveFragment fragment = new ImageTextLiveFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRoomId = getArguments().getString(EXTRA_CHAT_ROOM_ID);
        isAnchor = getArguments().getBoolean(EXTRA_IS_ANCHOR);
        mWatchCount = getArguments().getInt(EXTRA_WATCH_COUNT, 0);
        ownerId = getArguments().getString(EXTRA_OWENERID);
        mStreamsEntity = (TextLiveListModel.StreamsEntity) getArguments().getSerializable(EXTRA_STREEM);
        mUser = EVApplication.getUser();
        mSetThumbPanel = Utils.getSetThumbBottomPanel(getActivity(), IMAGE_FILE_NAME,
                REQUEST_CODE_CAMERA, REQUEST_CODE_IMAGE);
        mPref = Preferences.getInstance(getContext());
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_text_live, container, false);
        assignViews(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void assignViews(View view) {
        mLlEmpty = (LinearLayout) view.findViewById(R.id.ll_empty);
        mRlOpertation = (RelativeLayout) view.findViewById(R.id.rl_operation);
        mGiftViewContainer = (GiftViewContainer) view.findViewById(R.id.GiftViewContainer);
        mImageTextLiveInputView = (ImageTextLiveInputView) view.findViewById(R.id.imageTextLiveInputView);
        mAutoLoadRecyclerView = (AutoLoadRecyclerView) view.findViewById(R.id.rcv_msg);
        liveEmptyTv = (TextView) view.findViewById(R.id.live_empty_tv);
        mEMMessageList = new LinkedList<>();
//        mMsgAdapter = new ImageTextLiveMsgAdapter(mEMMessageList);
        mDatas = new LinkedList<>();
        mImageTextLiveMsgListAdapter = new ImageTextLiveMsgListAdapter(mDatas);
        mAutoLoadRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAutoLoadRecyclerView.setAdapter(mImageTextLiveMsgListAdapter);
        mAutoLoadRecyclerView.setLoadMoreListener(this);
        if (isAnchor) {
            view.findViewById(R.id.ll_option).setVisibility(View.GONE);
            liveEmptyTv.setText(getString(R.string.image_text_live_has_not_started_my));
        } else {
            RelativeLayout.LayoutParams layoutParams1 = (RelativeLayout.LayoutParams) mRlOpertation.getLayoutParams();
            layoutParams1.bottomMargin = (int) ViewUtil.dp2Px(getContext(), 156);
            view.findViewById(R.id.ll_option).setVisibility(View.VISIBLE);
            view.findViewById(R.id.iv_shot).setOnClickListener(this);
            view.findViewById(R.id.iv_gift).setOnClickListener(this);
            view.findViewById(R.id.iv_chat).setOnClickListener(this);
            mImageTextLiveInputView.setVisibility(View.GONE);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mAutoLoadRecyclerView.getLayoutParams();
            layoutParams.bottomMargin = 0;
        }
        mImageTextLiveInputView.setInputViewListener(new ImageTextLiveInputView.InputViewListener() {
            @Override
            public void sendMessage(String msgType, String content) {
                sendMsg(content, msgType);
            }

            @Override
            public void onImageButtonClick() {
                mSetThumbPanel.show();
            }

            @Override
            public void onReply(String content, ImageTextLiveInputView.ReplyModel replyModel) {

            }
        });
        TextView tvTime = (TextView) view.findViewById(R.id.tv_time);
        tvTime.setText(DateTimeUtil.formatDate(getContext(), System.currentTimeMillis(), getString(R.string.image_date_pattern)));
        mTvWatchCount = (TextView) view.findViewById(R.id.tv_watch_count);
        DecimalFormat df2 = new DecimalFormat("###,###");
        mTvWatchCount.setText(getString(R.string.image_live_room_fans, df2.format(mWatchCount)));
        mExpressionGiftLayout = (GiftPagerView) view.findViewById(R.id.expression_gift_layout);
        mExpressionGiftLayout.setOnViewClickListener(mOnGiftSendCallBack);
        if (EVApplication.getUser() != null) {
            mExpressionGiftLayout
                    .setSelf(EVApplication.getUser().getNickname(), EVApplication.getUser().getLogourl());
        }
        mFlGiftContainer = (FrameLayout) view.findViewById(R.id.fl_gift_container);
        mFlGiftContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mFlGiftContainer.setVisibility(View.GONE);
                return true;
            }
        });
        onMessageListInit(false);
    }

    private void sendMsg(String content, String msgType) {
        EMMessage message = EMMessage.createTxtSendMessage(content, mRoomId);
        message.setChatType(EMMessage.ChatType.ChatRoom);
        message.setAttribute("tp", msgType);
        message.setAttribute("nk", mUser.getNickname());
        message.setAttribute("rnk", "");
        message.setAttribute("rct", "");
        EMClient.getInstance().chatManager().sendMessage(message);
        List<EMMessage> list = new ArrayList<>();
        list.add(message);
        EventBus.getDefault().post(new ImageTextLiveMessageEvent(list));
        EMMessageWrapper messageWrapper = new EMMessageWrapper(message);
        uploadChatMsg(messageWrapper);
    }

    /**
     * 从服务器拉取直播数据
     */
    private void onMessageListInit(final boolean isLoadMore) {
        HooviewApiHelper.getInstance().getImageTextLiveHistory(mRoomId,start,"30", System.currentTimeMillis()/1000, new MyRequestCallBack<ImageTextLiveHistoryModel>() {
            @Override
            public void onSuccess(ImageTextLiveHistoryModel result) {

                if (result != null && result.getMsgs().size() >0) {

                    LinkedList<MsgsBean> tempDatas = new LinkedList<MsgsBean>();
                    tempDatas.addAll(result.getMsgs());
                    Iterator<MsgsBean> it = tempDatas.iterator();
                    while(it.hasNext()){
                        MsgsBean msg = it.next();
                        if (!msg.getFrom().equals(ownerId))  //去掉不是主播的消息
                        {
                            it.remove();
                        }
                    }
                    if (isLoadMore) {
                        mDatas.addAll(tempDatas);
                        start = result.getNext();
                    } else
                    {
                        mDatas.clear();
                        mDatas.addAll(tempDatas);
                    }


                    mLlEmpty.setVisibility(View.GONE);
                    mImageTextLiveMsgListAdapter.notifyDataSetChanged();
                }else
                {
                    if (mDatas.size() <= 0)
                    mLlEmpty.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(String msg) {
                Log.d("Misuzu","fail ---"+msg);
            }
        });
    }


    private void initConversation() {
        mEMConversation = EMClient.getInstance().chatManager().getConversation(mRoomId, EMConversation.EMConversationType.ChatRoom, true);
        if (mEMConversation == null) {
            return;
        }
        mEMConversation.markAllMessagesAsRead();
        final List<EMMessage> msgs = mEMConversation.getAllMessages();
        int msgCount = msgs != null ? msgs.size() : 0;
        if (msgCount < mEMConversation.getAllMsgCount() && msgCount < pagesize) {
            String msgId = null;
            if (msgs != null && msgs.size() > 0) {
                msgId = msgs.get(0).getMsgId();
            }
            mEMConversation.loadMoreMsgFromDB(msgId, pagesize - msgCount);
        }
    }

    private void handleMsg(EMMessage message) {
        EMMessageWrapper emMessageWrapper = new EMMessageWrapper(message);
        if (mEMMessageList.size() != 0 && mEMMessageList.get(0).isStickMessage) {
            if (emMessageWrapper.isStickMessage) {
                mEMMessageList.removeFirst();
                mEMMessageList.addFirst(emMessageWrapper);
            } else {
                mEMMessageList.add(1, emMessageWrapper);
            }
        } else {
            mEMMessageList.addFirst(emMessageWrapper);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ImageTextLiveMessageEvent event) {
        Logger.d(TAG, "onMessageEvent: " + event.type);
        if (event.type == ImageTextLiveMessageEvent.MSG_TYPE_CMD) {
            for (int i = 0; i < event.mEMMessageList.size(); i++) {
                handleCmdMsg(event.mEMMessageList.get(i));
            }
        } else if (event.type == ImageTextLiveMessageEvent.MSG_TYPE_MESSAGE) {
            EMMessage message;
            for (int i = 0; i < event.mEMMessageList.size(); i++) {
                message = event.mEMMessageList.get(i);
                Logger.d(TAG, "onMessageReceived:  message=" + message.toString() + "  getTo()  " + message.getTo());
                if (message.getFrom().equals(ownerId)) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            onMessageListInit(false); // 跳出循环 如果是主播的消息 更新界面
                        }
                    },1000);
                    break;
                }
            }
//            }
//            Logger.d(TAG, "onMessageReceived: mEMMessageList " + mEMMessageList.size());
//            mHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    if (mEMMessageList.size() == 0) {
//                        mLlEmpty.setVisibility(View.VISIBLE);
//                    } else {
//                        mLlEmpty.setVisibility(View.GONE);
//                    }
//                    mMsgAdapter.notifyDataSetChanged();
//                }
//            });
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(JoinRoomSuccessEvent event) {
        initConversation();
        onMessageListInit(false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(AppBarLayoutOffsetChangeEvent event) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mRlOpertation.getLayoutParams();
        layoutParams.bottomMargin = (int) ViewUtil.dp2Px(getContext(), 156) + event.offset;
        mRlOpertation.setLayoutParams(layoutParams);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(HideGiftViewEvent e) {
        hideGiftToolsBar();
    }


    private void handleCmdMsg(EMMessage message) {
        GiftEntity giftEntity = new GiftEntity();
        giftEntity.setNickname(message.getStringAttribute("nk", ""));
        giftEntity.setGiftName(message.getStringAttribute("gnm", ""));
        giftEntity.setGiftCount(message.getIntAttribute("gct", 0));
        mGiftViewContainer.addAndPlayGift(giftEntity);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (mSetThumbPanel != null && mSetThumbPanel.isShowing()) {
            mSetThumbPanel.dismiss();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Logger.d(TAG, "onActivityResult: onActivityResult");
        if (resultCode != Activity.RESULT_CANCELED) {
            switch (requestCode) {
                case REQUEST_CODE_IMAGE:
                    Logger.d(TAG, "onActivityResult: REQUEST_CODE_IMAGE");
                    sendPicture(Utils.getRealFilePath(getContext(), data.getData()));
                    break;
                case REQUEST_CODE_CAMERA:
                    Logger.d(TAG, "onActivityResult: REQUEST_CODE_CAMERA");
                    if (android.os.Environment.getExternalStorageState()
                            .equals(android.os.Environment.MEDIA_MOUNTED)) {
                        File tempFile = new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME);
                        sendPicture(tempFile.getAbsolutePath());
                    } else {
                        SingleToast.show(getContext(), getActivity().getResources().getString(R.string.msg_alert_no_sd_card));
                    }
                    break;
            }
        }
    }

    private void sendPicture(final String filePath) {
        if (!(new File(filePath).exists())) {
            return;
        }
        EMMessage message = EMMessage.createImageSendMessage(filePath, false, mRoomId);
        message.setChatType(EMMessage.ChatType.GroupChat);
        EMClient.getInstance().chatManager().sendMessage(message);
        List<EMMessage> list = new ArrayList<>();
        list.add(message);
        EventBus.getDefault().post(new ImageTextLiveMessageEvent(list));
        EMMessageWrapper messageWrapper = new EMMessageWrapper(message);
        uploadChatMsg(messageWrapper);
    }


    private void sendGiftMsg(String nk, String gnm, int count) {
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.CMD);
        message.setAttribute("nk", nk);
        message.setAttribute("gnm", gnm);
        message.setAttribute("gct", count);
        EMClient.getInstance().chatManager().sendMessage(message);
        List<EMMessage> list = new ArrayList<>();
        list.add(message);
        ImageTextLiveMessageEvent imageTextLiveMessageEvent = new ImageTextLiveMessageEvent(list);
        imageTextLiveMessageEvent.type = ImageTextLiveMessageEvent.MSG_TYPE_CMD;
        EventBus.getDefault().post(imageTextLiveMessageEvent);
    }

    private GiftPagerView.OnGiftSendCallBack mOnGiftSendCallBack = new GiftPagerView.OnGiftSendCallBack() {
        @Override
        public void sendGift(final GiftEntity data) {
            if (mStreamsEntity == null) return;
            sendGiftMsg(mUser.getNickname(), data.getGiftName(), data.getGiftCount());
            ApiHelper.getInstance().sendGiftString(mRoomId, data.getGiftId(), data.getGiftCount(),
                    false, mStreamsEntity.getUserEntity().getName(), new MyRequestCallBack<String>() {
                        @Override
                        public void onSuccess(String result) {

                        }

                        @Override
                        public void onError(String errorInfo) {
                            super.onError(errorInfo);
                            mExpressionGiftLayout.updateAssetInfo(data.getGiftCost());
                        }

                        @Override
                        public void onFailure(String msg) {
                            mExpressionGiftLayout.updateAssetInfo(data.getGiftCost());
                        }
                    });
        }

        @Override
        public void sendBurstGift(final GiftEntity entity) {
        }

        @Override
        public void onBurstCountChanged(int burstCount, GiftEntity entity) {
        }

        @Override
        public void onUpdateView() {
            hideGiftToolsBar();
        }

        @Override
        public void jumpCashInActivity() {
            CashInActivity.start(getActivity());
        }

        @Override
        public void onECoinChanged(long eCoin) {
//            mPref.putLong(Preferences.KEY_PARAM_ASSET_E_COIN_ACCOUNT, eCoin);
        }

        @Override
        public void onECoinNoEnough() {
            showCashInPrompt();
        }
    };
    private CommonPromptDialog mCommonPromptDialog;

    public void showCashInPrompt() {
        if (mCommonPromptDialog == null) {
            mCommonPromptDialog = new CommonPromptDialog(getActivity());
            mCommonPromptDialog.setPrompt(getString(R.string.cash_prompt));
            mCommonPromptDialog.setButtonText(getString(R.string.cancel), getString(R.string.go_cash_in));
            mCommonPromptDialog.setOnButtonClickListen(new CommonPromptDialog.OnButtonClickListener() {
                @Override
                public void onLeftButtonClick(View view) {
                    mCommonPromptDialog.dismiss();
                }

                @Override
                public void onRightButtonClick(View view) {
                    mCommonPromptDialog.dismiss();
                    Intent rechargeIntent = new Intent(getActivity(), CashInActivity.class);
                    startActivity(rechargeIntent);
                }
            });
        }
        mCommonPromptDialog.show();
    }
    protected void hideGiftToolsBar() {
        mFlGiftContainer.setVisibility(View.GONE);
        mExpressionGiftLayout.setVisibility(View.INVISIBLE);
        if (getActivity() instanceof ImageTextLiveActivity) {
            ((ImageTextLiveActivity) getActivity()).setGiftShown(false);
        }
    }

    protected void showGiftToolsBar() {
        mFlGiftContainer.setVisibility(View.VISIBLE);
        mExpressionGiftLayout.setVisibility(View.VISIBLE);
        mExpressionGiftLayout.updateAssetInfo();
        if (getActivity() instanceof ImageTextLiveActivity) {
            ((ImageTextLiveActivity) getActivity()).setGiftShown(true);
        }
    }

    @Override
    public void onClick(View v) {
        BaseImageTextLiveActivity activity = (BaseImageTextLiveActivity) getActivity();
        switch (v.getId()) {
            case R.id.iv_gift:
                showGiftToolsBar();
                break;
            case R.id.iv_chat:
                activity.goToChat();
                break;
            case R.id.iv_shot:
                activity.share();
                break;
        }
    }

    @Override
    public void loadMore() {
        onMessageListInit(true);
    }
}
