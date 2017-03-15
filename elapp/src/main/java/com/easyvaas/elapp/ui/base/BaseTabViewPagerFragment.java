package com.easyvaas.elapp.ui.base;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hooview.app.R;

public abstract class BaseTabViewPagerFragment extends BaseFragment {
    protected TabLayout mTabLayout;
    protected ViewPager mViewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getContext(), R.layout.layout_tab_viewpager, null);
        mTabLayout = (TabLayout) view.findViewById(R.id.tabLayout);
        mViewPager = (ViewPager) view.findViewById(R.id.viewPager);
        initView(view);
        return view;
    }

    public abstract void initView(View view);

}
