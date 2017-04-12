package com.easyvaas.elapp.ui.news;

import android.support.v4.app.FragmentManager;

import com.easyvaas.elapp.ui.base.mybase.MyBaseActivity;
import com.hooview.app.R;

/**
 * Date   2017/4/12
 * Editor  Misuzu
 * 专题界面
 */

public class TopicActivity extends MyBaseActivity {


    @Override
    protected int getLayout() {
        return R.layout.acivity_topic_layout;
    }

    @Override
    protected void initViewAndData() {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().add(R.id.topic_fragment,TopicFragment.newInstance()).commit();
    }


}
