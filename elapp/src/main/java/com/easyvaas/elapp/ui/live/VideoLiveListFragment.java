package com.easyvaas.elapp.ui.live;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easyvaas.common.widget.RoundImageView;
import com.easyvaas.elapp.bean.video.RecommendVideoListModel;
import com.easyvaas.elapp.bean.video.VideoEntity;
import com.easyvaas.elapp.net.HooviewApiHelper;
import com.easyvaas.elapp.net.MyRequestCallBack;
import com.easyvaas.elapp.ui.base.BaseListRcvFragment;
import com.easyvaas.elapp.utils.DateTimeUtil;
import com.easyvaas.elapp.utils.Logger;
import com.easyvaas.elapp.utils.Utils;
import com.hooview.app.R;

import java.util.ArrayList;
import java.util.List;

import static com.alibaba.sdk.android.feedback.impl.FeedbackAPI.mContext;


public class VideoLiveListFragment extends BaseListRcvFragment {
    private static final String TAG = "VideoLiveListFragment";
    private VideoAdapter mMyAdapter;
    private RecommendVideoListModel recommendVideoListModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void iniView(View view) {
        mRecyclerView.setAdapter(mMyAdapter = new VideoAdapter());
        recommendVideoListModel = new RecommendVideoListModel();
    }

    @Override
    public void onStart() {
        super.onStart();
        loadData(false);
    }

    @Override
    protected void loadData(boolean isLoadMore) {
        super.loadData(isLoadMore);
        loadVideoList(isLoadMore);
    }

    public void loadVideoList(final boolean isLoadMore) {
        HooviewApiHelper.getInstance().getRecommendVideoList(count + "", mNextPageIndex + "", new MyRequestCallBack<RecommendVideoListModel>() {
            @Override
            public void onSuccess(RecommendVideoListModel result) {
                if (result != null) {
                    mNextPageIndex = result.getNext();
                    if(isLoadMore){
                        appendNews(result);
                    }else{
                        setImportantNewsModel(result);
                    }
                    mMyAdapter.notifyDataSetChanged();
                    onRefreshComplete(result == null ? 0 : result.getCount());
                }
            }

            @Override
            public void onFailure(String msg) {
                onRefreshComplete(0);
            }

            @Override
            public void onError(String errorInfo) {
                super.onError(errorInfo);
                onRefreshComplete(0);
            }
        });
    }

    public void appendNews(RecommendVideoListModel importantNewsModel) {
        if (recommendVideoListModel == null) {
            recommendVideoListModel = importantNewsModel;
        } else {
            recommendVideoListModel.getHotrecommend().addAll(importantNewsModel.getHotrecommend());
            recommendVideoListModel.getRecommend().addAll(importantNewsModel.getRecommend());
        }
    }

    public void setImportantNewsModel(RecommendVideoListModel importantNewsModel) {
        this.recommendVideoListModel = importantNewsModel;
    }


    private class VideoAdapter extends RecyclerView.Adapter {
        private int ITEM_VIEW_HEADER = 1;
        private int ITEM_VIEW_VIDEO = 2;


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder viewHolder;
            if (viewType == ITEM_VIEW_HEADER) {
                viewHolder = new HeaderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_live_header, parent, false));
            } else {
                viewHolder = new VideoItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_live_video, parent, false));
            }
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            if (holder instanceof HeaderViewHolder) {
                ((HeaderViewHolder) holder).setRecommendList(recommendVideoListModel.getHotrecommend());
            } else if (holder instanceof VideoItemViewHolder) {
                ((VideoItemViewHolder) holder).setVideo(recommendVideoListModel.getRecommend().get(position - 1));
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PlayerActivity.start(getActivity(),
                                recommendVideoListModel.getRecommend().get(position - 1).getVid(),
                                recommendVideoListModel.getRecommend().get(position - 1).getLiving(),
                                recommendVideoListModel.getRecommend().get(position - 1).getMode(),
                                recommendVideoListModel.getRecommend().get(position - 1).getPermission());
                    }
                });
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return ITEM_VIEW_HEADER;
            } else {
                return ITEM_VIEW_VIDEO;
            }
        }

        @Override
        public int getItemCount() {
            return 1 + (recommendVideoListModel != null && recommendVideoListModel.getRecommend() != null ? recommendVideoListModel.getRecommend().size() : 0);
        }
    }

    @Override
    public void onRefresh() {
        super.onRefresh();
        loadVideoList(false);
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {
        private RecyclerView mRvHotList;
        public HotVideoAdapter mAdapter;
        private LinearLayout mLlHot;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            mRvHotList = (RecyclerView) itemView.findViewById(R.id.rv_hot);
            mAdapter = new HotVideoAdapter();
            LinearLayoutManager l = new LinearLayoutManager(getContext());
            l.setOrientation(LinearLayoutManager.HORIZONTAL);
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(),
                    DividerItemDecoration.HORIZONTAL);
            dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.shape_hot_recommend_divider));
            mRvHotList.addItemDecoration(dividerItemDecoration);
            mRvHotList.setLayoutManager(l);
            mRvHotList.setAdapter(mAdapter);
            mLlHot = (LinearLayout) itemView.findViewById(R.id.ll_hot);
        }

        public void setRecommendList(List<VideoEntity> list) {
            if (list == null || list.size() == 0) {
                mLlHot.setVisibility(View.GONE);
            } else {
                mLlHot.setVisibility(View.VISIBLE);
                mAdapter.setVideoList(list);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    private class VideoItemViewHolder extends RecyclerView.ViewHolder {
        private FrameLayout mFlCover;
        private ImageView mIvCover;
        private ImageView mIvPlayback;
        private TextView mTvTagLiving;
        private TextView mTvTitle;
        private TextView mTvName;
        private TextView mTvWatchCount;
        private VideoEntity videoEntity;

        public VideoItemViewHolder(View itemView) {
            super(itemView);
            mFlCover = (FrameLayout) itemView.findViewById(R.id.fl_cover);
            mIvCover = (ImageView) itemView.findViewById(R.id.iv_cover);
            mIvPlayback = (ImageView) itemView.findViewById(R.id.iv_tag_payback);
            mTvTagLiving = (TextView) itemView.findViewById(R.id.tv_tag_living);
            mTvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            mTvName = (TextView) itemView.findViewById(R.id.tv_name);
            mTvWatchCount = (TextView) itemView.findViewById(R.id.tv_watch_count);
        }

        public void setVideo(VideoEntity videoEntity) {
            if (videoEntity != null) {
                this.videoEntity = videoEntity;
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
            }
        }

    }

    private class HotVideoAdapter extends RecyclerView.Adapter {
        private List<VideoEntity> hotVideoList;

        public HotVideoAdapter() {
            hotVideoList = new ArrayList<>();
        }

        public void setVideoList(List<VideoEntity> hotVideoList) {
            this.hotVideoList = hotVideoList;
            Logger.d(TAG, "HotVideoAdapter setVideoList: " + (hotVideoList == null ? 0 : hotVideoList.size()));
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Logger.d(TAG, "HotVideoAdapter onCreateViewHolder: ");

            return new HotVideoViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hot_video, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            Logger.d(TAG, "HotVideoAdapter onBindViewHolder: ");

            HotVideoViewHolder hotVideoViewHolder = (HotVideoViewHolder) holder;
            hotVideoViewHolder.setVideo(recommendVideoListModel.getHotrecommend().get(position));
            hotVideoViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PlayerActivity.start(getContext(), hotVideoList.get(position).getVid(), hotVideoList.get(position).getLiving(), hotVideoList.get(position).getMode(),hotVideoList.get(position).getPermission());
                }
            });
        }

        @Override
        public int getItemCount() {
            Logger.d(TAG, "HotVideoAdapter getItemCount: " + (hotVideoList == null ? 0 : hotVideoList.size()));
            return hotVideoList == null ? 0 : hotVideoList.size();
        }
    }

    private class HotVideoViewHolder extends RecyclerView.ViewHolder {
        TextView mTvTitle;
        TextView mTvTime;
        RoundImageView mRivThumb;

        HotVideoViewHolder(View itemView) {
            super(itemView);
            mTvTitle = (TextView) itemView.findViewById(R.id.tv_hot_title);
            mTvTime = (TextView) itemView.findViewById(R.id.tv_hot_time);
            mRivThumb = (RoundImageView) itemView.findViewById(R.id.riv_thumb);
        }

        public void setVideo(VideoEntity video) {
            if (video != null) {
                mTvTitle.setText(video.getTitle());
                Utils.showImage(video.getThumb(), R.drawable.account_bitmap_list, mRivThumb);
                mTvTime.setText(DateTimeUtil.getLiveTime(mContext,video.getLive_start_time())+getString(R.string.update));
            }
        }
    }

}
