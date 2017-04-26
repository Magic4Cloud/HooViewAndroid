package com.easyvaas.elapp.adapter.live;

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
import com.easyvaas.elapp.bean.video.TextLiveListModel;
import com.easyvaas.elapp.ui.base.mybase.MyBaseAdapter;
import com.easyvaas.elapp.ui.live.ImageTextLiveActivity;
import com.easyvaas.elapp.ui.user.LoginActivity;
import com.easyvaas.elapp.utils.NumberUtil;
import com.easyvaas.elapp.utils.Utils;
import com.hooview.app.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.tencent.open.utils.Global.getContext;

/**
 * Date    2017/4/26
 * Author  xiaomao
 */

public class LiveImageTextListAdapter extends MyBaseAdapter<TextLiveListModel.StreamsEntity> {

    private static final int TYPE_HOT = 1;
    private static final int TYPE_IMAGE_TEXT = 2;
    private List<TextLiveListModel.StreamsEntity> mHotList;
    private boolean mHasHot = false;

    public LiveImageTextListAdapter(List<TextLiveListModel.StreamsEntity> data) {
        super(data);
    }

    @Override
    protected int getItemViewByType(int position) {
        if (position == 0 && mHasHot) {
            return TYPE_HOT;
        }
        return TYPE_IMAGE_TEXT;
    }

    @Override
    protected BaseViewHolder OnCreateViewByHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        switch (viewType) {
            case TYPE_HOT:
                return new HotViewHolder(inflater.inflate(R.layout.item_live_image_text_hot, null));
            case TYPE_IMAGE_TEXT:
                return new ImageTextViewHolder(inflater.inflate(R.layout.item_live_image_text, null));
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
    protected void convert(BaseViewHolder helper, TextLiveListModel.StreamsEntity item) {
        int position = helper.getLayoutPosition();
        if (position == 0 && mHasHot && helper instanceof HotViewHolder) {
            ((HotViewHolder) helper).setHotList(mHotList);
        } else if (helper instanceof ImageTextViewHolder) {
            if (mHasHot) {
                item = mData.get(position - 1 < 0 ? 0 : position - 1);
            }
            ((ImageTextViewHolder) helper).setModel(item);
        }
    }

    public void setHotList(List<TextLiveListModel.StreamsEntity> list) {
        if (list != null && list.size() > 0) {
            mHasHot = true;
            mHotList = list;
            notifyItemChanged(0);
        }
    }

    class HotViewHolder extends BaseViewHolder {

        @BindView(R.id.image_text_hot)
        RecyclerView mHotRecyclerView;
        HotImageTextAdapter mHotAdapter;

        public HotViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        void setHotList(List<TextLiveListModel.StreamsEntity> list) {
            mHotAdapter = new HotImageTextAdapter(list);
            mHotRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, OrientationHelper.HORIZONTAL, false));
            mHotRecyclerView.setAdapter(mHotAdapter);
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
                mTitleTv.setText(model.getName());
                // introduce
                mIntroduceTv.setText(model.getUserEntity().getSignature());
                // hot // TODO: 2017/4/26
                mHotIv.setVisibility(View.GONE);
                // follow count
                mFollowCountTv.setText(String.format(mContext.getString(R.string.image_text_watch_count), NumberUtil.format(model.getViewcount())));
                // click
                mRootView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (EVApplication.isLogin()) {
                            ImageTextLiveActivity.start(mContext, model);
                        } else {
                            LoginActivity.start(getContext());
                        }
                    }
                });
            }
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
                    mHotNameTv.setText(model.getName());
                    // hot // TODO: 2017/4/26
                    mHotHotIv.setVisibility(View.GONE);
                    // watch count
                    mHotWatchCountTv.setText(String.format(mContext.getString(R.string.image_text_watch_count), NumberUtil.format(model.getViewcount())));
                    // click
                    mHotRootView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (EVApplication.isLogin()) {
                                ImageTextLiveActivity.start(mContext, model);
                            } else {
                                LoginActivity.start(getContext());
                            }
                        }
                    });
                }
            }
        }
    }
}
