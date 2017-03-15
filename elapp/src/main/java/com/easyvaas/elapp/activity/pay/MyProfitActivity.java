/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.activity.pay;

import java.text.DecimalFormat;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easyvaas.common.gift.GiftManager;

import com.hooview.app.R;
import com.easyvaas.elapp.base.BaseActivity;
import com.easyvaas.elapp.bean.pay.MyAssetEntity;
import com.easyvaas.elapp.db.Preferences;
import com.easyvaas.elapp.net.ApiHelper;
import com.easyvaas.elapp.net.MyRequestCallBack;
import com.easyvaas.elapp.net.RequestUtil;
import com.easyvaas.elapp.utils.SingleToast;

public class MyProfitActivity extends BaseActivity {
    public static final String EXTRA_RICE_COUNT = "extra_rice_count";
    public static final String EXTRA_RISE_CASH = "extra_rise_cash";
    public static final String EXTRA_CASH_OUT_LIMIT = "extra_cash_out_limit";
    public static final String EXTRA_CASH_OUT_FEE_RATE = "extra_cash_out_fee_rate";
    public static final String EXTRA_CASH_OUT_ENABLED = "extra_cash_out_enable";

    private TextView mRiceCountTv;
    private TextView mRiseCashTv;
    private TextView mTodayRiseCashTv;
    private ImageView mCloseIv;
    private TextView mCommitTv;
    private TextView mCenterContentTv;
    private LinearLayout mCommitLl;
    private boolean mCashEnabled;

    private int mRiseCashAmount;
    private int mCashOutLimitAmount;
    private int mLimitCash;
    private int mCashValue;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.rise_cash_btn:
                    if (mRiseCashAmount < 2) {
                        SingleToast.show(getApplicationContext(), R.string.msg_cash_out_too_little);
                    } else {
                        Intent intent = new Intent(getApplicationContext(), CashOutActivity.class);
                        int cashOutFeeRate = getIntent().getIntExtra(EXTRA_CASH_OUT_FEE_RATE, 0);
                        intent.putExtra(MyProfitActivity.EXTRA_CASH_OUT_FEE_RATE, cashOutFeeRate);
                        intent.putExtra(MyProfitActivity.EXTRA_CASH_OUT_LIMIT, mCashOutLimitAmount);
                        startActivity(intent);
                    }
                    break;
                case R.id.add_option_view:
                    Intent intent = new Intent(MyProfitActivity.this, PayRecordListActivity.class);
                    intent.putExtra(PayRecordListActivity.EXTRA_ACTIVITY_TYPE,
                            PayRecordListActivity.TYPE_CASH_OUT);
                    startActivity(intent);
                    break;
                case R.id.close_iv:
                    finish();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profit);
        setTitle(R.string.my_profit);

        mRiceCountTv = (TextView) findViewById(R.id.rice_count_tv);
        mRiseCashTv = (TextView) findViewById(R.id.rise_cash_tv);
        mTodayRiseCashTv = (TextView) findViewById(R.id.today_rise_crash_tv);
        mCloseIv = (ImageView) findViewById(R.id.close_iv);
        mCommitTv = (TextView) findViewById(R.id.add_option_iv);
        mCenterContentTv = (TextView) findViewById(R.id.common_custom_title_tv);
        mCommitLl = (LinearLayout) findViewById(R.id.add_option_view);

        mCommitTv.setVisibility(View.VISIBLE);
        mCommitTv.setText(getString(R.string.cash_out_record));
        mCenterContentTv.setText(getString(R.string.my_profit));
        mCommitLl.setOnClickListener(mOnClickListener);
        mCloseIv.setOnClickListener(mOnClickListener);
        Button commitBtn = (Button) findViewById(R.id.rise_cash_btn);
        commitBtn.setOnClickListener(mOnClickListener);
        commitBtn.setClickable(mCashEnabled);
        commitBtn.setEnabled(mCashEnabled);

        long riceRoll = getIntent().getLongExtra(EXTRA_RICE_COUNT, 0);
        mCashValue = getIntent().getIntExtra(EXTRA_RISE_CASH, 0);
        mLimitCash = getIntent().getIntExtra(EXTRA_CASH_OUT_LIMIT, 0);
        updateView(riceRoll, mCashValue, mLimitCash, mCashEnabled);

        getUserAsset();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.complete, menu);
        menu.findItem(R.id.menu_complete).setTitle(R.string.cash_out_record);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_complete:
                Intent intent = new Intent(MyProfitActivity.this, PayRecordListActivity.class);
                intent.putExtra(PayRecordListActivity.EXTRA_ACTIVITY_TYPE,
                        PayRecordListActivity.TYPE_CASH_OUT);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getUserAsset() {
        showLoadingDialog(R.string.loading_data, false, true);
        ApiHelper.getInstance().getAssetInfo(new MyRequestCallBack<MyAssetEntity>() {
            @Override
            public void onSuccess(MyAssetEntity result) {
                if (result != null) {
                    Preferences pref = Preferences.getInstance(getApplicationContext());
                    pref.putLong(Preferences.KEY_PARAM_ASSET_E_COIN_ACCOUNT, result.getEcoin());
                    GiftManager.setECoinCount(getApplicationContext(), result.getEcoin());
                    mCashEnabled = false;
                    mCashValue = result.getCash();
                }
                dismissLoadingDialog();
            }

            @Override
            public void onError(String errorInfo) {
                super.onError(errorInfo);
                dismissLoadingDialog();
            }

            @Override
            public void onFailure(String msg) {
                dismissLoadingDialog();
                RequestUtil.handleRequestFailed(msg);
            }
        });
    }

    @Override
    protected void onDestroy() {
        mIsCancelRequestAfterDestroy = false;
        super.onDestroy();
    }

    private void updateView(long riceRoll, int cashValue, int limitCash, boolean cashEnabled) {
        mRiseCashAmount = cashValue;
        mCashOutLimitAmount = limitCash;
        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        mRiceCountTv.setText(riceRoll + "");
        mRiseCashTv.setText("￥" + decimalFormat.format(cashValue / 100.f) + "");
        mTodayRiseCashTv.setText("￥" + decimalFormat.format(limitCash / 100.f) + "");

        findViewById(R.id.rise_cash_btn).setClickable(cashEnabled);
        findViewById(R.id.rise_cash_btn).setEnabled(cashEnabled);
    }
}
