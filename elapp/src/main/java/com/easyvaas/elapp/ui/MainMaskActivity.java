package com.easyvaas.elapp.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.easyvaas.elapp.ui.base.BaseActivity;
import com.hooview.app.R;

public class MainMaskActivity extends BaseActivity {

    private static final String TOP = "top";

    private MainActivity mMainActivity;
    private int mMarginTop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_mask);
        initData();
        initViews();
    }

    private void initData() {
        mMarginTop = getIntent().getIntExtra(TOP, 0);
    }


    private void initViews() {

    }

    //把对象设置进去，以便操作MainActivity
    public void setMainActivity(MainActivity activity) {
        this.mMainActivity = activity;
    }

    public static void start(Context context, int mTop) {
        Intent intent = new Intent(context, MainMaskActivity.class);
        intent.putExtra(TOP, mTop);
        context.startActivity(intent);
    }
}
