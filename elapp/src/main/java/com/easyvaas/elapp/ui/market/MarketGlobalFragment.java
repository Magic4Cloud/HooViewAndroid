package com.easyvaas.elapp.ui.market;

import com.easyvaas.elapp.adapter.recycler.MarketGlobalListAdapter;
import com.easyvaas.elapp.bean.market.MarketGlobalModel;
import com.easyvaas.elapp.net.mynet.NetSubscribe;
import com.easyvaas.elapp.net.mynet.RetrofitHelper;
import com.easyvaas.elapp.ui.base.BaseListLazyFragment;
import com.easyvaas.elapp.utils.Logger;
import com.hooview.app.R;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Date    2017/4/11
 * Author  xiaomao
 * 行情---全球
 */

public class MarketGlobalFragment extends BaseListLazyFragment {

    private MarketGlobalListAdapter mAdapter;

    /**
     * 初始化adapter
     */
    @Override
    protected void initAdapter() {
        mAdapter = new MarketGlobalListAdapter(getContext());
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onRefresh() {
        loadData();
    }

    @Override
    public void onOperation() {
        loadData();
    }

    @Override
    protected void onVisibleChanged(boolean isVisible) {
        if (isVisible && !mIsLoad) {
            loadData();
            mIsLoad = true;
        }
        if (!isVisible) {
            if (mSwipeRefreshLayout != null && mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }
    }

    private void loadData() {
        mSwipeRefreshLayout.setRefreshing(true);
        RetrofitHelper.getInstance().getService().getMarketGlobalStock()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSubscribe<List<MarketGlobalModel>>() {
                    @Override
                    public void OnSuccess(List<MarketGlobalModel> list) {
                        if (list != null && list.size() > 0) {
                            if (mAdapter != null) {
                                mAdapter.setData(list);
                            }
                            showOperationView(R.drawable.logo);
                        } else {
                            showWithoutData();
                        }
                        mSwipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void OnFailue(String msg) {
                        Logger.e(MarketGlobalFragment.class.getSimpleName(), msg);
                        showWithoutData();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
    }

    public static MarketGlobalFragment newInstance() {
        return new MarketGlobalFragment();
    }

    private void showWithoutData() {
        showEmptyView();
        hideOperationView();
    }

}
