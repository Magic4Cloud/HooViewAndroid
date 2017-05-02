package com.easyvaas.elapp.ui.pop;

import android.app.Activity;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

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
    @BindView(R.id.pop_confirm_tv)
    TextView mPopConfirmTv;
    private Handler mHandler;
    private String mInputText;

    public UserInfoEditPopupWindow(Activity activity) {
        super(activity);
        mHandler = new Handler();
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
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
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
        mPopInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String s = editable.toString();
                if (TextUtils.isEmpty(s) || s.equals(mPopInput.getHint().toString().trim())) {
                    mPopConfirmTv.setBackgroundColor(mActivity.getResources().getColor(R.color.base_gray));
                    mPopConfirm.setEnabled(false);
                } else {
                    mPopConfirmTv.setBackgroundColor(mActivity.getResources().getColor(R.color.tab_text_color_selected));
                    mPopConfirm.setEnabled(true);
                }
            }
        });
        mPopConfirm.setEnabled(false);
    }

    /**
     * 初始化
     */
    public UserInfoEditPopupWindow init(String text) {
        mInputText = text;
        mPopInput.setText(text);
        if (!TextUtils.isEmpty(text)) {
            mPopConfirmTv.setBackgroundColor(mActivity.getResources().getColor(R.color.tab_text_color_selected));
            mPopConfirm.setEnabled(true);
        }
        return this;
    }

    /**
     * 设置输入最大长度
     *
     * @param length int > 0
     */
    public UserInfoEditPopupWindow setMaxLength(int length) {
        if (mPopInput != null && length > 0) {
            InputFilter[] filters = new InputFilter[1];
            filters[0] = new InputFilter.LengthFilter(length);
            mPopInput.setFilters(filters);
        }
        return this;
    }

    public UserInfoEditPopupWindow setHint(String hint) {
        mPopInput.setHint(hint);
        return this;
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
        mPopConfirmTv.setBackgroundColor(mActivity.getResources().getColor(R.color.base_gray));
        mPopConfirm.setEnabled(false);
        close();
    }

    public interface OnConfirmListener {
        void onConfirm(String text);
    }

    private OnConfirmListener mListener;

    public void setOnConfirmListener(OnConfirmListener listener) {
        mListener = listener;
    }

    public void showWithInputMethod() {
        showAtBottom();
        mPopInput.requestFocus();
        showInputMethod();
    }

    /**
     * 异步弹出软键盘
     */
    protected void showInputMethod() {
        if (mHandler == null) {
            mHandler = new Handler();
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }, 0);
    }

    /**
     * 隐藏软键盘
     */
    protected void hideInputMethod() {
        InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(INPUT_METHOD_SERVICE);
        if (mActivity.getCurrentFocus() != null) {
            if (mActivity.getCurrentFocus().getWindowToken() != null) {
                imm.hideSoftInputFromWindow(mActivity.getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    @Override
    public void close() {
        super.close();
        hideInputMethod();
    }
}
