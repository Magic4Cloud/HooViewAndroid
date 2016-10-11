/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.activity.setting;

import android.os.Bundle;
import android.widget.TextView;

import com.hooview.app.base.BaseActivity;

public class AboutYizhiboActivity extends BaseActivity {
    private TextView tvVersion = null;
    private Bundle bundle = null;
    private String mCurrentVersion = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.hooview.app.R.layout.activity_copyright);

        setTitle(com.hooview.app.R.string.about_us);

        TextView textView = (TextView) findViewById(com.hooview.app.R.id.content_tv);
        textView.setText(com.hooview.app.R.string.about_yizhibo_content);
    }
}
