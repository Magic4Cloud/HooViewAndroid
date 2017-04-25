package com.easyvaas.elapp.adapter.live;

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

import static android.R.attr.mode;

/**
 * Date    2017/4/25
 * Author  xiaomao
 * adapter: 视频直播
 */

public class LiveVideoListAdapter extends MyBaseAdapter<VideoEntity> {

    private static final int TYPE_HOT = 1;
    private static final int TYPE_VIDEO = 2;
    private List<VideoEntity> mHotList;
    private boolean mHasHot = false;

    public LiveVideoListAdapter(List data) {
        super(data);
    }

    @Override
    protected int getItemViewByType(int position) {
        if (position == 0 && mHasHot) {
            return TYPE_HOT;
        }
        return TYPE_VIDEO;
    }

    @Override
    protected BaseViewHolder OnCreateViewByHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        switch (viewType) {
            case TYPE_HOT:
                return new HotViewHolder(inflater.inflate(R.layout.item_live_video_hot, null));
            case TYPE_VIDEO:
                return new VideoViewHolder(inflater.inflate(R.layout.item_video_living, null));
        }
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
    protected void convert(BaseViewHolder helper, VideoEntity item) {
        int position = helper.getLayoutPosition();
        if (position == 0 && mHasHot && helper instanceof HotViewHolder) {
            ((HotViewHolder)helper).setHot(mHotList);
        } else if (helper instanceof VideoViewHolder) {
            if (mHasHot) {
                item = mData.get(position - 1 < 0 ? 0 : position - 1);
            }
            ((VideoViewHolder)helper).setModel(item);
        }
    }

    public void setHotList(List<VideoEntity> list) {
        if (list != null && list.size() > 0) {
            mHasHot = true;
            mHotList = list;
            notifyItemChanged(0);
        }
    }

    class HotViewHolder extends BaseViewHolder {

        @BindView(R.id.video_hot)
        RecyclerView mHotRecyclerView;
        HotVideoAdapter mHotAdapter;

        public HotViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        void setHot(List<VideoEntity> list) {
            mHotAdapter = new HotVideoAdapter(list);
            mHotRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, OrientationHelper.HORIZONTAL, false));
            mHotRecyclerView.setAdapter(mHotAdapter);
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

        public VideoViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        void setModel(final VideoEntity videoEntity) {
            if (videoEntity != null) {
                // cover
                Utils.showNewsImage(videoEntity.getThumb(), mCoverIv);
                // hot // TODO: 2017/4/20
                mHotIv.setVisibility(View.GONE);
                // watch_count
                mWatchCountTv.setText(mContext.getString(R.string.watch_count, NumberUtil.format(videoEntity.getWatch_count())));
                // title
                mTitleTv.setText(videoEntity.getTitle());
                // name
                mNameTv.setText(videoEntity.getNickname());
                // operator 视频类型（0，回放；1，直播中；2，精品）
                // time
                if (mode == 1) {
                    mTimeTv.setVisibility(View.GONE);
                } else {
                    mTimeTv.setVisibility(View.VISIBLE);
                    mTimeTv.setText(DateTimeUtil.getNewsTime(mContext, videoEntity.getLive_start_time()));
                }
                final int mode = videoEntity.getMode();
                switch (mode) {
                    case 0:
                        mOperatorTv.setText("回放");
                        mOperatorTv.setBackgroundColor(mContext.getResources().getColor(R.color.video_living_playback));
                        break;
                    case 1:
                        mOperatorTv.setText("直播中");
                        mOperatorTv.setBackgroundColor(mContext.getResources().getColor(R.color.video_living_living));
                        break;
                    case 2:
                        mOperatorTv.setText("精品");
                        mOperatorTv.setBackgroundColor(mContext.getResources().getColor(R.color.video_living_good));
                        break;
                }
                // pay 权限（0，Published;1，Shared;2，Personal;3，AllFriends;4，AllowList;5，ForbidList;6，Password;7，PayLive
                int permission = videoEntity.getPermission();
                if (permission == 7) {
                    mPayCv.setVisibility(View.VISIBLE);
                } else {
                    mPayCv.setVisibility(View.GONE);
                }
                // click
                mRootView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PlayerActivity.start(mContext, videoEntity.getVid(), videoEntity.getLiving(), videoEntity.getMode());
                    }
                });
            }
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
                ((HotVideoViewHolder)helper).setHotItem(item);
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
                    // hot // TODO: 2017/4/25
                    mHotHotIv.setVisibility(View.GONE);
                    // watch count
                    mHotWatchCountTv.setText(mContext.getString(R.string.watch_count, NumberUtil.format(videoEntity.getWatch_count())));
                    // title
                    mHotTitleTv.setText(videoEntity.getTitle());
                    // click
                    mHotRootView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            PlayerActivity.start(mContext, videoEntity.getVid(), videoEntity.getLiving(), videoEntity.getMode());
                        }
                    });
                }
            }
        }
    }

}
