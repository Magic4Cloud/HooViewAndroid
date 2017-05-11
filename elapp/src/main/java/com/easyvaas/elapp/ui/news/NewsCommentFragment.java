package com.easyvaas.elapp.ui.news;

import android.os.Bundle;

import com.easyvaas.elapp.adapter.usernew.NormalCommentAdapter;
import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.bean.user.UserPageCommentModel.PostsBean;
import com.easyvaas.elapp.bean.video.VideoCommentModel;
import com.easyvaas.elapp.net.mynet.NetSubscribe;
import com.easyvaas.elapp.net.mynet.RetrofitHelper;
import com.easyvaas.elapp.ui.base.mybase.AppConstants;
import com.easyvaas.elapp.ui.base.mybase.MyBaseListFragment;
import com.hooview.app.R;

import java.util.ArrayList;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Date   2017/5/11
 * Editor  Misuzu
 * 新闻评论列表
 */

public class NewsCommentFragment extends MyBaseListFragment<NormalCommentAdapter>  {

    private String newsId = "";
    private String userId = "";
    private String orderBy;

    @Override
    protected NormalCommentAdapter initAdapter() {
        return new NormalCommentAdapter(new ArrayList<PostsBean>(),NormalCommentAdapter.NEWS_TYPE);
    }

    @Override
    protected void changeRecyclerView() {
        setPaddingTop(4);
    }

    @Override
    protected void initSomeData() {
        if (EVApplication.isLogin())
            userId = EVApplication.getUser().getUserid();
        newsId = getArguments().getString(AppConstants.NEWS_ID);
        orderBy = AppConstants.HEATS;
    }

    @Override
    protected void changeEmptyView() {
        mEmptyView.setEmptyTxt(getString(R.string.empty_video_no_comment));
    }

    @Override
    protected void getListData(final Boolean isLoadMore) {

        Subscription subscription = RetrofitHelper.getInstance().getService()
               .getCommentListByType(newsId,userId,0,start,orderBy)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSubscribe<VideoCommentModel>() {
                    @Override
                    public void OnSuccess(VideoCommentModel userPageCommentModel) {
                        if (userPageCommentModel != null)
                            mAdapter.dealLoadData(NewsCommentFragment.this,isLoadMore,userPageCommentModel.getPosts());
                    }

                    @Override
                    public void OnFailue(String msg) {
                        mAdapter.dealLoadError(NewsCommentFragment.this,isLoadMore);
                    }
                });
        addSubscribe(subscription);
    }

    /**
     * 切换排序规则
     */
   public void switchOrderBy(String orderBy)
   {
       this.orderBy = orderBy;
       autoRefresh();
   }

    public static NewsCommentFragment newInstance(String newsid) {
        Bundle args = new Bundle();
        args.putString(AppConstants.NEWS_ID,newsid);
        NewsCommentFragment fragment = new NewsCommentFragment();
        fragment.setArguments(args);
        return fragment;
    }

}
