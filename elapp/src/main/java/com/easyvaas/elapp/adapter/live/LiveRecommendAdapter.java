package com.easyvaas.elapp.adapter.live;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
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
 * Date    2017/5/03
 * Editor  Misuzu
 * 视频推荐
 */

public class LiveRecommendAdapter extends MyBaseAdapter<VideoEntity> {

    private boolean mHasHeader = false;

    public LiveRecommendAdapter(List<VideoEntity> data) {
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

    @Override
    protected void convert(BaseViewHolder helper, VideoEntity item) {
        if (helper instanceof VideoViewHolder) {
            ((VideoViewHolder) helper).setModel(item);
        }
    }

    /**
     * 加载简介信息
     */
    public void setHeaderData(Context context, String data)
    {
        if (!mHasHeader)
        {
            View headerView = LayoutInflater.from(context).inflate(R.layout.item_video_recommend_header,null);
            TextView textView = (TextView) headerView.findViewById(R.id.video_recommend_introduce);
            if (TextUtils.isEmpty(data))
                textView.setVisibility(View.GONE);
            textView.setText(data);
            addHeaderView(headerView);
            mHasHeader =true;
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
                } else {
                    mPayCv.setVisibility(View.GONE);
                }
                // click
                mRootView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PlayerActivity.start(mContext, videoEntity.getVid(), videoEntity.getLiving(), videoEntity.getMode(), videoEntity.getPermission());
                    }
                });
            }
        }
    }


}
