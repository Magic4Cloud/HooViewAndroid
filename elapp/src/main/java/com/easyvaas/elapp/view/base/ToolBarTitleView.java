package com.easyvaas.elapp.view.base;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hooview.app.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Date   2017/4/18
 * Editor  Misuzu
 * 通用标题栏
 */

public class ToolBarTitleView extends LinearLayout {

    @BindView(R.id.title_back)
    ImageView mTitleBack;
    @BindView(R.id.title_right_img)
    ImageView mTitleRightImg;
    @BindView(R.id.title_text_right)
    TextView mTitleTextRight;
    @BindView(R.id.title_text)
    TextView mTitleText;
    @BindView(R.id.title_toolbar)
    Toolbar mTitleToolbar;

    public ToolBarTitleView(Context context) {
        super(context);
        initView();
    }

    public ToolBarTitleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ToolBarTitleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        inflate(getContext(), R.layout.toolbar_title_layout, this);
        ButterKnife.bind(this, this);
    }

    /**
     * 设置标题
     *
     * @param title 标题
     */
    public void setTitleText(String title) {
        mTitleText.setText(title);
    }

    /**
     * 设置右边图标
     *
     * @param imgRes   图片资源
     * @param listener 点击监听
     */
    public void setTitleRightImg(@DrawableRes int imgRes, @Nullable OnClickListener listener) {
        mTitleRightImg.setVisibility(VISIBLE);
        mTitleRightImg.setImageResource(imgRes);
        mTitleRightImg.setOnClickListener(listener);
    }

    /**
     * 设置右边文字
     *
     * @param text     文字
     * @param color    文字颜色
     * @param listener 点击监听
     */
    public void setTitleTextRight(String text, @ColorRes int color, @Nullable OnClickListener listener) {
        mTitleTextRight.setVisibility(VISIBLE);
        mTitleTextRight.setText(text);
        mTitleTextRight.setOnClickListener(listener);
        mTitleTextRight.setTextColor(ContextCompat.getColor(getContext(), color));
    }

    /**
     * back 返回监听
     *
     * @param listener 点击监听
     */
    public void setTitleBackListener(@Nullable OnClickListener listener) {
        mTitleBack.setOnClickListener(listener);
    }


}
