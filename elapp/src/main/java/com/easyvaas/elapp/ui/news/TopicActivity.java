package com.easyvaas.elapp.ui.news;

import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.easyvaas.elapp.bean.news.TopicModel;
import com.easyvaas.elapp.ui.base.mybase.MyBaseActivity;
import com.hooview.app.R;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
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
    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.topic_fragment)
    FrameLayout topicFragment;
    @BindView(R.id.topic_back)
    ImageView topicBack;
    @BindView(R.id.topic_title)
    TextView mTopicTitle;
    @BindView(R.id.topic_line)
    View toolbarLine;
    @BindView(R.id.topic_img)
    ImageView mTopicImg;
    @BindView(R.id.item_topic_readcounts)
    TextView mItemTopicReadcounts;
    @BindView(R.id.topic_content)
    TextView mTopicContent;
    @BindView(R.id.topic_title_top)
    TextView mTopicTitleTop;


    @Override
    protected int getLayout() {
        return R.layout.acivity_topic_layout;
    }

    @Override
    protected String getTitleText() {
        return null;
    }

    @Override
    protected void initViewAndData() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }
        initToolBar();
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().add(R.id.topic_fragment, TopicFragment.newInstance(getIntent().getStringExtra("id"))).commit();
    }

    private void initToolBar() {
        // 动态改变toolbar
        appbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int scrollRangle = appBarLayout.getTotalScrollRange();
                if (verticalOffset == 0) {
                    mTopicTitleTop.setAlpha(0.0f);
                    toolbarLine.setAlpha(0.0f);
                } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
                    toolbarLine.setAlpha(1);
                    mTopicTitleTop.setAlpha(1);
                } else {
                    float alpha = Math.abs(Math.round(1.0f * verticalOffset / scrollRangle) * 10) / 10;
                    mTopicTitleTop.setAlpha(0.0f);
                    toolbarLine.setAlpha(0.0f);
                }
            }
        });
    }


    /**
     * 初始化header数据
     */
    public void initHeaderDatas(TopicModel topicModel)
    {
        mTopicTitle.setText("      "+topicModel.getTitle());
        mTopicTitleTop.setText(topicModel.getTitle());
        mTopicContent.setText(topicModel.getIntroduce());
        mItemTopicReadcounts.setText(topicModel.getViewCount()+"");
        Picasso.with(this).load(topicModel.getCover()).placeholder(R.drawable.account_bitmap_list).into(mTopicImg);

    }

    @OnClick(R.id.topic_back)
    public void onViewClicked() {
        finish();
    }

}
