package com.easyvaas.elapp.ui.market;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easyvaas.elapp.adapter.recycler.SelectStockListAdapter;
import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.bean.market.StockListModel;
import com.easyvaas.elapp.net.HooviewApiHelper;
import com.easyvaas.elapp.net.MyRequestCallBack;
import com.easyvaas.elapp.ui.base.LazyLoadFragment;
import com.easyvaas.elapp.ui.search.SearchStockActivity;
import com.easyvaas.elapp.utils.ViewUtil;
import com.hooview.app.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Date    2017/4/10
 * Author  xiaomao
 * 行情--自选
 */
public class MarketOptionalFragment extends LazyLoadFragment {

    @BindView(R.id.stock_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.stock_rv)
    RecyclerView mRecyclerView;
    private SelectStockListAdapter mAdapter;
    @BindView(R.id.tv_prompt)
    TextView mTvPrompt;
    @BindView(R.id.iv_prompt)
    ImageView mIvPrompt;
    @BindView(R.id.tv_add_select)
    TextView mTvAddSelect;
    @BindView(R.id.ll_empty)
    LinearLayout mLLEmpty;
    @BindView(R.id.optional_edit)
    ImageView mOptionalEdit;
    private Unbinder mUnbinder;
    private boolean isFirstLoad = true;
    private StockListModel mStockListModel;

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getContext(), R.layout.fragment_market_optional, null);
        mUnbinder = ButterKnife.bind(this, view);
        init();
        return view;
    }

    /**
     * 懒加载所需要加载的数据
     **/
    @Override
    public void lazyLoadView() {
        getStockList();
    }

    @Override
    public void onResume() {
        super.onResume();
        // 要确保 lazyLoadVie() 和 onResume() 都不是第一次执行
        if (!isFirstLoad && isFirstLoad() && mAdapter != null) {
            getStockList();
        } else {
            isFirstLoad = false;
        }
    }

    private void init() {
        ViewUtil.initSwipeRefreshLayout(mSwipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getStockList();
            }
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new SelectStockListAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mTvPrompt.setText(R.string.empty_data);
    }

    /**
     * 从网络上得到自选股的列表（会获取到全部的自选股， 没有分类）.没有加载更多
     */
    private void getStockList() {
        HooviewApiHelper.getInstance().getUserStockList(EVApplication.getUser().getName(), new MyRequestCallBack<StockListModel>() {
            @Override
            public void onSuccess(StockListModel result) {
                mSwipeRefreshLayout.setRefreshing(false);
                if (result == null) return;
                mStockListModel = result;
                if (result.getData() != null && result.getData().size() > 0) {
                    hideEmptyView();
                    mAdapter.setStockListModel(result.getData());
                    return;
                }
                showAddBtn();
            }

            @Override
            public void onFailure(String msg) {
                mSwipeRefreshLayout.setRefreshing(false);
                showAddBtn();
            }

            @Override
            public void onError(String errorInfo) {
                mSwipeRefreshLayout.setRefreshing(false);
                showAddBtn();
            }
        });
    }

    public static MarketOptionalFragment newInstance() {
        return new MarketOptionalFragment();
    }

    @OnClick(R.id.tv_add_select)
    public void onAddOptionalStock() {
        SearchStockActivity.start(getContext());
    }

    @OnClick(R.id.optional_edit)
    public void onEditOptionalStock() {
        EditMySelectedStockActivity.start(getContext(), mStockListModel);
    }

    public void showEmptyView() {
        mLLEmpty.setVisibility(View.VISIBLE);
        mOptionalEdit.setVisibility(View.GONE);
    }

    public void hideEmptyView() {
        if (mLLEmpty.getVisibility() != View.GONE) mLLEmpty.setVisibility(View.GONE);
        mOptionalEdit.setVisibility(View.VISIBLE);
    }

    public void showAddBtn() {
        showEmptyView();
        mTvAddSelect.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }
}
