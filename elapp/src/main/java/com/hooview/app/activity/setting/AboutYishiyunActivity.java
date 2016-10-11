/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.activity.setting;

import android.os.Bundle;
import android.widget.TextView;

import com.hooview.app.R;
import com.hooview.app.base.BaseActivity;

public class AboutYishiyunActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_copyright);

        setTitle(R.string.about_us);

        TextView textView = (TextView) findViewById(R.id.content_tv);
        textView.setText(R.string.about_yizhibo_content);
    }
}
