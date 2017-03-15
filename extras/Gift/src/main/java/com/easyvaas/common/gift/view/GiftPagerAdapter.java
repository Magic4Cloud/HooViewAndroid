package com.easyvaas.common.gift.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.easyvaas.common.gift.bean.GoodsEntity;
import com.easyvaas.common.gift.R;

class GiftPagerAdapter extends PagerAdapter {
    private static final int DEFAULT_PAGE_CAPACITY_SIZE = 8;

    private Context mContext;
    private int mCurrentPageIndex;
    private int mTotalPage;
    private GiftGridAdapter mCurrentSelectGridAdapter;
    private GiftGridAdapter.OnGiftViewClickCallBack mOnGiftViewClickCallBack;

    private List<ArrayList<GoodsEntity>> mGiftDataList;

    public GiftPagerAdapter(Context context) {
        this.mContext = context;
        this.mGiftDataList = new ArrayList<ArrayList<GoodsEntity>>();
    }

    public void setData(List<GoodsEntity> data) {
        calculatePage(data);
        notifyDataSetChanged();
    }

    public void setOnGridItemClickListener(GiftGridAdapter.OnGiftViewClickCallBack listener) {
        mOnGiftViewClickCallBack = listener;
    }

    private void calculatePage(List<GoodsEntity> data) {
        mGiftDataList.clear();
        if (data == null) {
            return;
        }
        int dataCount = data.size();
        int rest = dataCount % DEFAULT_PAGE_CAPACITY_SIZE;
        int totalPage = dataCount / DEFAULT_PAGE_CAPACITY_SIZE;
        if (rest > 0) {
            totalPage = totalPage + 1;
        }
        for (int j = 0; j < totalPage; j++) {
            mGiftDataList.add(new ArrayList<GoodsEntity>());
        }
        int page = 1;
        for (int i = 0; i < dataCount; i++) {
            if (i > page * DEFAULT_PAGE_CAPACITY_SIZE - 1) {
                page++;
            }
            mGiftDataList.get(page - 1).add(data.get(i));
        }
        mTotalPage = totalPage;
    }

    public int getCurrentPageIndex() {
        return mCurrentPageIndex;
    }

    public int getTotalPage() {
        return mTotalPage;
    }

    public void setCurrentPageIndex(int currentPage) {
        mCurrentPageIndex = currentPage;
    }

    public void notifySelectItemChanged() {
        if (mCurrentSelectGridAdapter != null) {
            mCurrentSelectGridAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return mGiftDataList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_gift_content, null);
        GridView giftContentGv = (GridView) itemView.findViewById(R.id.gift_content_gv);
        GiftGridAdapter mGiftGridAdapter = new GiftGridAdapter(mContext);
        giftContentGv.setAdapter(mGiftGridAdapter);
        mGiftGridAdapter.setData(mGiftDataList.get(position));
        giftContentGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                GoodsEntity giftData = (GoodsEntity) adapterView.getAdapter().getItem(position);
                if (giftData.isChecked()) {
                    return;
                }
                mOnGiftViewClickCallBack.onGiftViewItemClick(giftData);
                ((GiftGridAdapter) adapterView.getAdapter()).notifyDataSetChanged();
                if (mCurrentSelectGridAdapter != null) {
                    mCurrentSelectGridAdapter.notifyDataSetChanged();
                }
                mCurrentSelectGridAdapter = (GiftGridAdapter) adapterView.getAdapter();
            }
        });
        container.addView(itemView);
        return itemView;
    }
}
