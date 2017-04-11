package com.easyvaas.elapp.ui.market;

import android.view.View;

import com.easyvaas.elapp.adapter.recycler.MarketGlobalListAdapter;
import com.easyvaas.elapp.bean.market.MarketGlobalModel;
import com.easyvaas.elapp.net.mynet.NetSubscribe;
import com.easyvaas.elapp.net.mynet.RetrofitHelper;
import com.easyvaas.elapp.ui.base.BaseListFragment;
import com.easyvaas.elapp.utils.Logger;
import com.hooview.app.R;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Date    2017/4/11
 * Author  xiaomao
 * <p>
 * 行情----全球
 */

public class MarketGlobalFragment extends BaseListFragment {

    private MarketGlobalListAdapter mAdapter;

    @Override
    public void iniView(View view) {
        mAdapter = new MarketGlobalListAdapter(getContext());
        mRecyclerView.setAdapter(mAdapter);
        loadData();
    }

    @Override
    public void onRefresh() {
        loadData();
    }

    @Override
    public void onOperation() {
        loadData();
    }

    private void loadData() {
        mSwipeRefreshLayout.setRefreshing(true);
        RetrofitHelper.getInstance().getService().getMarketGlobalStock()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSubscribe<List<MarketGlobalModel>>() {
                    @Override
                    public void OnSuccess(List<MarketGlobalModel> list) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        if (list != null && list.size() > 0) {
                            if (mAdapter != null) {
                                mAdapter.setData(list);
                            }
                            showOperationView(R.drawable.logo);
                        } else {
                            showWithoutData();
                        }
                    }

                    @Override
                    public void OnFailue(String msg) {
                        Logger.e(MarketGlobalFragment.class.getSimpleName(), msg);
                        mSwipeRefreshLayout.setRefreshing(false);
                        showWithoutData();
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
