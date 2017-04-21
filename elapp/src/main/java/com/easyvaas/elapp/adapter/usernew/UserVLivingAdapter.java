package com.easyvaas.elapp.adapter.usernew;

import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.bean.imageTextLive.CheckImageTextLiveModel;
import com.easyvaas.elapp.bean.imageTextLive.ImageTextLiveRoomModel;
import com.easyvaas.elapp.bean.user.UserInfoModel;
import com.easyvaas.elapp.bean.video.TextLiveListModel;
import com.easyvaas.elapp.bean.video.VideoEntity;
import com.easyvaas.elapp.db.Preferences;
import com.easyvaas.elapp.ui.base.mybase.MyBaseAdapter;
import com.easyvaas.elapp.ui.live.ImageTextLiveActivity;
import com.easyvaas.elapp.ui.live.MyImageTextLiveRoomActivity;
import com.easyvaas.elapp.ui.live.PlayerActivity;
import com.easyvaas.elapp.utils.DateTimeUtil;
import com.easyvaas.elapp.utils.NumberUtil;
import com.easyvaas.elapp.utils.Utils;
import com.hooview.app.R;

import java.text.DecimalFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.R.attr.mode;

/**
 * Date    2017/4/20
 * Author  xiaomao
 * adapter: 我的发布---直播
 */

public class UserVLivingAdapter extends MyBaseAdapter<VideoEntity> {

    private static final int TYPE_IMAGE_TEXT = 1;
    private static final int TYPE_VIDEO = 2;
    private boolean mHasHeader = false;
    private ImageTextLiveRoomModel mImageTextModel;

    public UserVLivingAdapter(List<VideoEntity> data) {
        super(data);
    }


    public void showHeader(boolean showHeader) {
        mHasHeader = showHeader;
    }

    @Override
    protected int getItemViewByType(int position) {
        if (mHasHeader && position == 0) {
            return TYPE_IMAGE_TEXT;
        } else {
            return TYPE_VIDEO;
        }
    }

    @Override
    public int getItemCount() {
        int count = mHasHeader ? 1 : 0;
        if (mData != null) {
            count = count + mData.size();
        }
        return count;
    }

    @Override
    protected BaseViewHolder OnCreateViewByHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if (TYPE_IMAGE_TEXT == viewType) {
            return new ImageTextViewHolder(inflater.inflate(R.layout.item_image_text_header, null));
        } else if (TYPE_VIDEO == viewType) {
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
        if (position == 0 && mHasHeader && helper instanceof ImageTextViewHolder) {
            ((ImageTextViewHolder) helper).setModel(mImageTextModel);
        } else if (helper instanceof VideoViewHolder) {
            if (mHasHeader) {
                item = mData.get(position - 1 < 0 ? 0 : position - 1);
            }
            ((VideoViewHolder) helper).setModel(item);
        }
    }

    /**
     * 设置图文直播数据
     */
    public void setHeaderModel(ImageTextLiveRoomModel imageTextModel) {
        if (mHasHeader) {
            mImageTextModel = imageTextModel;
            notifyItemChanged(0);
        }
    }

    class ImageTextViewHolder extends BaseViewHolder {

        @BindView(R.id.item_image_text)
        View mRootView;
        @BindView(R.id.image_text_follow)
        TextView mFollowTv;
        @BindView(R.id.image_text_name)
        TextView mNameTv;
        @BindView(R.id.image_text_tag)
        TextView mTagTv;
        @BindView(R.id.image_text_old)
        TextView mOldTv;

        public ImageTextViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        void setModel(final ImageTextLiveRoomModel model) {
            if (model != null) {
                final CheckImageTextLiveModel checkImageTextLiveModel = new CheckImageTextLiveModel();
                checkImageTextLiveModel.setData(model);
                // follow
                DecimalFormat df2 = new DecimalFormat("###,###");
                mFollowTv.setText(mContext.getString(R.string.image_text_follow_count, df2.format(model.getViewcount())));
                // name
                mNameTv.setText(model.getName());
                // tag
                List<UserInfoModel.TagsEntity> tags = model.getTags();
                if (tags != null && tags.size() > 0) {
                    String tag = "";
                    for (int i = 0; i < tags.size(); i++) {
                        UserInfoModel.TagsEntity entity = tags.get(i);
                        if (entity == null) {
                            continue;
                        }
                        if (i == tags.size() - 1) {
                            tag += entity.getName();
                        } else {
                            tag += entity.getName() + "，";
                        }
                    }
                    mTagTv.setText(tag);
                }
                // click
                mRootView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (Preferences.getInstance(mContext).isLogin() && EVApplication.isLogin() && EVApplication.getUser() != null && model.getOwnerid().equals(EVApplication.getUser().getName()))
                            MyImageTextLiveRoomActivity.start(mContext, checkImageTextLiveModel);
                        else {
                            TextLiveListModel.StreamsEntity streamsEntity = new TextLiveListModel.StreamsEntity();
                            streamsEntity.setName(model.getName());
                            streamsEntity.setViewcount(model.getViewcount());
                            streamsEntity.setOwnerid(model.getOwnerid());
                            streamsEntity.setId(model.getId());
                            streamsEntity.setUserEntity(null);// TODO: 2017/4/20
                            ImageTextLiveActivity.start(mContext, streamsEntity);
                        }
                    }
                });
            }
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


}
