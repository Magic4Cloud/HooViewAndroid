package com.easyvaas.elapp.ui.news;

import com.easyvaas.elapp.adapter.news.NormalNewsAdapter;
import com.easyvaas.elapp.bean.news.TopRatedModel;
import com.easyvaas.elapp.bean.news.TopicModel;
import com.easyvaas.elapp.net.mynet.NetSubscribe;
import com.easyvaas.elapp.net.mynet.RetrofitHelper;
import com.easyvaas.elapp.ui.base.mybase.MyBaseListFragment;

import java.util.ArrayList;

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
        RetrofitHelper.getInstance().getService().getTopicListTest("https://demo2821846.mockable.io/news/topic")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSubscribe<TopicModel>() {
                    @Override
                    public void OnSuccess(TopicModel topicModel) {
                        if (topicModel != null)
                            mAdapter.dealLoadData(isLoadMore,topicModel.getNews());
                        mSwiprefreshlayout.setRefreshing(false);
                    }

                    @Override
                    public void OnFailue(String msg) {

                    }
                });
    }

    public static TopicFragment newInstance() {
        return new TopicFragment();
    }
}
