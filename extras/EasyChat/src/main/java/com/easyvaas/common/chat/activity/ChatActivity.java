package com.easyvaas.common.chat.activity;

import java.io.File;
import java.lang.ref.SoftReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.ClipboardManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.EMValueCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMConversation.EMConversationType;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.MessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.VoiceMessageBody;
import com.easemob.util.NetUtils;
import com.easemob.util.PathUtil;
import com.easemob.util.VoiceRecorder;

import com.easyvaas.common.chat.ChatHXSDKHelper;
import com.easyvaas.common.chat.R;
import com.easyvaas.common.chat.VoicePlayClickListener;
import com.easyvaas.common.chat.adapter.ExpressionAdapter;
import com.easyvaas.common.chat.adapter.ExpressionPagerAdapter;
import com.easyvaas.common.chat.adapter.MessageAdapter;
import com.easyvaas.common.chat.applib.controller.HXSDKHelper;
import com.easyvaas.common.chat.applib.model.GroupRemoveListener;
import com.easyvaas.common.chat.base.BasePlayerActivity;
import com.easyvaas.common.chat.db.ChatDB;
import com.easyvaas.common.chat.utils.ChatConstants;
import com.easyvaas.common.chat.utils.ChatLogger;
import com.easyvaas.common.chat.utils.ChatUserUtil;
import com.easyvaas.common.chat.utils.CommonUtils;
import com.easyvaas.common.chat.utils.ImageUtils;
import com.easyvaas.common.chat.utils.SingleToast;
import com.easyvaas.common.chat.view.ExpandGridView;
import com.easyvaas.common.chat.view.PasteEditText;
import com.easyvaas.common.emoji.utils.SmileUtils;

public class ChatActivity extends BasePlayerActivity implements OnClickListener {
    private static final String TAG = "ChatActivity";

    public static final String EXTRA_IM_CHAT_USER_ID = "extra_im_user_id";
    public static final String EXTRA_IM_CHAT_GROUP_ID = "extra_im_chat_group_id";
    public static final String EXTRA_IM_CHAT_GROUP_NAME = "extra_message_group_name";
    public static final String EXTRA_IM_CHAT_TYPE = "extra_im_chat_type";
    public static final String EXTRA_IS_USER_FOLLOWED = "extra_is_user_followed";

    private static final int PAGE_SIZE = 20;

    private static final int REQUEST_CODE_EMPTY_HISTORY = 2;
    private static final int REQUEST_CODE_CAMERA = 18;

    public static final int REQUEST_CODE_CONTEXT_MENU = 3;
    public static final int REQUEST_CODE_TEXT = 5;
    public static final int REQUEST_CODE_VOICE = 6;
    public static final int REQUEST_CODE_PICTURE = 7;
    public static final int REQUEST_CODE_COPY_AND_PASTE = 11;
    private static final int REQUEST_CODE_GROUP_DISSOLUTION = 22;
    public static final int REQUEST_CODE_SHOW_AT_MEMBER = 12;

    public static final int RESULT_CODE_COPY = 1;
    public static final int RESULT_CODE_DELETE = 2;
    public static final int RESULT_CODE_FORWARD = 3;
    public static final int RESULT_CODE_OPEN = 4;

    public static final int CHAT_TYPE_SINGLE = 1;
    public static final int CHAT_TYPE_GROUP = 2;

    public static final String COPY_IMAGE = "EASEMOBIMG";
    public static int resendPos;

    private ListView mListView;
    private PasteEditText mEditTextContent;
    private View mSendBtn;
    private LinearLayout mEmojiIconContainerLl;
    private LinearLayout mButtonsContainerBtn;
    private View mMoreLl;
    private ClipboardManager mClipboard;
    private ImageView mEmoticonsNormalIv;
    private ImageView mEmoticonsCheckedIv;
    private View mSendEditTextRl;

    private InputMethodManager mInputMethodManager;
    private PowerManager.WakeLock mWakeLock;

    private EMConversation mEmConversation;
    private EMGroup mEmGroup;
    private String mGroupMember = "";
    private String mGroupName;
    private JSONArray mJsonArray;
    private ArrayList<String> imUserNickNames = new ArrayList<>();
    private ArrayList<String> userNickNames = new ArrayList<>();

    private MessageAdapter mMessageAdapter;
    private String mToChatUsername;
    private boolean mIsToChatUserFollowed;
    private File mCameraShotFile;

    private List<String> mEmojiResList;
    private int mChatType;
    private boolean mIsLoading;
    private boolean haveMoreData = true;
    private Button mMoreBtn;
    public String playMsgId;
    private static final int REQUEST_CODE_LOCAL = 19;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View mSetModeKeyboardBtn;
    private View mSetModeVoiceBtn;
    private View mPressToSpeakBtn;
    private VoiceRecorder mVoiceRecorder;
    private Handler mMicImageHandler;
    private Drawable[] mRecorderAnimateImages;
    private ImageView mMicImage;
    private View recordingContainer;
    private TextView mRecordingHintTv;

    private boolean isFirstEditTextChanged = true;
    private boolean isSecondEditTextChanged = true;
    private int mEditTextBeforeChangedSize;
    private int mEditTextLength;

    private EMEventListener mEmEventListener = new EMEventListener() {
        @Override
        public void onEvent(EMNotifierEvent event) {
            EMMessage message = (EMMessage) event.getData();
            switch (event.getEvent()) {
                case EventNewMessage:
                    String username = null;
                    if (message.getChatType() == ChatType.GroupChat
                            || message.getChatType() == ChatType.ChatRoom) {
                        username = message.getTo();
                    } else {
                        username = message.getFrom();
                    }

                    if (username.equals(mToChatUsername)) {
                        refreshUIWithNewMessage();
                        HXSDKHelper.getInstance().getNotifier().vibrateAndPlayTone(message);
                    } else {
                        boolean b = ChatDB
                                .getInstance(getApplicationContext())
                                .getBoolean(ChatDB.KEY_NOTICE_PUSH_NEW_CHAT, true);
                        if (b) {
                            HXSDKHelper.getInstance().getNotifier().onNewMsg(message);
                        }
                    }
                    break;
                case EventDeliveryAck:
                    refreshUI();
                    break;
                case EventReadAck:
                    refreshUI();
                    break;
                case EventOfflineMessage:
                    //a list of offline messages
                    refreshUI();
                    break;
                default:
                    break;
            }
        }
    };

    private EMConnectionListener mEmConnectionListener = new EMConnectionListener() {
        @Override
        public void onConnected() {
            ChatLogger.d(TAG, "onConnected()");
        }

        @Override
        public void onDisconnected(final int error) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (error == EMError.USER_REMOVED) {
                        ChatLogger.d(TAG, "onDisconnected(), account removed!");
                    } else if (error == EMError.CONNECTION_CONFLICT) {
                        ChatLogger.d(TAG, "onDisconnected(), account login other device !");
                    } else {
                        if (NetUtils.hasNetwork(getApplicationContext())) {
                            ChatLogger.d(TAG, "onDisconnected(), network ok, can not connect chat server !");
                        } else {
                            ChatLogger
                                    .d(TAG,
                                            "onDisconnected(), network invalid, can not connect chat server !");
                        }
                    }
                }
            });
        }
    };

    private static class MicImageHandler extends Handler {
        SoftReference<ChatActivity> softReference;

        public MicImageHandler(ChatActivity activity) {
            softReference = new SoftReference<ChatActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ChatActivity activity = softReference.get();
            if (activity == null) {
                return;
            }
            int index = msg.what >= activity.mRecorderAnimateImages.length
                    ? activity.mRecorderAnimateImages.length - 1 : msg.what;
            index = index < 0 ? 0 : index;
            activity.mMicImage.setImageDrawable(activity.mRecorderAnimateImages[index]);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mClipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        mWakeLock = ((PowerManager) getSystemService(Context.POWER_SERVICE)).newWakeLock(
                PowerManager.SCREEN_DIM_WAKE_LOCK, "chat_elapp");

        setContentView(R.layout.activity_chat);
        mMicImageHandler = new MicImageHandler(this);

        mChatType = getIntent().getIntExtra(ChatActivity.EXTRA_IM_CHAT_TYPE, CHAT_TYPE_SINGLE);
        if (mChatType == CHAT_TYPE_SINGLE) {
            mToChatUsername = getIntent().getStringExtra(EXTRA_IM_CHAT_USER_ID);
            ChatUserUtil.getUserId(mToChatUsername, this); // To ensure user id is not empty.
            mIsToChatUserFollowed = getIntent().getBooleanExtra(EXTRA_IS_USER_FOLLOWED, true);
        } else if (mChatType == CHAT_TYPE_GROUP) {
            mToChatUsername = getIntent().getStringExtra(EXTRA_IM_CHAT_GROUP_ID);
            mGroupName = getIntent().getStringExtra(EXTRA_IM_CHAT_GROUP_NAME);
            mIsToChatUserFollowed = true;
            loadGroupInfo();
        } else {
            mToChatUsername = getIntent().getStringExtra(EXTRA_IM_CHAT_GROUP_ID);
        }
        setTitle(ChatUserUtil.getNickName(mToChatUsername, this));
        if (TextUtils.isEmpty(mToChatUsername) || TextUtils.isEmpty(ChatHXSDKHelper.getInstance().getHXId())) {
            SingleToast.show(this, R.string.msg_chat_im_user_null);
            finish();
        }

        initView();
        setUpView();

        try {
            onConversationInit();
            onListViewCreation();
        } catch (Exception ex) {
            ChatLogger.w(TAG, "onCoverstation init failed!", ex);
        }
        String forwardMsgId = getIntent().getStringExtra(ForwardMessageActivity.EXTRA_MESSAGE_ID);
        if (!TextUtils.isEmpty(forwardMsgId)) {
            forwardMessage(forwardMsgId);
        }
    }

    public void setModeVoice(View view) {
        hideKeyboard();
        mSendEditTextRl.setVisibility(View.GONE);
        mMoreLl.setVisibility(View.GONE);
        view.setVisibility(View.GONE);
        mSetModeKeyboardBtn.setVisibility(View.VISIBLE);
        mSendBtn.setVisibility(View.GONE);
        mMoreBtn.setVisibility(View.VISIBLE);
        mPressToSpeakBtn.setVisibility(View.VISIBLE);
        mEmoticonsNormalIv.setVisibility(View.VISIBLE);
        mEmoticonsCheckedIv.setVisibility(View.INVISIBLE);
        mButtonsContainerBtn.setVisibility(View.VISIBLE);
        mEmojiIconContainerLl.setVisibility(View.GONE);
    }

    private void initView() {
        recordingContainer = findViewById(R.id.recording_container);
        mListView = (ListView) findViewById(R.id.list);
        mMicImage = (ImageView) findViewById(R.id.mic_image);
        mRecordingHintTv = (TextView) findViewById(R.id.recording_hint);
        mEditTextContent = (PasteEditText) findViewById(R.id.et_sendmessage);
        mSendEditTextRl = findViewById(R.id.edittext_layout);
        mSendBtn = findViewById(R.id.btn_send);
        mSetModeKeyboardBtn = findViewById(R.id.btn_set_mode_keyboard);
        mSetModeVoiceBtn = findViewById(R.id.btn_set_mode_voice);
        mPressToSpeakBtn = findViewById(R.id.btn_press_to_speak);
        ViewPager expressionViewpager = (ViewPager) findViewById(R.id.vPager);
        mEmojiIconContainerLl = (LinearLayout) findViewById(R.id.ll_face_container);
        mButtonsContainerBtn = (LinearLayout) findViewById(R.id.ll_btn_container);
        mEmoticonsNormalIv = (ImageView) findViewById(R.id.iv_emoticons_normal);
        mEmoticonsCheckedIv = (ImageView) findViewById(R.id.iv_emoticons_checked);
        mMoreBtn = (Button) findViewById(R.id.btn_more);
        mEmoticonsNormalIv.setVisibility(View.VISIBLE);
        mEmoticonsCheckedIv.setVisibility(View.INVISIBLE);
        mMoreLl = findViewById(R.id.more);
        mSendEditTextRl.setBackgroundResource(R.drawable.input_bar_bg_normal);
        mRecorderAnimateImages = new Drawable[] {
                getResources().getDrawable(R.drawable.record_animate_01),
                getResources().getDrawable(R.drawable.record_animate_02),
                getResources().getDrawable(R.drawable.record_animate_03),
                getResources().getDrawable(R.drawable.record_animate_04),
                getResources().getDrawable(R.drawable.record_animate_05),
                getResources().getDrawable(R.drawable.record_animate_06),
                getResources().getDrawable(R.drawable.record_animate_07),
                getResources().getDrawable(R.drawable.record_animate_08),
        };

        mEmoticonsNormalIv.setOnClickListener(this);
        mEmoticonsCheckedIv.setOnClickListener(this);

        mPressToSpeakBtn.setOnTouchListener(new PressToSpeakListen());
        mVoiceRecorder = new VoiceRecorder(mMicImageHandler);

        // Emoji list
        mEmojiResList = getExpressionRes(80);
        List<View> views = new ArrayList<View>();
        for (int i = 0, n = mEmojiResList.size() / 20; i < n; i++) {
            views.add(getGridChildView(i + 1));
        }
        expressionViewpager.setAdapter(new ExpressionPagerAdapter(views));
        mSendEditTextRl.requestFocus();
        mEditTextContent.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mSendEditTextRl.setBackgroundResource(R.drawable.input_bar_bg_active);
                } else {
                    mSendEditTextRl.setBackgroundResource(R.drawable.input_bar_bg_normal);
                }

            }
        });
        mEditTextContent.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mSendEditTextRl.setBackgroundResource(R.drawable.input_bar_bg_active);
                mMoreLl.setVisibility(View.GONE);
                mEmoticonsNormalIv.setVisibility(View.VISIBLE);
                mEmoticonsCheckedIv.setVisibility(View.INVISIBLE);
                mEmojiIconContainerLl.setVisibility(View.GONE);
                mButtonsContainerBtn.setVisibility(View.GONE);
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.chat_swipe_layout);
        swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        if (mListView.getFirstVisiblePosition() == 0 && !mIsLoading && haveMoreData) {
                            List<EMMessage> messages;
                            try {
                                if (mChatType == CHAT_TYPE_SINGLE) {
                                    messages = mEmConversation.loadMoreMsgFromDB(
                                            mMessageAdapter.getItem(0).getMsgId(), PAGE_SIZE);
                                } else {
                                    messages = mEmConversation.loadMoreGroupMsgFromDB(
                                            mMessageAdapter.getItem(0).getMsgId(), PAGE_SIZE);
                                }
                            } catch (Exception e1) {
                                swipeRefreshLayout.setRefreshing(false);
                                return;
                            }
                            if (messages.size() > 0) {
                                mMessageAdapter.notifyDataSetChanged();
                                mMessageAdapter.refreshSeekTo(messages.size() - 1);
                                if (messages.size() != PAGE_SIZE) {
                                    haveMoreData = false;
                                }
                            } else {
                                haveMoreData = false;
                            }
                            mIsLoading = false;
                        } else {
                            SingleToast.show(ChatActivity.this,
                                    getResources().getString(R.string.no_more_messages));
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });

        mEditTextContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    if (!isFirstEditTextChanged) {
                        isFirstEditTextChanged = false;
                        if (mEditTextLength - 1 >= s.length()) {
                            isFirstEditTextChanged = true;
                            String finalEdit = "";
                            if (null != userNickNames && !userNickNames.isEmpty()) {
                                userNickNames.remove(userNickNames.get(userNickNames.size() - 1));
                                for (int i = 0; i < userNickNames.size(); i++) {
                                    finalEdit = finalEdit + userNickNames.get(i) + "@";
                                }
                                int index = finalEdit.lastIndexOf("@");
                                if (index != -1) {
                                    finalEdit = finalEdit.substring(0, index);
                                }
                                if (userNickNames.size() == 0) {
                                    mEditTextContent.setText(" ");
                                } else {
                                    mEditTextContent.setText("@" + finalEdit);
                                }
                                mEditTextContent.setSelection(finalEdit.length() + 1);
                            }
                        }
                    }
                    if (isSecondEditTextChanged) {
                        isFirstEditTextChanged = false;
                        isSecondEditTextChanged = false;
                        mEditTextLength = s.length();
                    }
                    String atMemberStr = s.toString();
                    char c = atMemberStr.charAt(atMemberStr.length() - 1);
                    String LastIndexCharToString = String.valueOf(c);
                    if (LastIndexCharToString.equals("@") && mChatType == CHAT_TYPE_GROUP) {
                        Intent removeMemberIntent = new Intent(ChatActivity.this,
                                FriendsSelectorListActivity.class);
                        removeMemberIntent.putExtra(FriendsSelectorListActivity.EXTRA_MESSAGE_SELECT_CONTACT_TYPE,
                                FriendsSelectorListActivity.SELECT_CONTACT_TYPE_ONLY_AT_MEMBER);
                        removeMemberIntent
                                .putExtra(ChatConstants.EXTRA_MESSAGE_VIEW_GROUP_MEMBER, mGroupMember);
                        startActivityForResult(removeMemberIntent, REQUEST_CODE_SHOW_AT_MEMBER);
                    } else {
                        mMoreBtn.setVisibility(View.GONE);
                        mSendBtn.setVisibility(View.VISIBLE);
                    }
                } else {
                    mMoreBtn.setVisibility(View.VISIBLE);
                    mSendBtn.setVisibility(View.GONE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (userNickNames == null) {
                    return;
                }
                if (!userNickNames.isEmpty()) {
                    if (userNickNames.size() < mEditTextBeforeChangedSize) {
                        isFirstEditTextChanged = false;
                        isSecondEditTextChanged = true;
                    }
                } else {
                    isFirstEditTextChanged = true;
                    isSecondEditTextChanged = true;
                }
            }
        });
    }

    private void setUpView() {
        if (mChatType == CHAT_TYPE_SINGLE) {
            ((TextView) findViewById(R.id.name)).setText(ChatUserUtil.getNickName(mToChatUsername, this));
        } else if (mChatType == CHAT_TYPE_GROUP) {
            hideNoUseView();
            onGroupViewCreation();
        }
    }

    private void hideNoUseView() {
        findViewById(R.id.container_to_group).setVisibility(View.GONE);
        findViewById(R.id.container_remove).setVisibility(View.GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // register the event listener when enter the foreground
        EMChatManager.getInstance().registerEventListener(
                mEmEventListener,
                new EMNotifierEvent.Event[] {
                        EMNotifierEvent.Event.EventNewMessage,
                        EMNotifierEvent.Event.EventOfflineMessage,
                        EMNotifierEvent.Event.EventDeliveryAck,
                        EMNotifierEvent.Event.EventReadAck,
                });
        EMChatManager.getInstance().addConnectionListener(mEmConnectionListener);
        ChatHXSDKHelper.getInstance().pushActivity(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mMessageAdapter != null) {
            mMessageAdapter.refresh();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mWakeLock.isHeld())
            mWakeLock.release();
        if (VoicePlayClickListener.isPlaying && VoicePlayClickListener.currentPlayListener != null) {
            VoicePlayClickListener.currentPlayListener.stopPlayVoice();
        }

        try {
            if (mVoiceRecorder.isRecording()) {
                mVoiceRecorder.discardRecording();
                recordingContainer.setVisibility(View.INVISIBLE);
            }
        } catch (Exception e) {
            ChatLogger.w(TAG, "", e);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        String username = intent.getStringExtra(EXTRA_IM_CHAT_USER_ID);
        if (!TextUtils.isEmpty(username) && mToChatUsername.equals(username)) {
            super.onNewIntent(intent);
        } else {
            finish();
            startActivity(intent);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        EMChatManager.getInstance().unregisterEventListener(mEmEventListener);
        EMChatManager.getInstance().removeConnectionListener(mEmConnectionListener);
        ChatHXSDKHelper.getInstance().popActivity(this);
    }

    protected void onConversationInit() {
        if (mChatType == CHAT_TYPE_SINGLE) {
            mEmConversation = EMChatManager
                    .getInstance().getConversationByType(mToChatUsername, EMConversationType.Chat);
        } else if (mChatType == CHAT_TYPE_GROUP) {
            mEmConversation = EMChatManager
                    .getInstance().getConversationByType(mToChatUsername, EMConversationType.GroupChat);
        }

        mEmConversation.markAllMessagesAsRead();

        final List<EMMessage> allMessages = mEmConversation.getAllMessages();
        int msgCount = allMessages != null ? allMessages.size() : 0;
        if (msgCount < mEmConversation.getAllMsgCount() && msgCount < PAGE_SIZE) {
            String msgId = null;
            if (allMessages != null && allMessages.size() > 0) {
                msgId = allMessages.get(0).getMsgId();
            }
            if (mChatType == CHAT_TYPE_SINGLE) {
                mEmConversation.loadMoreMsgFromDB(msgId, PAGE_SIZE);
            } else {
                mEmConversation.loadMoreGroupMsgFromDB(msgId, PAGE_SIZE);
            }
        }
    }

    private void onListViewCreation() {
        mMessageAdapter = new MessageAdapter(ChatActivity.this, mToChatUsername, mChatType);
        mListView.setAdapter(mMessageAdapter);
        mMessageAdapter.refreshSelectLast();
        mListView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                mMoreLl.setVisibility(View.GONE);
                mEmoticonsNormalIv.setVisibility(View.VISIBLE);
                mEmoticonsCheckedIv.setVisibility(View.INVISIBLE);
                mEmojiIconContainerLl.setVisibility(View.GONE);
                mButtonsContainerBtn.setVisibility(View.GONE);
                return false;
            }
        });
    }

    private void onGroupViewCreation() {
        if (!TextUtils.isEmpty(mGroupName)) {
            ((TextView) findViewById(R.id.name)).setText(mGroupName);
        } else {
            if (mEmGroup != null) {
                ((TextView) findViewById(R.id.name)).setText(mEmGroup.getGroupName());
            }
        }
        EMGroupManager.getInstance().addGroupChangeListener(new GroupListener());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CONTEXT_MENU) {
            switch (resultCode) {
                case RESULT_CODE_COPY:
                    EMMessage copyMsg = mMessageAdapter.getItem(data.getIntExtra("position", -1));
                    // mClipboard.setText(SmileUtils.getSmiledText(ChatActivity.this,
                    // ((TextMessageBody) copyMsg.getBody()).getMessage()));
                    mClipboard.setText(((TextMessageBody) copyMsg.getBody()).getMessage());
                    break;
                case RESULT_CODE_DELETE:
                    EMMessage deleteMsg = mMessageAdapter.getItem(data.getIntExtra("position", -1));
                    mEmConversation.removeMessage(deleteMsg.getMsgId());
                    mMessageAdapter
                            .refreshSeekTo(data.getIntExtra("position", mMessageAdapter.getCount()) - 1);
                    break;
                case RESULT_CODE_FORWARD:
                    EMMessage forwardMsg = mMessageAdapter.getItem(data.getIntExtra("position", 0));
                    Intent intent = new Intent(this, ForwardMessageActivity.class);
                    intent.putExtra(ForwardMessageActivity.EXTRA_MESSAGE_ID, forwardMsg.getMsgId());
                    intent.putExtra(ForwardMessageActivity.EXTRA_CURRENT_CHAT_USERNAME, mToChatUsername);
                    startActivity(intent);
                    break;
                case REQUEST_CODE_SHOW_AT_MEMBER:
                    isFirstEditTextChanged = true;
                    isSecondEditTextChanged = true;
                    String userNickName = data.getStringExtra(ChatConstants.EXTRA_KEY_AT_IM_USER_NAME);
                    userNickNames = data.getStringArrayListExtra(ChatConstants.EXTRA_MESSAGE_VIEW_AT_GROUP_MEMBERS);
                    imUserNickNames = data
                            .getStringArrayListExtra(ChatConstants.EXTRA_MESSAGE_VIEW_AT_GROUP_IM_USERS);
                    mEditTextBeforeChangedSize = userNickNames.size();
                    String replaceUserNickName = userNickName.replace(",", "@");
                    String finalNickName = "@" + replaceUserNickName + " ";
                    mEditTextContent.setText(finalNickName);
                    mEditTextContent.setSelection(finalNickName.length());
                default:
                    break;
            }
        }
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_EMPTY_HISTORY) {
                // clear conversation
                EMChatManager.getInstance().clearConversation(mToChatUsername);
                mMessageAdapter.refresh();
            } else if (requestCode == REQUEST_CODE_CAMERA) {
                if (mCameraShotFile != null && mCameraShotFile.exists())
                    sendPicture(mCameraShotFile.getAbsolutePath());
            } else if (requestCode == REQUEST_CODE_TEXT
                    || requestCode == REQUEST_CODE_PICTURE
                    || requestCode == REQUEST_CODE_VOICE) {
                resendMessage();
            } else if (requestCode == REQUEST_CODE_LOCAL) { // send local picture
                if (data != null) {
                    Uri selectedImage = data.getData();
                    if (selectedImage != null) {
                        sendPicByUri(selectedImage);
                    }
                }
            } else if (requestCode == REQUEST_CODE_COPY_AND_PASTE) {
                if (!TextUtils.isEmpty(mClipboard.getText())) {
                    String pasteText = mClipboard.getText().toString();
                    if (pasteText.startsWith(COPY_IMAGE)) {
                        sendPicture(pasteText.replace(COPY_IMAGE, ""));
                    }
                }
            } else if (mEmConversation.getMsgCount() > 0) {
                mMessageAdapter.refresh();
                setResult(RESULT_OK);
            }
        }
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.user_info_btn) {
            if (mChatType == CHAT_TYPE_GROUP) {
                Intent userIntent = new Intent();
                userIntent.setClass(getApplicationContext(), GroupInfoActivity.class);
                userIntent.putExtra(ChatActivity.EXTRA_IM_CHAT_GROUP_ID, mToChatUsername);
                startActivityForResult(userIntent, REQUEST_CODE_GROUP_DISSOLUTION);
            } else {
                ChatUserUtil.showUserInfoByIM(this, mToChatUsername);
            }
        } else if (i == R.id.btn_send) {
            sendText(mEditTextContent.getText().toString());
        } else if (i == R.id.btn_take_picture) {
            selectPicFromCamera();
        } else if (i == R.id.btn_picture) {
            selectPicFromLocal();
        } else if (i == R.id.iv_emoticons_normal) {
            mMoreLl.setVisibility(View.VISIBLE);
            mEmoticonsNormalIv.setVisibility(View.INVISIBLE);
            mEmoticonsCheckedIv.setVisibility(View.VISIBLE);
            mButtonsContainerBtn.setVisibility(View.GONE);
            mEmojiIconContainerLl.setVisibility(View.VISIBLE);
            hideKeyboard();
        } else if (i == R.id.iv_emoticons_checked) {
            mEmoticonsNormalIv.setVisibility(View.VISIBLE);
            mEmoticonsCheckedIv.setVisibility(View.INVISIBLE);
            mButtonsContainerBtn.setVisibility(View.VISIBLE);
            mEmojiIconContainerLl.setVisibility(View.GONE);
            mMoreLl.setVisibility(View.GONE);
        }
    }

    private void refreshUIWithNewMessage() {
        if (mMessageAdapter == null) {
            return;
        }

        runOnUiThread(new Runnable() {
            public void run() {
                mMessageAdapter.refreshSelectLast();
            }
        });
    }

    private void refreshUI() {
        if (mMessageAdapter == null) {
            return;
        }

        runOnUiThread(new Runnable() {
            public void run() {
                mMessageAdapter.refresh();
            }
        });
    }

    private void selectPicFromCamera() {
        if (!CommonUtils.isExitsSdcard()) {
            String st = getResources().getString(R.string.sd_card_does_not_exist);
            SingleToast.show(getApplicationContext(), st);
            return;
        }

        mCameraShotFile = new File(PathUtil.getInstance().getImagePath(),
                ChatHXSDKHelper.getInstance().getHXId() + System.currentTimeMillis() + ".jpg");
        if (mCameraShotFile.getParentFile() != null) {
            mCameraShotFile.getParentFile().mkdirs();
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mCameraShotFile));
            startActivityForResult(intent, REQUEST_CODE_CAMERA);
        }
    }

    private void selectPicFromLocal() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");

        } else {
            intent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        startActivityForResult(intent, REQUEST_CODE_LOCAL);
    }

    private EMMessage assembleMessage(EMMessage.Type type, MessageBody messageBody) {
        EMMessage message = EMMessage.createSendMessage(type);
        if (mChatType == CHAT_TYPE_GROUP) {
            message.setChatType(ChatType.GroupChat);
            message.setAttribute(ChatConstants.MESSAGE_ATTR_AT_MEMBERS,
                    mJsonArray == null ? new JSONArray() : mJsonArray);
        } else {
            message.setChatType(ChatType.Chat);
        }
        message.addBody(messageBody);
        message.setReceipt(mToChatUsername);

        return message;
    }

    public void sendText(String content) {
        //TODO Need to check why mEmConversation is null sometimes
        if (content.length() > 0) {
            if (mEmConversation != null) {
                TextMessageBody txtBody = new TextMessageBody(content);
                mJsonArray = new JSONArray();
                if (imUserNickNames != null) {
                    if (!imUserNickNames.isEmpty()) {
                        for (int i = 0; i < imUserNickNames.size(); i++) {
                            try {
                                mJsonArray.put(i, imUserNickNames.get(i));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        try {
                            mJsonArray.put(imUserNickNames.size(), content);
                            mJsonArray.put(imUserNickNames.size() + 1,
                                    ChatDB.getInstance(this).getString(ChatDB.KEY_USER_NICKNAME));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    imUserNickNames.clear();
                }
                mEmConversation.addMessage(assembleMessage(EMMessage.Type.TXT, txtBody));
                mMessageAdapter.refreshSelectLast();
                mEditTextContent.setText("");
                setResult(RESULT_OK);
            }
        }
    }

    private void sendVoice(String filePath, String fileName, String length, boolean isResend) {
        if (!(new File(filePath).exists())) {
            return;
        }
        VoiceMessageBody body = new VoiceMessageBody(new File(filePath), Integer.parseInt(length));
        mEmConversation.addMessage(assembleMessage(EMMessage.Type.VOICE, body));
        mMessageAdapter.refreshSelectLast();
        setResult(RESULT_OK);
    }

    private void sendPicture(final String filePath) {
        if (!(new File(filePath).exists())) {
            return;
        }
        ImageMessageBody body = new ImageMessageBody(new File(filePath));
        // 默认超过100k的图片会压缩后发给对方，可以设置成发送原图
        // body.setSendOriginalImage(true);
        mEmConversation.addMessage(assembleMessage(EMMessage.Type.IMAGE, body));

        mListView.setAdapter(mMessageAdapter);
        mMessageAdapter.refreshSelectLast();
        setResult(RESULT_OK);
    }

    private void sendPicByUri(Uri selectedImage) {
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        String st8 = getResources().getString(R.string.cant_find_pictures);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            if (TextUtils.isEmpty(picturePath)) {
                SingleToast.show(this, st8);
                return;
            }
            sendPicture(picturePath);
        } else {
            File file = new File(selectedImage.getPath());
            if (!file.exists()) {
                SingleToast.show(this, st8);
                return;

            }
            sendPicture(file.getAbsolutePath());
        }
    }

    private void resendMessage() {
        EMMessage msg = msg = mEmConversation.getMessage(resendPos);
        // msg.setBackSend(true);
        msg.status = EMMessage.Status.CREATE;
        mMessageAdapter.refreshSeekTo(resendPos);
    }

    public void setModeKeyboard(View view) {
        mSendEditTextRl.setVisibility(View.VISIBLE);
        mMoreLl.setVisibility(View.GONE);
        view.setVisibility(View.GONE);
        mSetModeVoiceBtn.setVisibility(View.VISIBLE);
        // mEditTextContent.setVisibility(View.VISIBLE);
        mEditTextContent.requestFocus();
        // mSendBtn.setVisibility(View.VISIBLE);
        mPressToSpeakBtn.setVisibility(View.GONE);
        if (TextUtils.isEmpty(mEditTextContent.getText())) {
            mMoreBtn.setVisibility(View.VISIBLE);
            mSendBtn.setVisibility(View.GONE);
        } else {
            mMoreBtn.setVisibility(View.GONE);
            mSendBtn.setVisibility(View.VISIBLE);
        }
    }

    public void emptyHistory(View view) {
        Intent intent = new Intent(this, AlertDialogActivity.class);
        intent.putExtra("titleIsCancel", true);
        intent.putExtra("msg", getResources().getString(R.string.Whether_to_empty_all_chats));
        intent.putExtra("cancel", true);
        startActivityForResult(intent, REQUEST_CODE_EMPTY_HISTORY);
    }

    public void toggleMore(View view) {
        if (mMoreLl.getVisibility() == View.GONE) {
            ChatLogger.d(TAG, "mMoreLl gone");
            hideKeyboard();
            mMoreLl.setVisibility(View.VISIBLE);
            mButtonsContainerBtn.setVisibility(View.VISIBLE);
            mEmojiIconContainerLl.setVisibility(View.GONE);
        } else {
            if (mEmojiIconContainerLl.getVisibility() == View.VISIBLE) {
                mEmojiIconContainerLl.setVisibility(View.GONE);
                mButtonsContainerBtn.setVisibility(View.VISIBLE);
                mEmoticonsNormalIv.setVisibility(View.VISIBLE);
                mEmoticonsCheckedIv.setVisibility(View.INVISIBLE);
            } else {
                mMoreLl.setVisibility(View.GONE);
            }
        }
    }

    public void editClick(View v) {
        mListView.setSelection(mListView.getCount() - 1);
        if (mMoreLl.getVisibility() == View.VISIBLE) {
            mMoreLl.setVisibility(View.GONE);
            mEmoticonsNormalIv.setVisibility(View.VISIBLE);
            mEmoticonsCheckedIv.setVisibility(View.INVISIBLE);
        }
    }

    private View getGridChildView(int i) {
        View view = View.inflate(this, R.layout.view_expression_gridview, null);
        ExpandGridView gv = (ExpandGridView) view.findViewById(R.id.gridview);
        List<String> list = new ArrayList<String>();
        int endIndex = i * 20 > mEmojiResList.size() ? mEmojiResList.size() : i * 20;
        list.addAll(mEmojiResList.subList((i - 1) * 20, endIndex));
        list.add("icon_del");

        final ExpressionAdapter expressionAdapter = new ExpressionAdapter(this, 1, list);
        gv.setAdapter(expressionAdapter);
        gv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String filename = expressionAdapter.getItem(position);
                try {
                    // 文字输入框可见时，才可输入表情
                    // 按住说话可见，不让输入表情
                    if (mSetModeKeyboardBtn.getVisibility() != View.VISIBLE) {
                        if (!filename.equals("icon_del")) { // 不是删除键，显示表情
                            // 这里用的反射，所以混淆的时候不要混淆SmileUtils这个类
                            Class clz = Class.forName("com.easyvaas.common.emoji.utils.SmileUtils");
                            Field field = clz.getField(filename);
                            mEditTextContent.append(SmileUtils.getSmiledText(ChatActivity.this,
                                    (String) field.get(null)));
                        } else { // 删除文字或者表情
                            if (!TextUtils.isEmpty(mEditTextContent.getText())) {
                                int selectionStart = mEditTextContent.getSelectionStart();// 获取光标的位置
                                if (selectionStart > 0) {
                                    String body = mEditTextContent.getText().toString();
                                    String tempStr = body.substring(0, selectionStart);
                                    int i = tempStr.lastIndexOf("[");// 获取最后一个表情的位置
                                    if (i != -1) {
                                        CharSequence cs = tempStr.substring(i, selectionStart);
                                        if (SmileUtils.containsKey(cs.toString()))
                                            mEditTextContent.getEditableText().delete(i, selectionStart);
                                        else
                                            mEditTextContent.getEditableText().delete(selectionStart - 1,
                                                    selectionStart);
                                    } else {
                                        mEditTextContent.getEditableText()
                                                .delete(selectionStart - 1, selectionStart);
                                    }
                                }
                            }

                        }
                    }
                } catch (Exception e) {
                    ChatLogger.w(TAG, "", e);
                }
            }
        });
        return view;
    }

    private List<String> getExpressionRes(int getSum) {
        List<String> resList = new ArrayList<String>();
        for (int x = 1; x <= getSum; x++) {
            String filename = "emoji_" + x;

            resList.add(filename);

        }
        return resList;

    }

    private void hideKeyboard() {
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null) {
                mInputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    public void back(View view) {
        finish();
    }

    @Override
    public void onBackPressed() {
        if (mMoreLl.getVisibility() == View.VISIBLE) {
            mMoreLl.setVisibility(View.GONE);
            mEmoticonsNormalIv.setVisibility(View.VISIBLE);
            mEmoticonsCheckedIv.setVisibility(View.INVISIBLE);
        } else {
            super.onBackPressed();
        }
    }

    protected void forwardMessage(String forward_msg_id) {
        final EMMessage forward_msg = EMChatManager.getInstance().getMessage(forward_msg_id);
        if (forward_msg != null) {
            EMMessage.Type type = forward_msg.getType();
            switch (type) {
                case TXT:
                    String content = ((TextMessageBody) forward_msg.getBody()).getMessage();
                    sendText(content);
                    break;
                case IMAGE:
                    String filePath = ((ImageMessageBody) forward_msg.getBody()).getLocalUrl();
                    if (filePath != null) {
                        File file = new File(filePath);
                        if (!file.exists()) {
                            filePath = ImageUtils.getThumbnailImagePath(filePath);
                        }
                        sendPicture(filePath);
                    }
                    break;
                default:
                    break;
            }

            if (forward_msg.getChatType() == EMMessage.ChatType.ChatRoom) {
                EMChatManager.getInstance().leaveChatRoom(forward_msg.getTo());
            }
        } else {
            sendText(forward_msg_id);
        }
    }

    public ListView getListView() {
        return mListView;
    }

    private void loadGroupInfo() {
        ChatHXSDKHelper.getInstance().getGroupInfo(mToChatUsername, new EMValueCallBack() {
            @Override
            public void onSuccess(Object o) {
                mEmGroup = ChatHXSDKHelper.getInstance().getLocalGroupInfo(mToChatUsername);
                List<String> members = mEmGroup.getMembers();
                if (members != null) {
                    for (int i = 0, j = members.size(); i < j; i++) {
                        String imUserName = members.get(i);
                        mGroupMember = mGroupMember + imUserName + ",";
                    }
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    private class PressToSpeakListen implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (!CommonUtils.isExitsSdcard()) {
                        String st4 = getResources().getString(R.string.Send_voice_need_sdcard_support);
                        SingleToast.show(ChatActivity.this, st4);
                        return false;
                    }
                    try {
                        v.setPressed(true);
                        mWakeLock.acquire();
                        if (VoicePlayClickListener.isPlaying)
                            VoicePlayClickListener.currentPlayListener.stopPlayVoice();
                        recordingContainer.setVisibility(View.VISIBLE);
                        mRecordingHintTv.setText(getString(R.string.move_up_to_cancel));
                        mRecordingHintTv.setBackgroundColor(Color.TRANSPARENT);
                        mVoiceRecorder.startRecording(null, mToChatUsername, getApplicationContext());
                    } catch (Exception e) {
                        e.printStackTrace();
                        v.setPressed(false);
                        if (mWakeLock.isHeld())
                            mWakeLock.release();
                        if (mVoiceRecorder != null)
                            mVoiceRecorder.discardRecording();
                        recordingContainer.setVisibility(View.INVISIBLE);
                        SingleToast.show(ChatActivity.this, R.string.recoding_fail);
                        return false;
                    }

                    return true;
                case MotionEvent.ACTION_MOVE: {
                    if (event.getY() < 0) {
                        mRecordingHintTv.setText(getString(R.string.release_to_cancel));
                        mRecordingHintTv.setBackgroundResource(R.drawable.recording_text_hint_bg);
                    } else {
                        mRecordingHintTv.setText(getString(R.string.move_up_to_cancel));
                        mRecordingHintTv.setBackgroundColor(Color.TRANSPARENT);
                    }
                    return true;
                }
                case MotionEvent.ACTION_UP:
                    v.setPressed(false);
                    recordingContainer.setVisibility(View.INVISIBLE);
                    if (mWakeLock.isHeld())
                        mWakeLock.release();
                    if (event.getY() < 0) {
                        // discard the recorded audio.
                        mVoiceRecorder.discardRecording();

                    } else {
                        // stopAsync recording and send voice file
                        String st1 = getResources().getString(R.string.Recording_without_permission);
                        String st2 = getResources().getString(R.string.The_recording_time_is_too_short);
                        String st3 = getResources().getString(R.string.send_failure_please);
                        try {
                            int length = mVoiceRecorder.stopRecoding();
                            if (length > 0) {
                                sendVoice(mVoiceRecorder.getVoiceFilePath(),
                                        mVoiceRecorder.getVoiceFileName(mToChatUsername),
                                        Integer.toString(length), false);
                            } else if (length == EMError.INVALID_FILE) {
                                SingleToast.show(getApplicationContext(), st1);
                            } else {
                                SingleToast.show(getApplicationContext(), st2);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            SingleToast.show(ChatActivity.this, st3);
                        }

                    }
                    return true;
                default:
                    recordingContainer.setVisibility(View.INVISIBLE);
                    if (mVoiceRecorder != null)
                        mVoiceRecorder.discardRecording();
                    return false;
            }
        }
    }

    private class GroupListener extends GroupRemoveListener {
        @Override
        public void onUserRemoved(final String groupId, String groupName) {
            runOnUiThread(new Runnable() {
                String st13 = getResources().getString(R.string.you_are_group);

                public void run() {
                    if (mToChatUsername.equals(groupId)) {
                        SingleToast.show(ChatActivity.this, st13);
                        /*if (GroupDetailsActivity.instance != null)
                            GroupDetailsActivity.instance.finish();*/
                        finish();
                    }
                }
            });
        }

        @Override
        public void onGroupDestroy(final String groupId, String groupName) {
            // 群组解散正好在此页面，提示群组被解散，并finish此页面
            runOnUiThread(new Runnable() {
                public void run() {
                    if (mToChatUsername.equals(groupId)) {
                        SingleToast.show(ChatActivity.this, R.string.the_current_group);
                        /*if (GroupDetailsActivity.instance != null)
                            GroupDetailsActivity.instance.finish();*/
                        finish();
                    }
                }
            });
        }
    }
}
