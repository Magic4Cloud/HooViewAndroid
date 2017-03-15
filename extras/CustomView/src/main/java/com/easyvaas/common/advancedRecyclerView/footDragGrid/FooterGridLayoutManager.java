package com.easyvaas.common.advancedRecyclerView.footDragGrid;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.util.AttributeSet;

/**
 * Created by guojun on 2016/12/31 11:43.
 */

public class FooterGridLayoutManager extends GridLayoutManager {
    public FooterGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setSpanSizeLookup(new FooterSpanSizeLookup());

    }

    public FooterGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
        setSpanSizeLookup(new FooterSpanSizeLookup());

    }

    public FooterGridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
        setSpanSizeLookup(new FooterSpanSizeLookup());
    }

    @Override
    public void setSpanSizeLookup(SpanSizeLookup spanSizeLookup) {
        super.setSpanSizeLookup(spanSizeLookup);
    }


    public final class FooterSpanSizeLookup extends SpanSizeLookup {

        @Override
        public int getSpanSize(int position) {
            if (position == getItemCount() - 1) {
                return 3;
            }
            return 1;
        }

        @Override
        public int getSpanIndex(int position, int spanCount) {
            return position % spanCount;
        }
    }

}
