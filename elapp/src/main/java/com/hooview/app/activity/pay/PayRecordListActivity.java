/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.activity.pay;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easyvaas.common.recycler.PullToLoadView;

import com.hooview.app.adapter.recycler.PayCommonRcvAdapter;
import com.hooview.app.base.BaseRvcActivity;
import com.hooview.app.bean.pay.PayCommonRecordEntityArray;
import com.hooview.app.bean.pay.PayRecordListEntity;
import com.hooview.app.net.ApiConstant;
import com.hooview.app.net.ApiHelper;
import com.hooview.app.net.JsonParserUtil;
import com.hooview.app.net.MyRequestCallBack;
import com.hooview.app.net.RequestUtil;
import com.hooview.app.utils.SingleToast;

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
    private String mType;
    private boolean mIsLoadMore;

    private MyRequestCallBack<PayCommonRecordEntityArray> mRequestCallBack
            = new MyRequestCallBack<PayCommonRecordEntityArray>() {
        @Override
        public void onSuccess(PayCommonRecordEntityArray result) {
            if (result != null) {
                if (!mIsLoadMore) {
                    mRecordListEntityList.clear();
                    if (result.getCount() == 0) {
                        findViewById(com.hooview.app.R.id.line_bottom_iv).setVisibility(View.GONE);
                    } else {
                        findViewById(com.hooview.app.R.id.line_bottom_iv).setVisibility(View.VISIBLE);
                    }
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
            SingleToast.show(PayRecordListActivity.this, com.hooview.app.R.string.msg_no_score_record);
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
        setContentView(com.hooview.app.R.layout.activity_pay_record_list);
        mType = getIntent().getStringExtra(EXTRA_ACTIVITY_TYPE);
        mEmptyView.setEmptyIcon(com.hooview.app.R.drawable.personal_empty);
        if (mType.equals(TYPE_CASH_OUT)) {
            setTitle(com.hooview.app.R.string.cash_out_record);
            mEmptyView.setTitle(getString(com.hooview.app.R.string.empty_no_cash_out));
        } else if (mType.equals(TYPE_CASH_IN)) {
            setTitle(com.hooview.app.R.string.cash_in_record);
            mEmptyView.setTitle(getString(com.hooview.app.R.string.empty_no_amount));
        }

        mRecordListEntityList = new ArrayList<PayRecordListEntity>();
        mPullToLoadRcvView = (PullToLoadView) findViewById(com.hooview.app.R.id.pull_load_view);
        mTotalAmountTv = (TextView) findViewById(com.hooview.app.R.id.total_amount_tv);
        mCloseIv = (ImageView) findViewById(com.hooview.app.R.id.close_iv);
        mCloseIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mCommitTv = (TextView) findViewById(com.hooview.app.R.id.add_option_iv);
        mCenterContentTv = (TextView) findViewById(com.hooview.app.R.id.common_custom_title_tv);
        mCommitLl = (LinearLayout) findViewById(com.hooview.app.R.id.add_option_view);
        if (mType.equals(TYPE_CASH_OUT)) {
            mCenterContentTv.setText(getString(com.hooview.app.R.string.cash_out_record));
        } else if (mType.equals(TYPE_CASH_IN)) {
            mCenterContentTv.setText(getString(com.hooview.app.R.string.cash_in_record));
        }
        updateAmountTotalInfo(0);
        GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 1);
        layoutManager.setOrientation(GridLayoutManager.VERTICAL);
        mPullToLoadRcvView.getRecyclerView().setLayoutManager(layoutManager);
        mAdapter = new PayCommonRcvAdapter(getApplicationContext(), mRecordListEntityList, mType);
        mPullToLoadRcvView.getRecyclerView().setAdapter(mAdapter);
        mPullToLoadRcvView.initLoad();
    }

    private void updateAmountTotalInfo(int totalAmount) {
        if (mType.equals(TYPE_CASH_OUT)) {
            mTotalAmountTv.setText(getString(com.hooview.app.R.string.cash_out_amount_total, totalAmount));
        } else if (mType.equals(TYPE_CASH_IN)) {
            mTotalAmountTv.setText(getString(com.hooview.app.R.string.recharge_amount_total, totalAmount));
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

}
