package com.easyvaas.elapp.adapter.live;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.easyvaas.elapp.bean.video.VideoEntity;
import com.easyvaas.elapp.ui.base.mybase.MyBaseAdapter;
import com.easyvaas.elapp.ui.live.PlayerActivity;
import com.easyvaas.elapp.utils.DateTimeUtil;
import com.easyvaas.elapp.utils.NumberUtil;
import com.easyvaas.elapp.utils.Utils;
import com.hooview.app.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Date    2017/4/25
 * Author  xiaomao
 * adapter: 视频直播
 */

public class LiveVideoListAdapter extends MyBaseAdapter<VideoEntity> {

    private boolean mHasHot = false;
    private HotViewHolder mHeaderHolder;

    public LiveVideoListAdapter(List data) {
        super(data);
    }

    @Override
    protected int getItemViewByType(int position) {
        return 0;
    }

    @Override
    protected BaseViewHolder OnCreateViewByHolder(ViewGroup parent, int viewType) {
        return new VideoViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_video_living, null));
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
    protected void convert(BaseViewHolder helper, VideoEntity item) {
        if (helper instanceof VideoViewHolder) {
            ((VideoViewHolder) helper).setModel(item, helper.getLayoutPosition());
        }
    }

    class VideoViewHolder extends BaseViewHolder {

        @BindView(R.id.item_video_living)
        View mRootView;
        @BindView(R.id.iv_cover)
        ImageView mCoverIv;
        @BindView(R.id.iv_hot)
        ImageView mHotIv;
        @BindView(R.id.tv_watch_count)
        TextView mWatchCountTv;
        @BindView(R.id.tv_title)
        TextView mTitleTv;
        @BindView(R.id.tv_name)
        TextView mNameTv;
        @BindView(R.id.tv_time)
        TextView mTimeTv;
        @BindView(R.id.cv_operator)
        CardView mOperatorCv;
        @BindView(R.id.tv_operator)
        TextView mOperatorTv;
        @BindView(R.id.cv_pay)
        CardView mPayCv;
        @BindView(R.id.tv_pay)
        TextView mPayTv;
        @BindView(R.id.view_divider)
        View mDivider;

        public VideoViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        void setModel(final VideoEntity videoEntity, int position) {
            if (videoEntity != null) {
                // cover
                Utils.showNewsImage(videoEntity.getThumb(), mCoverIv);
                // hot
                if (videoEntity.getWatch_count() > 10000) {
                    mHotIv.setVisibility(View.VISIBLE);
                } else {
                    mHotIv.setVisibility(View.GONE);
                }
                // watch_count
                mWatchCountTv.setText(NumberUtil.formatLive(videoEntity.getWatch_count()));
                // title
                mTitleTv.setText(videoEntity.getTitle());
                // name
                mNameTv.setText(videoEntity.getNickname());
                // operator 视频类型（0，回放；1，直播中；2，精品）
                int mode = videoEntity.getMode();
                // 0，回放；1，直播
                int living = videoEntity.getLiving();
                // time
                if (living == 1) {
                    mTimeTv.setVisibility(View.GONE);
                } else {
                    mTimeTv.setVisibility(View.VISIBLE);
                    mTimeTv.setText(DateTimeUtil.getTimeVideo(videoEntity.getLive_start_time()));
                }
                if (mode == 2) {
                    mOperatorTv.setText("精品");
                    mOperatorTv.setBackgroundColor(mContext.getResources().getColor(R.color.video_living_good));
                } else {
                    if (living == 0) {
                        mOperatorTv.setText("回放");
                        mOperatorTv.setBackgroundColor(mContext.getResources().getColor(R.color.video_living_playback));
                    } else if (living == 1) {
                        mOperatorTv.setText("直播中");
                        mOperatorTv.setBackgroundColor(mContext.getResources().getColor(R.color.video_living_living));
                    }
                }
                // pay 权限（0，Published;1，Shared;2，Personal;3，AllFriends;4，AllowList;5，ForbidList;6，Password;7，PayLive
                int permission = videoEntity.getPermission();
                if (permission == 7) {
                    mPayCv.setVisibility(View.VISIBLE);
                    mPayTv.setText("付费");
                    mPayTv.setBackgroundColor(mContext.getResources().getColor(R.color.video_living_pay));
                } else {
                    mPayCv.setVisibility(View.GONE);
                    // TODO: 2017/5/5  
                }
                // divider
                int size = mData.size();
                if (!mHasHot) {
                    size -= 1;
                }
                if (position == size) {
                    mDivider.setVisibility(View.GONE);
                } else {
                    mDivider.setVisibility(View.VISIBLE);
                }
                // click
                mRootView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PlayerActivity.start(mContext, videoEntity.getVid(), videoEntity.getLiving(), videoEntity.getMode(),videoEntity.getPermission());
                    }
                });
            }
        }
    }

    public void setHotList(Context context, List<VideoEntity> list) {
        if (list != null && list.size() > 0) {
            if (mHeaderHolder == null && !mHasHot) {
                View view = LayoutInflater.from(context).inflate(R.layout.item_live_video_hot, null);
                mHeaderHolder = new HotViewHolder(view);
                addHeaderView(view);
                mHasHot = true;
            }
            mHeaderHolder.setHot(list);
        }
    }

    class HotViewHolder {

        @BindView(R.id.video_hot)
        RecyclerView mHotRecyclerView;
        HotVideoAdapter mHotAdapter;

        public HotViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        void setHot(List<VideoEntity> list) {
            mHotAdapter = new HotVideoAdapter(list);
            mHotRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, OrientationHelper.HORIZONTAL, false));
            mHotRecyclerView.setAdapter(mHotAdapter);
        }

    }

    /**
     * 热门推荐
     */
    class HotVideoAdapter extends MyBaseAdapter<VideoEntity> {
        public HotVideoAdapter(List<VideoEntity> data) {
            super(data);
        }

        /**
         * Implement this method and use the helper to adapt the view to the given item.
         *
         * @param helper A fully initialized helper.
         * @param item   The item that needs to be displayed.
         */
        @Override
        protected void convert(BaseViewHolder helper, VideoEntity item) {
            if (helper instanceof HotVideoViewHolder) {
                ((HotVideoViewHolder) helper).setHotItem(item);
            }
        }

        @Override
        protected int getItemViewByType(int position) {
            return 0;
        }

        @Override
        protected BaseViewHolder OnCreateViewByHolder(ViewGroup parent, int viewType) {
            return new HotVideoViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_live_video_hot_item, null));
        }

        @Override
        protected void initOnItemClickListener() {

        }

        class HotVideoViewHolder extends BaseViewHolder {

            @BindView(R.id.item_hot_video)
            View mHotRootView;
            @BindView(R.id.hot_cover)
            ImageView mHotCoverIv;
            @BindView(R.id.hot_hot)
            ImageView mHotHotIv;
            @BindView(R.id.hot_watch_count)
            TextView mHotWatchCountTv;
            @BindView(R.id.hot_title)
            TextView mHotTitleTv;

            public HotVideoViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }

            void setHotItem(final VideoEntity videoEntity) {
                if (videoEntity != null) {
                    // cover
                    Utils.showNewsImage(videoEntity.getThumb(), mHotCoverIv);
                    // hot
                    if (videoEntity.getWatch_count() > 10000) {
                        mHotHotIv.setVisibility(View.VISIBLE);
                    } else {
                        mHotHotIv.setVisibility(View.GONE);
                    }
                    // watch count
                    mHotWatchCountTv.setText(NumberUtil.formatLive(videoEntity.getWatch_count()));
                    // title
                    mHotTitleTv.setText(videoEntity.getTitle());
                    // click
                    mHotRootView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            PlayerActivity.start(mContext, videoEntity.getVid(), videoEntity.getLiving(), videoEntity.getMode(),videoEntity.getPermission());
                        }
                    });
                }
            }
        }
    }

}
