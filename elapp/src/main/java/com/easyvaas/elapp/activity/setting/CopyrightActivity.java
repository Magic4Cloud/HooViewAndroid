/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.activity.setting;

import android.os.Bundle;

import com.hooview.app.R;
import com.easyvaas.elapp.base.BaseActivity;

public class CopyrightActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_copyright);

        setTitle(R.string.copyright);
    }
}
