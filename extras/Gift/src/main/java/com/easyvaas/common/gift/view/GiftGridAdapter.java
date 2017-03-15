package com.easyvaas.common.gift.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.easyvaas.common.gift.R;
import com.easyvaas.common.gift.bean.GoodsEntity;
import com.easyvaas.common.gift.utils.GiftUtility;

import java.util.ArrayList;
import java.util.List;

class GiftGridAdapter extends BaseAdapter {
    private Context mContext;
    private List<GoodsEntity> mGridList = new ArrayList<GoodsEntity>();

    public GiftGridAdapter(Context context) {
        this.mContext = context;
    }

    public void setData(List<GoodsEntity> gridList) {
        this.mGridList = gridList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mGridList.size();
    }

    @Override
    public Object getItem(int position) {
        return mGridList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        GridViewHolder mGridViewHolder;
        Drawable drawable = null;
        if (convertView == null) {
            mGridViewHolder = new GridViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_gift_grid_view, null);
            mGridViewHolder.giftIv = (ImageView) convertView.findViewById(R.id.gift_iv);
            mGridViewHolder.giftPriceTv = (TextView) convertView.findViewById(R.id.gift_price_tv);
            mGridViewHolder.giftExpTv = (TextView) convertView.findViewById(R.id.gift_exp_tv);
            mGridViewHolder.giftName = (TextView) convertView.findViewById(R.id.gift_name);
            mGridViewHolder.giftSelectedIv = (ImageView) convertView.findViewById(R.id.gift_selected_iv);

            convertView.setTag(mGridViewHolder);
        } else {
            mGridViewHolder = (GridViewHolder) convertView.getTag();
        }
        GoodsEntity mGiftData = mGridList.get(position);
//        if (mGiftData.getCosttype() == GoodsEntity.COST_TYPE_BALERY) {
//            drawable = mContext.getResources().getDrawable(R.drawable.living_icon_barly);
//        } else if (mGiftData.getCosttype() == GoodsEntity.COST_TYPE_E_COIN) {
//            drawable = mContext.getResources().getDrawable(R.drawable.living_icon_money);
//        } else {
//             drawable = mContext.getResources().getDrawable(R.drawable.living_icon_barly);
//        }
//        if (drawable != null) {
//            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
//        }
//        mGridViewHolder.giftPriceTv.setCompoundDrawables(null, null, drawable, null);
        mGridViewHolder.giftPriceTv
                .setText(mContext.getString(R.string.gift_price,mGiftData.getCost()));
//        mGridViewHolder.giftExpTv.setText(mContext.getString(R.string.gift_exp_value, mGiftData.getExp()));
        mGridViewHolder.giftName.setText(mGiftData.getName());
        if (mGiftData.getAnitype() == 1) {
            GiftUtility.setGoodsCacheImage(mGiftData.getPic(), mGridViewHolder.giftIv);
        } else if (mGiftData.getAnitype() == 2) {
            GiftUtility.setGoodsCacheImage(mGiftData.getAni(), mGridViewHolder.giftIv);
        } else {
            GiftUtility.setGoodsCacheImage(mGiftData.getPic(), mGridViewHolder.giftIv);
        }
//        mGridViewHolder.giftSelectedIv.setVisibility(View.VISIBLE);
        if (mGiftData.isChecked()) {
            mGridViewHolder.giftIv.setSelected(true);
        } else {
            mGridViewHolder.giftIv.setSelected(false);
        }
        return convertView;
    }

    public static class GridViewHolder {
        public ImageView giftIv;
        public TextView giftPriceTv;
        public TextView giftExpTv;
        public TextView giftName;
        public ImageView giftSelectedIv;
    }

    public interface OnGiftViewClickCallBack {
        void onGiftViewItemClick(GoodsEntity data);
    }
}
