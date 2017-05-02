package com.easyvaas.elapp.utils;

import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hooview.app.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Date    2017/5/2
 * Author  xiaomao
 * 自定义toast
 */

public class ToastHelper {

    private static ToastHelper INSTANCE = null;
    private Toast mToast = null;
    private Context mContext = null;
    private boolean mIsShowing = false;
    private Handler mHandler = null;
    @BindView(R.id.toast_icon)
    ImageView mToastIcon;
    @BindView(R.id.toast_text)
    TextView mToastText;

    private ToastHelper(Context context) {
        mContext = context;
        mHandler = new Handler();
        mIsShowing = false;
        init();
    }

    public static ToastHelper getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new ToastHelper(context);
        }
        return INSTANCE;
    }

    private void init() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_hooview_toast, null);
        ButterKnife.bind(this, view);
        mToast = new Toast(mContext);
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.setView(view);
    }

    public ToastHelper setToastIcon(int resId) {
        if (mToastIcon != null) {
            mToastIcon.setImageResource(resId);
        }
        return INSTANCE;
    }

    public ToastHelper setToastText(String text) {
        if (mToastText != null) {
            mToastText.setText(text);
        }
        return INSTANCE;
    }

    public void show() {
        if (mIsShowing || mToast == null) {
            return;
        }
        mToast.show();
        mIsShowing = true;
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mIsShowing = false;
            }
        }, 1200);
    }
}
