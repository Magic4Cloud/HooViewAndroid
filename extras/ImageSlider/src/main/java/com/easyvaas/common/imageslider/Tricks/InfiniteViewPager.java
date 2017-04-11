package com.easyvaas.common.imageslider.Tricks;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

import java.lang.reflect.Field;

/**
 * A {@link ViewPager} that allows pseudo-infinite paging with a wrap-around effect. Should be used with an {@link
 * InfinitePagerAdapter}.
 */
public class InfiniteViewPager extends ViewPagerEx {
    PagerAdapter mPagerAdapter;
    public InfiniteViewPager(Context context) {
        super(context);
    }

    public InfiniteViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
        super.setAdapter(mPagerAdapter = adapter);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        /**
         * 坑，解决在RecyclerView中使用的bug
         * 设ViewPager中有3张照片
         * 当ViewPager滑动一遍之后，向下滑动RecyclerView列表
         * 直到完全隐藏此ViewPager，并执行了onDetachedFromWindow
         * 再回来时，将会出现bug，第一次滑动时没有动画效果，并且，经常出现view没有加载的情况
         */
        try {
            Field mFirstLayout = ViewPager.class.getDeclaredField("mFirstLayout");
            mFirstLayout.setAccessible(true);
            mFirstLayout.set(this, false);
            mPagerAdapter.notifyDataSetChanged();
            setCurrentItem(getCurrentItem());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}