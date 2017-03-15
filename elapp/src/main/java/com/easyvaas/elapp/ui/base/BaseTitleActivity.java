package com.easyvaas.elapp.ui.base;


import android.view.View;
import android.widget.TextView;

import com.hooview.app.R;

public class BaseTitleActivity extends BaseActivity {
    protected TextView mTvTitle;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        View view = findViewById(R.id.tv_title);
        if (view != null) {
            mTvTitle = (TextView) view;
        }
        View back = findViewById(R.id.iv_back);
        if (back != null) {
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }
}
