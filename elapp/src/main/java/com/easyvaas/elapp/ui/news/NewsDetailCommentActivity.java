package com.easyvaas.elapp.ui.news;

import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.bean.NoResponeBackModel;
import com.easyvaas.elapp.net.mynet.NetSubscribe;
import com.easyvaas.elapp.net.mynet.RetrofitHelper;
import com.easyvaas.elapp.ui.base.mybase.AppConstants;
import com.easyvaas.elapp.ui.base.mybase.MyBaseActivity;
import com.easyvaas.elapp.utils.SingleToast;
import com.easyvaas.elapp.view.base.BottomSendView;
import com.hooview.app.R;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Date   2017/5/11
 * Editor  Misuzu
 * 新闻详情全部评论
 */

public class NewsDetailCommentActivity extends MyBaseActivity implements BottomSendView.OnBottomInputListener{
    @BindView(R.id.all_comment_order_hot)
    TextView mAllCommentOrderHot;
    @BindView(R.id.all_comment_order_date)
    TextView mAllCommentOrderDate;
    @BindView(R.id.all_comment_container)
    FrameLayout mAllCommentContainer;
    @BindView(R.id.all_comment_bottom)
    BottomSendView mAllCommentBottom;
    private String newsId;
    NewsCommentFragment mNewsCommentFragment;

    @Override
    protected int getLayout() {
        return R.layout.activity_news_all_comment_layout;
    }

    @Override
    protected String getTitleText() {
        return getString(R.string.all_comments);
    }

    @Override
    protected void initViewAndData() {
        newsId = getIntent().getStringExtra(AppConstants.NEWS_ID);
        mAllCommentOrderHot.setSelected(true);
        mAllCommentBottom.setType(BottomSendView.TYPE_COMMENT);
        mAllCommentBottom.setOnBottomInputListener(this);
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().add(R.id.all_comment_container, mNewsCommentFragment = NewsCommentFragment.newInstance(newsId)).commit();
    }


    /**
     * 发送评论
     */
    @Override
    public void sendText(String inputString, int type) {
        Subscription subscription = RetrofitHelper.getInstance().getService()
                .sendCommentByType(newsId, EVApplication.getUser().getName(),inputString,0)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSubscribe<NoResponeBackModel>() {
                    @Override
                    public void OnSuccess(NoResponeBackModel noResponeBackModel) {
                        SingleToast.show(NewsDetailCommentActivity.this,getString(R.string.msg_comment_success));
                        mNewsCommentFragment.autoRefresh();
                    }

                    @Override
                    public void OnFailue(String msg) {
                        SingleToast.show(NewsDetailCommentActivity.this,getString(R.string.opreat_fail));
                    }
                });
        addSubscribe(subscription);
    }

    @OnClick({R.id.all_comment_order_hot, R.id.all_comment_order_date})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.all_comment_order_hot: // 根据热度排序
                mAllCommentOrderDate.setSelected(false);
                mAllCommentOrderHot.setSelected(true);
                mNewsCommentFragment.switchOrderBy(AppConstants.HEATS);
                break;
            case R.id.all_comment_order_date:// 根据时间排序
                mAllCommentOrderDate.setSelected(true);
                mAllCommentOrderHot.setSelected(false);
                mNewsCommentFragment.switchOrderBy(AppConstants.DATELINE);
                break;
        }
    }
    @Override
    public void openGiftWindow() {

    }
}
