package com.easyvaas.elapp.ui.news;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.easyvaas.elapp.ui.base.mybase.MyBaseActivity;
import com.hooview.app.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Date   2017/4/12
 * Editor  Misuzu
 * 专题界面
 */

public class TopicActivity extends MyBaseActivity {


    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.iv_content)
    ImageView ivContent;
    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.topic_fragment)
    FrameLayout topicFragment;
    @BindView(R.id.topic_back)
    ImageView topicBack;
    @BindView(R.id.topic_title)
    TextView topicTitle;
    @BindView(R.id.toolbar_line)
    View toolbarLine;

    @Override
    protected int getLayout() {
        return R.layout.acivity_topic_layout;
    }

    @Override
    protected void initViewAndData() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }
        initToolBar();
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().add(R.id.topic_fragment, TopicFragment.newInstance()).commit();
    }

    private void initToolBar() {
        appbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int scrollRangle = appBarLayout.getTotalScrollRange();
                if (verticalOffset == 0) {
                    topicTitle.setAlpha(0.0f);
                    toolbarLine.setAlpha(0.0f);
                } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
                    toolbarLine.setAlpha(1);
                } else {
                    float alpha = Math.abs(Math.round(1.0f * verticalOffset / scrollRangle) * 10) / 10;
                    topicTitle.setAlpha(alpha);
                    toolbarLine.setAlpha(0.0f);
                }
            }
        });
    }

    @OnClick(R.id.topic_back)
    public void onViewClicked() {
        finish();
    }
}
