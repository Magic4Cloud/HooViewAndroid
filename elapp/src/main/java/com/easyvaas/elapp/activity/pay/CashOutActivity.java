/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.activity.pay;

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

import com.hooview.app.R;
import com.easyvaas.elapp.activity.setting.BindUserAuthActivity;
import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.base.BaseActivity;
import com.easyvaas.elapp.bean.pay.MyAssetEntity;
import com.easyvaas.elapp.bean.user.User;
import com.easyvaas.elapp.net.ApiHelper;
import com.easyvaas.elapp.net.MyRequestCallBack;
import com.easyvaas.elapp.net.RequestUtil;
import com.easyvaas.elapp.utils.DialogUtil;
import com.easyvaas.elapp.utils.SingleToast;

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
                case R.id.commit_btn:
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
                            SingleToast.show(getApplicationContext(), R.string.msg_cash_out_min_limit);
                        } else if (mCashOutAmount > mCashOutLimitAmount) {
                            SingleToast.show(getApplicationContext(), R.string.msg_cash_out_too_much);
                        } else {
                            hideInputMethod();
                            cashOutByWeixin(mCashOutAmount * 100);
                        }
                    } else {
                        SingleToast.show(getApplicationContext(), R.string.msg_need_bind_weixin_first,
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
        setTitle(R.string.title_cash_out);
        setContentView(R.layout.activity_cash_out);

        mCashOutAmountEt = (EditText) findViewById(R.id.cash_out_amount_et);
        mCashOutAmountEt.requestFocus();
        mCashOutBindWeixinTip = (TextView) findViewById(R.id.cash_out_bind_weixin_tip);
        mCommitBtn = (Button) findViewById(R.id.commit_btn);
        mCloseIv = (ImageView) findViewById(R.id.close_iv);
        mCommitTv = (TextView) findViewById(R.id.add_option_iv);
        mCenterContentTv = (TextView) findViewById(R.id.common_custom_title_tv);
        mCommitLl = (LinearLayout) findViewById(R.id.add_option_view);
        mCenterContentTv.setText(getString(R.string.title_cash_out));
        mCommitBtn.setOnClickListener(mOnClickListener);

        mCashOutLimitAmount = getIntent().getIntExtra(MyProfitActivity.EXTRA_CASH_OUT_LIMIT, 0);
        mCashOutAmountEt.setHint(getString(R.string.cash_out_money_amount_hint, mCashOutLimitAmount / 100.f));

        mCashOunAmountActualTv = (TextView) findViewById(R.id.cash_amount_actual_tv);
        mCashOutAmountTv = (TextView) findViewById(R.id.rise_cash_amount_tv);
        mCashOutFeeTv = (TextView) findViewById(R.id.wx_procedure_fee_tv);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (isWeixinBind()) {
            mCashOutBindWeixinTip.setText(getResources().getString(R.string.cash_out_tip_bind));
        } else {
            mCashOutBindWeixinTip.setText(getResources().getString(R.string.cash_out_tip_no_bind));
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
        mCashOunAmountActualTv.setText(getString(R.string.cash_count, actualCount));
        mCashOutAmountTv.setText(getString(R.string.cash_count, Float.parseFloat(mCashOutAmount + "")));
        mCashOutFeeTv.setText(getString(R.string.cash_count, produceFeeCount));

        setTitle(R.string.title_rise_cash_detail);
        findViewById(R.id.cash_out_amount_input_ll).setVisibility(View.GONE);
        findViewById(R.id.cash_out_detail_rl).setVisibility(View.VISIBLE);
        mCommitBtn.setText(getResources().getString(R.string.complete));
    }

    private void cashOutByWeixin(int cashOutAmount) {
        showLoadingDialog(R.string.loading_data, false, true);
        ApiHelper.getInstance().cashOutByWeixin(cashOutAmount,
                new MyRequestCallBack<MyAssetEntity>() {
                    @Override
                    public void onSuccess(MyAssetEntity result) {
                        showCashOutSuccessView();
                        dismissLoadingDialog();
                        mIsCashOutSuccess = true;
                        SingleToast.show(getApplication(), R.string.msg_cash_out_success);
                        showCashOutSuccessRemind();
                    }

                    @Override
                    public void onError(String errorInfo) {
                        super.onError(errorInfo);
                        dismissLoadingDialog();
                        SingleToast.show(getApplication(), R.string.msg_cash_out_failed);
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
                .getOneButtonDialog(CashOutActivity.this, getString(R.string.msg_cash_out_success_remind),
                        true, true, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
        CashOutSuccessDialog.show();
    }
}
