package com.easyvaas.common.viewpageindicator;

import android.widget.ImageView;

public interface IconPagerAdapter {
    /**
     * Get icon representing the page at {@code index} in the adapter.
     */
    int getIconResId(int index);

    // From PagerAdapter
    int getCount();

    void setTabIconDrawable(int index, boolean isSelect, ImageView imageView);
}
