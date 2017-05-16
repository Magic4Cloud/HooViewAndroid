package com.easyvaas.elapp.view.news;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easyvaas.common.widget.MyUserPhoto;
import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.bean.NoResponeBackModel;
import com.easyvaas.elapp.bean.user.UserPageCommentModel.PostsBean;
import com.easyvaas.elapp.net.mynet.NetSubscribe;
import com.easyvaas.elapp.net.mynet.RetrofitHelper;
import com.easyvaas.elapp.ui.user.LoginActivity;
import com.easyvaas.elapp.utils.DateTimeUtil;
import com.easyvaas.elapp.utils.SingleToast;
import com.easyvaas.elapp.utils.Utils;
import com.hooview.app.R;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;



/**
 * Date   2017/5/9
 * Editor  Misuzu
 * 评论item
 */

public class NewsDetailCommentView extends LinearLayout {

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
    PostsBean data;

    public NewsDetailCommentView(Context context) {
        super(context);
        initView();
    }

    public NewsDetailCommentView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView()
    {
        inflate(getContext(),R.layout.news_detail_comment_layout, this);
        ButterKnife.bind(this,this);
    }

    public void setData(PostsBean data) {
        this.data = data;
        mUserCommentName.setText(data.getUser().getNickname());
        mUserCommentContent.setText(data.getContent());
        mUserCommentTime.setText(DateTimeUtil.getShortTime(getContext(), data.getTime()));
        mUserCommentPraiseCounts.setText(String.valueOf(data.getHeats()));
        if (!TextUtils.isEmpty(data.getUser().getAvatar()))
            Picasso.with(getContext()).load(data.getUser().getAvatar()).placeholder(R.drawable.user_avtor).into(mUserCommentAvator);

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
            final int action = mUserCommentPraiseIcon.isSelected() ? 0 : 1;
            RetrofitHelper.getInstance().getService()
                    .praiseClick(EVApplication.getUser().getName(),
                            EVApplication.getUser().getSessionid(),
                            data.getId(), 0, action)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetSubscribe<NoResponeBackModel>() {
                        @Override
                        public void OnSuccess(NoResponeBackModel noResponeBackModel) {
                            int counts = Integer.parseInt(data.getHeats());
                            if (action == 0) {
                                data.setLike(0);
                                data.setHeats(String.valueOf(counts - 1));
                                mUserCommentPraiseIcon.setSelected(false);
                                SingleToast.show(getContext(), R.string.user_praise_cancel);
                            } else {
                                data.setLike(1);
                                data.setHeats(String.valueOf(counts + 1));
                                mUserCommentPraiseIcon.setSelected(true);
                                SingleToast.show(getContext(), R.string.user_praise_success);
                            }
                            mUserCommentPraiseCounts.setText(data.getHeats());
                        }

                        @Override
                        public void OnFailue(String msg) {
                            SingleToast.show(getContext(), R.string.opreat_fail);
                        }
                    });
        } else
            LoginActivity.start(getContext());
    }

    /**
     * 跳转个人主页
     */
    @OnClick(R.id.user_comment_layout)
    public void layoutClick()
    {
        Utils.toUserPager(getContext(),data.getUser().getId(),data.getUser().getVip());

    }
}
