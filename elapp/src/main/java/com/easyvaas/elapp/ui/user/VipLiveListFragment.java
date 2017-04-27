package com.easyvaas.elapp.ui.user;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.bean.imageTextLive.CheckImageTextLiveModel;
import com.easyvaas.elapp.bean.user.UserInfoModel;
import com.easyvaas.elapp.bean.video.TextLiveListModel;
import com.easyvaas.elapp.bean.video.VideoEntity;
import com.easyvaas.elapp.bean.video.VideoEntityArray;
import com.easyvaas.elapp.db.Preferences;
import com.easyvaas.elapp.net.ApiConstant;
import com.easyvaas.elapp.net.ApiHelper;
import com.easyvaas.elapp.net.HooviewApiHelper;
import com.easyvaas.elapp.net.MyRequestCallBack;
import com.easyvaas.elapp.net.RequestUtil;
import com.easyvaas.elapp.ui.base.BaseListRcvFragment;
import com.easyvaas.elapp.ui.live.ImageTextLiveActivity;
import com.easyvaas.elapp.ui.live.MyImageTextLiveRoomActivity;
import com.easyvaas.elapp.ui.live.PlayerActivity;
import com.easyvaas.elapp.utils.Logger;
import com.easyvaas.elapp.utils.Utils;
import com.easyvaas.elapp.utils.ViewUtil;
import com.hooview.app.R;

import java.text.DecimalFormat;
import java.util.LinkedList;

import static com.alibaba.sdk.android.feedback.impl.FeedbackAPI.mContext;


public class VipLiveListFragment extends BaseListRcvFragment {
    protected int mNextPageIndex;
    private MyAdapter mMyAdapter;
    public static final String EXTRA_USER = "extra_user";
    private UserInfoModel mUserInfoModel;
    private LinkedList data = new LinkedList();

    public static VipLiveListFragment newInstance(UserInfoModel userInfoModel) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_USER, userInfoModel);
        VipLiveListFragment fragment = new VipLiveListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserInfoModel = (UserInfoModel) getArguments().getSerializable(EXTRA_USER);
    }

    @Override
    public void iniView(View view) {
        mRecyclerView.setAdapter(mMyAdapter = new MyAdapter());
        loadData(false);
    }

    @Override
    protected void loadData(boolean isLoadMore) {
        super.loadData(isLoadMore);
        if (!isLoadMore) {
            data.clear();
            checkoutImageTextLiveRoom();
        }
        if (isLoadMore) {
            getVideoList(true);
        }

    }

    private void checkoutImageTextLiveRoom() {
        HooviewApiHelper.getInstance().checkImageTextLiveRoom(mUserInfoModel.getName(), mUserInfoModel.getNickname(), mUserInfoModel.getImuser(), new MyRequestCallBack<CheckImageTextLiveModel>() {
            @Override
            public void onSuccess(CheckImageTextLiveModel result) {
                if (result != null) {
                    if (data.size() > 0 && data.get(0) instanceof CheckImageTextLiveModel) {
                        data.removeFirst();
                    }
                    data.addFirst(result);
                    mMyAdapter.notifyDataSetChanged();
                    getVideoList(false);
                }
            }

            @Override
            public void onFailure(String msg) {
            }

            @Override
            public void onError(String errorInfo) {
                super.onError(errorInfo);
            }
        });
    }

    public void getVideoList(final boolean isLoadMore) {
        int pageIndex =
                isLoadMore && mNextPageIndex > 0 ? mNextPageIndex : ApiConstant.DEFAULT_FIRST_PAGE_INDEX;
        ApiHelper.getInstance().getUserVideoList(mUserInfoModel.getName(), pageIndex,
                ApiConstant.DEFAULT_PAGE_SIZE, new MyRequestCallBack<VideoEntityArray>() {
                    @Override
                    public void onSuccess(VideoEntityArray result) {
                        Logger.d(VideoListActivity.class, "Result: " + result);
                        if (result != null && result.getVideos().size() > 0) {
                            data.addAll(result.getVideos());
                            mNextPageIndex = result.getNext();
                            onRefreshComplete(result == null ? 0 : result.getCount());
                        }

                        mMyAdapter.notifyDataSetChanged();
                        onRefreshComplete(result == null ? 0 : result.getCount());
                    }

                    @Override
                    public void onError(String errorInfo) {
                        super.onError(errorInfo);
//                            onRefreshComplete(0);
                    }

                    @Override
                    public void onFailure(String msg) {
                        RequestUtil.handleRequestFailed(msg);
                        onRequestFailed(msg);
                    }
                });
    }

    public class MyAdapter extends RecyclerView.Adapter {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder viewHolder;
            if (viewType == 1) {
                viewHolder = new ImageTextLiveViewHolder(LayoutInflater.from(getContext()).inflate(R.layout.item_vip_image_live, parent, false));
            } else {
                viewHolder = new VideoItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_live_video, parent, false));
            }
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof ImageTextLiveViewHolder) {
                ((ImageTextLiveViewHolder) holder).setModel((CheckImageTextLiveModel) data.get(position));
            } else if (holder instanceof VideoItemViewHolder) {
                ((VideoItemViewHolder) holder).setVideo((VideoEntity) data.get(position));
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (data.get(position) instanceof CheckImageTextLiveModel) {
                return 1;
            } else {
                return 2;
            }
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    public class ImageTextLiveViewHolder extends RecyclerView.ViewHolder {
        private TextView mTvName;
        private TextView mTvWatchCount;
        private LinearLayout mLLTagContainer;
        private RelativeLayout myLiveRoomRl;

        public ImageTextLiveViewHolder(View view) {
            super(view);
            mTvName = (TextView) view.findViewById(R.id.tv_name);
            mTvWatchCount = (TextView) view.findViewById(R.id.tv_watch_count);
            mLLTagContainer = (LinearLayout) view.findViewById(R.id.ll_tag_container);
            myLiveRoomRl = (RelativeLayout) view.findViewById(R.id.my_live_room_rl);
            myLiveRoomRl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckImageTextLiveModel model = (CheckImageTextLiveModel) myLiveRoomRl.getTag();
                    if (model == null) return;
                    if (Preferences.getInstance(v.getContext()).isLogin() && EVApplication.isLogin() && EVApplication.getUser() != null && model.getData().getOwnerid().equals(EVApplication.getUser().getName()))
                        MyImageTextLiveRoomActivity.start(getContext(), (CheckImageTextLiveModel) myLiveRoomRl.getTag());
                    else {
                        TextLiveListModel.StreamsEntity streamsEntity = new TextLiveListModel.StreamsEntity();
                        streamsEntity.setName(model.getData().getName());
                        streamsEntity.setViewcount(model.getData().getViewcount());
                        streamsEntity.setOwnerid(model.getData().getOwnerid());
                        streamsEntity.setId(model.getData().getId());
                        streamsEntity.setUserEntity(mUserInfoModel);
                        ImageTextLiveActivity.start(getContext(), streamsEntity);
                    }

                }
            });
        }

        public void setModel(CheckImageTextLiveModel checkImageTextLiveModel) {
            if (mUserInfoModel != null) {
                mTvName.setText(checkImageTextLiveModel.getData().getName());
                DecimalFormat df2 = new DecimalFormat("###,###");
                mTvWatchCount.setText(getString(R.string.image_live_room_fans, df2.format(checkImageTextLiveModel.getData().getViewcount())));
                if (mUserInfoModel.getTags() != null) {
                    mLLTagContainer.removeAllViews();
                    for (int i = 0; i < mUserInfoModel.getTags().size(); i++) {
                        UserInfoModel.TagsEntity tagsEntity = mUserInfoModel.getTags().get(i);
                        TextView textView = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.layout_use_tag, null);
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        layoutParams.rightMargin = (int) ViewUtil.dp2Px(getContext(), 10);
                        mLLTagContainer.addView(textView, layoutParams);
                        textView.setText(tagsEntity.getName());
                    }
                }
                checkImageTextLiveModel.getData().setOwnerid(mUserInfoModel.getName());
                myLiveRoomRl.setTag(checkImageTextLiveModel);
            }
        }
    }

    private class VideoItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView mIvCover;
        private ImageView mIvPlayback;
        private TextView mTvTagLiving;
        private TextView mTvTitle;
        private TextView mTvName;
        private TextView mTvWatchCount;

        public VideoItemViewHolder(View itemView) {
            super(itemView);
            mIvCover = (ImageView) itemView.findViewById(R.id.iv_cover);
            mIvPlayback = (ImageView) itemView.findViewById(R.id.iv_tag_payback);
            mTvTagLiving = (TextView) itemView.findViewById(R.id.tv_tag_living);
            mTvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            mTvName = (TextView) itemView.findViewById(R.id.tv_name);
            mTvWatchCount = (TextView) itemView.findViewById(R.id.tv_watch_count);
        }

        public void setVideo(final VideoEntity videoEntity) {
            if (videoEntity != null) {
                mTvTitle.setText(videoEntity.getTitle());
                mTvName.setText(videoEntity.getNickname());
                mTvWatchCount.setText(mContext.getString(R.string.watch_count, videoEntity.getWatch_count() + ""));
                Utils.showImage(videoEntity.getThumb(), R.drawable.account_bitmap_list, mIvCover);
                if (videoEntity.getLiving() == 1) {
                    mTvTagLiving.setVisibility(View.VISIBLE);
                    mIvPlayback.setVisibility(View.GONE);
                } else {
                    mTvTagLiving.setVisibility(View.GONE);
                    mIvPlayback.setVisibility(View.VISIBLE);
                }
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PlayerActivity.start(getActivity(), videoEntity.getVid(), videoEntity.getLiving(), videoEntity.getMode(),videoEntity.getPermission());
                    }
                });
            }
        }

    }

}
