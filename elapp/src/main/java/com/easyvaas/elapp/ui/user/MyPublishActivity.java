package com.easyvaas.elapp.ui.user;

import android.widget.FrameLayout;

import com.easyvaas.elapp.ui.base.mybase.MyBaseActivity;
import com.hooview.app.R;

import butterknife.BindView;

/**
 * Date    2017/4/20
 * Author  xiaomao
 * 个人中心---我的发布
 */

public class MyPublishActivity extends MyBaseActivity {

    @BindView(R.id.fl_content)
    FrameLayout mFrameLayout;

    @Override
    protected int getLayout() {
        return R.layout.activity_my_publish;
    }

    @Override
    protected String getTitleText() {
        return "我的发布";
    }

    @Override
    protected void initViewAndData() {
        getSupportFragmentManager().beginTransaction().add(R.id.fl_content, MyLivingFragment.newInstance()).commit();
    }
}
