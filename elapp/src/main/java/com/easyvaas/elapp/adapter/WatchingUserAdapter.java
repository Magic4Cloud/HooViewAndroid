/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.easyvaas.common.widget.MyUserPhoto;

import com.hooview.app.R;
import com.easyvaas.elapp.bean.chat.ChatUser;
import com.easyvaas.elapp.utils.Utils;

public class WatchingUserAdapter extends RecyclerView.Adapter<WatchingUserAdapter.ViewHolder> {
    private static final int WATCHING_NUMBER_LIMIT = 50;

    private OnItemClickListener mOnItemClickListener;
    private final Object lock = new Object();
    private LayoutInflater mInflater;
    private List<ChatUser> mListData;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public WatchingUserAdapter(Context context, List<ChatUser> data) {
        mInflater = LayoutInflater.from(context);
        mListData = data;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View view) {
            super(view);
            mLogoIv = (MyUserPhoto) view.findViewById(R.id.watching_user_logo_iv);

            if (mOnItemClickListener != null) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnItemClickListener.onItemClick(itemView, getLayoutPosition());
                    }
                });
            }
        }

        MyUserPhoto mLogoIv;
        TextView mNameTv;
    }

    @Override
    public int getItemCount() {
        return mListData.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public String getId(int position) {
        return mListData.get(position).getName();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(mInflater.inflate(R.layout.item_watching_user, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        Utils.showImage(mListData.get(i).getLogourl(), R.drawable.guestavatar, viewHolder.mLogoIv);
    }

    public void add(int location, ChatUser object) {
        synchronized (lock) {
            mListData.add(location, object);
            notifyItemInserted(location);
        }
    }

    public boolean add(ChatUser object) {
        synchronized (lock) {
            int lastIndex = mListData.size();
            if (mListData.add(object)) {
                notifyItemInserted(lastIndex);
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean addAll(int location, Collection<? extends ChatUser> collection) {
        synchronized (lock) {
            if (mListData.addAll(location, collection)) {
                notifyItemRangeInserted(location, collection.size());
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean addAll(List<ChatUser> collection) {
        synchronized (lock) {
            collection = calculateLimitList(collection);
            int lastIndex = mListData.size();
            if (mListData.addAll(collection)) {
                notifyItemRangeInserted(lastIndex, collection.size());
                return true;
            } else {
                return false;
            }
        }
    }

    private List<ChatUser> calculateLimitList(List<ChatUser> watchingAddList) {
        if (mListData.size() < WATCHING_NUMBER_LIMIT) {
            int freeCount = WATCHING_NUMBER_LIMIT - mListData.size();
            if (watchingAddList.size() >= WATCHING_NUMBER_LIMIT) {
                mListData.clear();
                notifyDataSetChanged();
                watchingAddList = watchingAddList.subList(0, WATCHING_NUMBER_LIMIT);
            } else {
                if (watchingAddList.size() > freeCount && freeCount > 0) {
                    updateOriginData(watchingAddList.size() - freeCount);
                }
            }
        } else {
            if (watchingAddList.size() >= WATCHING_NUMBER_LIMIT) {
                mListData.clear();
                notifyDataSetChanged();
                watchingAddList = watchingAddList.subList(0, WATCHING_NUMBER_LIMIT);
            } else {
                int needRemoveCount = watchingAddList.size();
                updateOriginData(needRemoveCount);
            }
        }
        return watchingAddList;
    }

    private void updateOriginData(int needRemoveCount) {
        if (needRemoveCount < mListData.size()) {
            List<ChatUser> needRemoveItems = new ArrayList<ChatUser>(
                    mListData.subList(0, needRemoveCount));
            mListData.removeAll(needRemoveItems);
        } else {
            mListData.clear();
        }
        notifyDataSetChanged();
    }

    public boolean removeItem(ChatUser object) {
        synchronized (lock) {
            int index = -1;
            for (int i = 0, n = mListData.size(); i < n; i++) {
                if (mListData.get(i).getName().equals(object.getName())) {
                    index = i;
                }
            }
            if (index > -1 && mListData.remove(index) != null) {
                notifyItemRemoved(index);
                return true;
            } else {
                return false;
            }
        }
    }
}
