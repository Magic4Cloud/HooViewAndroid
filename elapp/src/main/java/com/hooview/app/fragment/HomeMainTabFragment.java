package com.hooview.app.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.hooview.app.R;
import com.hooview.app.base.BaseFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Author:   yyl
 * Description:
 * CreateDate:     2016/10/19
 */
public class HomeMainTabFragment extends BaseFragment {

    @BindView(R.id.banner)
    ConvenientBanner mBanner;
    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_main_tab, container, false);
        ButterKnife.bind(this, view);
        return view;
    }
}
