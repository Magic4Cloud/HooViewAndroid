/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.activity.pay;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hooview.app.activity.setting.BindUserAuthActivity;
import com.hooview.app.app.EVApplication;
import com.hooview.app.base.BaseActivity;
import com.hooview.app.bean.pay.MyAssetEntity;
import com.hooview.app.bean.user.User;
import com.hooview.app.net.ApiHelper;
import com.hooview.app.net.MyRequestCallBack;
import com.hooview.app.net.RequestUtil;
import com.hooview.app.utils.DialogUtil;
import com.hooview.app.utils.SingleToast;

public class CashOutActivity extends BaseActivity {
    private EditText mCashOutAmountEt;
    private TextView mCashOutBindWeixinTip;

    private TextView mCashOunAmountActualTv;
    private TextView mCashOutAmountTv;
    private TextView mCashOutFeeTv;
    private Button mCommitBtn;

    private ImageView mCloseIv;
    private TextView mCommitTv;
    private TextView mCenterContentTv;
    private LinearLayout mCommitLl;

    private int mCashOutAmount;
    private int mCashOutLimitAmount;
    private boolean mIsCashOutSuccess;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case com.hooview.app.R.id.commit_btn:
                    if (mIsCashOutSuccess) {
                        finish();
                    } else if (isWeixinBind()) {
                        String inputStr = mCashOutAmountEt.getText().toString();
                        if (!TextUtils.isEmpty(inputStr)) {
                            mCashOutAmount = Integer.parseInt(inputStr);
                        } else {
                            mCashOutAmount = 0;
                        }
                        if (mCashOutAmount < 2) {
                            SingleToast.show(getApplicationContext(), com.hooview.app.R.string.msg_cash_out_min_limit);
                        } else if (mCashOutAmount > mCashOutLimitAmount) {
                            SingleToast.show(getApplicationContext(), com.hooview.app.R.string.msg_cash_out_too_much);
                        } else {
                            hideInputMethod();
                            cashOutByWeixin(mCashOutAmount * 100);
                        }
                    } else {
                        SingleToast.show(getApplicationContext(), com.hooview.app.R.string.msg_need_bind_weixin_first,
                                SingleToast.LENGTH_LONG);
                        Intent bindIn = new Intent(getApplicationContext(), BindUserAuthActivity.class);
                        bindIn.putExtra(BindUserAuthActivity.EXTRA_KEY_IS_BIND_WEIXIN_CASH, true);
                        startActivity(bindIn);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(com.hooview.app.R.string.title_cash_out);
        setContentView(com.hooview.app.R.layout.activity_cash_out);

        mCashOutAmountEt = (EditText) findViewById(com.hooview.app.R.id.cash_out_amount_et);
        mCashOutAmountEt.requestFocus();
        mCashOutBindWeixinTip = (TextView) findViewById(com.hooview.app.R.id.cash_out_bind_weixin_tip);
        mCommitBtn = (Button) findViewById(com.hooview.app.R.id.commit_btn);
        mCloseIv = (ImageView) findViewById(com.hooview.app.R.id.close_iv);
        mCommitTv = (TextView) findViewById(com.hooview.app.R.id.add_option_iv);
        mCenterContentTv = (TextView) findViewById(com.hooview.app.R.id.common_custom_title_tv);
        mCommitLl = (LinearLayout) findViewById(com.hooview.app.R.id.add_option_view);
        mCenterContentTv.setText(getString(com.hooview.app.R.string.title_cash_out));
        mCommitBtn.setOnClickListener(mOnClickListener);

        mCashOutLimitAmount = getIntent().getIntExtra(MyProfitActivity.EXTRA_CASH_OUT_LIMIT, 0);
        mCashOutAmountEt.setHint(getString(com.hooview.app.R.string.cash_out_money_amount_hint, mCashOutLimitAmount / 100.f));

        mCashOunAmountActualTv = (TextView) findViewById(com.hooview.app.R.id.cash_amount_actual_tv);
        mCashOutAmountTv = (TextView) findViewById(com.hooview.app.R.id.rise_cash_amount_tv);
        mCashOutFeeTv = (TextView) findViewById(com.hooview.app.R.id.wx_procedure_fee_tv);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (isWeixinBind()) {
            mCashOutBindWeixinTip.setText(getResources().getString(com.hooview.app.R.string.cash_out_tip_bind));
        } else {
            mCashOutBindWeixinTip.setText(getResources().getString(com.hooview.app.R.string.cash_out_tip_no_bind));
        }
    }

    private boolean isWeixinBind() {
        User user = EVApplication.getUser();
        if (user != null) {
            for (User.AuthEntity authEntity : user.getAuth()) {
                if (User.AUTH_TYPE_WEIXIN.equals(authEntity.getType())) {
                    return true;
                }
            }
        }
        return false;
    }

    private void showCashOutSuccessView() {
        int feeRate = getIntent().getIntExtra(MyProfitActivity.EXTRA_CASH_OUT_FEE_RATE, 0);
        float produceFeeCount = (float) Math.ceil(mCashOutAmount * 100 * (feeRate / 10000.0)) / 100;
        float actualCount = mCashOutAmount - produceFeeCount;
        mCashOunAmountActualTv.setText(getString(com.hooview.app.R.string.cash_count, actualCount));
        mCashOutAmountTv.setText(getString(com.hooview.app.R.string.cash_count, Float.parseFloat(mCashOutAmount + "")));
        mCashOutFeeTv.setText(getString(com.hooview.app.R.string.cash_count, produceFeeCount));

        setTitle(com.hooview.app.R.string.title_rise_cash_detail);
        findViewById(com.hooview.app.R.id.cash_out_amount_input_ll).setVisibility(View.GONE);
        findViewById(com.hooview.app.R.id.cash_out_detail_rl).setVisibility(View.VISIBLE);
        mCommitBtn.setText(getResources().getString(com.hooview.app.R.string.complete));
    }

    private void cashOutByWeixin(int cashOutAmount) {
        showLoadingDialog(com.hooview.app.R.string.loading_data, false, true);
        ApiHelper.getInstance().cashOutByWeixin(cashOutAmount,
                new MyRequestCallBack<MyAssetEntity>() {
                    @Override
                    public void onSuccess(MyAssetEntity result) {
                        showCashOutSuccessView();
                        dismissLoadingDialog();
                        mIsCashOutSuccess = true;
                        SingleToast.show(getApplication(), com.hooview.app.R.string.msg_cash_out_success);
                        showCashOutSuccessRemind();
                    }

                    @Override
                    public void onError(String errorInfo) {
                        super.onError(errorInfo);
                        dismissLoadingDialog();
                        SingleToast.show(getApplication(), com.hooview.app.R.string.msg_cash_out_failed);
                    }

                    @Override
                    public void onFailure(String msg) {
                        RequestUtil.handleRequestFailed(msg);
                        dismissLoadingDialog();
                    }
                });
    }

    private void showCashOutSuccessRemind() {
        Dialog CashOutSuccessDialog = DialogUtil
                .getOneButtonDialog(CashOutActivity.this, getString(com.hooview.app.R.string.msg_cash_out_success_remind),
                        true, true, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
        CashOutSuccessDialog.show();
    }
}
