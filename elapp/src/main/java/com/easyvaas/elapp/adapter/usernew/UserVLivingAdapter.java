package com.easyvaas.elapp.adapter.usernew;

import android.content.Context;
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
import com.easyvaas.elapp.bean.user.UserInfoArrayModel;
import com.easyvaas.elapp.bean.user.UserInfoModel;
import com.easyvaas.elapp.bean.video.TextLiveListModel;
import com.easyvaas.elapp.bean.video.VideoEntity;
import com.easyvaas.elapp.db.Preferences;
import com.easyvaas.elapp.net.ApiHelper;
import com.easyvaas.elapp.net.MyRequestCallBack;
import com.easyvaas.elapp.ui.base.mybase.MyBaseAdapter;
import com.easyvaas.elapp.ui.live.ImageTextLiveActivity;
import com.easyvaas.elapp.ui.live.MyImageTextLiveRoomActivity;
import com.easyvaas.elapp.ui.live.PlayerActivity;
import com.easyvaas.elapp.ui.user.LoginActivity;
import com.easyvaas.elapp.utils.DateTimeUtil;
import com.easyvaas.elapp.utils.NumberUtil;
import com.easyvaas.elapp.utils.SingleToast;
import com.easyvaas.elapp.utils.Utils;
import com.hooview.app.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Date    2017/4/20
 * Author  xiaomao
 * adapter: 我的发布---直播
 */

public class UserVLivingAdapter extends MyBaseAdapter<VideoEntity> {

    private boolean mHasHeader = false;
    private ImageTextLiveRoomModel mImageTextModel;
    private ImageTextViewHolder mHeaderHolder;

    public UserVLivingAdapter(List<VideoEntity> data) {
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
            ((VideoViewHolder) helper).setModel(item);
        }
    }

    /**
     * 设置图文直播数据
     */
    public void setHeaderModel(Context context, ImageTextLiveRoomModel imageTextModel) {
        if (imageTextModel != null) {
            mImageTextModel = imageTextModel;
            if (mHeaderHolder == null && !mHasHeader) {
                View view = LayoutInflater.from(context).inflate(R.layout.item_image_text_header, null);
                mHeaderHolder = new ImageTextViewHolder(context, view);
                addHeaderView(view);
                mHasHeader = true;
            }
            mHeaderHolder.setModel(imageTextModel);
        }
    }

    /**
     * 图文直播头
     */
    class ImageTextViewHolder {

        private Context mContext;
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
        private UserInfoModel mUserInfoModel;

        public ImageTextViewHolder(Context context, View view) {
            mContext = context;
            ButterKnife.bind(this, view);
        }

        void setModel(final ImageTextLiveRoomModel model) {
            if (model != null) {
                final CheckImageTextLiveModel checkImageTextLiveModel = new CheckImageTextLiveModel();
                checkImageTextLiveModel.setData(model);
                // follow
                DecimalFormat df2 = new DecimalFormat("###,###");
                mFollowTv.setText(this.mContext.getString(R.string.user_text_follow_count, df2.format(model.getViewcount())));
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
                        if (Preferences.getInstance(mContext).isLogin() && EVApplication.isLogin()) {
                            if (EVApplication.getUser() != null && model.getOwnerid() != null && model.getOwnerid().equals(EVApplication.getUser().getName())) {
                                MyImageTextLiveRoomActivity.start(mContext, checkImageTextLiveModel);
                            } else {
                                if (mUserInfoModel == null) {
                                    requestUserInfoModel(model);
                                } else {
                                    openImageTextRoom(model);
                                }
                            }
                        } else {
                            LoginActivity.start(mContext);
                        }
                    }
                });
            }
        }

        /**
         * 请求用户信息
         */
        private void requestUserInfoModel(final ImageTextLiveRoomModel model) {
            List<String> ids = new ArrayList<String>();
            ids.add(model.getOwnerid());
            ApiHelper.getInstance().getUserInfosNew(ids, new MyRequestCallBack<UserInfoArrayModel>() {
                @Override
                public void onSuccess(UserInfoArrayModel result) {
                    if (result != null && result.getUsers() != null && result.getUsers().size() == 1) {
                        UserInfoModel infoModel = result.getUsers().get(0);
                        if (infoModel != null) {
                            mUserInfoModel = infoModel;
                            openImageTextRoom(model);
                        } else {
                            SingleToast.show(mContext, "用户信息获取失败");
                        }
                    } else {
                        SingleToast.show(mContext, "用户信息获取失败");
                    }
                }

                @Override
                public void onFailure(String msg) {
                    SingleToast.show(mContext, "用户信息获取失败");
                }

                @Override
                public void onError(String errorInfo) {
                    SingleToast.show(mContext, "用户信息获取失败");
                }
            });
        }

        /**
         * 跳转到图文直播间
         */
        private void openImageTextRoom(ImageTextLiveRoomModel model) {
            TextLiveListModel.StreamsEntity streamsEntity = new TextLiveListModel.StreamsEntity();
            streamsEntity.setName(model.getName());
            streamsEntity.setViewcount(model.getViewcount());
            streamsEntity.setOwnerid(model.getOwnerid());
            streamsEntity.setId(model.getId());
            streamsEntity.setUserEntity(mUserInfoModel);
            ImageTextLiveActivity.start(mContext, streamsEntity);
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
                    mPayTv.setText("付费");
                    mPayTv.setBackgroundColor(mContext.getResources().getColor(R.color.video_living_pay));
                } else {
                    mPayCv.setVisibility(View.GONE);
                    // TODO: 2017/5/5
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
