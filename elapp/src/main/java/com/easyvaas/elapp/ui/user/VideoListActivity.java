/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.ui.user;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easyvaas.common.adapter.CommonRcvAdapter;
import com.easyvaas.common.bottomsheet.BottomSheet;
import com.easyvaas.common.sharelogin.model.ShareContent;
import com.easyvaas.common.sharelogin.model.ShareContentWebpage;
import com.easyvaas.elapp.ui.live.LivePrepareActivity;
import com.hooview.app.R;
import com.easyvaas.elapp.adapter.oldItem.VideoMineAdapterItem;
import com.easyvaas.elapp.adapter.oldRecycler.VideoSmallRcvAdapter;
import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.bean.user.User;
import com.easyvaas.elapp.bean.video.VideoEntity;
import com.easyvaas.elapp.bean.video.VideoEntityArray;
import com.easyvaas.elapp.db.Preferences;
import com.easyvaas.elapp.net.ApiConstant;
import com.easyvaas.elapp.net.ApiHelper;
import com.easyvaas.elapp.net.MyRequestCallBack;
import com.easyvaas.elapp.net.RequestUtil;
import com.easyvaas.elapp.net.UploadThumbAsyncTask;
import com.easyvaas.elapp.ui.base.BaseRvcActivity;
import com.easyvaas.elapp.ui.live.LivePrepareActivity;
import com.easyvaas.elapp.ui.live.PlayerActivity;
import com.easyvaas.elapp.utils.Constants;
import com.easyvaas.elapp.utils.DialogUtil;
import com.easyvaas.elapp.utils.Logger;
import com.easyvaas.elapp.utils.ShareHelper;
import com.easyvaas.elapp.utils.SingleToast;
import com.easyvaas.elapp.utils.Utils;
import com.hooview.app.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VideoListActivity extends BaseRvcActivity {
    public static final String TYPE_MY_VIDEO_LIST = "my_video_list";
    public static final String TYPE_TOPIC_VIDEO_LIST = "topic_video_list";

    private static final int REQUEST_CODE_IMAGE = 0;
    private static final int REQUEST_CODE_CAMERA = 1;
    private static final int REQUEST_CODE_RESULT = 2;
    private static final int COVER_THUMB_WIDTH = 480;
    private static final int COVER_THUMB_HEIGHT = 480;
    private static final String IMAGE_FILE_NAME = "video_thumb.jpg";

    private File mCoverThumbFile;

    private String mVideoListType;
    private VideoSmallRcvAdapter mVideoAdapter;
    private List<VideoEntity> mVideos;
    private VideoEntity mSelectVideo;

    private BottomSheet mBottomSheet;
    private BottomSheet mSetThumbBottomSheet;
    private Dialog mRemoveConfirmDialog;
    private Dialog mInputDialog;
    private RelativeLayout mCommonUserPage;
    private TextView mTvPrompt;
    private TextView mTvAddBtn;
    private ImageView mIvIcon;
    private int mVip;

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
        setContentView(R.layout.activity_video_list);
        mCommonUserPage = (RelativeLayout) findViewById(R.id.rl_empty);
        mTvAddBtn = (TextView) findViewById(R.id.tv_add);
        mTvPrompt = (TextView) findViewById(R.id.tv_prompt);
        mIvIcon = (ImageView)  findViewById(R.id.iv_icon);
        mTvAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mVip == 1){
                    LivePrepareActivity.start(VideoListActivity.this);
                }else{
                    SingleToast.show(VideoListActivity.this,R.string.common_user_live_toast);
                }
            }
        });

        String topTitle = getIntent().getStringExtra(Constants.EXTRA_KEY_TOPIC_TITLE);
        mVip = getIntent().getIntExtra(Constants.EXTRA_KEY_TYPE_VIP,0);
        if (topTitle == null || topTitle.length() == 0) {
            setTitle(R.string.user_my_live_list);
        } else {
            setTitle(topTitle);
        }
        if(mVip !=  1){
            showMyStockEmptyView();
            return;
        }
        mVideos = new ArrayList<>();
        mVideoListType = getIntent().getStringExtra(Constants.EXTRA_KEY_TYPE_VIDEO_LIST);

        mEmptyView.setEmptyIcon(R.drawable.ic_cry);

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
//                Utils.watchVideo(VideoListActivity.this, mVideos.get(position));
                PlayerActivity.start(VideoListActivity.this, mVideos.get(position).getVid(), mVideos.get(position).getLiving(), mVideos.get(position).getMode());
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
                            if (result != null&&result.getVideos().size()>0) {
                                if (!isLoadMore) {
                                    mVideos.clear();
                                }
                                mVideos.addAll(result.getVideos());
                                mNextPageIndex = result.getNext();
                                onRefreshComplete(result == null ? 0 : result.getCount());
                            }else{
                                showVipMyStockEmptyView();
                            }
                            mVideoAdapter.notifyDataSetChanged();
//                          onRefreshComplete(result == null ? 0 : result.getCount());
                        }

                        @Override
                        public void onError(String errorInfo) {
                            super.onError(errorInfo);
//                            onRefreshComplete(0);
                            showVipMyStockEmptyView();
                        }

                        @Override
                        public void onFailure(String msg) {
                            RequestUtil.handleRequestFailed(msg);
                            onRequestFailed(msg);
                        }
                    });
        } else if (TYPE_TOPIC_VIDEO_LIST.equals(mVideoListType)) {
            String topicId = getIntent().getStringExtra(Constants.EXTRA_KEY_TOPIC_ID);
            ApiHelper.getInstance()
                    .getTopicVideoList(topicId, false, pageIndex, ApiConstant.DEFAULT_PAGE_SIZE,
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
        if (mInputDialog != null && mInputDialog.isShowing()) {
            mInputDialog.dismiss();
        }
    }

    public void showMyStockEmptyView() {
        mCommonUserPage.setVisibility(View.VISIBLE);
        mTvAddBtn.setVisibility(View.VISIBLE);
        mTvPrompt.setText(R.string.common_user_live_tips);
        mTvAddBtn.setText(R.string.common_user_become_liver);
        mIvIcon.setImageResource(R.drawable.ic_smile);
    }

    public void showVipMyStockEmptyView() {
        mCommonUserPage.setVisibility(View.VISIBLE);
        mTvAddBtn.setVisibility(View.VISIBLE);
        mTvPrompt.setText(R.string.vip_user_become_liver);
        mTvAddBtn.setText(R.string.vip_user_become_add);
        mIvIcon.setImageResource(R.drawable.ic_smile);
    }

    private void showOperationMenu() {
        if (mBottomSheet == null) {
            mBottomSheet = new BottomSheet.Builder(this)
                    .sheet(R.menu.video_operation)
                    .listener(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case R.id.menu_share:
                                    showShareMenu();
                                    break;
                                case R.id.menu_thumb:
                                    showSetThumbMenu();
                                    break;
                                case R.id.menu_title:
                                    changeVideoTitle();
                                    break;
                                case R.id.menu_remove:
                                    showRemoveConfirmDialog();
                                    break;
                                case R.id.menu_cancel:
                                    break;
                            }
                        }
                    }).build();
        }
        mBottomSheet.show();
    }

    private void showShareMenu() {
        String content = getString(R.string.share_mine_video_content, mSelectVideo.getNickname());
        ShareContent shareContent = new ShareContentWebpage(mSelectVideo.getTitle(), content,
                mSelectVideo.getShare_url(), mSelectVideo.getShare_thumb_url());
        ShareHelper.getInstance(VideoListActivity.this).showShareBottomPanel(shareContent);

    }

    private void showSetThumbMenu() {
        if (mSetThumbBottomSheet == null) {
            mSetThumbBottomSheet = Utils
                    .getSetThumbBottomPanel(this, IMAGE_FILE_NAME, REQUEST_CODE_CAMERA, REQUEST_CODE_IMAGE);
        }
        mSetThumbBottomSheet.show();
    }

    private void showRemoveConfirmDialog() {
        if (mRemoveConfirmDialog != null) {
            mRemoveConfirmDialog.show();
            return;
        }
        mRemoveConfirmDialog = DialogUtil.getButtonsDialog(this,
                getString(R.string.dialog_confirm_delete_video),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeSelectedVideo();
                        dialog.dismiss();
                    }
                });
        mRemoveConfirmDialog.show();
    }

    private void changeVideoTitle() {
        if (mInputDialog == null) {
            final EditText input = new EditText(this);
            InputFilter[] filters = {new InputFilter.LengthFilter(20)};
            input.setFilters(filters);
            input.setText(mSelectVideo.getTitle());
            input.setSelection(input.getText().length());
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            input.setPadding(30, input.getPaddingTop(), 30, input.getPaddingBottom());
            mInputDialog = new AlertDialog.Builder(this)
                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog1, int which) {
                            final String newTitle = input.getText().toString();
                            if (TextUtils.isEmpty(newTitle)) {
                                return;
                            }
                            ApiHelper.getInstance().videoSetTitle(mSelectVideo.getVid(), newTitle,
                                    new MyRequestCallBack<String>() {
                                        @Override
                                        public void onSuccess(String result1) {
                                            mSelectVideo.setTitle(newTitle);
                                            mVideoAdapter.notifyDataSetChanged();
                                        }

                                        @Override
                                        public void onFailure(String msg) {

                                        }
                                    });
                        }
                    })
                    .setView(input)
                    .setCancelable(true)
                    .setTitle(R.string.modify_video_title)
                    .create();
            mInputDialog.setCanceledOnTouchOutside(true);
        }
        mInputDialog.show();
    }

    private void removeSelectedVideo() {
        ApiHelper.getInstance().videoRemove(mSelectVideo.getVid(),
                new MyRequestCallBack<String>() {
                    @Override
                    public void onSuccess(String result) {
                        SingleToast.show(getApplicationContext(), R.string.msg_remove_success);
                        mVideos.remove(mSelectVideo);
                        mVideoAdapter.notifyDataSetChanged();
                        onRefreshComplete(mVideos.size());
                        User user = EVApplication.getUser();
                        user.setVideo_count(user.getVideo_count() - 1);
                    }

                    @Override
                    public void onError(String errorInfo) {
                        super.onError(errorInfo);
                        SingleToast.show(getApplicationContext(), R.string.msg_remove_failed);
                    }

                    @Override
                    public void onFailure(String msg) {
                        RequestUtil.handleRequestFailed(msg);
                        SingleToast.show(getApplicationContext(), R.string.msg_remove_failed);
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case REQUEST_CODE_IMAGE:
                    mCoverThumbFile = Utils.startPhotoZoom(this, data.getData(),
                            COVER_THUMB_WIDTH, COVER_THUMB_HEIGHT, REQUEST_CODE_RESULT);
                    break;
                case REQUEST_CODE_CAMERA:
                    if (Environment.getExternalStorageState()
                            .equals(Environment.MEDIA_MOUNTED)) {
                        File tempFile = new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME);
                        mCoverThumbFile = Utils.startPhotoZoom(this, Uri.fromFile(tempFile),
                                COVER_THUMB_WIDTH, COVER_THUMB_HEIGHT, REQUEST_CODE_RESULT);
                    } else {
                        SingleToast.show(this, getResources().getString(R.string.msg_alert_no_sd_card));
                    }
                    break;
                case REQUEST_CODE_RESULT:
                    if (mCoverThumbFile == null || !mCoverThumbFile.exists()) {
                        return;
                    }
                    AsyncTask uploadTask = new UploadThumbAsyncTask();
                    String uploadUrl = ApiConstant.UPLOAD_VIDEO_LOGO
                            + "sessionid=" + Preferences.getInstance(this).getSessionId()
                            + "&vid=" + mSelectVideo.getVid()
                            + "&file=" + mCoverThumbFile.getName();
                    uploadTask
                            .execute(uploadUrl, BitmapFactory.decodeFile(mCoverThumbFile.getAbsolutePath()));
                    mSelectVideo.setThumb(mCoverThumbFile.getAbsolutePath());
                    mVideoAdapter.notifyDataSetChanged();
                    break;
            }
        }
    }
}
