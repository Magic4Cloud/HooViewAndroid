package com.easyvaas.elapp.ui.search;

import com.easyvaas.elapp.adapter.news.NormalNewsAdapter;
import com.easyvaas.elapp.bean.news.NormalNewsModel;
import com.easyvaas.elapp.bean.news.TopRatedModel.HomeNewsBean;
import com.easyvaas.elapp.event.SearchEvent;
import com.easyvaas.elapp.net.mynet.NetSubscribe;
import com.easyvaas.elapp.net.mynet.RetrofitHelper;
import com.easyvaas.elapp.ui.base.mybase.MyBaseListFragment;
import com.hooview.app.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Date   2017/4/20
 * Editor  Misuzu
 * 新闻搜索
 */

public class GloabalNewsSearchFragment extends MyBaseListFragment<NormalNewsAdapter> {

    private String mKeyWord;  //关键词

    @Override
    protected NormalNewsAdapter initAdapter() {
        return new NormalNewsAdapter(new ArrayList<HomeNewsBean>());
    }

    @Override
    protected void changeEmptyView() {
        mEmptyView.showEmptyOnNoData();
        mEmptyView.setEmptyImg(R.drawable.ic_smile);
        mEmptyView.setEmptyTxt(getContext().getString(R.string.search_news_empty_prompt));
        EventBus.getDefault().register(this);
    }

    @Override
    protected void getListData(final Boolean isLoadMore) {

        Subscription subscription = RetrofitHelper.getInstance().getService()
                .searchNews(mKeyWord,start)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSubscribe<NormalNewsModel>() {
                    @Override
                    public void OnSuccess(NormalNewsModel normalNewsModel) {
                        if (normalNewsModel != null)
                            mAdapter.dealLoadData(GloabalNewsSearchFragment.this,isLoadMore,normalNewsModel.getNews());
                    }

                    @Override
                    public void OnFailue(String msg) {
                        mAdapter.dealLoadError(GloabalNewsSearchFragment.this,isLoadMore);
                    }
                });
    }

    //接收输入的关键词
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SearchEvent event) {
        this.mKeyWord = event.keyWord;
        mEmptyView.setEmptyImg(R.drawable.ic_cry);
        mEmptyView.setEmptyTxt(getContext().getString(R.string.search_live_empty_prompt));
        setLoading(true);
        onRefresh();
    }

    public static GloabalNewsSearchFragment newInstance() {
        return new GloabalNewsSearchFragment();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
