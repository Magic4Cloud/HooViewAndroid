package com.easyvaas.elapp.ui.pop;

import android.app.Activity;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.hooview.app.R;

import butterknife.BindView;
import butterknife.OnClick;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Date    2017/4/19
 * Author  xiaomao
 * 介绍自己、详细资料
 */

public class UserInfoEditPopupWindow extends BasePopupWindow {

    @BindView(R.id.pop_input)
    EditText mPopInput;
    @BindView(R.id.cv_confirm)
    CardView mPopConfirm;
    @BindView(R.id.cv_cancel)
    CardView mPopCancel;

    public UserInfoEditPopupWindow(Activity activity) {
        super(activity);
    }

    /**
     * 设置出入动画
     */
    @Override
    protected int getAnimation() {
        return 0;
    }

    /**
     * 设置宽高等
     */
    @Override
    protected void setLayoutParams() {
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
    }

    /**
     * 设置内容
     */
    @Override
    protected View getView() {
        return LayoutInflater.from(mActivity).inflate(R.layout.popup_window_user_info_edit, null);
    }

    /**
     * 初始化
     */
    @Override
    protected void initViews() {

    }

    /**
     * 取消
     */
    @OnClick(R.id.cv_cancel)
    public void onCancelClick() {
        close();
    }

    /**
     * 确定
     */
    @OnClick(R.id.cv_confirm)
    public void onConfirmClick() {
        String text = "";
        if (mPopInput != null) {
            text = mPopInput.getText().toString().trim();
        }
        if (mListener != null) {
            mListener.onConfirm(text);
        }
        close();
    }

    public EditText getPopInput() {
        return mPopInput;
    }

    public interface OnConfirmListener {
        void onConfirm(String text);
    }

    private OnConfirmListener mListener;

    public void setOnConfirmListener(OnConfirmListener listener) {
        mListener = listener;
    }

    public void show() {
        showAtBottom();
        mPopInput.requestFocus();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }, 0);
    }

    protected void showInputMethod(View view) {
        InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }

    public void hideInputMethod() {
        InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(INPUT_METHOD_SERVICE);
        if (mActivity.getCurrentFocus() != null) {
            if (mActivity.getCurrentFocus().getWindowToken() != null) {
                imm.hideSoftInputFromWindow(mActivity.getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }
}
