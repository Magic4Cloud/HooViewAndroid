/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.activity.user;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.easyvaas.common.adapter.CommonRcvAdapter;

import com.easyvaas.common.bottomsheet.BottomSheet;
import com.easyvaas.common.sharelogin.model.ShareContent;
import com.easyvaas.common.sharelogin.model.ShareContentWebpage;
import com.hooview.app.adapter.item.VideoMineAdapterItem;
import com.hooview.app.adapter.recycler.VideoSmallRcvAdapter;
import com.hooview.app.app.EVApplication;
import com.hooview.app.base.BaseRvcActivity;
import com.hooview.app.bean.user.User;
import com.hooview.app.bean.video.VideoEntity;
import com.hooview.app.bean.video.VideoEntityArray;
import com.hooview.app.db.Preferences;
import com.hooview.app.net.ApiConstant;
import com.hooview.app.net.ApiHelper;
import com.hooview.app.net.MyRequestCallBack;
import com.hooview.app.net.RequestUtil;
import com.hooview.app.utils.Constants;
import com.hooview.app.utils.DialogUtil;
import com.hooview.app.utils.Logger;
import com.hooview.app.utils.ShareHelper;
import com.hooview.app.utils.SingleToast;
import com.hooview.app.utils.Utils;

public class VideoListActivity extends BaseRvcActivity {
    public static final String TYPE_MY_VIDEO_LIST = "my_video_list";
    public static final String TYPE_TOPIC_VIDEO_LIST = "topic_video_list";

    private String mVideoListType;
    private VideoSmallRcvAdapter mVideoAdapter;
    private List<VideoEntity> mVideos;
    private VideoEntity mSelectVideo;

    private BottomSheet mBottomSheet;
    private Dialog mRemoveConfirmDialog;

    private VideoMineAdapterItem.VideoItemOptionListener mOperationListener
            = new VideoMineAdapterItem.VideoItemOptionListener() {
        @Override
        public void onOperationClick(VideoEntity selectVideo) {
            mSelectVideo = selectVideo;
            showOperationMenu();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIsUserTapTopView = true;
        setContentView(com.hooview.app.R.layout.activity_video_list);

        String topTitle = getIntent().getStringExtra(Constants.EXTRA_KEY_TOPIC_TITLE);
        if (topTitle == null || topTitle.length() == 0) {
            setTitle(com.hooview.app.R.string.video);
        } else {
            setTitle(topTitle);
        }
        mVideos = new ArrayList<>();
        mVideoListType = getIntent().getStringExtra(Constants.EXTRA_KEY_TYPE_VIDEO_LIST);

        mEmptyView.setEmptyIcon(com.hooview.app.R.drawable.personal_empty);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mPullToLoadRcvView.getRecyclerView().setLayoutManager(linearLayoutManager);
        mVideoAdapter = new VideoSmallRcvAdapter(mVideos);
        mVideoAdapter.setVideoOptionListener(mOperationListener);
        mVideoAdapter.setOnItemClickListener(new CommonRcvAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mVideos.get(position)
                        .setName(Preferences.getInstance(getApplicationContext()).getUserNumber());
                Utils.watchVideo(VideoListActivity.this, mVideos.get(position));
            }
        });

        mPullToLoadRcvView.getRecyclerView().setAdapter(mVideoAdapter);
        mPullToLoadRcvView.initLoad();

        loadData(false);
    }

    @Override
    protected void loadData(final boolean isLoadMore) {
        super.loadData(isLoadMore);
        int pageIndex =
                isLoadMore && mNextPageIndex > 0 ? mNextPageIndex : ApiConstant.DEFAULT_FIRST_PAGE_INDEX;
        if (TYPE_MY_VIDEO_LIST.equals(mVideoListType)) {
            ApiHelper.getInstance().getUserVideoList("", pageIndex,
                    ApiConstant.DEFAULT_PAGE_SIZE, new MyRequestCallBack<VideoEntityArray>() {
                        @Override
                        public void onSuccess(VideoEntityArray result) {
                            Logger.d(VideoListActivity.class, "Result: " + result);
                            if (isFinishing()) {
                                return;
                            }
                            if (result != null) {
                                if (!isLoadMore) {
                                    mVideos.clear();
                                }
                                mVideos.addAll(result.getVideos());
                                mNextPageIndex = result.getNext();
                            }
                            mVideoAdapter.notifyDataSetChanged();
                            onRefreshComplete(result == null ? 0 : result.getCount());
                        }

                        @Override
                        public void onError(String errorInfo) {
                            super.onError(errorInfo);
                            onRefreshComplete(0);
                        }

                        @Override
                        public void onFailure(String msg) {
                            RequestUtil.handleRequestFailed(msg);
                            onRequestFailed(msg);
                        }
                    });
        } else if (TYPE_TOPIC_VIDEO_LIST.equals(mVideoListType)) {
            String topicId = getIntent().getStringExtra(Constants.EXTRA_KEY_TOPIC_ID);
            ApiHelper.getInstance().getTopicVideoList(topicId, false, pageIndex, ApiConstant.DEFAULT_PAGE_SIZE,
                    new MyRequestCallBack<VideoEntityArray>() {
                        @Override
                        public void onSuccess(VideoEntityArray result) {
                            Logger.d(VideoListActivity.class, "Result: " + result);
                            if (result != null) {
                                if (!isLoadMore) {
                                    mVideos.clear();
                                }
                                mVideos.addAll(result.getVideos());
                                mVideoAdapter.notifyDataSetChanged();

                                mNextPageIndex = result.getNext();
                            }
                            onRefreshComplete(result == null ? 0 : result.getCount());
                        }

                        @Override
                        public void onError(String errorInfo) {
                            super.onError(errorInfo);
                            onRefreshComplete(0);
                        }

                        @Override
                        public void onFailure(String msg) {
                            RequestUtil.handleRequestFailed(msg);
                            onRequestFailed(msg);
                        }
                    });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRemoveConfirmDialog != null && mRemoveConfirmDialog.isShowing()) {
            mRemoveConfirmDialog.dismiss();
        }
    }

    private void showOperationMenu() {
        if (mBottomSheet == null) {
            mBottomSheet = new BottomSheet.Builder(this)
                    .sheet(com.hooview.app.R.menu.video_operation)
                    .listener(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case com.hooview.app.R.id.menu_share:
                                    showShareMenu();
                                    break;
                                case com.hooview.app.R.id.menu_remove:
                                    showRemoveConfirmDialog();
                                    break;
                                case com.hooview.app.R.id.menu_cancel:
                                    break;
                            }
                        }
                    }).build();
        }
        mBottomSheet.show();
    }

    private void showShareMenu() {
        String content = getString(com.hooview.app.R.string.share_mine_video_content, mSelectVideo.getNickname());
        ShareContent shareContent = new ShareContentWebpage(mSelectVideo.getTitle(), content,
                mSelectVideo.getShare_url(), mSelectVideo.getShare_thumb_url());
        ShareHelper.getInstance(VideoListActivity.this).showShareBottomPanel(shareContent);

    }

    private void showRemoveConfirmDialog() {
        if (mRemoveConfirmDialog != null) {
            mRemoveConfirmDialog.show();
            return;
        }
        mRemoveConfirmDialog = DialogUtil.getButtonsDialog(this,
                getString(com.hooview.app.R.string.dialog_confirm_delete_video),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeSelectedVideo();
                        dialog.dismiss();
                    }
                });
        mRemoveConfirmDialog.show();
    }

    private void removeSelectedVideo() {
        ApiHelper.getInstance().videoRemove(mSelectVideo.getVid(),
                new MyRequestCallBack<String>() {
                    @Override
                    public void onSuccess(String result) {
                        SingleToast.show(getApplicationContext(), com.hooview.app.R.string.msg_remove_success);
                        mVideos.remove(mSelectVideo);
                        mVideoAdapter.notifyDataSetChanged();
                        onRefreshComplete(mVideos.size());
                        User user = EVApplication.getUser();
                        user.setVideo_count(user.getVideo_count() - 1);
                    }

                    @Override
                    public void onError(String errorInfo) {
                        super.onError(errorInfo);
                        SingleToast.show(getApplicationContext(), com.hooview.app.R.string.msg_remove_failed);
                    }

                    @Override
                    public void onFailure(String msg) {
                        RequestUtil.handleRequestFailed(msg);
                        SingleToast.show(getApplicationContext(), com.hooview.app.R.string.msg_remove_failed);
                    }
                });
    }
}
