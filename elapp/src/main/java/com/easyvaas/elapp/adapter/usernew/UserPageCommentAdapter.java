package com.easyvaas.elapp.adapter.usernew;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.easyvaas.common.widget.MyUserPhoto;
import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.bean.NoResponeBackModel;
import com.easyvaas.elapp.bean.user.UserPageCommentModel.PostsBean;
import com.easyvaas.elapp.bean.video.VideoEntity;
import com.easyvaas.elapp.net.mynet.NetSubscribe;
import com.easyvaas.elapp.net.mynet.RetrofitHelper;
import com.easyvaas.elapp.ui.base.mybase.MyBaseAdapter;
import com.easyvaas.elapp.ui.live.PlayerActivity;
import com.easyvaas.elapp.ui.user.LoginActivity;
import com.easyvaas.elapp.utils.DateTimeUtil;
import com.easyvaas.elapp.utils.Utils;
import com.hooview.app.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Date   2017/4/24
 * Editor  Misuzu
 * 用户主页评论
 */

public class UserPageCommentAdapter extends MyBaseAdapter<PostsBean> {


    public UserPageCommentAdapter(List<PostsBean> data) {
        super(data);
    }

    @Override
    protected int getItemViewByType(int position) {
        return 0;
    }

    @Override
    protected BaseViewHolder OnCreateViewByHolder(ViewGroup parent, int viewType) {
        return new UserCommentViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_user_page_comment_layout, parent, false));
    }

    @Override
    protected void initOnItemClickListener() {
        setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                PostsBean data = mData.get(position);
                switch (data.getTopic().getType()) //话题类型（0，新闻；1，视频；2，股票）
                {
                    case 0:
                        Utils.showNewsDetail(mContext, data.getTopic().getTitle(), data.getTopic().getId());
                        break;
                    case 1:
                        Utils.showStockDetail(mContext, data.getTopic().getTitle(), data.getTopic().getId(), true);
                        break;
                    case 2:
                        PlayerActivity.start(mContext,data.getTopic().getId(), VideoEntity.IS_VIDEO,VideoEntity.IS_GOOD_VIDEO);
                        break;
                }

            }
        });
    }

    @Override
    protected void convert(BaseViewHolder helper, PostsBean item) {
        ((UserCommentViewHolder) helper).setData(item);
    }


    public class UserCommentViewHolder extends BaseViewHolder {

        @BindView(R.id.user_comment_icon)
        ImageView mUserCommentIcon;
        @BindView(R.id.user_comment_target_text)
        TextView mUserCommentTargetText;
        @BindView(R.id.user_comment_target_bg_layout)
        LinearLayout mUserCommentTargetBgLayout;
        @BindView(R.id.user_comment_avator)
        MyUserPhoto mUserCommentAvator;
        @BindView(R.id.user_comment_name)
        TextView mUserCommentName;
        @BindView(R.id.user_comment_praise_counts)
        TextView mUserCommentPraiseCounts;
        @BindView(R.id.user_comment_praise_icon)
        ImageView mUserCommentPraiseIcon;
        @BindView(R.id.user_comment_content)
        TextView mUserCommentContent;
        @BindView(R.id.user_comment_time)
        TextView mUserCommentTime;

        public UserCommentViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(PostsBean data) {
            mUserCommentName.setText(data.getUser().getNickname());
            mUserCommentContent.setText(data.getContent());
            mUserCommentTime.setText(DateTimeUtil.getShortTime(mContext, data.getTime()));
            mUserCommentTargetText.setText(data.getTopic().getTitle());
            mUserCommentPraiseCounts.setText(String.valueOf(data.getHeats()));
            Picasso.with(mContext).load(data.getUser().getAvatar()).placeholder(R.drawable.user_avtor).into(mUserCommentAvator);

            if (data.getLike() == 1) // 是否点赞
                mUserCommentPraiseIcon.setSelected(true);
            else
                mUserCommentPraiseIcon.setSelected(false);

            switch (data.getTopic().getType()) //话题类型（0，新闻；1，视频；2，股票）
            {
                case 0:
                    mUserCommentTargetBgLayout.setBackgroundResource(R.drawable.shape_user_comment_blue);
                    mUserCommentIcon.setImageResource(R.drawable.ic_user_new);
                    break;
                case 1:
                    mUserCommentTargetBgLayout.setBackgroundResource(R.drawable.shape_user_comment_purple);
                    mUserCommentIcon.setImageResource(R.drawable.ic_user_video);
                    break;
                case 2:
                    mUserCommentTargetBgLayout.setBackgroundResource(R.drawable.shape_user_comment_orange);
                    mUserCommentIcon.setImageResource(R.drawable.ic_user_stock);
                    break;
            }

        }

        /**
         * 点赞
         */
        @OnClick(R.id.user_comment_praise_icon)
        public void praiseClick() {
            if (EVApplication.isLogin()) {
                PostsBean postsBean = mData.get(getLayoutPosition());
                RetrofitHelper.getInstance().getService()
                        .praiseClick(EVApplication.getUser().getName(),EVApplication.getUser().getSessionid(),postsBean.getId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new NetSubscribe<NoResponeBackModel>() {
                            @Override
                            public void OnSuccess(NoResponeBackModel noResponeBackModel) {

                            }

                            @Override
                            public void OnFailue(String msg) {

                            }
                        });
            }else
                LoginActivity.start(mContext);
        }
    }

}
