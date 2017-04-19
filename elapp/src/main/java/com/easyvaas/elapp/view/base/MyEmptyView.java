package com.easyvaas.elapp.view.base;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hooview.app.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Date   2017/4/19
 * Editor  Misuzu
 * 界面状态显示View
 */

public class MyEmptyView extends LinearLayout {

    @BindView(R.id.empty_img)
    ImageView mEmptyImg;
    @BindView(R.id.empty_txt)
    TextView mEmptyTxt;
    @BindView(R.id.empty_jump)
    TextView mEmptyJump;
    @BindView(R.id.empty_layout)
    LinearLayout mEmptyLayout;

    public MyEmptyView(Context context) {
        super(context);
        initView();
    }

    public MyEmptyView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public MyEmptyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        inflate(getContext(), R.layout.my_empty_layout, this);
        ButterKnife.bind(this,this);
    }

    /**
     * 设置显示图片
     */
    public void setEmptyImg(@DrawableRes int imgRes)
    {
        mEmptyImg.setImageResource(imgRes);
    }

    /**
     * 设置提示文字
     */
    public void setEmptyTxt(String text)
    {
        mEmptyTxt.setText(text);
    }

    /**
     * 设置跳转按钮和提示文字
     */
    public void setEmptyJump(String text,OnClickListener listener)
    {
        mEmptyImg.setVisibility(VISIBLE);
        mEmptyJump.setText(text);
        mEmptyJump.setVisibility(VISIBLE);
    }

    /**
     * 设置无数据时 默认显示
     */
    public void showEmptyOnNoData()
    {
        setVisibility(VISIBLE);
        mEmptyJump.setVisibility(GONE);
    }

    /**
     * 设置网络错误时 默认显示
     */
    public void showErrorOnNetError(OnClickListener listener)
    {
        setVisibility(VISIBLE);
        mEmptyJump.setVisibility(VISIBLE);
        mEmptyTxt.setText(getContext().getString(R.string.empty_net_error));
        mEmptyJump.setText(getContext().getString(R.string.empty_net_refresh));
        mEmptyJump.setOnClickListener(listener);
    }

    /**
     * 隐藏空View
     */
    public void hideEmptyView()
    {
        setVisibility(GONE);
    }
}
