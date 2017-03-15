package com.easyvaas.elapp.ui.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.easyvaas.elapp.ui.base.BaseActivity;
import com.hooview.app.R;


public class CashInProtocolActivity extends BaseActivity implements View.OnClickListener {
    public static void start(Context context) {
        Intent starter = new Intent(context, CashInProtocolActivity.class);
        context.startActivity(starter);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_in_protocl);
        findViewById(R.id.iv_back).setOnClickListener(this);
        TextView title = (TextView) findViewById(R.id.tv_title);
        title.setText(R.string.user_cash_in_protocol);
        WebView webView = (WebView) findViewById(R.id.web_view);
        webView.loadUrl("file:///android_asset/protocol.html");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }
}
