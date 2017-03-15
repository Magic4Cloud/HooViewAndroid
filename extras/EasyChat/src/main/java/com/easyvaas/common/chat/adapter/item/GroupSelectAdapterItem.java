package com.easyvaas.common.chat.adapter.item;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.easemob.EMValueCallBack;
import com.easemob.chat.EMGroup;

import com.easyvaas.common.adapter.AdapterItem;
import com.easyvaas.common.chat.ChatHXSDKHelper;
import com.easyvaas.common.chat.R;
import com.easyvaas.common.chat.utils.ChatUserUtil;

public class GroupSelectAdapterItem implements AdapterItem<EMGroup> {
    private ImageView logoIv;
    private TextView titleTv;
    private TextView subtitleTv;
    private Context mContext;

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
        subtitleTv.setVisibility(View.VISIBLE);
        root.findViewById(R.id.msg_date_time_tv).setVisibility(View.GONE);
        root.findViewById(R.id.group_owner_iv).setVisibility(View.GONE);
        root.findViewById(R.id.msg_unread_count_tv).setVisibility(View.GONE);
    }

    @Override
    public void onSetViews() {

    }

    @Override
    public void onUpdateViews(EMGroup model, int position) {
        ChatUserUtil.setUserAvatar(mContext, model.getOwner(), logoIv);
        titleTv.setText(model.getGroupName());
        getGroupInfo(model.getGroupId(), subtitleTv);
    }

    private void getGroupInfo(String groupId, final TextView showNumberView) {
        ChatHXSDKHelper.getInstance().getGroupInfo(groupId, new EMValueCallBack() {
            @Override
            public void onSuccess(final Object o) {
                if (showNumberView == null) {
                    return;
                }
                showNumberView.post(new Runnable() {
                    @Override
                    public void run() {
                        EMGroup emGroup = (EMGroup) o;
                        if (emGroup.getMembers() != null) {
                            showNumberView.setText(mContext.getString(R.string.unit_person,
                                    String.valueOf(emGroup.getMembers() == null ?
                                            0 : emGroup.getMembers().size())));
                        }
                    }
                });
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }
}
