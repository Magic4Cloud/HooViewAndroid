package com.easyvaas.elapp.ui.news;

import android.os.Bundle;

import com.easyvaas.elapp.adapter.news.NormalNewsAdapter;
import com.easyvaas.elapp.bean.news.TopRatedModel;
import com.easyvaas.elapp.bean.news.TopicModel;
import com.easyvaas.elapp.net.mynet.NetSubscribe;
import com.easyvaas.elapp.net.mynet.RetrofitHelper;
import com.easyvaas.elapp.ui.base.mybase.MyBaseListFragment;

import java.util.ArrayList;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Date   2017/4/12
 * Editor  Misuzu
 * 专题fragment
 */

public class TopicFragment extends MyBaseListFragment<NormalNewsAdapter> {

    @Override
    protected NormalNewsAdapter initAdapter() {
        return new NormalNewsAdapter(new ArrayList<TopRatedModel.HomeNewsBean>());
    }

    @Override
    protected void getListData(final Boolean isLoadMore) {
        Subscription subscription =  RetrofitHelper.getInstance().getService().getTopicList(getArguments().getString("id"),start)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSubscribe<TopicModel>() {
                    @Override
                    public void OnSuccess(TopicModel topicModel) {
                        if (topicModel != null)
                        {
                            mAdapter.dealLoadData(isLoadMore,topicModel.getNews());
                            if (!isLoadMore) {
                                ((TopicActivity) getActivity()).initHeaderDatas(topicModel);
                            }
                        }
                        mSwiprefreshlayout.setRefreshing(false);
                    }

                    @Override
                    public void OnFailue(String msg) {
                        if (isLoadMore)
                            mAdapter.loadMoreFail();
                        else
                            mSwiprefreshlayout.setRefreshing(false);
                    }
                });
        addSubscribe(subscription);
    }

    public static TopicFragment newInstance(String id) {

        Bundle args = new Bundle();
        args.putString("id",id);
        TopicFragment fragment = new TopicFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
