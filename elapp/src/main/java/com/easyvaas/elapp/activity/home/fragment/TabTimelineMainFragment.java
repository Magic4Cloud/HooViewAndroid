/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.activity.home.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hooview.app.R;
import com.easyvaas.elapp.activity.home.HomeTopicListActivity;

public class TabTimelineMainFragment extends TabBaseMainFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        updateTabTitle(R.string.person_item_title_friend, R.string.timeline_item_title_now,
                R.string.person_item_title_rank);

        TabTimeLineFriendFragment friendFragment = new TabTimeLineFriendFragment();
        TabTimeLineHomeFragment nowFragment = new TabTimeLineHomeFragment();
        TabAssetsRankFragment rankFragment = new TabAssetsRankFragment();
        addTabs(friendFragment, nowFragment, rankFragment);

        setCurrentTab(PAGE_SECOND_INDEX);

        view.findViewById(R.id.home_topic_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(getContext(), HomeTopicListActivity.class));
            }
        });

        return view;
    }

    @Override
    protected void updateTitleState(int position) {
        super.updateTitleState(position);
        switch (position) {
            case PAGE_FIRST_INDEX:
                break;
            case PAGE_SECOND_INDEX:
                break;
            case PAGE_THIRD_INDEX:
                break;
        }
    }
}
