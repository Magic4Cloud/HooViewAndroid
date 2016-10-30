package com.easyvaas.common.chat.adapter.item;

import java.util.Date;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMMessage;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.DateUtils;

import com.easyvaas.common.adapter.AdapterItem;
import com.easyvaas.common.chat.ChatHXSDKHelper;
import com.easyvaas.common.chat.R;
import com.easyvaas.common.chat.db.ChatDB;
import com.easyvaas.common.chat.utils.ChatConstants;
import com.easyvaas.common.chat.utils.ChatUserUtil;
import com.easyvaas.common.chat.utils.CommonUtils;
import com.easyvaas.common.emoji.utils.SmileUtils;

public class MyGroupAdapterItem implements AdapterItem<EMGroup> {
    private ImageView logoIv;
    private TextView titleTv;
    private TextView subtitleTv;
    private TextView dataTimeTv;
    private TextView unreadCountTv;
    private Context mContext;
    private ImageView mGroupMainIconIv;
    @Override
    public int getLayoutResId() {
        return R.layout.item_chat_group;
    }

    @Override
    public void onBindViews(View root) {
        mContext = root.getContext();
        logoIv = (ImageView) root.findViewById(R.id.msg_logo_iv);
        titleTv = (TextView) root.findViewById(R.id.msg_title_tv);
        subtitleTv = (TextView) root.findViewById(R.id.msg_subtitle_tv);
        dataTimeTv = (TextView) root.findViewById(R.id.msg_date_time_tv);
        mGroupMainIconIv = (ImageView) root.findViewById(R.id.group_owner_iv);
        unreadCountTv = (TextView) root.findViewById(R.id.msg_unread_count_tv);
    }

    @Override
    public void onSetViews() {

    }

    @Override
    public void onUpdateViews(EMGroup model, int position) {
        ChatUserUtil.setUserAvatar(mContext, model.getOwner(), logoIv);
        titleTv.setText(model.getGroupName());
        EMConversation conversation = ChatHXSDKHelper.getInstance().getGroupConversation(model.getGroupId());
        if (conversation.getMsgCount() > 0) {
            int unreadCount = conversation.getUnreadMsgCount();
            subtitleTv.setVisibility(View.VISIBLE);
            dataTimeTv.setVisibility(View.VISIBLE);
            EMMessage lastMessage = conversation.getLastMessage();
            String smiledText =
                    CommonUtils.getMessageDigest(lastMessage, mContext);
            try {
                JSONArray em_atSomeone_message = lastMessage
                        .getJSONArrayAttribute(ChatConstants.MESSAGE_ATTR_AT_MEMBERS);
                if (null != em_atSomeone_message && em_atSomeone_message.length() != 0 && unreadCount > 0) {
                    for (int j = 0; j < em_atSomeone_message.length(); j++) {
                        try {
                            String imServerUsername = (String) em_atSomeone_message.get(j);
                            String imLocalUsername = ChatHXSDKHelper.getInstance().getHXId();
                            String localNickname = ChatDB.getInstance(mContext)
                                    .getString(ChatDB.KEY_USER_NICKNAME);
                            String atContent = (String) em_atSomeone_message
                                    .get(em_atSomeone_message.length() - 2);
                            String content = (String) em_atSomeone_message
                                    .get(em_atSomeone_message.length() - 1);
                            String atMe = mContext.getResources().getString(R.string.at_me_message);
                            String atAll = mContext.getResources().getString(R.string.at_all_message);
                            String atMessage = mContext.getResources().getString(R.string.at_message);
                            int atTextColor = mContext.getResources().getColor(R.color.red_alpha_percent_80);
                            if (imServerUsername.equals(imLocalUsername) && smiledText
                                    .contains(localNickname)
                                    || imServerUsername.equals(imLocalUsername) && smiledText
                                    .contains("@" + atAll)) {
                                subtitleTv.setText("[" + atMessage + "@" + atMe + "]" + smiledText);
                                subtitleTv.setTextColor(atTextColor);
                                break;
                            }else{
                                subtitleTv.setText(SmileUtils.getSmiledText(mContext,
                                        CommonUtils.getMessageDigest(lastMessage, mContext),
                                        mContext.getResources()
                                                .getDimensionPixelSize(R.dimen.emoji_size_small)),
                                        TextView.BufferType.SPANNABLE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    subtitleTv.setText(SmileUtils.getSmiledText(mContext,
                            CommonUtils.getMessageDigest(lastMessage, mContext),
                            mContext.getResources()
                                    .getDimensionPixelSize(R.dimen.emoji_size_small)),
                            TextView.BufferType.SPANNABLE);
                }

            } catch (EaseMobException e) {
                e.printStackTrace();
            }
            dataTimeTv.setText(
                    DateUtils.getTimestampString(new Date(lastMessage.getMsgTime())));
            if (lastMessage.direct == EMMessage.Direct.SEND
                    && lastMessage.status == EMMessage.Status.FAIL) {
                Drawable drawable = mContext.getResources().getDrawable(R.drawable.msg_state_fail_resend);
                if (drawable != null) {
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                }
                subtitleTv.setCompoundDrawables(drawable, null, null, null);
            } else {
                subtitleTv.setCompoundDrawables(null, null, null, null);
            }

            if (unreadCount > 0) {
                String count = unreadCount > 99 ? "99+" : unreadCount + "";
                unreadCountTv.setText(count);
                unreadCountTv.setVisibility(View.VISIBLE);
            } else {
                unreadCountTv.setVisibility(View.GONE);
            }
        } else {
            subtitleTv.setVisibility(View.INVISIBLE);
            dataTimeTv.setVisibility(View.INVISIBLE);
            unreadCountTv.setVisibility(View.GONE);
        }
        String imUser = ChatHXSDKHelper.getInstance().getHXId();
        if (!TextUtils.isEmpty(imUser) && imUser.equals(model.getOwner())) {
            mGroupMainIconIv.setVisibility(View.VISIBLE);
        } else {
            mGroupMainIconIv.setVisibility(View.GONE);
        }
    }
}
