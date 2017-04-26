package com.easyvaas.elapp.view.live;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.easyvaas.elapp.db.Preferences;
import com.easyvaas.elapp.ui.base.mybase.AppConstants;
import com.hooview.app.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Date   2017/4/25
 * Editor  Misuzu
 * 支付弹出页
 */

public class PlayerPayDialogFragment extends BottomSheetDialogFragment {

    @BindView(R.id.pay_counts)
    TextView mPayCounts;
    @BindView(R.id.pay_coin_left)
    TextView mPayCoinLeft;
    @BindView(R.id.pay_submit_button)
    Button mPaySubmitButton;
    @BindView(R.id.pay_no_enough_tips)
    TextView mTips;
    int payCounts;  //付款金额
    boolean isNeedRecharge;
    Unbinder unbinder;
    PayOrRechargeListener mPayOrRechargeListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof PayOrRechargeListener)
            mPayOrRechargeListener = (PayOrRechargeListener) context;
    }



    @Override
    public void setupDialog(Dialog dialog, int style) {
        payCounts = getArguments().getInt(AppConstants.PARAMS);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.player_pay_dialog_layout, null);
        unbinder = ButterKnife.bind(this, view);
        dialog.setContentView(view);
    }


    private void setData(int payCounts)
    {
        mPayCounts.setText(String.valueOf(payCounts));
        int ecoin = (int) Preferences.getInstance(getContext()).getLong(Preferences.KEY_PARAM_ASSET_E_COIN_ACCOUNT,0);
        mPayCoinLeft.setText(String.valueOf(ecoin));
        if (payCounts <= ecoin)
        {
            isNeedRecharge = false;
            mPaySubmitButton.setText(R.string.player_go_buy);
            mTips.setVisibility(View.GONE);
        }else
        {
            isNeedRecharge = true;
            mPaySubmitButton.setText(R.string.player_go_recharge);
            mTips.setVisibility(View.VISIBLE);
        }


    }

    public static PlayerPayDialogFragment newInstance(int payCounts) {

        Bundle args = new Bundle();
        args.putInt(AppConstants.PARAMS, payCounts);
        PlayerPayDialogFragment fragment = new PlayerPayDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        setData(payCounts);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * 购买或者充值的跳转
     */
    @OnClick(R.id.pay_submit_button)
    public void onViewClicked() {

        if (isNeedRecharge)
            mPayOrRechargeListener.skipToReCharge();
        else
        {
            mPayOrRechargeListener.payForVideo();
            dismiss();
        }
    }

    public interface PayOrRechargeListener
    {
        /**
         * 余额足够 直接购买
         */
        public void payForVideo();

        /**
         * 余额不足 需要跳转充值
         */
        public void skipToReCharge();
    }
}
