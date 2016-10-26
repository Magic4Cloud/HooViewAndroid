/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.activity.setting;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.hooview.app.R;
import com.hooview.app.base.BaseActivity;

public class AboutHooViewActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_hooview);

        TextView tv = (TextView) findViewById(R.id.common_custom_title_tv);
        tv.setText(R.string.about_us);
        findViewById(R.id.close_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }

        });
        TextView textView = (TextView) findViewById(R.id.content_tv);
        textView.setText(R.string.about_yizhibo_content);
    }
}
