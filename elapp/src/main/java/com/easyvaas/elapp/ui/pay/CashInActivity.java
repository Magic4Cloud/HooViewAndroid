/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.ui.pay;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.easyvaas.common.gift.GiftManager;
import com.easyvaas.common.sharelogin.model.PlatformActionListener;
import com.easyvaas.common.sharelogin.wechat.WechatPayManager;
import com.easyvaas.elapp.ui.user.CashInProtocolActivity;
import com.hooview.app.R;
import com.easyvaas.elapp.activity.WebViewActivity;
import com.easyvaas.elapp.adapter.CashInAmountAdapter;
import com.easyvaas.elapp.base.BaseActivity;
import com.easyvaas.elapp.bean.pay.CashInOptionEntity;
import com.easyvaas.elapp.bean.pay.CashInOptionEntityArray;
import com.easyvaas.elapp.bean.pay.MyAssetEntity;
import com.easyvaas.elapp.bean.pay.PayOrderEntity;
import com.easyvaas.elapp.db.Preferences;
import com.easyvaas.elapp.net.ApiHelper;
import com.easyvaas.elapp.net.MyRequestCallBack;
import com.easyvaas.elapp.net.RequestUtil;
import com.easyvaas.elapp.pay.AlipayHelper;
import com.easyvaas.elapp.pay.PayResult;
import com.easyvaas.elapp.utils.Logger;
import com.easyvaas.elapp.utils.SingleToast;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

public class CashInActivity extends BaseActivity {
    private static final String TAG = "CashInActivity";

    private List<CashInOptionEntity> mCashInOptionList;
    private List<CashInOptionEntity> mCashInOptionWeixinList;
    private List<CashInOptionEntity> mCashInOptionAlipayList;
    private long mECoinBalance = 0;
    private int mSelectPosition = -1;

    private RadioButton mWeixinOptionRb;
    private RadioButton mAlipayOptionRb;
    private TextView mECoinBalanceTv;
    private CashInAmountAdapter mCashInAmountAdapter;

    private ImageView mCloseIv;
    private TextView mCommitTv;
    private TextView mCenterContentTv;
    private LinearLayout mCommitLl;

    private MyHandler mHandler;

    public static void start(Context context) {
        Intent starter = new Intent(context, CashInActivity.class);
        context.startActivity(starter);
    }

    public static class MyHandler extends Handler {
        private SoftReference<CashInActivity> softReference;

        public MyHandler(CashInActivity cashInActivity) {
            softReference = new SoftReference<>(cashInActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            final CashInActivity activity = softReference.get();
            if (activity == null) {
                return;
            }
            switch (msg.what) {
                case AlipayHelper.MSG_SDK_PAY_FLAG:
                    PayResult payResult = new PayResult((String) msg.obj);
                    Logger.d(TAG, "Pay result: " + payResult);
                    Log.d("pay","Pay result: " + payResult);
                    /**
                     * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
                     * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
                     * docType=1) 建议商户依赖异步通知
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息

                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        SingleToast.show(activity, R.string.msg_cash_in_success);
                        CashInOptionEntity cashedInEntity = activity.mCashInOptionList
                                .get(activity.mSelectPosition);
                        Logger.d(TAG, "handleMessage: " + cashedInEntity.toString());
                        activity.mECoinBalance += cashedInEntity.getEcoin() + cashedInEntity.getFree();
                        activity.updateECoinAmount(activity.mECoinBalance);
                        activity.dismissLoadingDialog();
                    } else {
                        // 判断resultStatus 为非"9000"则代表可能支付失败
                        // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，
                        // 最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            SingleToast.show(activity, R.string.msg_cash_in_confirmation);
                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            SingleToast.show(activity, R.string.msg_pay_failed);
                        }
                    }
                    break;
            }
        }
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.cash_in_confirm_btn:

                    if (mSelectPosition >= 0) {
                        CashInOptionEntity optionEntity = mCashInOptionList.get(mSelectPosition);
                        Logger.e(TAG, "Pay start: mSelectPosition=" + mSelectPosition + "   " + optionEntity.getRmb());
                        if (optionEntity.getPlatform() == CashInOptionEntity.PLATFORM_WEIXIN) {
                            showLoadingDialog(R.string.loading_data, false, true);
                            cashInByWeixin(optionEntity.getRmb());
                        } else if (optionEntity.getPlatform() == CashInOptionEntity.PLATFORM_ALIPAY) {
                            if (optionEntity.getRmb() > 0) {
                                showLoadingDialog(R.string.loading_data, false, true);
                                cashInByAlipay(optionEntity.getRmb());
                            } else {
                                SingleToast.show(getApplicationContext(),
                                        R.string.msg_cash_in_need_select_amount);
                            }
                        }
                    } else {
                        SingleToast.show(getApplicationContext(), R.string.msg_cash_in_need_select_amount);
                    }
                    break;
                case R.id.add_option_view:
                    Intent intent = new Intent(CashInActivity.this, PayRecordListActivity.class);
                    intent.putExtra(PayRecordListActivity.EXTRA_ACTIVITY_TYPE,
                            PayRecordListActivity.TYPE_CASH_IN);
                    startActivity(intent);
                    break;
                case R.id.close_iv:
                    finish();
                    break;
                case R.id.tv_user_protocol:
                    CashInProtocolActivity.start(CashInActivity.this);
                    break;
            }
        }
    };

    private RadioGroup.OnCheckedChangeListener mOnCheckedChangeListener
            = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (mSelectPosition >= 0) {
                mCashInOptionList.get(mSelectPosition).setIsChecked(false);
            }
            mSelectPosition = -1;
            hideInputMethod();
            switch (checkedId) {
                case R.id.cash_in_option_weixin_rb:
                    if (mCashInOptionWeixinList.size() > 0) {
                        mWeixinOptionRb.setVisibility(View.VISIBLE);
                        mCashInOptionList.clear();
                        mCashInOptionList.addAll(mCashInOptionWeixinList);
                        if (mCashInOptionList.size() > 0) {
                            mCashInOptionList.get(0).setIsChecked(true);
                            mSelectPosition = 0;
                        }
                        mWeixinOptionRb.setChecked(true);
                        showCashInOptionList();
                        mCashInAmountAdapter.notifyDataSetChanged();
                    } else {
                        mWeixinOptionRb.setVisibility(View.GONE);
                        mWeixinOptionRb.setChecked(false);
                    }
                    break;
                case R.id.cash_in_option_alipay_rb:
                    if (mCashInOptionAlipayList.size() > 0) {
                        mAlipayOptionRb.setVisibility(View.VISIBLE);
                        showCashInOptionList();
                        mCashInOptionList.clear();
                        mCashInOptionList.addAll(mCashInOptionAlipayList);
                        mAlipayOptionRb.setChecked(true);
                        if (mCashInOptionList.size() > 0) {
                            mCashInOptionList.get(0).setIsChecked(true);
                            mSelectPosition = 0;
                        }
                        mCashInAmountAdapter.notifyDataSetChanged();
                    } else {
                        mAlipayOptionRb.setVisibility(View.GONE);
                        mAlipayOptionRb.setChecked(false);
                    }
                    break;
            }
        }
    };

    private PlatformActionListener mPayListener = new PlatformActionListener() {
        @Override
        public void onComplete(Bundle userInfo) {
            SingleToast.show(getApplicationContext(), R.string.msg_cash_in_success);
            CashInOptionEntity entity = mCashInOptionList.get(mSelectPosition);
            mECoinBalance += entity.getEcoin() + entity.getFree();
            updateECoinAmount(mECoinBalance);
            dismissLoadingDialog();
        }

        @Override
        public void onError() {
            SingleToast.show(getApplicationContext(), R.string.msg_pay_failed);
            dismissLoadingDialog();
        }

        @Override
        public void onCancel() {
            SingleToast.show(getApplicationContext(), R.string.msg_pay_cancel);
            dismissLoadingDialog();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.cash_in);
        setContentView(R.layout.activity_cash_in);
        mHandler = new MyHandler(this);

        mCashInOptionList = new ArrayList<>();
        mCashInOptionWeixinList = new ArrayList<>();
        mCashInOptionAlipayList = new ArrayList<>();

        initView();
        loadCashInOptions();
        getUserAsset();
        findViewById(R.id.tv_user_protocol).setOnClickListener(mOnClickListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        dismissLoadingDialog();
    }

    private void initView() {
        mECoinBalance = Preferences.getInstance(this).getLong(Preferences.KEY_PARAM_ASSET_E_COIN_ACCOUNT, 0);
        mECoinBalanceTv = (TextView) findViewById(R.id.e_coin_balance_tv);
        TextView cashInExplainTv = (TextView) findViewById(R.id.cash_in_explain_tv);
        SpannableString spannableString = getClickableSpan();
        cashInExplainTv.setHighlightColor(getResources().getColor(android.R.color.transparent));
        cashInExplainTv.setMovementMethod(LinkMovementMethod.getInstance());
        cashInExplainTv.setText(spannableString);

        mCloseIv = (ImageView) findViewById(R.id.close_iv);
        mCommitTv = (TextView) findViewById(R.id.add_option_iv);
        mCenterContentTv = (TextView) findViewById(R.id.common_custom_title_tv);
        mCommitLl = (LinearLayout) findViewById(R.id.add_option_view);
        mCommitTv.setVisibility(View.VISIBLE);
        mCommitTv.setText("");
        mCenterContentTv.setText(getString(R.string.cash_in));
//        mCommitLl.setOnClickListener(mOnClickListener);
        mCloseIv.setOnClickListener(mOnClickListener);

        updateECoinAmount(mECoinBalance);
        ((RadioGroup) findViewById(R.id.cash_in_option_rg))
                .setOnCheckedChangeListener(mOnCheckedChangeListener);
        mWeixinOptionRb = (RadioButton) findViewById(R.id.cash_in_option_weixin_rb);
        mAlipayOptionRb = (RadioButton) findViewById(R.id.cash_in_option_alipay_rb);

        RecyclerView rechargeAmountRv = (RecyclerView) this.findViewById(R.id.cash_in_amount_rv);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        layoutManager.setOrientation(GridLayoutManager.VERTICAL);
        rechargeAmountRv.setLayoutManager(layoutManager);
        mCashInAmountAdapter = new CashInAmountAdapter(mCashInOptionList);
        mCashInAmountAdapter.setOnEndClickViewListener(new CashInAmountAdapter.OnConfirmClickListener() {
            @Override
            public void confirm() {
                if (mSelectPosition >= 0) {
                    CashInOptionEntity optionEntity = mCashInOptionList.get(mSelectPosition);
                    if (optionEntity.getPlatform() == CashInOptionEntity.PLATFORM_WEIXIN) {
                        showLoadingDialog(R.string.loading_data, false, true);
                        cashInByWeixin(optionEntity.getRmb());
                    } else if (optionEntity.getPlatform() == CashInOptionEntity.PLATFORM_ALIPAY) {
                        if (optionEntity.getRmb() > 0) {
                            showLoadingDialog(R.string.loading_data, false, true);
                            cashInByAlipay(optionEntity.getRmb());
                        } else {
                            SingleToast.show(getApplicationContext(),
                                    R.string.msg_cash_in_need_select_amount);
                        }
                    }
                } else {
                    SingleToast.show(getApplicationContext(), R.string.msg_cash_in_need_select_amount);
                }
            }

            @Override
            public void explain() {
                String url = Preferences.getInstance(CashInActivity.this)
                        .getString(Preferences.KEY_PARAM_ASSET_FAQ_URL);
                Intent intentFaq = new Intent(CashInActivity.this, WebViewActivity.class);
                intentFaq.putExtra(WebViewActivity.EXTRA_KEY_URL, url);
                intentFaq.putExtra(WebViewActivity.EXTRA_KEY_TITLE, getString(R.string.faq_assert));
                startActivity(intentFaq);

            }
        });
//        mCashInAmountAdapter.setOnclickViewListener(new CashInAmountAdapter.OnclickViewListener() {
//            @Override
//            public void onCheckedChange(int position) {
//                CashInOptionEntity optionEntity = mCashInOptionList.get(position);
//                if (mSelectPosition >= 0) {
//                    mCashInOptionList.get(mSelectPosition).setIsChecked(false);
//                }
//                optionEntity.setIsChecked(true);
//                mSelectPosition = position;
//                mHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        mCashInAmountAdapter.notifyDataSetChanged();
//                    }
//                });
//            }
//        });
        mCashInAmountAdapter.setOnItemSelectListener(new CashInAmountAdapter.OnItemSelectListener() {
            @Override
            public void onItemSelect(int position) {
                mSelectPosition = position;
            }
        });
        rechargeAmountRv.setAdapter(mCashInAmountAdapter);

        findViewById(R.id.cash_in_confirm_btn).setOnClickListener(mOnClickListener);
    }

    private void showCashInOptionList() {
        findViewById(R.id.cash_in_amount_lv).setVisibility(View.VISIBLE);
    }

    private SpannableString getClickableSpan() {
        ClickableSpan clickableSpan = new ClickableSpan() {
            public void onClick(View v) {
                Intent intent = new Intent(CashInActivity.this, WebViewActivity.class);
                intent.putExtra(WebViewActivity.EXTRA_KEY_TITLE, getString(R.string.app_name));
                intent.putExtra(WebViewActivity.EXTRA_KEY_URL, Preferences.getInstance(CashInActivity.this)
                        .getString(Preferences.KEY_PARAM_WEB_CHAT_INFO_US_URL));
                startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                if (ds != null) {
                    ds.setColor(getResources().getColor(R.color.text_common));
                }
            }
        };
        String kindlyReminder = getResources().getString(R.string.kindly_reminder);

        SpannableString sInfo = new SpannableString(kindlyReminder);
        sInfo.setSpan(clickableSpan, kindlyReminder.length() - 9, kindlyReminder.length() - 6,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return sInfo;
    }

    private void updateECoinAmount(long amount) {
        mECoinBalanceTv.setText(amount + "");
//        SpannableString spannableString = new SpannableString(mECoinBalanceTv.getText());
//        spannableString.setSpan(new RelativeSizeSpan(1.5f), 0, mECoinBalanceTv.getText().length() - 2,
//                SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
//        mECoinBalanceTv.setText(spannableString);

        Preferences.getInstance(CashInActivity.this)
                .putLong(Preferences.KEY_PARAM_ASSET_E_COIN_ACCOUNT, mECoinBalance);
        GiftManager.setECoinCount(getApplicationContext(), mECoinBalance);
    }

    private void getUserAsset() {
        ApiHelper.getInstance().getAssetInfo(new MyRequestCallBack<MyAssetEntity>() {
            @Override
            public void onSuccess(MyAssetEntity result) {
                if (result != null) {
                    updateECoinAmount(result.getEcoin());
                }
            }

            @Override
            public void onError(String errorInfo) {
                super.onError(errorInfo);
            }

            @Override
            public void onFailure(String msg) {
                RequestUtil.handleRequestFailed(msg);
            }
        });
    }

    private void loadCashInOptions() {
        showLoadingDialog(R.string.loading_data, false, false);
        ApiHelper.getInstance().getCashInOptions(new MyRequestCallBack<CashInOptionEntityArray>() {
            @Override
            public void onSuccess(CashInOptionEntityArray result) {
                if (result != null && result.getOptionlist() != null) {
                    mCashInOptionList.clear();
                    boolean isHaveAlipay = false;
                    List<CashInOptionEntity> optionList = result.getOptionlist();
                    for (int i = 0; i < optionList.size(); i++) {
                        if (optionList.get(i).getPlatform() == CashInOptionEntity.PLATFORM_ALIPAY) {
                            isHaveAlipay = true;
                            break;
                        }
                    }
                    if (isHaveAlipay) {
                        CashInOptionEntity cashInOptionEntity = new CashInOptionEntity();
                        int rmb = Preferences.getInstance(getApplicationContext())
                                .getInt(Preferences.KEY_LAST_CUSTOM_ALIPAY_COIN, -1);
                        if (rmb > 0) {
                            cashInOptionEntity.setRmb(rmb * 100);
                            cashInOptionEntity.setEcoin(rmb * 10);
                        }
//                        CashInOptionEntity cashInOptionEndEntity = new CashInOptionEntity();
//                        cashInOptionEndEntity.setPinned(CashInOptionEntity.PINNED_END);
//                        cashInOptionEndEntity.setPlatform(CashInOptionEntity.PLATFORM_ALIPAY);
//                        optionList.add(cashInOptionEndEntity);
                    }
//                    CashInOptionEntity cashInOptionWeiXinEndEntity = new CashInOptionEntity();
//                    cashInOptionWeiXinEndEntity.setPinned(CashInOptionEntity.PINNED_END);
//                    cashInOptionWeiXinEndEntity.setPlatform(CashInOptionEntity.PLATFORM_WEIXIN);
//                    optionList.add(cashInOptionWeiXinEndEntity);
                    updateCashInOptions(optionList);
                    mCashInAmountAdapter.notifyDataSetChanged();
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
            }
        });
    }

    private void updateCashInOptions(List<CashInOptionEntity> list) {
        for (CashInOptionEntity option : list) {
            if (option.getPlatform() == CashInOptionEntity.PLATFORM_WEIXIN) {
                mCashInOptionWeixinList.add(option);
            } else if (option.getPlatform() == CashInOptionEntity.PLATFORM_ALIPAY) {
                mCashInOptionAlipayList.add(option);
            }
        }
        Logger.e(TAG, "wechat " + mCashInOptionWeixinList.size() + "   alipay" + mCashInOptionAlipayList.size());
        // 微信列表
//        if (mCashInOptionWeixinList.size() > 0) {
//            mWeixinOptionRb.setVisibility(View.VISIBLE);
////            mCashInOptionList.addAll(mCashInOptionWeixinList);
//            mWeixinOptionRb.setChecked(true);
//            mCashInOptionList = mCashInOptionWeixinList;
//            mCashInAmountAdapter.notifyDataSetChanged();
//            showCashInOptionList();
//        } else {
//            mWeixinOptionRb.setVisibility(View.GONE);
//            mWeixinOptionRb.setChecked(false);
//        }
        // 支付宝列表
        if (mCashInOptionAlipayList.size() > 0) {
            mAlipayOptionRb.setVisibility(View.VISIBLE);
            mAlipayOptionRb.setChecked(true);
            mCashInOptionList = mCashInOptionAlipayList;
            mCashInAmountAdapter.notifyDataSetChanged();
            showCashInOptionList();
        } else {
            mAlipayOptionRb.setVisibility(View.GONE);
            mAlipayOptionRb.setChecked(false);
        }
    }

    private void cashInByWeixin(int cashInECoinAmount) {
        Logger.e(TAG, "wechat pay...");
        ApiHelper.getInstance().cashInByWeixin(cashInECoinAmount,
                new MyRequestCallBack<PayOrderEntity>() {
                    @Override
                    public void onSuccess(PayOrderEntity result) {
                        WechatPayManager payManager = new WechatPayManager(CashInActivity.this);
                        payManager.pay(result.getAppid(), result.getPartnerid(), result.getPrepayid(),
                                result.getPackageX(), result.getNoncestr(), result.getTimestamp(),
                                result.getSign(), mPayListener);
                    }

                    @Override
                    public void onError(String errorInfo) {
                        super.onError(errorInfo);
                        Logger.e(TAG, "onError" + errorInfo);
                        dismissLoadingDialog();
                        Toast.makeText(CashInActivity.this, "连接失败，请稍后再试", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(String msg) {
                        RequestUtil.handleRequestFailed(msg);
                    }
                });
    }

    private void cashInByAlipay(final int cashInECoinAmount) {
        Logger.e(TAG, "alipay pay...");
        ApiHelper.getInstance().cashInByAlipay(cashInECoinAmount,
                new MyRequestCallBack<PayOrderEntity>() {
                    @Override
                    public void onSuccess(PayOrderEntity result) {
                        if (result != null) {
                            new AlipayHelper(CashInActivity.this, mHandler).aliPay(result);
                        }
                    }

                    @Override
                    public void onError(String errorInfo) {
                        super.onError(errorInfo);
                        Logger.e(TAG, "onError" + errorInfo);
                        dismissLoadingDialog();
                        Toast.makeText(CashInActivity.this, "连接失败，请稍后再试", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(String msg) {
                        RequestUtil.handleRequestFailed(msg);
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.complete, menu);
        menu.findItem(R.id.menu_complete).setTitle(R.string.cash_in_record);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_complete:
                Intent intent = new Intent(this, PayRecordListActivity.class);
                intent.putExtra(PayRecordListActivity.EXTRA_ACTIVITY_TYPE,
                        PayRecordListActivity.TYPE_CASH_IN);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        mIsCancelRequestAfterDestroy = false;
        super.onDestroy();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                hideInputMethod();
                break;
        }
        return super.onTouchEvent(event);
    }
}
