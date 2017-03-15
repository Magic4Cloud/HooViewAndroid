package com.easyvaas.common.gift.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.easyvaas.common.gift.R;

public class PageIndicateView extends LinearLayout {
    private static final int PAGE_COUNT_MAX = 5;
    private Context mContext;
    private int mPageCount;
    private int mCurrentPageIndex;

    public PageIndicateView(Context context) {
        super(context);
        init(context);
    }

    public PageIndicateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PageIndicateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
    }

    public void setPageCount(int pageCount) {
        if (pageCount > PAGE_COUNT_MAX) {
            mPageCount = PAGE_COUNT_MAX;
        } else {
            mPageCount = pageCount;
        }
    }

    public void setCurrentPageIndex(int pageIndex) {
        mCurrentPageIndex = pageIndex;
        indicatePageIndex();
    }

    private void indicatePageIndex() {
        removeAllViews();
        if (mPageCount < 2) {
            return;
        }
        for (int i = 0; i < this.mPageCount; i++) {
            ImageView imageView = new ImageView(mContext);
            LayoutParams layoutParams = new LayoutParams(30,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(10, 0, 10, 0);
            imageView.setLayoutParams(layoutParams);
            if (i == mCurrentPageIndex) {
                imageView.setImageResource(R.drawable.dot_select_shape);
            } else {
                imageView.setImageResource(R.drawable.dot_unselect_shape);
            }
            addView(imageView);
        }
    }

}
