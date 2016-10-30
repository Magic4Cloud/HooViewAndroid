package com.easyvaas.common.chat.base;

import android.os.Bundle;

public abstract class BaseFragmentActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mIsFragmentActivity = true;
        super.onCreate(savedInstanceState);
    }
}
