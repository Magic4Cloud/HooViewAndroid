package com.easyvaas.elapp.ui.user.usernew.fragment;

import com.easyvaas.elapp.adapter.news.NormalNewsAdapter;
import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.bean.news.NormalNewsModel;
import com.easyvaas.elapp.bean.news.TopRatedModel.HomeNewsBean;
import com.easyvaas.elapp.event.UserHistoryCleanEvent;
import com.easyvaas.elapp.net.mynet.NetSubscribe;
import com.easyvaas.elapp.net.mynet.RetrofitHelper;
import com.easyvaas.elapp.ui.base.mybase.MyBaseListFragment;
import com.hooview.app.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Date   2017/4/20
 * Editor  Misuzu
 * 用户历史阅读记录
 */

public class UserHistoryReadNewFragment extends MyBaseListFragment<NormalNewsAdapter> {

    @Override
    protected NormalNewsAdapter initAdapter() {
        return new NormalNewsAdapter(new ArrayList<HomeNewsBean>());
    }

    @Override
    protected void changeRecyclerView() {
        setPaddingTop(4);
    }

    @Override
    protected void initSomeData() {
        EventBus.getDefault().register(this);
    }

    @Override
    protected void changeEmptyView() {
        super.changeEmptyView();
        mEmptyView.setEmptyTxt(getString(R.string.empty_no_read));
    }

    @Override
    protected void getListData(final Boolean isLoadMore) {
        Subscription subscription = RetrofitHelper.getInstance().getService()
                .getUserReadHistory(EVApplication.getUser().getName(),EVApplication.getUser().getSessionid(),start)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSubscribe<NormalNewsModel>() {
                    @Override
                    public void OnSuccess(NormalNewsModel normalNewsModel) {
                        if (normalNewsModel != null)
                        mAdapter.dealLoadData(UserHistoryReadNewFragment.this,isLoadMore,normalNewsModel.getNews());
                    }

                    @Override
                    public void OnFailue(String msg) {
                        mAdapter.dealLoadError(UserHistoryReadNewFragment.this,isLoadMore);
                    }
                });
        addSubscribe(subscription);
    }

    public static UserHistoryReadNewFragment newInstance() {
        return new UserHistoryReadNewFragment();
    }

    /**
     * 清空事件
     */
    @Subscribe
    public void cleanHistory(UserHistoryCleanEvent historyCleanEvent)
    {
        if (historyCleanEvent.getType() == 1)
            onRefresh();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
