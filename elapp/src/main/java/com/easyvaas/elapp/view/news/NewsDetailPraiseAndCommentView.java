package com.easyvaas.elapp.view.news;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easyvaas.common.widget.RoundImageView;
import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.bean.NoResponeBackModel;
import com.easyvaas.elapp.bean.news.NewsDetailModel;
import com.easyvaas.elapp.bean.user.UserPageCommentModel.PostsBean;
import com.easyvaas.elapp.net.mynet.NetSubscribe;
import com.easyvaas.elapp.net.mynet.RetrofitHelper;
import com.easyvaas.elapp.ui.base.mybase.AppConstants;
import com.easyvaas.elapp.ui.news.NewsDetailCommentActivity;
import com.easyvaas.elapp.ui.user.LoginActivity;
import com.easyvaas.elapp.utils.SingleToast;
import com.easyvaas.elapp.utils.UserUtil;
import com.easyvaas.elapp.utils.Utils;
import com.easyvaas.elapp.utils.ViewUtil;
import com.hooview.app.R;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Date   2017/5/9
 * Editor  Misuzu
 * 新闻点赞部分和评论部分
 */

public class NewsDetailPraiseAndCommentView extends LinearLayout {

    @BindView(R.id.detail_praise_counts)
    TextView mDetailPraiseCounts;
    @BindView(R.id.detail_praise_counts_anime)
    TextView mDetailPraiseCountsAnime;
    @BindView(R.id.detail_unlike)
    TextView mDetailUnlike;
    @BindView(R.id.detail_header_user_header)
    RoundImageView mDetailHeaderUserHeader;
    @BindView(R.id.detail_header_user_name)
    TextView mDetailHeaderUserName;
    @BindView(R.id.detail_header_user_info)
    TextView mDetailHeaderUserInfo;
    @BindView(R.id.detail_header_user_layout)
    RelativeLayout mDetailHeaderUserLayout;
    @BindView(R.id.detail_comment_divider_line)
    View mDetailCommentDividerLine;
    @BindView(R.id.detail_all_comment_counts)
    TextView mDetailAllCommentCounts;
    @BindView(R.id.detail_comment_container)
    LinearLayout mDetailCommentContainer;
    NewsDetailModel data;

    public NewsDetailPraiseAndCommentView(Context context) {
        super(context);
        initView();
    }

    public NewsDetailPraiseAndCommentView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        setOrientation(VERTICAL);
        inflate(getContext(), R.layout.news_detail_opreator_layout, this);
        ButterKnife.bind(this, this);
        data = new NewsDetailModel();
    }

    public void setData(NewsDetailModel data)
    {
        this.data = data;
        // 加载点赞和推荐用户信息
        if (data.getLike() == 0)
            mDetailPraiseCounts.setSelected(false);
        else
            mDetailPraiseCounts.setSelected(true);
        mDetailPraiseCounts.setText(String.valueOf(data.getLikeCount()));
        mDetailHeaderUserName.setText(data.getRecommendPerson().getName());
        mDetailHeaderUserInfo.setText(data.getRecommendPerson().getDescription());
        UserUtil.showUserPhoto(getContext(),data.getRecommendPerson().getAvatar(),mDetailHeaderUserHeader);

        //加载热门评论数据
        if (data.getPosts().size() > 0) {
            int childSize = mDetailCommentContainer.getChildCount();
            if (childSize > 3)  // 此处是为了刷新 评论数据 必须先移除之前的数据
            {
                mDetailCommentContainer.removeViews(1,childSize - 3);
            }
            List<PostsBean> mPostsBeanList = data.getPosts();
            Collections.reverse(mPostsBeanList);
            for (int i = 0; i < mPostsBeanList.size(); i++) {
                if (i >=3)
                    break;
                PostsBean postsBean = mPostsBeanList.get(i);
                NewsDetailCommentView mCommentView = new NewsDetailCommentView(getContext());
                mCommentView.setData(postsBean);
                mDetailCommentContainer.addView(mCommentView,1);
            }
            mDetailCommentDividerLine.setVisibility(VISIBLE);
        }else
            mDetailCommentDividerLine.setVisibility(GONE);
        mDetailAllCommentCounts.setText(getContext().getString(R.string.news_all_comment,data.getPostCount()));
    }

    @OnClick({R.id.detail_praise_counts, R.id.detail_unlike, R.id.detail_header_user_layout, R.id.detail_all_comment_counts})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.detail_praise_counts: //点赞
                if (EVApplication.isLogin())
                     praiseClick();
                else
                    LoginActivity.start(getContext());
                break;
            case R.id.detail_unlike: // 不喜欢
                SingleToast.show(getContext(),R.string.feature_no_open_prompt);
                break;
            case R.id.detail_header_user_layout: // 跳转大V主页
                Utils.toUserPager(getContext(),data.getRecommendPerson().getId(),1);
                break;
            case R.id.detail_all_comment_counts: //跳转全部评论
                Intent intent = new Intent(getContext(),NewsDetailCommentActivity.class);
                intent.putExtra(AppConstants.NEWS_ID,data.getId());
                getContext().startActivity(intent);
                break;
        }
    }


    /**
     * 文章点赞
     */
    private void praiseClick()
    {
        final int action = mDetailPraiseCounts.isSelected() ? 0 : 1;
        RetrofitHelper.getInstance().getService().newsPraiseClick(data.getId(), EVApplication.getUser().getUserid(),action)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSubscribe<NoResponeBackModel>() {
                    @Override
                    public void OnSuccess(NoResponeBackModel noResponeBackModel) {
                        int counts = data.getLikeCount();
                        if (action == 0) {
                            data.setLike(0);
                            data.setLikeCount(counts - 1);
                            mDetailPraiseCounts.setSelected(false);
                            startPraiseAnime(0);
                        } else {
                            data.setLike(1);
                            data.setLikeCount(counts + 1);
                            mDetailPraiseCounts.setSelected(true);
                            startPraiseAnime(1);
                        }
                        mDetailPraiseCounts.setText(String.valueOf(data.getLikeCount()));
                    }

                    @Override
                    public void OnFailue(String msg) {
                        SingleToast.show(getContext(),R.string.opreat_fail);
                    }
                });
    }

    /**
     * 开始点赞动画
     */
    private void startPraiseAnime(int action)
    {
        if (action == 0) {
            mDetailPraiseCountsAnime.setSelected(false);
            mDetailPraiseCountsAnime.setText("-1");
        } else {
            mDetailPraiseCountsAnime.setSelected(true);
            mDetailPraiseCountsAnime.setText("+1");
        }
        PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat(View.ALPHA,0,1,0);
        PropertyValuesHolder translationY = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y,0, -ViewUtil.dp2Px(getContext(),40));
        PropertyValuesHolder translationX = PropertyValuesHolder.ofFloat(View.TRANSLATION_X,0,5,0,5,0);

        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(mDetailPraiseCountsAnime,alpha,translationY,translationX);

        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                    }

                    @Override
                    public void onAnimationStart(Animator animation) {
                        mDetailPraiseCountsAnime.setVisibility(VISIBLE);
                    }
                });
            animator.setDuration(1000).start();
    }
}
