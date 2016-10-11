
/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.activity.home.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hooview.app.bean.video.VideoEntityArray;
import com.hooview.app.db.Preferences;
import com.hooview.app.net.ApiConstant;
import com.hooview.app.net.ApiHelper;
import com.hooview.app.net.MyRequestCallBack;
import com.hooview.app.net.RequestUtil;

public class TabTimeLineHomeFragment extends TabTimelineCategoryList {
    private static final String TOPIC_ID_HOT = "0";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        mTopicId = Preferences.getInstance(getActivity())
                .getString(Preferences.KEY_HOME_CURRENT_TOPIC_ID, "0");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        String topicId = Preferences.getInstance(getContext())
                .getString(Preferences.KEY_HOME_CURRENT_TOPIC_ID);
        if (!TextUtils.isEmpty(topicId) && !topicId.equals(mTopicId)) {
            mTopicId = topicId;
        }
        updateVideoList(false);
    }

    @Override
    protected void updateVideoList(boolean isLoadMore) {
        if (TOPIC_ID_HOT.equals(mTopicId)) {
            loadHotVideoList(isLoadMore);
        } else {
            loadTopicVideoList(isLoadMore, mTopicId, true);
        }
    }

    private void loadHotVideoList(final boolean isLoadMore) {
        // Live mark: Request live video first but need to request playback when last video is playback.
        int position =
                isLoadMore && mNextPageIndex > 0 ? mNextPageIndex : ApiConstant.DEFAULT_FIRST_PAGE_INDEX;
        ApiHelper.getInstance().getHotVideoList(position,
                ApiConstant.DEFAULT_PAGE_SIZE, new MyRequestCallBack<VideoEntityArray>() {
                    @Override
                    public void onSuccess(VideoEntityArray result) {
                        if (result != null) {
                            updateListView(isLoadMore, true, true, result);
                        }
                        onRefreshComplete(result == null ? 0 : result.getCount());
                    }

                    @Override
                    public void onError(String errorInfo) {
                        super.onError(errorInfo);
                        onRequestFailed(errorInfo);
                    }

                    @Override
                    public void onFailure(String msg) {
                        RequestUtil.handleRequestFailed(msg);
                        onRequestFailed(msg);
                    }
                });
    }
}
