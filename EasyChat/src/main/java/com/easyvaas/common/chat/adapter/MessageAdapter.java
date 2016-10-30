package com.easyvaas.common.chat.adapter;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.easemob.EMCallBack;
import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.EMMessage.Direct;
import com.easemob.chat.FileMessageBody;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.VoiceMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.DateUtils;
import com.easemob.util.PathUtil;
import com.easyvaas.common.chat.utils.ChatConstants;
import com.easyvaas.common.emoji.utils.SmileUtils;

import com.easyvaas.common.chat.VoicePlayClickListener;
import com.easyvaas.common.chat.activity.AlertDialogActivity;
import com.easyvaas.common.chat.activity.ChatActivity;
import com.easyvaas.common.chat.activity.ContextMenuActivity;
import com.easyvaas.common.chat.activity.ShowBigImageActivity;
import com.easyvaas.common.chat.task.LoadImageTask;
import com.easyvaas.common.chat.utils.ChatLogger;
import com.easyvaas.common.chat.utils.CommonUtils;
import com.easyvaas.common.chat.utils.ImageCache;
import com.easyvaas.common.chat.utils.ImageUtils;
import com.easyvaas.common.chat.utils.SingleToast;
import com.easyvaas.common.chat.utils.ChatUserUtil;
import com.easyvaas.common.chat.R;

public class MessageAdapter extends BaseAdapter {
    private final static String TAG = MessageAdapter.class.getSimpleName();

    private static final int MESSAGE_TYPE_RECV_TXT = 0;
    private static final int MESSAGE_TYPE_SENT_TXT = 1;
    private static final int MESSAGE_TYPE_SENT_IMAGE = 2;
    private static final int MESSAGE_TYPE_SENT_LOCATION = 3;
    private static final int MESSAGE_TYPE_RECV_LOCATION = 4;
    private static final int MESSAGE_TYPE_RECV_IMAGE = 5;
    private static final int MESSAGE_TYPE_SENT_VOICE = 6;
    private static final int MESSAGE_TYPE_RECV_VOICE = 7;

    public static final String IMAGE_DIR = "chat/image/";

    private static final int HANDLER_MESSAGE_REFRESH_LIST = 0;
    private static final int HANDLER_MESSAGE_SELECT_LAST = 1;
    private static final int HANDLER_MESSAGE_SEEK_TO = 2;

    private String mImUserName;
    private LayoutInflater mLayoutInflater;
    private Activity mActivity;

    // reference to conversation object in chatsdk
    private EMConversation conversation;
    private EMMessage[] messages = null;
    private Handler handler = new MyHandler(this);
    private Map<String, Timer> timers = new Hashtable<String, Timer>();

    private static class MyHandler extends Handler {
        private SoftReference<MessageAdapter> softReference;

        public MyHandler(MessageAdapter adapter) {
            softReference = new SoftReference<MessageAdapter>(adapter);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MessageAdapter adapter = softReference.get();
            if (adapter == null) {
                return;
            }
            switch (msg.what) {
                case HANDLER_MESSAGE_REFRESH_LIST:
                    // UI线程不能直接使用conversation.getAllMessages()
                    // 否则在UI刷新过程中，如果收到新的消息，会导致并发问题
                    adapter.messages = adapter.conversation.getAllMessages()
                            .toArray(new EMMessage[adapter.conversation.getAllMessages().size()]);
                    for (int i = 0; i < adapter.messages.length; i++) {
                        // getMessage will set message as read status
                        adapter.conversation.getMessage(i);
                    }
                    adapter.notifyDataSetChanged();
                    break;
                case HANDLER_MESSAGE_SELECT_LAST:
                    if (adapter.mActivity instanceof ChatActivity) {
                        ListView listView = ((ChatActivity) adapter.mActivity).getListView();
                        if (adapter.messages.length > 0) {
                            listView.setSelection(adapter.messages.length - 1);
                        }
                    }
                    break;
                case HANDLER_MESSAGE_SEEK_TO:
                    int position = msg.arg1;
                    if (adapter.mActivity instanceof ChatActivity) {
                        ListView listView = ((ChatActivity) adapter.mActivity).getListView();
                        listView.setSelection(position);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public MessageAdapter(Context context, String username, int chatType) {
        this.mImUserName = username;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mActivity = (Activity) context;
        this.conversation = EMChatManager.getInstance().getConversation(username);
    }

    @Override
    public int getCount() {
        return messages == null ? 0 : messages.length;
    }

    public void refresh() {
        if (handler.hasMessages(HANDLER_MESSAGE_REFRESH_LIST)) {
            return;
        }
        android.os.Message msg = handler.obtainMessage(HANDLER_MESSAGE_REFRESH_LIST);
        handler.sendMessage(msg);
    }

    /**
     * Refresh view, select the last one
     */
    public void refreshSelectLast() {
        handler.sendMessage(handler.obtainMessage(HANDLER_MESSAGE_REFRESH_LIST));
        handler.sendMessage(handler.obtainMessage(HANDLER_MESSAGE_SELECT_LAST));
    }

    /**
     * Refresh view, select Position
     */
    public void refreshSeekTo(int position) {
        handler.sendMessage(handler.obtainMessage(HANDLER_MESSAGE_REFRESH_LIST));
        android.os.Message msg = handler.obtainMessage(HANDLER_MESSAGE_SEEK_TO);
        msg.arg1 = position;
        handler.sendMessage(msg);
    }

    @Override
    public EMMessage getItem(int position) {
        if (messages != null && position < messages.length) {
            return messages[position];
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 18;
    }

    @Override
    public int getItemViewType(int position) {
        EMMessage message = getItem(position);
        if (message == null) {
            return -1;
        }
        if (message.getType() == EMMessage.Type.TXT) {
            return message.direct == EMMessage.Direct.RECEIVE ?
                    MESSAGE_TYPE_RECV_TXT :
                    MESSAGE_TYPE_SENT_TXT;
        }
        if (message.getType() == EMMessage.Type.IMAGE) {
            return message.direct == EMMessage.Direct.RECEIVE ?
                    MESSAGE_TYPE_RECV_IMAGE :
                    MESSAGE_TYPE_SENT_IMAGE;

        }
        if (message.getType() == EMMessage.Type.LOCATION) {
            return message.direct == EMMessage.Direct.RECEIVE ?
                    MESSAGE_TYPE_RECV_LOCATION :
                    MESSAGE_TYPE_SENT_LOCATION;
        }
        if (message.getType() == EMMessage.Type.VOICE) {
            return message.direct == EMMessage.Direct.RECEIVE ?
                    MESSAGE_TYPE_RECV_VOICE :
                    MESSAGE_TYPE_SENT_VOICE;
        }

        return -1;// invalid
    }

    private View createViewByMessage(EMMessage message, ViewGroup parent) {
        switch (message.getType()) {
            case IMAGE:
                return message.direct == EMMessage.Direct.RECEIVE ?
                        mLayoutInflater.inflate(R.layout.item_chat_received_picture, parent, false) :
                        mLayoutInflater.inflate(R.layout.item_chat_sent_picture, parent, false);

            case VOICE:
                return message.direct == EMMessage.Direct.RECEIVE ?
                        mLayoutInflater.inflate(R.layout.item_chat_received_voice, parent, false) :
                        mLayoutInflater.inflate(R.layout.item_chat_sent_voice, parent, false);
            default:
                return message.direct == EMMessage.Direct.RECEIVE ?
                        mLayoutInflater.inflate(R.layout.item_chat_received_message, parent, false) :
                        mLayoutInflater.inflate(R.layout.item_chat_sent_message, parent, false);
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final EMMessage message = getItem(position);
        ChatType chatType = message.getChatType();
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = createViewByMessage(message, parent);
            if (message.getType() == EMMessage.Type.IMAGE) {
                holder.sendPictureIv = ((ImageView) convertView.findViewById(R.id.iv_sendPicture));
                holder.chatPlayIconIv = (ImageView) convertView.findViewById(R.id.image_play_iv);
                holder.userLogoIv = (ImageView) convertView.findViewById(R.id.iv_userhead);
                holder.chatContentTv = (TextView) convertView.findViewById(R.id.percentage);
                holder.sendingPb = (ProgressBar) convertView.findViewById(R.id.progressBar);
                holder.statusIv = (ImageView) convertView.findViewById(R.id.msg_status);
                holder.nicknameTv = (TextView) convertView.findViewById(R.id.tv_userid);
            } else if (message.getType() == EMMessage.Type.TXT) {
                holder.sendingPb = (ProgressBar) convertView.findViewById(R.id.pb_sending);
                holder.statusIv = (ImageView) convertView.findViewById(R.id.msg_status);
                holder.userLogoIv = (ImageView) convertView.findViewById(R.id.iv_userhead);
                // Chat text content
                holder.chatContentTv = (TextView) convertView.findViewById(R.id.tv_chatcontent);
                holder.nicknameTv = (TextView) convertView.findViewById(R.id.tv_userid);
                holder.sendPictureRl = ((RelativeLayout) convertView.findViewById(R.id.rl_red_package));
                holder.redPackageTitle = (TextView) convertView.findViewById(R.id.red_pack_card_title_tv);
                holder.redPackageContent = (TextView) convertView.findViewById(R.id.red_pack_card_content_tv);

            } else if (message.getType() == EMMessage.Type.VOICE) {
                holder.sendPictureIv = ((ImageView) convertView.findViewById(R.id.iv_voice));
                holder.userLogoIv = (ImageView) convertView.findViewById(R.id.iv_userhead);
                holder.chatContentTv = (TextView) convertView.findViewById(R.id.tv_length);
                holder.sendingPb = (ProgressBar) convertView.findViewById(R.id.pb_sending);
                holder.statusIv = (ImageView) convertView.findViewById(R.id.msg_status);
                holder.nicknameTv = (TextView) convertView.findViewById(R.id.tv_userid);
                holder.readStatusIv = (ImageView) convertView.findViewById(R.id.iv_unread_voice);
            }
            holder.timestampTv = (TextView) convertView.findViewById(R.id.timestamp);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Set timestamp
        if (position == 0) {
            holder.timestampTv.setText(DateUtils.getTimestampString(new Date(message.getMsgTime())));
            holder.timestampTv.setVisibility(View.VISIBLE);
        } else {
            EMMessage prevMessage = getItem(position - 1);
            if (prevMessage != null && DateUtils
                    .isCloseEnough(message.getMsgTime(), prevMessage.getMsgTime())) {
                holder.timestampTv.setVisibility(View.GONE);
            } else {
                holder.timestampTv.setText(DateUtils.getTimestampString(new Date(message.getMsgTime())));
                holder.timestampTv.setVisibility(View.VISIBLE);
            }
        }
        if (message.direct == EMMessage.Direct.SEND) {
            if (holder.nicknameTv != null) {
                holder.nicknameTv.setText(ChatUserUtil.getCurrentUserNickname());
            }
        }
        // Set user logo
        if (message.direct == Direct.SEND) {
            ChatUserUtil.setCurrentUserAvatar(mActivity, holder.userLogoIv);
        } else {
            ChatUserUtil.setUserAvatar(mActivity, message.getFrom(), holder.userLogoIv);
        }
        holder.userLogoIv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                ChatUserUtil.showUserInfo(mActivity, message.getFrom());
            }
        });

        switch (message.getType()) {
            case IMAGE:
                handleImageMessage(message, holder, position);
                break;
            case TXT:
                handleTextMessage(message, holder, position);
                break;
            case VOICE:
                handleVoiceMessage(message, holder, position);
                break;
            default:
                // not supported
        }

        if (message.direct == EMMessage.Direct.SEND) {
            if (holder.statusIv != null) {
                holder.statusIv.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mActivity, AlertDialogActivity.class);
                        intent.putExtra("msg", mActivity.getString(R.string.confirm_resend));
                        intent.putExtra("title", mActivity.getString(R.string.resend));
                        intent.putExtra("cancel", true);
                        intent.putExtra("position", position);
                        if (message.getType() == EMMessage.Type.TXT)
                            mActivity.startActivityForResult(intent, ChatActivity.REQUEST_CODE_TEXT);
                        else if (message.getType() == EMMessage.Type.IMAGE)
                            mActivity.startActivityForResult(intent, ChatActivity.REQUEST_CODE_PICTURE);
                        else if (message.getType() == EMMessage.Type.VOICE)
                            mActivity.startActivityForResult(intent, ChatActivity.REQUEST_CODE_VOICE);
                    }
                });
            }
        }

        return convertView;
    }

    private void handleTextMessage(EMMessage message, ViewHolder holder, final int position) {
        TextMessageBody txtBody = (TextMessageBody) message.getBody();
        Spannable span = SmileUtils.getSmiledText(mActivity, txtBody.getMessage());
        holder.chatContentTv.setVisibility(View.VISIBLE);
        holder.sendPictureRl.setVisibility(View.GONE);
        holder.chatContentTv.setText(span, BufferType.SPANNABLE);
        holder.chatContentTv.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(mActivity, ContextMenuActivity.class);
                intent.putExtra(ContextMenuActivity.EXTRA_KEY_POSITION, position);
                intent.putExtra(ContextMenuActivity.EXTRA_KEY_TYPE, EMMessage.Type.TXT.ordinal());
                mActivity.startActivityForResult(intent, ChatActivity.REQUEST_CODE_CONTEXT_MENU);
                return true;
            }
        });

        if (message.direct == EMMessage.Direct.SEND) {
            switch (message.status) {
                case SUCCESS:
                    holder.sendingPb.setVisibility(View.GONE);
                    holder.statusIv.setVisibility(View.GONE);
                    break;
                case FAIL:
                    holder.sendingPb.setVisibility(View.GONE);
                    holder.statusIv.setVisibility(View.VISIBLE);
                    break;
                case INPROGRESS:
                    holder.sendingPb.setVisibility(View.VISIBLE);
                    holder.statusIv.setVisibility(View.GONE);
                    break;
                default:
                    sendMsgInBackground(message, holder);
            }
        }
    }

    private void handleImageMessage(final EMMessage message, final ViewHolder holder, final int position) {
        holder.sendingPb.setTag(position);
        holder.sendPictureIv.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(mActivity, ContextMenuActivity.class);
                intent.putExtra(ContextMenuActivity.EXTRA_KEY_POSITION, position);
                intent.putExtra(ContextMenuActivity.EXTRA_KEY_TYPE, EMMessage.Type.IMAGE.ordinal());
                mActivity.startActivityForResult(intent, ChatActivity.REQUEST_CODE_CONTEXT_MENU);
                return true;
            }
        });

        if (CommonUtils.isLivingImage(message)) {
            holder.chatPlayIconIv.setVisibility(View.VISIBLE);
        } else {
            holder.chatPlayIconIv.setVisibility(View.GONE);
        }

        if (message.direct == EMMessage.Direct.RECEIVE) {
            if (message.status == EMMessage.Status.INPROGRESS) {
                // "!!!! back receive";
                holder.sendPictureIv.setImageResource(R.drawable.default_image);
                showDownloadImageProgress(message, holder);
                // downloadImage(message, holder);
            } else {
                // "!!!! not back receive, show image directly");
                holder.sendingPb.setVisibility(View.GONE);
                holder.chatContentTv.setVisibility(View.GONE);
                holder.sendPictureIv.setImageResource(R.drawable.default_image);
                ImageMessageBody imgBody = (ImageMessageBody) message.getBody();
                if (imgBody.getLocalUrl() != null) {
                    String remotePath = imgBody.getRemoteUrl();
                    String imageName = remotePath
                            .substring(remotePath.lastIndexOf("/") + 1, remotePath.length());
                    String path = PathUtil.getInstance().getImagePath() + "/" + imageName;
                    String thumbRemoteUrl = imgBody.getThumbnailUrl();
                    if (TextUtils.isEmpty(thumbRemoteUrl) && !TextUtils.isEmpty(remotePath)) {
                        thumbRemoteUrl = remotePath;
                    }
                    String thumbnailPath = ImageUtils.getThumbnailImagePath(thumbRemoteUrl);
                    showImageView(thumbnailPath, holder.sendPictureIv, path, imgBody.getRemoteUrl(), message);
                }
            }
        } else if (message.direct == Direct.SEND) {
            ImageMessageBody imgBody = (ImageMessageBody) message.getBody();
            String filePath = imgBody.getLocalUrl();
            if (filePath != null && new File(filePath).exists()) {
                showImageView(ImageUtils.getThumbnailImagePath(filePath),
                        holder.sendPictureIv, filePath, null, message);
            } else {
                showImageView(ImageUtils.getThumbnailImagePath(filePath),
                        holder.sendPictureIv, filePath, IMAGE_DIR, message);
            }
            switch (message.status) {
                case SUCCESS:
                    holder.sendingPb.setVisibility(View.GONE);
                    holder.chatContentTv.setVisibility(View.GONE);
                    holder.statusIv.setVisibility(View.GONE);
                    break;
                case FAIL:
                    holder.sendingPb.setVisibility(View.GONE);
                    holder.chatContentTv.setVisibility(View.GONE);
                    holder.statusIv.setVisibility(View.VISIBLE);
                    break;
                case INPROGRESS:
                    holder.statusIv.setVisibility(View.GONE);
                    holder.sendingPb.setVisibility(View.VISIBLE);
                    holder.chatContentTv.setVisibility(View.VISIBLE);
                    if (timers.containsKey(message.getMsgId())) {
                        return;
                    }
                    // set a timer
                    final Timer timer = new Timer();
                    timers.put(message.getMsgId(), timer);
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            mActivity.runOnUiThread(new Runnable() {
                                public void run() {
                                    holder.sendingPb.setVisibility(View.VISIBLE);
                                    holder.chatContentTv.setVisibility(View.VISIBLE);
                                    holder.chatContentTv.setText(message.progress + "%");
                                    if (message.status == EMMessage.Status.SUCCESS) {
                                        holder.sendingPb.setVisibility(View.GONE);
                                        holder.chatContentTv.setVisibility(View.GONE);
                                        // message.setSendingStatus(Message.SENDING_STATUS_SUCCESS);
                                        timer.cancel();
                                    } else if (message.status == EMMessage.Status.FAIL) {
                                        holder.sendingPb.setVisibility(View.GONE);
                                        holder.chatContentTv.setVisibility(View.GONE);
                                        // message.setSendingStatus(Message.SENDING_STATUS_FAIL);
                                        // message.setProgress(0);
                                        holder.statusIv.setVisibility(View.VISIBLE);
                                        SingleToast.show(mActivity, mActivity.getString(R.string.send_fail)
                                                + mActivity.getString(R.string.connect_failed_toast));
                                        timer.cancel();
                                    }
                                }
                            });
                        }
                    }, 0, 500);
                    break;
                default:
                    sendPictureMessage(message, holder);
            }
        }
    }

    private void handleVoiceMessage(final EMMessage message, final ViewHolder holder, final int position) {
        VoiceMessageBody voiceBody = (VoiceMessageBody) message.getBody();
        int len = voiceBody.getLength();
        if (len > 0) {
            holder.chatContentTv.setText(voiceBody.getLength() + "\"");
            holder.chatContentTv.setVisibility(View.VISIBLE);
        } else {
            holder.chatContentTv.setVisibility(View.INVISIBLE);
        }
        holder.sendPictureIv.setOnClickListener(new VoicePlayClickListener(
                message, holder.sendPictureIv, holder.readStatusIv, this, mActivity, mImUserName));
        holder.sendPictureIv.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(mActivity, ContextMenuActivity.class);
                intent.putExtra(ContextMenuActivity.EXTRA_KEY_POSITION, position);
                intent.putExtra(ContextMenuActivity.EXTRA_KEY_TYPE, EMMessage.Type.VOICE.ordinal());
                mActivity.startActivityForResult(intent, ChatActivity.REQUEST_CODE_CONTEXT_MENU);
                return true;
            }
        });
        if (((ChatActivity) mActivity).playMsgId != null
                && ((ChatActivity) mActivity).playMsgId.equals(message.getMsgId())
                && VoicePlayClickListener.isPlaying) {
            AnimationDrawable voiceAnimation;
            if (message.direct == EMMessage.Direct.RECEIVE) {
                holder.sendPictureIv.setImageResource(R.drawable.voice_from_icon);
            } else {
                holder.sendPictureIv.setImageResource(R.drawable.voice_to_icon);
            }
            voiceAnimation = (AnimationDrawable) holder.sendPictureIv.getDrawable();
            voiceAnimation.start();
        } else {
            if (message.direct == EMMessage.Direct.RECEIVE) {
                holder.sendPictureIv.setImageResource(R.drawable.chatfrom_voice_playing);
            } else {
                holder.sendPictureIv.setImageResource(R.drawable.chatto_voice_playing);
            }
        }

        if (message.direct == EMMessage.Direct.RECEIVE) {
            if (message.isListened()) {
                holder.readStatusIv.setVisibility(View.INVISIBLE);
            } else {
                holder.readStatusIv.setVisibility(View.VISIBLE);
            }
            ChatLogger.d(TAG, "it is receive msg");
            if (message.status == EMMessage.Status.INPROGRESS) {
                holder.sendingPb.setVisibility(View.VISIBLE);
                ChatLogger.d(TAG, "!!!! back receive");
                ((FileMessageBody) message.getBody()).setDownloadCallback(new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                holder.sendingPb.setVisibility(View.INVISIBLE);
                                notifyDataSetChanged();
                            }
                        });
                    }

                    @Override
                    public void onProgress(int progress, String status) {
                    }

                    @Override
                    public void onError(int code, String message) {
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                holder.sendingPb.setVisibility(View.INVISIBLE);
                            }
                        });
                    }
                });
            } else {
                holder.sendingPb.setVisibility(View.INVISIBLE);
            }
        } else if (message.direct == Direct.SEND) {
            switch (message.status) {
                case SUCCESS:
                    holder.sendingPb.setVisibility(View.GONE);
                    holder.statusIv.setVisibility(View.GONE);
                    break;
                case FAIL:
                    holder.sendingPb.setVisibility(View.GONE);
                    holder.statusIv.setVisibility(View.VISIBLE);
                    break;
                case INPROGRESS:
                    holder.sendingPb.setVisibility(View.VISIBLE);
                    holder.statusIv.setVisibility(View.GONE);
                    break;
                default:
                    sendMsgInBackground(message, holder);
            }
        }
    }

    public void sendMsgInBackground(final EMMessage message, final ViewHolder holder) {
        holder.statusIv.setVisibility(View.GONE);
        holder.sendingPb.setVisibility(View.VISIBLE);

        EMChatManager.getInstance().sendMessage(message, new EMCallBack() {
            @Override
            public void onSuccess() {
                updateSentView(message, holder);
            }

            @Override
            public void onError(int code, String error) {
                updateSentView(message, holder);
            }

            @Override
            public void onProgress(int progress, String status) {
            }
        });
    }

    /*
     * chat sdk will automatic download thumbnail image for the image message we
     * need to register callback show the download progress
     */
    private void showDownloadImageProgress(final EMMessage message, final ViewHolder holder) {
        ChatLogger.d(TAG, "!!! show download image progress");
        final FileMessageBody messageBody = (FileMessageBody) message.getBody();
        holder.sendingPb.setVisibility(View.VISIBLE);
        holder.chatContentTv.setVisibility(View.VISIBLE);

        messageBody.setDownloadCallback(new EMCallBack() {
            @Override
            public void onSuccess() {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // message.setBackReceive(false);
                        if (message.getType() == EMMessage.Type.IMAGE) {
                            holder.sendingPb.setVisibility(View.GONE);
                            holder.chatContentTv.setVisibility(View.GONE);
                        }
                        notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onError(int code, String message) {

            }

            @Override
            public void onProgress(final int progress, String status) {
                if (message.getType() == EMMessage.Type.IMAGE) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            holder.chatContentTv.setText(progress + "%");
                        }
                    });
                }
            }
        });
    }

    /*
     * send message with new sdk
     */
    private void sendPictureMessage(final EMMessage message, final ViewHolder holder) {
        // before send, update ui
        holder.statusIv.setVisibility(View.GONE);
        holder.sendingPb.setVisibility(View.VISIBLE);
        holder.chatContentTv.setVisibility(View.VISIBLE);
        holder.chatContentTv.setText("0%");

        EMChatManager.getInstance().sendMessage(message, new EMCallBack() {
            @Override
            public void onSuccess() {
                ChatLogger.d(TAG, "send image message successfully");
                mActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        // send success
                        holder.sendingPb.setVisibility(View.GONE);
                        holder.chatContentTv.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onError(int code, String error) {
                mActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        holder.sendingPb.setVisibility(View.GONE);
                        holder.chatContentTv.setVisibility(View.GONE);
                        // message.setSendingStatus(Message.SENDING_STATUS_FAIL);
                        holder.statusIv.setVisibility(View.VISIBLE);
                        SingleToast.show(mActivity, mActivity.getString(R.string.send_fail)
                                + mActivity.getString(R.string.connect_failed_toast));
                    }
                });
            }

            @Override
            public void onProgress(final int progress, String status) {
                mActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        holder.chatContentTv.setText(progress + "%");
                    }
                });
            }
        });
    }

    private void updateSentView(final EMMessage message, final ViewHolder holder) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // send success
                if (message.getType() == EMMessage.Type.VIDEO) {
                    holder.chatContentTv.setVisibility(View.GONE);
                }
                ChatLogger.d(TAG, "message status : " + message.status);
                if (message.status == EMMessage.Status.SUCCESS) {
                    // if (message.getType() == EMMessage.Type.FILE) {
                    // holder.sendingPb.setVisibility(View.INVISIBLE);
                    // holder.statusIv.setVisibility(View.INVISIBLE);
                    // } else {
                    // holder.sendingPb.setVisibility(View.GONE);
                    // holder.statusIv.setVisibility(View.GONE);
                    // }
                } else if (message.status == EMMessage.Status.FAIL) {
                    // if (message.getType() == EMMessage.Type.FILE) {
                    // holder.sendingPb.setVisibility(View.INVISIBLE);
                    // } else {
                    // holder.sendingPb.setVisibility(View.GONE);
                    // }
                    // holder.statusIv.setVisibility(View.VISIBLE);
                    if (message.getError() == EMError.MESSAGE_SEND_INVALID_CONTENT) {
                        SingleToast.show(mActivity, mActivity.getString(R.string.send_fail)
                                + mActivity.getString(R.string.error_send_invalid_content));
                    } else if (message.getError() == EMError.MESSAGE_SEND_NOT_IN_THE_GROUP) {
                        SingleToast.show(mActivity, mActivity.getString(R.string.send_fail)
                                + mActivity.getString(R.string.error_send_not_in_the_group));
                    } else {
                        SingleToast.show(mActivity, mActivity.getString(R.string.send_fail)
                                + mActivity.getString(R.string.connect_failed_toast));
                    }
                }
                notifyDataSetChanged();
            }
        });
    }

    /**
     * load image into image view
     */
    private boolean showImageView(final String thumbnailPath, final ImageView iv,
            final String localFullSizePath, String remoteDir, final EMMessage message) {
        final String remote = remoteDir;
        ChatLogger.d(TAG, "local = " + localFullSizePath + " remote: " + remote);
        // first check if the thumbnail image already loaded into cache
        Bitmap bitmap = ImageCache.getInstance().get(thumbnailPath);
        if (bitmap != null) {
            // thumbnail image is already loaded, reuse the drawable
            iv.setImageBitmap(bitmap);
            iv.setClickable(true);
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ChatLogger.d(TAG, "image view on click");
                    if (CommonUtils.isLivingImage(message)) {
                        String vid = "";
                        try {
                            vid = message.getStringAttribute("vid");
                        } catch (EaseMobException e) {
                            ChatLogger.e(TAG, "get vid failed!", e);
                        }
                        Intent watchVideoIntent = new Intent(ChatConstants.EXTERNAL_ACTION_GO_WATCH_VIDEO);
                        watchVideoIntent.putExtra(ChatConstants.EXTERNAL_EXTRA_KEY_VIDEO_ID, vid);
                        mActivity.sendBroadcast(watchVideoIntent);
                    } else {
                        Intent intent = new Intent(mActivity, ShowBigImageActivity.class);
                        File file = new File(localFullSizePath);
                        if (file.exists()) {
                            Uri uri = Uri.fromFile(file);
                            intent.putExtra(ShowBigImageActivity.LOCAL_IMAGE_URL, uri);
                            ChatLogger.d(TAG, "here need to check why download everytime");
                        } else {
                            // The local full size pic does not exist yet.
                            // ShowBigImage needs to download it from the server first
                            ImageMessageBody body = (ImageMessageBody) message.getBody();
                            intent.putExtra("secret", body.getSecret());
                            intent.putExtra(ShowBigImageActivity.REMOTE_IMAGE_URL, remote);
                        }
                        mActivity.startActivity(intent);
                    }

                    if (message.direct == Direct.RECEIVE && !message.isAcked
                            && message.getChatType() != ChatType.GroupChat
                            && message.getChatType() != ChatType.ChatRoom) {
                        try {
                            EMChatManager.getInstance().ackMessageRead(message.getFrom(), message.getMsgId());
                            message.isAcked = true;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            return true;
        } else {
            new LoadImageTask()
                    .execute(thumbnailPath, localFullSizePath, remote, message.getChatType(), iv, mActivity,
                            message);
            return true;
        }

    }

    public static class ViewHolder {
        TextView timestampTv;
        ImageView sendPictureIv;
        RelativeLayout sendPictureRl;
        ImageView chatPlayIconIv;
        TextView chatContentTv;
        TextView redPackageTitle;
        TextView redPackageContent;
        ProgressBar sendingPb;
        ImageView statusIv;
        ImageView userLogoIv;
        TextView nicknameTv;

        ImageView readStatusIv;

    }

}
