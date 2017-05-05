package com.easyvaas.elapp.adapter.live;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.easyvaas.common.widget.RoundImageView;
import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.bean.imageTextLive.CheckImageTextLiveModel;
import com.easyvaas.elapp.bean.imageTextLive.ImageTextLiveRoomModel;
import com.easyvaas.elapp.bean.video.TextLiveListModel;
import com.easyvaas.elapp.db.Preferences;
import com.easyvaas.elapp.ui.base.mybase.MyBaseAdapter;
import com.easyvaas.elapp.ui.live.ImageTextLiveActivity;
import com.easyvaas.elapp.ui.live.MyImageTextLiveRoomActivity;
import com.easyvaas.elapp.ui.user.LoginActivity;
import com.easyvaas.elapp.utils.NumberUtil;
import com.easyvaas.elapp.utils.Utils;
import com.hooview.app.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Date    2017/4/26
 * Author  xiaomao
 */

public class LiveImageTextListAdapter extends MyBaseAdapter<TextLiveListModel.StreamsEntity> {

    private boolean mHasHot = false;
    private HotViewHolder mHeaderHolder;

    public LiveImageTextListAdapter(List<TextLiveListModel.StreamsEntity> data) {
        super(data);
    }

    @Override
    protected int getItemViewByType(int position) {
        return 0;
    }

    @Override
    protected BaseViewHolder OnCreateViewByHolder(ViewGroup parent, int viewType) {
        return new ImageTextViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_live_image_text, parent, false));
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
    protected void convert(BaseViewHolder helper, TextLiveListModel.StreamsEntity item) {
        if (helper instanceof ImageTextViewHolder) {
            ((ImageTextViewHolder) helper).setModel(item);
        }
    }

    class ImageTextViewHolder extends BaseViewHolder {

        @BindView(R.id.item_image_text)
        View mRootView;
        @BindView(R.id.image_text_cover)
        RoundImageView mCoverRiv;
        @BindView(R.id.image_text_title)
        TextView mTitleTv;
        @BindView(R.id.image_text_introduce)
        TextView mIntroduceTv;
        @BindView(R.id.image_text_hot)
        ImageView mHotIv;
        @BindView(R.id.image_text_follow_count)
        TextView mFollowCountTv;

        public ImageTextViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        void setModel(final TextLiveListModel.StreamsEntity model) {
            if (model != null) {
                // cover
                Utils.showNewsImage(model.getUserEntity().getLogourl(), mCoverRiv);
                // title
                mTitleTv.setText(model.getUserEntity().getNickname() + mContext.getResources().getString(R.string.image_text_room_postfix));
                // introduce
                mIntroduceTv.setText(model.getUserEntity().getSignature());
                // hot
                if (model.getViewcount() > 10000) {
                    mHotIv.setVisibility(View.VISIBLE);
                } else {
                    mHotIv.setVisibility(View.GONE);
                }
                // follow count
                mFollowCountTv.setText(String.format(mContext.getString(R.string.image_text_watch_count), NumberUtil.format(model.getViewcount())));
                // click
                mRootView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onItemClick(model);
                    }
                });
            }
        }
    }

    /**
     * 点击事件
     */
    private void onItemClick(TextLiveListModel.StreamsEntity model) {
        if (Preferences.getInstance(mContext).isLogin() && EVApplication.isLogin()) {
            if (EVApplication.getUser() != null && model != null && model.getOwnerid() != null
                    && model.getOwnerid().equals(EVApplication.getUser().getName())) {
                CheckImageTextLiveModel checkImageTextLiveModel = new CheckImageTextLiveModel();
                ImageTextLiveRoomModel roomModel = new ImageTextLiveRoomModel();
                roomModel.setId(model.getId());
                roomModel.setName(model.getName());
                roomModel.setOwnerid(model.getOwnerid());
                roomModel.setViewcount(model.getViewcount());
                roomModel.setUserInfo(model.getUserEntity());
                checkImageTextLiveModel.setData(roomModel);
                MyImageTextLiveRoomActivity.start(mContext, checkImageTextLiveModel);
            } else {
                ImageTextLiveActivity.start(mContext, model);
            }
        } else {
            LoginActivity.start(mContext);
        }
    }

    /**
     * 设置头部
     *
     * @param context
     * @param list
     */
    public void setHotList(Context context, List<TextLiveListModel.StreamsEntity> list) {
        /*if (list != null && list.size() > 0) {
            if (mHeaderHolder == null && !mHasHot) {
                View view = LayoutInflater.from(context).inflate(R.layout.item_live_image_text_hot, null);
                mHeaderHolder = new HotViewHolder(view);
                addHeaderView(view);
                mHasHot = true;
            }
            mHeaderHolder.setHotList(list);
        }*/
    }

    class HotViewHolder {

        @BindView(R.id.image_text_hot)
        RecyclerView mHotRecyclerView;
        HotImageTextAdapter mHotAdapter;

        public HotViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        void setHotList(List<TextLiveListModel.StreamsEntity> list) {
            mHotAdapter = new HotImageTextAdapter(list);
            mHotRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, OrientationHelper.HORIZONTAL, false));
            mHotRecyclerView.setAdapter(mHotAdapter);
        }
    }

    class HotImageTextAdapter extends MyBaseAdapter<TextLiveListModel.StreamsEntity> {

        public HotImageTextAdapter(List<TextLiveListModel.StreamsEntity> data) {
            super(data);
        }

        /**
         * Implement this method and use the helper to adapt the view to the given item.
         *
         * @param helper A fully initialized helper.
         * @param item   The item that needs to be displayed.
         */
        @Override
        protected void convert(BaseViewHolder helper, TextLiveListModel.StreamsEntity item) {
            if (helper instanceof ItemViewHolder) {
                ((ItemViewHolder) helper).setHotModel(item);
            }
        }

        @Override
        protected int getItemViewByType(int position) {
            return 0;
        }

        @Override
        protected BaseViewHolder OnCreateViewByHolder(ViewGroup parent, int viewType) {
            return new ItemViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_live_image_text_hot_item, null));
        }

        @Override
        protected void initOnItemClickListener() {

        }

        class ItemViewHolder extends BaseViewHolder {

            @BindView(R.id.item_image_text_hot)
            View mHotRootView;
            @BindView(R.id.image_text_hot_thumb)
            RoundImageView mHotThumbRiv;
            @BindView(R.id.image_text_hot_name)
            TextView mHotNameTv;
            @BindView(R.id.image_text_hot_hot)
            ImageView mHotHotIv;
            @BindView(R.id.image_text_hot_watch_count)
            TextView mHotWatchCountTv;

            public ItemViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }

            void setHotModel(final TextLiveListModel.StreamsEntity model) {
                if (model != null) {
                    // thumb
                    Utils.showNewsImage(model.getUserEntity().getLogourl(), mHotThumbRiv);
                    // name
                    mHotNameTv.setText(model.getUserEntity().getNickname() + mContext.getResources().getString(R.string.image_text_room_postfix));
                    // hot
                    if (model.getViewcount() > 10000) {
                        mHotHotIv.setVisibility(View.VISIBLE);
                    } else {
                        mHotHotIv.setVisibility(View.GONE);
                    }
                    // watch count
                    mHotWatchCountTv.setText(String.format(mContext.getString(R.string.image_text_watch_count), NumberUtil.format(model.getViewcount())));
                    // click
                    mHotRootView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            onItemClick(model);
                        }
                    });
                }
            }
        }
    }
}
