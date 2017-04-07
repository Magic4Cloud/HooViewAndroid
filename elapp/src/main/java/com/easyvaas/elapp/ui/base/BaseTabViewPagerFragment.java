package com.easyvaas.elapp.ui.base;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hooview.app.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseTabViewPagerFragment extends BaseFragment {
    @BindView(R.id.tabLayout)
    protected TabLayout mTabLayout;
    @BindView(R.id.viewPager)
    protected ViewPager mViewPager;
    private Unbinder mUnbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getContext(), R.layout.layout_tab_viewpager, null);
        mUnbinder = ButterKnife.bind(this, view);
        initView(view);
        return view;
    }

    public abstract void initView(View view);

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }
}
