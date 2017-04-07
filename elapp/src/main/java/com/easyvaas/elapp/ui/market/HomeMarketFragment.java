package com.easyvaas.elapp.ui.market;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.bean.market.StockListModel;
import com.easyvaas.elapp.db.Preferences;
import com.easyvaas.elapp.event.MarketRefreshEvent;
import com.easyvaas.elapp.ui.base.BaseFragment;
import com.easyvaas.elapp.ui.search.GlobalSearchActivity;
import com.easyvaas.elapp.ui.user.LoginActivity;
import com.hooview.app.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 行情
 */
public class HomeMarketFragment extends BaseFragment implements View.OnClickListener {
    private FragmentManager mFm;
    private MarketMainFragment mMarketMainFragment;
    private MySelectedStockMainFragment mMySelectedStockMainFragment;
    private StockListModel stockListModel;
    @BindView(R.id.tv_market)
    RadioButton mRbMarket;
    @BindView(R.id.tv_my_stock)
    RadioButton mRbMyStock;
    @BindView(R.id.iv_edit)
    ImageView mIvEdit;
    @BindView(R.id.iv_search)
    ImageView mIvSearch;
    @BindView(R.id.iv_refresh)
    ImageView mIvRefresh;
    private Unbinder mUnbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getContext(), R.layout.fragment_home_market, null);
        EventBus.getDefault().register(this);
        mUnbinder = ButterKnife.bind(this, view);
        init();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mRbMarket.isChecked()) {
            showMarket();
        } else {
            showMyStock();
        }
    }

    /**
     * 初始化
     */
    private void init() {
        // 设置监听
        mRbMarket.setOnClickListener(this);
        mRbMyStock.setOnClickListener(this);
        mIvEdit.setOnClickListener(this);
        mIvSearch.setOnClickListener(this);
        mFm = getChildFragmentManager();
        mIvRefresh.setOnClickListener(this);
        // 子fragment
        mMarketMainFragment = new MarketMainFragment();
        mMySelectedStockMainFragment = new MySelectedStockMainFragment();
        mFm.beginTransaction()
                .add(R.id.fl_content, mMarketMainFragment)
                .add(R.id.fl_content, mMySelectedStockMainFragment)
                .hide(mMySelectedStockMainFragment)
                .show(mMarketMainFragment).commit();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 市场
            case R.id.tv_market:
                showMarket();
                break;
            // 自选
            case R.id.tv_my_stock:
                if (EVApplication.isLogin() && Preferences.getInstance(getContext()).isLogin()) {
                    // 已登录，显示自选
                    showMyStock();
                } else {
                    // 未登录，去登录
                    mRbMarket.setChecked(true);
                    LoginActivity.start(getContext());
                }
                break;
            // 编辑自选
            case R.id.iv_edit:
                EditMySelectedStockActivity.start(getContext(), stockListModel);
                break;
            // 搜索
            case R.id.iv_search:
                GlobalSearchActivity.start(getContext());
                break;
            // 刷新
            case R.id.iv_refresh:
                EventBus.getDefault().post(new MarketRefreshEvent());
                break;
        }
    }

    /**
     * 显示市场，隐藏自选
     */
    private void showMarket() {
        mFm.beginTransaction().hide(mMySelectedStockMainFragment).show(mMarketMainFragment).commit();
        mIvEdit.setVisibility(View.GONE);
    }

    /**
     * 显示自选，隐藏市场
     */
    private void showMyStock() {
        mFm.beginTransaction().hide(mMarketMainFragment).show(mMySelectedStockMainFragment).commit();
        mIvEdit.setVisibility(View.VISIBLE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(StockListModel event) {
        stockListModel = event;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
