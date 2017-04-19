package com.easyvaas.elapp.ui.pop;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.easyvaas.elapp.utils.ViewUtil;
import com.hooview.app.R;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Date    2017/4/19
 * Author  xiaomao
 * 头像选择、性别选择
 */

public class OperationPopupWindow extends BasePopupWindow {

    @BindView(R.id.pop_button_1)
    TextView mPopButton1;
    @BindView(R.id.pop_button_2)
    TextView mPopButton2;
    @BindView(R.id.pop_cancel)
    TextView mPopCancel;

    public OperationPopupWindow(Activity activity) {
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
        setWidth((int) (ViewUtil.getScreenWidth(mActivity) - ViewUtil.dp2Px(mActivity, 60)));
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    /**
     * 设置内容
     */
    @Override
    protected View getView() {
        return LayoutInflater.from(mActivity).inflate(R.layout.popup_window_operation, null);
    }

    /**
     * 初始化
     */
    @Override
    protected void initViews() {

    }

    public void init(String up, String down) {
        if (mPopButton1 != null && up != null) {
            mPopButton1.setText(up);
        }
        if (mPopButton2 != null && down != null) {
            mPopButton2.setText(down);
        }
    }

    @OnClick(R.id.pop_button_1)
    public void onUpButtonClick() {
        if (mListener != null) {
            mListener.onUp();
        }
        close();
    }

    @OnClick(R.id.pop_button_2)
    public void onDownButtonClick() {
        if (mListener != null) {
            mListener.onDown();
        }
        close();
    }

    @OnClick(R.id.pop_cancel)
    public void onCancelClick() {
        close();
    }

    public interface OnOperationListener{
        void onUp();
        void onDown();
    }

    private OnOperationListener mListener;

    public void setOnOperationListener(OnOperationListener listener) {
        mListener = listener;
    }
}
