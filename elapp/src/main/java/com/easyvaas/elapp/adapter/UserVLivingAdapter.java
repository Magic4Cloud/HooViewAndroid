package com.easyvaas.elapp.adapter;

import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseViewHolder;
import com.easyvaas.elapp.ui.base.mybase.MyBaseAdapter;

import java.util.List;

/**
 * Date    2017/4/20
 * Author  xiaomao
 * adapter: 我的发布---直播
 */

public class UserVLivingAdapter extends MyBaseAdapter {

    private static final int TYPE_IMAGE_TEXT = 1;
    private static final int TYPE_VIDEO = 2;

    public UserVLivingAdapter(List data) {
        super(data);
    }

    @Override
    protected int getItemViewByType(int position) {
        if (position == 0) {
            return TYPE_IMAGE_TEXT;
        } else {
            return TYPE_VIDEO;
        }
    }

    @Override
    public int getItemCount() {
        int count = 1;
        if (mData != null) {
            count = count + mData.size();
        }
        return count;
    }

    @Override
    protected BaseViewHolder OnCreateViewByHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    protected void initOnItemClickListener() {

    }

    /**
     * Implement this method and use the helper to adapt the view to the given item.
     *
     * @param helper A fully initialized helper.
     * @param item   The item that needs to be displayed.
     */
    @Override
    protected void convert(BaseViewHolder helper, Object item) {

    }

}
