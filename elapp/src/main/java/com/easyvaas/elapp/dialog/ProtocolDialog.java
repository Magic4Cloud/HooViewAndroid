package com.easyvaas.elapp.dialog;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.hooview.app.R;

public class ProtocolDialog extends Dialog {
    public static void showProtocol(Context context) {
        ProtocolDialog protocolDialog = new ProtocolDialog(context);
        protocolDialog.show();
    }

    public ProtocolDialog(Context context) {
        super(context);
    }
    public ProtocolDialog(Context context, int themeResId) {
        super(context, R.style.BaseUserDialog);
    }
    private ImageView mIvBack;
    private TextView mTvTitle;
    private TextView mTvComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = this.getWindow();
        WindowManager.LayoutParams wl = window.getAttributes();
        window.getDecorView().setPadding(0, 0, 0, 0);
        wl.gravity = Gravity.BOTTOM;
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.MATCH_PARENT;
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        setContentView(R.layout.dialog_protocol);
        assignViews();
    }

    private void assignViews() {
        mIvBack = (ImageView) findViewById(R.id.iv_back);
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mTvComplete = (TextView) findViewById(R.id.tv_complete);
        mTvTitle.setText(R.string.user_protocol);
    }
}
