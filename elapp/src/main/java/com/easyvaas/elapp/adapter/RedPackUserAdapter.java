/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hooview.app.R;
import com.easyvaas.elapp.bean.chat.ChatRedPackUserEntity;
import com.easyvaas.elapp.utils.UserUtil;

public class RedPackUserAdapter extends BaseAdapter {
    private Context context;
    private List<ChatRedPackUserEntity> mList = new ArrayList<ChatRedPackUserEntity>();

    public RedPackUserAdapter(Context context, List<ChatRedPackUserEntity> users) {
        this.mList.addAll(users);
        this.context = context;
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.item_red_pack_user, parent, false);
            holder.userLogo = (ImageView) view.findViewById(R.id.red_pack_user_photo);
            holder.nameTv = (TextView) view.findViewById(R.id.red_pack_nickname_tv);
            holder.amountTv = (TextView) view.findViewById(R.id.red_pack_amount_tv);
            holder.bestTagTv = (TextView) view.findViewById(R.id.red_pack_best_tv);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final ChatRedPackUserEntity bean = mList.get(position);
        holder.nameTv.setText(bean.getNickname());
        holder.amountTv.setText(context.getString(R.string.e_coin_count_rear, bean.getEcoin()));
        UserUtil.showUserPhoto(context, bean.getLogourl(), holder.userLogo);
        if (bean.getIsbest() == ChatRedPackUserEntity.IS_BEST) {
            holder.bestTagTv.setVisibility(View.VISIBLE);
        } else {
            holder.bestTagTv.setVisibility(View.GONE);
        }
        return view;
    }

    public static class ViewHolder {
        ImageView userLogo;
        TextView nameTv;
        TextView amountTv;
        TextView bestTagTv;
    }
}
