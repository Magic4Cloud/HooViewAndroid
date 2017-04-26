package com.easyvaas.elapp.ui.live;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easyvaas.common.widget.RoundImageView;
import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.bean.imageTextLive.CheckImageTextLiveModel;
import com.easyvaas.elapp.bean.imageTextLive.ImageTextLiveRoomModel;
import com.easyvaas.elapp.bean.user.UserInfoArrayModel;
import com.easyvaas.elapp.bean.user.UserInfoModel;
import com.easyvaas.elapp.bean.video.TextLiveListModel;
import com.easyvaas.elapp.db.Preferences;
import com.easyvaas.elapp.net.ApiHelper;
import com.easyvaas.elapp.net.HooviewApiHelper;
import com.easyvaas.elapp.net.MyRequestCallBack;
import com.easyvaas.elapp.ui.base.BaseListRcvFragment;
import com.easyvaas.elapp.ui.user.LoginActivity;
import com.easyvaas.elapp.utils.Logger;
import com.easyvaas.elapp.utils.StringUtil;
import com.easyvaas.elapp.utils.Utils;
import com.easyvaas.elapp.utils.ViewUtil;
import com.hooview.app.R;

import java.util.ArrayList;
import java.util.List;

public class ImageTextLiveListFragment extends BaseListRcvFragment {
    private static final String TAG = "ImageTextLiveListFragment";
    private MyAdapter mMyAdapter;
    private TextLiveListModel mTextLiveListModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTextLiveListModel = new TextLiveListModel();
    }

    @Override
    public void iniView(View view) {
        mRecyclerView.setAdapter(mMyAdapter = new MyAdapter());
        loadData(false);
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onLoadMore() {
        super.onLoadMore();
    }

    @Override
    protected void loadData(boolean isLoadMore) {
        super.loadData(isLoadMore);
        loadTextLiveList();
    }

    private void loadTextLiveList() {
        HooviewApiHelper.getInstance().getTextLiveList(count, start, new MyRequestCallBack<TextLiveListModel>() {
            @Override
            public void onSuccess(TextLiveListModel result) {
                if (result != null) {
                    getUserInfos(result);
                    onRefreshComplete(result.getCount());
                }
            }

            @Override
            public void onFailure(String msg) {

            }
        });
    }

    private void getUserInfos(final TextLiveListModel textLiveListModel) {
//        mTextLiveListModel = textLiveListModel;
        List<String> names = new ArrayList<>();
        if (textLiveListModel.getHotstreams() != null) {
            for (int i = 0; i < textLiveListModel.getHotstreams().size(); i++) {
                names.add(textLiveListModel.getHotstreams().get(i).getOwnerid());
            }
        }
        if (textLiveListModel.getStreams() != null) {
            for (int i = 0; i < textLiveListModel.getStreams().size(); i++) {
                names.add(textLiveListModel.getStreams().get(i).getOwnerid());
            }
        }
        ApiHelper.getInstance().getUserInfosNew(names, new MyRequestCallBack<UserInfoArrayModel>() {
            @Override
            public void onSuccess(UserInfoArrayModel result) {
                if (result != null && result.getUsers() != null) {
                    for (int i = 0; i < result.getUsers().size(); i++) {
                        if (textLiveListModel.getHotstreams() != null) {
                            if (i < textLiveListModel.getHotstreams().size()) {
                                textLiveListModel.getHotstreams().get(i).setUserEntity(result.getUsers().get(i));
                            } else {
                                textLiveListModel.getStreams().get(i - textLiveListModel.getHotstreams().size()).setUserEntity(result.getUsers().get(i));
                            }
                        } else {
                            textLiveListModel.getStreams().get(i).setUserEntity(result.getUsers().get(i));
                        }
                    }
                    mTextLiveListModel = textLiveListModel;
                    onRefreshComplete(0);
                    mMyAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(String msg) {
                showEmptyView(R.string.has_no_data);
            }

            @Override
            public void onError(String errorInfo) {
                super.onError(errorInfo);
                showEmptyView(R.string.has_no_data);
            }
        });
    }

    private class MyAdapter extends RecyclerView.Adapter {
        private int ITEM_VIEW_HEADER = 1;
        private int ITEM_VIEW_NORMAL = 2;

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder viewHolder;
            if (viewType == ITEM_VIEW_HEADER) {
                viewHolder = new HeaderViewHolder(LayoutInflater.from(getContext()).inflate(R.layout.item_live_image_text_hot, parent, false));
            } else {
                viewHolder = new NormalItemViewHolder(LayoutInflater.from(getContext()).inflate(R.layout.item_live_image_text, parent, false));
            }
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder.getItemViewType() == ITEM_VIEW_HEADER) {
                HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
                headerViewHolder.setHotList(mTextLiveListModel.getHotstreams());
            } else {
                NormalItemViewHolder normalItemViewHolder = (NormalItemViewHolder) holder;
                normalItemViewHolder.setLivModel(mTextLiveListModel.getStreams().get(position - 1));
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return ITEM_VIEW_HEADER;
            } else {
                return ITEM_VIEW_NORMAL;
            }
        }

        @Override
        public int getItemCount() {
            if (mTextLiveListModel == null || mTextLiveListModel.getStreams() == null) {
                return 1;
            } else {
                return 1 + mTextLiveListModel.getStreams().size();
            }
        }
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {
        private RecyclerView mRvHotList;
        public HotVipAdapter mAdapter;
        private LinearLayout mLlHot;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            mRvHotList = (RecyclerView) itemView.findViewById(R.id.rv_hot);
            mAdapter = new HotVipAdapter();
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

        public void setHotList(List<TextLiveListModel.StreamsEntity> list) {
            if (list == null || list.size() == 0) {
                mLlHot.setVisibility(View.GONE);
            } else {
                mLlHot.setVisibility(View.VISIBLE);
                mAdapter.setVideoList(list);
                mAdapter.notifyDataSetChanged();
            }
        }

    }


    public class NormalItemViewHolder extends RecyclerView.ViewHolder {
        private TextView mTvTitle;
        private TextView mTvFollowCount;
        private LinearLayout mLlTagContainer;
        private RoundImageView mRivCover;

        public NormalItemViewHolder(View view) {
            super(view);
            mTvFollowCount = (TextView) view.findViewById(R.id.image_text_follow_count);
            mTvTitle = (TextView) view.findViewById(R.id.image_text_title);
            mLlTagContainer = (LinearLayout) view.findViewById(R.id.ll_tag_container);
            mRivCover = (RoundImageView) view.findViewById(R.id.image_text_cover);
        }

        public void setLivModel(final TextLiveListModel.StreamsEntity model) {
            if (model != null) {
                mTvTitle.setText(model.getName());
                if (model.getUserEntity() == null) {
                    return;
                }
                mTvFollowCount.setText(getString(R.string.image_text_follow_count, model.getUserEntity().getFans_count() + ""));
                Utils.showImage(model.getUserEntity().getLogourl(), R.drawable.somebody, mRivCover);
                mLlTagContainer.removeAllViews();
                if (model.getUserEntity().getTags() != null) {
                    for (int i = 0; i < model.getUserEntity().getTags().size(); i++) {
                        UserInfoModel.TagsEntity tagsEntity = model.getUserEntity().getTags().get(i);
                        TextView textView = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.layout_use_tag, null);
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        layoutParams.rightMargin = (int) ViewUtil.dp2Px(getContext(), 10);
                        mLlTagContainer.addView(textView, layoutParams);
                        textView.setText(tagsEntity.getName());
                    }
                }
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Preferences.getInstance(v.getContext()).isLogin() && EVApplication.isLogin() && EVApplication.getUser() != null && EVApplication.getUser().getName().equals(model.getOwnerid())){
                            CheckImageTextLiveModel liveModel = new CheckImageTextLiveModel();
                            ImageTextLiveRoomModel roomModel = new ImageTextLiveRoomModel();
                            roomModel.setId(model.getId());
                            roomModel.setOwnerid(model.getOwnerid());
                            roomModel.setName(model.getName());
                            roomModel.setViewcount(model.getViewcount());
                            liveModel.setData(roomModel);
                            MyImageTextLiveRoomActivity.start(getContext(), liveModel);
                        } else
                            ImageTextLiveActivity.start(getContext(), model);
                    }
                });
            }
        }
    }

    private class HotVipAdapter extends RecyclerView.Adapter {
        private List<TextLiveListModel.StreamsEntity> hotVideoList;

        public HotVipAdapter() {
            hotVideoList = new ArrayList<>();
        }

        public void setVideoList(List<TextLiveListModel.StreamsEntity> hotVideoList) {
            this.hotVideoList = hotVideoList;
            Logger.d(TAG, "HotVipAdapter setVideoList: " + (hotVideoList == null ? 0 : hotVideoList.size()));
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Logger.d(TAG, "HotVipAdapter onCreateViewHolder: ");
            return new HotVipViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_live_image_text_hot_item, parent, false));
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            Logger.d(TAG, "HotVipAdapter onBindViewHolder: ");
            HotVipViewHolder hotVipViewHolder = (HotVipViewHolder) holder;
            hotVipViewHolder.setHotLive(hotVideoList.get(position));
            hotVipViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (EVApplication.isLogin()) {
                        ImageTextLiveActivity.start(getContext(), hotVideoList.get(position));
                    } else {
                        LoginActivity.start(getContext());
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            Logger.d(TAG, "HotVipAdapter getItemCount: " + (hotVideoList == null ? 0 : hotVideoList.size()));
            return hotVideoList == null ? 0 : hotVideoList.size();
        }
    }

    private class HotVipViewHolder extends RecyclerView.ViewHolder {
        TextView mTvName;
        TextView mTvWatchCount;
        RoundImageView mRivThumb;

        HotVipViewHolder(View itemView) {
            super(itemView);
            mTvName = (TextView) itemView.findViewById(R.id.tv_name);
            mTvWatchCount = (TextView) itemView.findViewById(R.id.tv_watch_count);
            mRivThumb = (RoundImageView) itemView.findViewById(R.id.riv_thumb);
        }

        public void setHotLive(TextLiveListModel.StreamsEntity hotLive) {
            if (hotLive != null) {
                if (hotLive.getUserEntity() == null) {
                    return;
                }
                mTvName.setText(hotLive.getName());
                Utils.showImage(hotLive.getUserEntity().getLogourl(), R.drawable.account_bitmap_list, mRivThumb);
                if (hotLive.getViewcount() >= 10000) {
                    mTvWatchCount.setText(String.format(getString(R.string.image_text_watch_count2), StringUtil.formatTenThousand(hotLive.getViewcount())));
                } else {
                    mTvWatchCount.setText(String.format(getString(R.string.image_text_watch_count), hotLive.getViewcount() + ""));
                }
            }
        }
    }
}
