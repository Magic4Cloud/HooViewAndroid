package com.easyvaas.elapp.dialog;


import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.hooview.app.R;


public class CommonPromptDialog extends Dialog implements View.OnClickListener {
    private View contentView;
    private FrameLayout mFlContentContainer;
    private TextView mTvTitle;
    private TextView mTvLeft;
    private TextView mTvRight;

    public void setOnButtonClickListen(OnButtonClickListener mListener) {
        this.mListener = mListener;
    }

    private OnButtonClickListener mListener;

    public CommonPromptDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_common_dialog);
        setCancelable(false);
        assignViews();
    }

    private void assignViews() {
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mTvLeft = (TextView) findViewById(R.id.tv_left);
        mTvRight = (TextView) findViewById(R.id.tv_right);
        mTvRight.setOnClickListener(this);
        mTvLeft.setOnClickListener(this);
    }

    public void setPrompt(String prompt) {
        mTvTitle.setText(prompt);
    }

    public void setContentView(View contentView) {
        if (contentView == null) {
            return;
        }
        mFlContentContainer.removeAllViews();
        mFlContentContainer.addView(contentView);
    }


    public void setButtonText(String left, String right) {
        if (TextUtils.isEmpty(left)) {
            mTvLeft.setVisibility(View.GONE);
        } else {
            mTvLeft.setVisibility(View.VISIBLE);
            mTvLeft.setText(left);
        }
        if (TextUtils.isEmpty(right)) {
            mTvRight.setVisibility(View.GONE);
        } else {
            mTvRight.setVisibility(View.VISIBLE);
            mTvRight.setText(right);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_left:
                if (mListener != null) {
                    mListener.onLeftButtonClick(v);
                }
                break;
            case R.id.tv_right:
                if (mListener != null) {
                    mListener.onRightButtonClick(v);
                }
                break;
        }
    }

    public interface OnButtonClickListener {
        public void onLeftButtonClick(View view);

        public void onRightButtonClick(View view);
    }
}
