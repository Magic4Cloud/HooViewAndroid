package com.easyvaas.elapp.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hooview.app.R;

public class CommonItemButton extends RelativeLayout {
    private ImageView mIvLeft;
    private TextView mTvTitle;
    private TextView mTvPrompt;
    private ImageView mIvRight;

    public CommonItemButton(Context context) {
        this(context, null);
    }

    public CommonItemButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        assignViews(attrs);
    }

    private void assignViews(AttributeSet attrs) {
        LayoutInflater.from(getContext()).inflate(R.layout.view_common_item_button, this, true);
        mIvLeft = (ImageView) findViewById(R.id.iv_left);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mTvPrompt = (TextView) findViewById(R.id.tv_prompt);
        mIvRight = (ImageView) findViewById(R.id.iv_right);
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CommonItemButton);
        String title = typedArray.getString(R.styleable.CommonItemButton_title1);
        String prompt = typedArray.getString(R.styleable.CommonItemButton_prompt);
        float drawableLeftMarginLeft = typedArray.getDimension(R.styleable.CommonItemButton_drawableLeftMarginLeft, 0);
        float drawableRightMarginRight = typedArray.getDimension(R.styleable.CommonItemButton_drawableRightMarginRight, 0);
        float titleMarginLeft = typedArray.getDimension(R.styleable.CommonItemButton_titleMarginLeft, 0);
        float promptMarginRight = typedArray.getDimension(R.styleable.CommonItemButton_promptMarginRight, 0);
        Drawable drawableRight = typedArray.getDrawable(R.styleable.CommonItemButton_drawableRight);
        Drawable drawableLeft = typedArray.getDrawable(R.styleable.CommonItemButton_drawableLeft);
        typedArray.recycle();
        if (TextUtils.isEmpty(title)) {
            mTvTitle.setVisibility(INVISIBLE);
        } else {
            mTvTitle.setVisibility(VISIBLE);
            mTvTitle.setText(title);
        }
        if (TextUtils.isEmpty(prompt)) {
            mTvPrompt.setVisibility(INVISIBLE);
        } else {
            mTvPrompt.setVisibility(VISIBLE);
            mTvPrompt.setText(prompt);
        }
        if (drawableLeft != null) {
            mIvLeft.setVisibility(VISIBLE);
            mIvLeft.setImageDrawable(drawableLeft);
        } else {
            mIvLeft.setVisibility(INVISIBLE);
        }

        if (drawableRight != null) {
            mIvRight.setVisibility(VISIBLE);
            mIvRight.setImageDrawable(drawableRight);
        } else {
            mIvRight.setVisibility(INVISIBLE);
        }
        RelativeLayout.LayoutParams layoutParam = (LayoutParams) mIvLeft.getLayoutParams();
        layoutParam.leftMargin = (int) drawableLeftMarginLeft;
        mIvLeft.setLayoutParams(layoutParam);

        layoutParam = (LayoutParams) mTvTitle.getLayoutParams();
        layoutParam.leftMargin = (int) titleMarginLeft;
        mTvTitle.setLayoutParams(layoutParam);

//        LinearLayout.LayoutParams linearLayoutParam = (LinearLayout.LayoutParams) mTvPrompt.getLayoutParams();
//        linearLayoutParam.rightMargin = (int) promptMarginRight;
//        mTvPrompt.setLayoutParams(layoutParam);
//        RelativeLayout.LayoutParams layoutParamRight = (LayoutParams) mIvRight.getLayoutParams();
//        layoutParamRight.rightMargin = (int) drawableRightMarginRight;
//        mIvRight.setLayoutParams(layoutParam);
    }

    public void setPrompt(String prompt) {
        mTvPrompt.setVisibility(VISIBLE);
        mTvPrompt.setText(prompt);
    }

    public void setPrompt(int id) {
        mTvPrompt.setVisibility(VISIBLE);
        mTvPrompt.setText(id);
    }

    public void setTitle(String prompt) {
        mTvTitle.setVisibility(VISIBLE);
        mTvTitle.setText(prompt);
    }

    public void setTitle(int id) {
        mTvTitle.setVisibility(VISIBLE);
        mTvTitle.setText(id);
    }


}
