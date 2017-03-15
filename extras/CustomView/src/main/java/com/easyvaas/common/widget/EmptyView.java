package com.easyvaas.common.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class EmptyView extends RelativeLayout {
    public final static int TYPE_EMPTY = 1;
    public final static int TYPE_LOADING = 2;
    public final static int TYPE_BUTTON = 3;
    public final static int TYPE_ERROR = 4;

    private ImageView mImageView;
    private TextView mTitleView;
    private TextView mSubTitleView;
    private Button mButton;
    private ImageView mLoadingIcon;

    // Default values
    private int mEmptyType = TYPE_EMPTY;

    public EmptyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public EmptyView(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context){
        LayoutInflater.from(context).inflate(R.layout.view_empty_view, this);
        mImageView = (ImageView) findViewById(R.id.empty_iv);
        mTitleView = (TextView) findViewById(R.id.empty_title_tv);
        mSubTitleView = (TextView) findViewById(R.id.empty_sub_title_tv);
        mButton = (Button) findViewById(R.id.empty_btn);
        mLoadingIcon = (ImageView) findViewById(R.id.loading_icon_iv);

        hide();
    }

    public void hide() {
        if (getVisibility() == VISIBLE) {
            setVisibility(GONE);
        }
    }

    public void showEmptyView() {
        show(TYPE_EMPTY);
    }

    public void showLoadingView() {
        show(TYPE_LOADING);
    }

    public void showButtonView() {
        show(TYPE_BUTTON);
    }

    public void showErrorView() {
        show(TYPE_ERROR);
    }

    private void show(int type) {
        switch (type) {
            case TYPE_EMPTY:
                mImageView.setVisibility(VISIBLE);
                mTitleView.setVisibility(VISIBLE);
                mSubTitleView.setVisibility(VISIBLE);
                mButton.setVisibility(GONE);
                mLoadingIcon.setVisibility(GONE);
                setVisibility(VISIBLE);
                break;
            case TYPE_ERROR:
                mImageView.setVisibility(VISIBLE);
                mTitleView.setVisibility(VISIBLE);
                mSubTitleView.setVisibility(GONE);
                mButton.setVisibility(GONE);
                mLoadingIcon.setVisibility(GONE);
                setVisibility(VISIBLE);
                break;
            case TYPE_BUTTON:
                mImageView.setVisibility(GONE);
                mTitleView.setVisibility(VISIBLE);
                mSubTitleView.setVisibility(VISIBLE);
                mButton.setVisibility(VISIBLE);
                mLoadingIcon.setVisibility(GONE);
                setVisibility(VISIBLE);
                break;
            case TYPE_LOADING:
                mImageView.setVisibility(GONE);
                mTitleView.setVisibility(GONE);
                mSubTitleView.setVisibility(GONE);
                mButton.setVisibility(GONE);
                mLoadingIcon.setVisibility(GONE);
                mEmptyType = TYPE_LOADING;
                setVisibility(VISIBLE);
                break;
        }
        mEmptyType = type;
    }

    public void setEmptyIcon(int drawableId) {
        mImageView.setImageResource(drawableId);
    }

    public ImageView getEmptyIcon() {
        return mImageView;
    }

    public void setTitle(String title) {
        mTitleView.setText(title);
    }

    public TextView getTitleTextView() {
        return mTitleView;
    }

    public void setSubTitle(String title) {
        mSubTitleView.setText(title);
        if (mEmptyType == TYPE_EMPTY) {
            mSubTitleView.setVisibility(View.VISIBLE);
        }
    }

    public void setSubTitleBackGround(String title, OnClickListener onClickListener) {
        mSubTitleView.setText(title);
        mSubTitleView.setBackgroundResource(R.drawable.round_btn_selector);
        mSubTitleView.setOnClickListener(onClickListener);
        if (mEmptyType == TYPE_EMPTY) {
            mSubTitleView.setVisibility(View.VISIBLE);
        }
    }

    public TextView getSubTitleTextView() {
        return mSubTitleView;
    }

    public void setButtonClickListener(OnClickListener listener) {
        mButton.setOnClickListener(listener);
    }

    public Button getButtonView() {
        return mButton;
    }

    public int getEmptyType() {
        return mEmptyType;
    }
}

