package com.easyvaas.elapp.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.hooview.app.R;


public class ShareRightPanelDialog extends BaseRightDialog {
    private View.OnClickListener mOnclickListener;

    public ShareRightPanelDialog(Context context, View.OnClickListener onclickListener) {
        super(context);
        this.mOnclickListener = onclickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_share_right_pannel);
        assignViews();
    }

    private void assignViews() {
        findViewById(R.id.iv_qq).setOnClickListener(mOnclickListener);
        findViewById(R.id.iv_zone).setOnClickListener(mOnclickListener);
        findViewById(R.id.iv_wechat).setOnClickListener(mOnclickListener);
        findViewById(R.id.iv_weixin_circle).setOnClickListener(mOnclickListener);
        findViewById(R.id.iv_weibo).setOnClickListener(mOnclickListener);
    }

}
