package com.easyvaas.elapp.ui.user.newuser.fragment;

import com.easyvaas.elapp.adapter.news.NormalNewsAdapter;
import com.easyvaas.elapp.bean.news.NormalNewsModel;
import com.easyvaas.elapp.bean.news.TopRatedModel.HomeNewsBean;
import com.easyvaas.elapp.net.mynet.NetSubscribe;
import com.easyvaas.elapp.net.mynet.RetrofitHelper;
import com.easyvaas.elapp.ui.base.mybase.MyBaseListFragment;
import com.hooview.app.R;

import java.util.ArrayList;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Date   2017/4/20
 * Editor  xiaomao
 * 个人中心---我的发布---文章
 */

public class UserPublishArticleFragment extends MyBaseListFragment<NormalNewsAdapter> {

    @Override
    protected NormalNewsAdapter initAdapter() {
        return new NormalNewsAdapter(new ArrayList<HomeNewsBean>());
    }

    @Override
    protected void changeEmptyView() {
        super.changeEmptyView();
        mEmptyView.setEmptyTxt(getString(R.string.empty_no_article));
    }

    @Override
    protected void getListData(final Boolean isLoadMore) {
        Subscription subscription = RetrofitHelper.getInstance().getService()
                .getUserPublishArticleTest("http://192.168.8.125:8888/user/works?type=2")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSubscribe<NormalNewsModel>() {
                    @Override
                    public void OnSuccess(NormalNewsModel normalNewsModel) {
                        if (normalNewsModel != null) {
                            mAdapter.dealLoadData(UserPublishArticleFragment.this, isLoadMore, normalNewsModel.getNews());
                        }
                    }

                    @Override
                    public void OnFailue(String msg) {
                        mAdapter.dealLoadError(UserPublishArticleFragment.this, isLoadMore);
                    }
                });
        addSubscribe(subscription);
    }

    @Override
    protected void changeRecyclerView() {
        setPaddingTop(4);
    }

    public static UserPublishArticleFragment newInstance() {
        return new UserPublishArticleFragment();
    }

}
