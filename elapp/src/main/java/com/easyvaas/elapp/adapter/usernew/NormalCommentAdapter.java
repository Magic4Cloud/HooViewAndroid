package com.easyvaas.elapp.adapter.usernew;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.easyvaas.common.widget.MyUserPhoto;
import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.bean.NoResponeBackModel;
import com.easyvaas.elapp.bean.user.UserPageCommentModel.PostsBean;
import com.easyvaas.elapp.net.mynet.NetSubscribe;
import com.easyvaas.elapp.net.mynet.RetrofitHelper;
import com.easyvaas.elapp.ui.base.mybase.MyBaseAdapter;
import com.easyvaas.elapp.ui.user.LoginActivity;
import com.easyvaas.elapp.utils.DateTimeUtil;
import com.easyvaas.elapp.utils.SingleToast;
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
 * Date   2017/5/03
 * Editor  Misuzu
 * 通用评论列表
 */

public class NormalCommentAdapter extends MyBaseAdapter<PostsBean> {

    public static final int NEWS_TYPE = 0;
    public static final int VIDEO_TYPE = 1;
    public static final int STOCK_TYPE = 2;
    int type;

    public NormalCommentAdapter(List<PostsBean> data, int type) {
        super(data);
        this.type = type;
    }

    @Override
    protected int getItemViewByType(int position) {
        return 0;
    }

    @Override
    protected BaseViewHolder OnCreateViewByHolder(ViewGroup parent, int viewType) {
        return new UserCommentViewHolder(LayoutInflater.from(mContext).inflate(R.layout.normal_comment_layout, parent, false));
    }

    @Override
    protected void initOnItemClickListener() {
        setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                PostsBean data = mData.get(position);
                Utils.toUserPager(mContext,data.getUser().getId(),data.getUser().getVip());

            }
        });
    }

    @Override
    protected void convert(BaseViewHolder helper, PostsBean item) {
        ((UserCommentViewHolder) helper).setData(item);
    }


    public class UserCommentViewHolder extends BaseViewHolder {

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
            mUserCommentPraiseCounts.setText(String.valueOf(data.getHeats()));
            Picasso.with(mContext).load(data.getUser().getAvatar()).placeholder(R.drawable.user_avtor).into(mUserCommentAvator);

            if (data.getLike() == 1) // 是否点赞
                mUserCommentPraiseIcon.setSelected(true);
            else
                mUserCommentPraiseIcon.setSelected(false);
        }

        /**
         * 点赞
         */
        @OnClick(R.id.user_comment_praise_icon)
        public void praiseClick() {
            if (EVApplication.isLogin()) {
                PostsBean postsBean = mData.get(getLayoutPosition());
                final int action = mUserCommentPraiseIcon.isSelected() ? 0 : 1;
                RetrofitHelper.getInstance().getService()
                        .praiseClick(EVApplication.getUser().getName(),
                                EVApplication.getUser().getSessionid(),
                                postsBean.getId(), type, action)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new NetSubscribe<NoResponeBackModel>() {
                            @Override
                            public void OnSuccess(NoResponeBackModel noResponeBackModel) {
                                PostsBean data = mData.get(getLayoutPosition());
                                int counts = Integer.parseInt(data.getHeats());
                                if (action == 0) {
                                    data.setLike(0);
                                    data.setHeats(String.valueOf(counts - 1));
                                    mUserCommentPraiseIcon.setSelected(false);
                                    SingleToast.show(mContext, R.string.user_praise_cancel);
                                } else {
                                    data.setLike(1);
                                    data.setHeats(String.valueOf(counts + 1));
                                    mUserCommentPraiseIcon.setSelected(true);
                                    SingleToast.show(mContext, R.string.user_praise_success);
                                }
                                mUserCommentPraiseCounts.setText(data.getHeats());
                            }

                            @Override
                            public void OnFailue(String msg) {
                                SingleToast.show(mContext, R.string.opreat_fail);
                            }
                        });
            } else
                LoginActivity.start(mContext);
        }
    }

}
