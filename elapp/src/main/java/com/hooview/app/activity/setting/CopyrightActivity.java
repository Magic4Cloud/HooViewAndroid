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

public class CopyrightActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_copyright);

        TextView tv = (TextView) findViewById(R.id.common_custom_title_tv);
        tv.setText(R.string.copyright);

        findViewById(R.id.close_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }

        });
    }
}
