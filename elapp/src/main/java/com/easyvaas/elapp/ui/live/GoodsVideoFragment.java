package com.easyvaas.elapp.ui.live;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.easyvaas.common.widget.RoundImageView;
import com.easyvaas.elapp.bean.video.GoodsVideoListModel;
import com.easyvaas.elapp.bean.video.VideoEntity;
import com.easyvaas.elapp.net.HooviewApiHelper;
import com.easyvaas.elapp.net.MyRequestCallBack;
import com.easyvaas.elapp.ui.base.BaseListRcvFragment;
import com.easyvaas.elapp.utils.DateTimeUtil;
import com.easyvaas.elapp.utils.Utils;
import com.hooview.app.R;

import java.util.ArrayList;
import java.util.List;

public class GoodsVideoFragment extends BaseListRcvFragment {
    private MyAdapter mMyAdapter;
    private List<VideoEntity> mVideoList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mVideoList = new ArrayList<>();
    }

    @Override
    public void iniView(View view) {
        LinearLayoutManager l = new LinearLayoutManager(getContext());
        l.setOrientation(LinearLayoutManager.VERTICAL);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.shape_goods_video_divider));
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mRecyclerView.setAdapter(mMyAdapter = new MyAdapter());
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

    private void loadVideoList(final boolean isLoadMore) {
        HooviewApiHelper.getInstance().getGoodVideoList(count + "", mNextPageIndex + "", new MyRequestCallBack<GoodsVideoListModel>() {
            @Override
            public void onSuccess(GoodsVideoListModel result) {
                if (result != null && result.getVideos() != null) {
                    mNextPageIndex = result.getNext();
                    if(!isLoadMore){
                        mVideoList.clear();
                    }
                    mVideoList.addAll(result.getVideos());
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

    @Override
    public void onRefresh() {
        super.onRefresh();
        loadVideoList(false);
    }

    public class MyAdapter extends RecyclerView.Adapter {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(getContext(), R.layout.item_goods_video, null);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            MyViewHolder myViewHolder = (MyViewHolder) holder;
            myViewHolder.setVideo(mVideoList.get(position));
            myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PlayerActivity.start(getActivity(), mVideoList.get(position).getVid(), mVideoList.get(position).getLiving(), mVideoList.get(position).getMode());
                }
            });
        }

        @Override
        public int getItemCount() {
            return mVideoList != null ? mVideoList.size() : 0;
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        RoundImageView mIvHeader;
        TextView mTvCompany;
        TextView mTvName;
        TextView mTvWatchCount;
        TextView mTvTitle;
        TextView mTvDuration;
        ImageView mIvOperation;
        ImageView mIvThumb;

        public MyViewHolder(View itemView) {
            super(itemView);
            mIvHeader = (RoundImageView) itemView.findViewById(R.id.iv_header);
            mTvCompany = (TextView) itemView.findViewById(R.id.tv_company);
            mTvName = (TextView) itemView.findViewById(R.id.tv_name);
            mTvWatchCount = (TextView) itemView.findViewById(R.id.tv_watch_count);
            mTvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            mTvDuration = (TextView) itemView.findViewById(R.id.tv_duration);
            mIvOperation = (ImageView) itemView.findViewById(R.id.iv_operation);
            mIvThumb = (ImageView) itemView.findViewById(R.id.iv_thumbnail);
        }

        public void setVideo(VideoEntity video) {
            if (video != null) {
                Utils.showImage(video.getLogourl(), R.drawable.account_bitmap_user, mIvHeader);
                Utils.showImage(video.getThumb(), R.drawable.account_bitmap_list, mIvThumb);
                mTvName.setText(video.getNickname());
                mTvWatchCount.setText(getResources().getString(R.string.watch_count, video.getWatch_count() + ""));
                mTvTitle.setText(video.getTitle());
                mTvDuration.setText(DateTimeUtil.getDurationTime(getContext(), video.getDuration() * 1000));
            }
        }

    }
}
