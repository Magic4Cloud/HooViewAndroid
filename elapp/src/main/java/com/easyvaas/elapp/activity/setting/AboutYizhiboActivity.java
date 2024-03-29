/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.activity.setting;

import android.os.Bundle;
import android.widget.TextView;

import com.hooview.app.R;
import com.easyvaas.elapp.base.BaseActivity;

public class AboutYizhiboActivity extends BaseActivity {
    private TextView tvVersion = null;
    private Bundle bundle = null;
    private String mCurrentVersion = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_copyright);

        setTitle(R.string.about_us);

        TextView textView = (TextView) findViewById(R.id.content_tv);
        textView.setText(R.string.about_yizhibo_content);
    }
}
