/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.ui.pay;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easyvaas.common.gift.GiftManager;
import com.easyvaas.common.recycler.PullToLoadView;
import com.easyvaas.elapp.adapter.oldRecycler.PayCommonRcvAdapter;
import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.base.BaseRvcActivity;
import com.easyvaas.elapp.bean.pay.MyAssetEntity;
import com.easyvaas.elapp.bean.pay.PayCommonRecordEntityArray;
import com.easyvaas.elapp.bean.pay.PayRecordListEntity;
import com.easyvaas.elapp.db.Preferences;
import com.easyvaas.elapp.net.ApiConstant;
import com.easyvaas.elapp.net.ApiHelper;
import com.easyvaas.elapp.net.JsonParserUtil;
import com.easyvaas.elapp.net.MyRequestCallBack;
import com.easyvaas.elapp.net.RequestUtil;
import com.easyvaas.elapp.utils.NumberUtil;
import com.easyvaas.elapp.utils.SingleToast;
import com.hooview.app.R;

import java.util.ArrayList;
import java.util.List;

public class PayRecordListActivity extends BaseRvcActivity {
    public static final String EXTRA_ACTIVITY_TYPE = "extra_activity_type";
    public static final String TYPE_BARLEY = "barley_record";
    public static final String TYPE_CASH_OUT = "cash_out_record";
    public static final String TYPE_CASH_IN = "cash_in_record";

    private List<PayRecordListEntity> mRecordListEntityList;
    private PayCommonRcvAdapter mAdapter;
    private TextView mTotalAmountTv;
    private ImageView mCloseIv;
    private TextView mCommitTv;
    private TextView mCenterContentTv;
    private LinearLayout mCommitLl;
    private TextView mHootViewValueTv;
    private TextView mCashDetailTv;
    private TextView mCostTv;
    private TextView mCastTv;
    private TextView tvIncome;
    private TextView tvFireEyeballs;
    private TextView tvFireEyeballsUnit;
    private Button mCashInBt;
    private String mType;
    private boolean mIsLoadMore;

    private MyRequestCallBack<PayCommonRecordEntityArray> mRequestCallBack
            = new MyRequestCallBack<PayCommonRecordEntityArray>() {
        @Override
        public void onSuccess(PayCommonRecordEntityArray result) {
            if (result != null) {
                if (!mIsLoadMore) {
                    mRecordListEntityList.clear();
//                    if (result.getCount() == 0) {
//                        findViewById(R.id.line_bottom_iv).setVisibility(View.GONE);
//                    } else {
//                        findViewById(R.id.line_bottom_iv).setVisibility(View.VISIBLE);
//                    }
                }
                mRecordListEntityList.addAll(result.getRecordlist());
                mAdapter.notifyDataSetChanged();
                mNextPageIndex = result.getNext();

            }

            onRefreshComplete(result == null ? 0 : result.getCount());
        }

        @Override
        public void onFailure(String msg) {
            RequestUtil.handleRequestFailed(msg);
            onRequestFailed(msg);
        }

        @Override
        public void onError(String msg) {
            SingleToast.show(PayRecordListActivity.this, R.string.msg_no_score_record);
            onRefreshComplete(0);
        }
    };

    private MyRequestCallBack<String> mGetTotalCallBack = new MyRequestCallBack<String>() {
        @Override
        public void onSuccess(String result) {
            if (!TextUtils.isEmpty(result)) {
                int totalAmount = JsonParserUtil.getInt(result, ApiConstant.KEY_TOTAL_AMOUNT);
                updateAmountTotalInfo(totalAmount > 0 ? totalAmount / 100 : 0);
            }
        }

        @Override
        public void onFailure(String msg) {
            RequestUtil.handleRequestFailed(msg);
            onRequestFailed(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hootview_pay_record_list);
        mCostTv = (TextView) findViewById(R.id.tv_cost);
        mCastTv = (TextView) findViewById(R.id.tv_cash);
        mCastTv.setSelected(true);
        mType = getIntent().getStringExtra(EXTRA_ACTIVITY_TYPE);
        mEmptyView.setEmptyIcon(R.drawable.ic_cry);
        if (mType.equals(TYPE_CASH_OUT)) {
            setTitle(R.string.cash_out_record);
            mEmptyView.setTitle(getString(R.string.empty_no_cash_out));
        } else if (mType.equals(TYPE_CASH_IN)) {
            setTitle(R.string.cash_in_record);
            mEmptyView.setTitle(getString(R.string.empty_no_amount));
        }

        mRecordListEntityList = new ArrayList<PayRecordListEntity>();
        mPullToLoadRcvView = (PullToLoadView) findViewById(R.id.pull_load_view);
        mTotalAmountTv = (TextView) findViewById(R.id.total_amount_tv);
        mCloseIv = (ImageView) findViewById(R.id.close_iv);
        mCloseIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mCommitTv = (TextView) findViewById(R.id.add_option_iv);
        mCenterContentTv = (TextView) findViewById(R.id.common_custom_title_tv);
        mCommitLl = (LinearLayout) findViewById(R.id.add_option_view);
        mHootViewValueTv = (TextView) findViewById(R.id.tv_pay_value_header);
        mCashInBt = (Button) findViewById(R.id.bt_cash_in_confirm);
        mCashDetailTv = (TextView) findViewById(R.id.tv_pay_detail);
        tvIncome = (TextView) findViewById(R.id.tv_income);
        tvFireEyeballs = (TextView) findViewById(R.id.tv_fire_eyeballs);
        tvFireEyeballsUnit = (TextView) findViewById(R.id.tv_fire_eyeballs_unit);
        mCashInBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CashInActivity.start(PayRecordListActivity.this);
            }
        });
        mCostTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCostTv.setSelected(true);
                mCastTv.setSelected(false);
                mEmptyView.setTitle(getString(R.string.msg_no_score_record));
                mEmptyView.setVisibility(View.VISIBLE);
            }
        });
        mCastTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCostTv.setSelected(false);
                mCastTv.setSelected(true);
                mEmptyView.setVisibility(View.GONE);
                mPullToLoadRcvView.setVisibility(View.VISIBLE);
                mPullToLoadRcvView.initLoad();
            }
        });
        updateECoin();
        if (mType.equals(TYPE_CASH_OUT)) {
            mCenterContentTv.setText(getString(R.string.my_sum));
        } else if (mType.equals(TYPE_CASH_IN)) {
            mCenterContentTv.setText(getString(R.string.my_sum));
        }
        updateAmountTotalInfo(0);
        GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 1);
        layoutManager.setOrientation(GridLayoutManager.VERTICAL);
        mPullToLoadRcvView.getRecyclerView().setLayoutManager(layoutManager);
        mAdapter = new PayCommonRcvAdapter(getApplicationContext(), mRecordListEntityList, mType);
        mPullToLoadRcvView.getRecyclerView().setAdapter(mAdapter);
        mPullToLoadRcvView.initLoad();
        isShowFireEyeballs(EVApplication.getUser().getVip() == 1 ? View.VISIBLE : View.GONE);
        getAssetInfo();
    }

    private void updateAmountTotalInfo(int totalAmount) {
        if (mType.equals(TYPE_CASH_OUT)) {
            mTotalAmountTv.setText(getString(R.string.cash_out_amount_total, totalAmount));
        } else if (mType.equals(TYPE_CASH_IN)) {
            mTotalAmountTv.setText(getString(R.string.recharge_amount_total, totalAmount));
        }
    }
    @Override
    protected void loadData(final boolean isLoadMore) {
        super.loadData(isLoadMore);
        mIsLoadMore = isLoadMore;
        if (mType.equals(TYPE_CASH_OUT)) {
            ApiHelper.getInstance().getCashOutRecords(mNextPageIndex, ApiConstant.DEFAULT_PAGE_SIZE,
                    mRequestCallBack);
            ApiHelper.getInstance().getCashOutTotal(mGetTotalCallBack);
        } else if (mType.equals(TYPE_CASH_IN)) {
            ApiHelper.getInstance().getCashInRecords(mNextPageIndex, ApiConstant.DEFAULT_PAGE_SIZE,
                    mRequestCallBack);
            ApiHelper.getInstance().getRechargeTotal(mGetTotalCallBack);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateECoin();
    }

    private void updateECoin(){
        if (mHootViewValueTv != null) {
            long eCoinBalance = Preferences.getInstance(this).getLong(Preferences.KEY_PARAM_ASSET_E_COIN_ACCOUNT, 0);
            mHootViewValueTv.setText(NumberUtil.format(eCoinBalance) + getString(R.string.hooview_coin));
        }
    }

    private void isShowFireEyeballs(int status){
        tvIncome.setVisibility(status);
        tvFireEyeballs.setVisibility(status);
        tvFireEyeballsUnit.setVisibility(status);
    }

    // 得到火眼币和火眼豆
    private void getAssetInfo(){
        ApiHelper.getInstance().getAssetInfo(new MyRequestCallBack<MyAssetEntity>() {
            @Override
            public void onSuccess(MyAssetEntity result) {
                if (result != null){
                    tvFireEyeballs.setText(result.getRiceroll() + "");
                    mHootViewValueTv.setText(NumberUtil.format(result.getEcoin()) + getString(R.string.hooview_coin));
                    Preferences.getInstance(PayRecordListActivity.this).putLong(Preferences.KEY_PARAM_ASSET_E_COIN_ACCOUNT, result.getEcoin());
                    GiftManager.setECoinCount(PayRecordListActivity.this, result.getEcoin());
                }
            }

            @Override
            public void onFailure(String msg) {

            }
        });
    }

}
