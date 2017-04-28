package com.easyvaas.elapp.ui.pop;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

import butterknife.ButterKnife;

/**
 * PopupWindow基类
 * <p/>
 * Created by xiaomao on 2016/9/12.
 */
public abstract class BasePopupWindow extends PopupWindow {

    protected Activity mActivity;
    protected View mView;

    public BasePopupWindow(Activity activity) {
        mActivity = activity;
        // 设置出入动画
        this.setAnimationStyle(getAnimation());
        // 设置宽高
        setLayoutParams();
        // 防止被底部虚拟按键挡住
        this.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        this.setFocusable(true);
        this.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // 消失事件
        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                recoverAlpha();
            }
        });
        // 外部可点击
        this.setOutsideTouchable(true);
        this.update();
        // view
        mView = getView();
        setContentView(mView);
        ButterKnife.bind(this, mView);
        initViews();
    }

    /**
     * 设置出入动画
     */
    protected abstract int getAnimation();

    /**
     * 设置宽高等
     */
    protected abstract void setLayoutParams();

    /**
     * 设置内容
     */
    protected abstract View getView();

    /**
     * 初始化
     */
    protected abstract void initViews();

    /**
     * View.findViewById(int id)
     *
     * @param resId int
     * @return View
     */
    public View findViewById(int resId) {
        View view = null;
        if (mView != null) {
            view = mView.findViewById(resId);
        }
        return view;
    }

    /**
     * 设置activity背景透明度
     *
     * @param alpha
     */
    protected void setBackgroundAlpha(float alpha) {
        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
        lp.alpha = alpha;
        if (alpha == 1) {
            // 不移除该Flag的话,在有视频的页面上的视频会出现黑屏的bug
            mActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        } else {
            // 此行代码主要是解决在华为手机上半透明效果无效的bug
            mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }
        mActivity.getWindow().setAttributes(lp);
    }

    /**
     * 恢复Activity的透明效果
     */
    protected void recoverAlpha() {
        setBackgroundAlpha(1f);
    }

    /**
     * 设置Activity透明*
     */
    protected void setActivityAlpha() {
        setBackgroundAlpha(0.6f);
    }

    /**
     * 显示
     */
    public void showAtBottom() {
        this.showAtLocation(getContentView(), Gravity.BOTTOM, 0, 0);
        setActivityAlpha();
    }

    /**
     * 显示
     */
    public void showAtCenter() {
        this.showAtLocation(getContentView(), Gravity.CENTER, 0, 0);
        setActivityAlpha();
    }

    @Override
    public void showAsDropDown(View anchor) {
        super.showAsDropDown(anchor);
        setActivityAlpha();
    }

    public void showAsDropDownNoAlpha(View anchor) {
        super.showAsDropDown(anchor);
    }

    /**
     * 显示在某个位置
     *
     * @param gravity int
     */
    public void showAtLocation(int gravity) {
        this.showAtLocation(getContentView(), gravity, 0, 0);
        setActivityAlpha();
    }

    public View getRootView() {
        return mView;
    }

    /**
     * 关闭，dismiss
     */
    public void close() {
        if (this.isShowing()) {
            this.dismiss();
            recoverAlpha();
        }
    }

}
