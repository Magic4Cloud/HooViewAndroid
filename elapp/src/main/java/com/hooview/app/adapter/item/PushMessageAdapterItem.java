/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.adapter.item;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.easyvaas.common.adapter.AdapterItem;
import com.easyvaas.common.widget.MyUserPhoto;

import com.hooview.app.activity.home.HomeTabActivity;
import com.hooview.app.bean.user.UserEntity;
import com.hooview.app.db.Preferences;
import com.hooview.app.net.ApiHelper;
import com.hooview.app.net.MyRequestCallBack;
import com.hooview.app.net.RequestUtil;
import com.hooview.app.utils.Constants;
import com.hooview.app.utils.SingleToast;
import com.hooview.app.utils.UserUtil;
import com.hooview.app.utils.Utils;

public class PushMessageAdapterItem implements AdapterItem<UserEntity> {
    private boolean isPush;

    private MyUserPhoto myUserPhoto;
    private CheckBox location_toggle_tb;
    private TextView signature;
    private TextView nickname;

    public PushMessageAdapterItem(boolean isPUsh) {
        this.isPush = isPUsh;
    }

    @Override
    public int getLayoutResId() {
        return com.hooview.app.R.layout.item_push_message;
    }

    @Override
    public void onBindViews(View view) {
        myUserPhoto = (MyUserPhoto) view.findViewById(com.hooview.app.R.id.my_user_photo);
        location_toggle_tb = (CheckBox) view.findViewById(com.hooview.app.R.id.location_toggle_tbs);
        nickname = (TextView) view.findViewById(com.hooview.app.R.id.user_nickname_tv);
        signature = (TextView) view.findViewById(com.hooview.app.R.id.user_signature_tv);
    }

    @Override
    public void onSetViews() {
        location_toggle_tb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                UserEntity info = (UserEntity) location_toggle_tb.getTag();
                info.setSelected(compoundButton.isChecked());
            }
        });
    }

    @Override
    public void onUpdateViews(final UserEntity model, int position) {
        final Context context = location_toggle_tb.getContext();
        location_toggle_tb.setTag(model);
        if (isPush) {
            if (model.getSubscribed() == UserEntity.UN_SUBSCRIBED) {
                location_toggle_tb.setChecked(model.isSelected());
            } else {
                location_toggle_tb.setChecked(true);
            }
        } else {
            location_toggle_tb.setEnabled(false);
            location_toggle_tb.setFocusable(false);
            location_toggle_tb.setFocusableInTouchMode(false);
            location_toggle_tb.setChecked(true);
        }
        UserUtil.showUserPhoto(context, model.getLogourl(), myUserPhoto);
        nickname.setText(model.getNickname());

        signature.setText(model.getSignature());
        myUserPhoto.setIsVip(model.getVip());
        UserUtil.setGender(nickname, model.getGender());

        location_toggle_tb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (location_toggle_tb.isChecked()) {
                    model.setSubscribed(UserEntity.SUBSCRIBED);

                } else {
                    model.setSubscribed(UserEntity.UN_SUBSCRIBED);
                }
                attentionUser(context, model.getName(), model.getSubscribed(), location_toggle_tb);
            }
        });
        myUserPhoto.getRoundImageView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (model.getName().equals(Preferences.getInstance(context).getUserNumber())) {
                    Intent intent = new Intent(context, HomeTabActivity.class);
                    intent.putExtra(Constants.EXTRA_KEY_TAB_ID, HomeTabActivity.TAB_ID_MINE);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } else {
                    UserUtil.showUserInfo(context, model.getName());
                }
            }
        });
    }

    private void attentionUser(final Context context, String name, final int subscribe,
            final CheckBox checkBox) {
        String deviceId = Utils.getDeviceId(context);
        ApiHelper.getInstance().userSubscribe(
                name, subscribe + "", deviceId, new MyRequestCallBack<String>() {
                    @Override
                    public void onSuccess(String user) {
                        checkBox.setEnabled(true);
                        String actionString = "";
                        if (UserEntity.SUBSCRIBED == subscribe) {
                            actionString = context.getString(com.hooview.app.R.string.subscribe);
                        } else {
                            actionString = context.getString(com.hooview.app.R.string.shield);
                        }
                        SingleToast
                                .show(context, actionString + context.getString(com.hooview.app.R.string.msg_follow_success));
                    }

                    @Override
                    public void onFailure(String msg) {
                        RequestUtil.handleRequestFailed(msg);
                    }

                    @Override
                    public void onError(String msg) {
                        SingleToast.show(context, context.getString(com.hooview.app.R.string.msg_follow_failed));
                    }
                });
    }
}
