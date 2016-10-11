/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.activity.setting;

import android.os.Bundle;

import com.hooview.app.base.BaseActivity;

public class CopyrightActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.hooview.app.R.layout.activity_copyright);

        setTitle(com.hooview.app.R.string.copyright);
    }
}
