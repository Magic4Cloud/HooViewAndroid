package com.easyvaas.common.adapter;

import java.util.List;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author Jack Tony
 * @date 2015/5/17
 */
public abstract class CommonRcvAdapter<T> extends RecyclerView.Adapter {

    private final boolean DEBUG = false;
    
    private List<T> mDataList;

    private Object mItemType;

    private AdapterItemUtil mUtil = new AdapterItemUtil();

    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    protected CommonRcvAdapter(List<T> data) {
        mDataList = data;
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    public List<T> getDataList() {
        return mDataList;
    }

    /**
     * 可以被复写用于单条刷新等
     */
    public void updateData(@NonNull List<T> data) {
        mDataList = data;
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * instead by{@link #getItemViewType(Object)}
     */
    @Deprecated
    @Override
    public int getItemViewType(int position) {
        mItemType = getItemViewType(mDataList.get(position));
        return mUtil.getIntType(mItemType);
    }

    public Object getItemViewType(T t) {
        return null;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RcvAdapterItem(parent.getContext(), parent, getItemView(mItemType));
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (DEBUG) {
            RcvAdapterItem item = (RcvAdapterItem) holder;
            item.itemView.setBackgroundColor(item.isNew ? 0xffff0000 : 0xff00ff00);
            item.isNew = false;
        }
        ((RcvAdapterItem) holder).getItem().onUpdateViews(mDataList.get(position), position);
    }

    public abstract
    @NonNull AdapterItem<T> getItemView(Object type);

    private class RcvAdapterItem extends RecyclerView.ViewHolder {

        private AdapterItem<T> mItem;
        
        public boolean isNew = true; // debug中才用到
        
        protected RcvAdapterItem(Context context, ViewGroup parent, AdapterItem<T> item) {
            super(LayoutInflater.from(context).inflate(item.getLayoutResId(), parent, false));
            mItem = item;
            mItem.onBindViews(itemView);
            mItem.onSetViews();
            if (mOnItemClickListener != null) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnItemClickListener.onItemClick(v, getLayoutPosition());
                    }
                });
            }
            if (mOnItemLongClickListener != null) {
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        return mOnItemLongClickListener.onItemLongClick(v, getLayoutPosition());
                    }
                });
            }
        }

        protected AdapterItem<T> getItem() {
            return mItem;
        }
        
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        mOnItemLongClickListener = onItemLongClickListener;
    }

    /**
     * Interface definition for a callback to be invoked when an item in the
     * RecyclerView has been clicked.
     */
    public interface OnItemClickListener {
        /**
         * Callback method to be invoked when an item in the RecyclerView
         * has been clicked.
         *  @param view The view within the RecyclerView that was clicked
         * @param position The position of the view in the adapter.
         */
        void onItemClick(View view, int position);
    }

    /**
     * Interface definition for a callback to be invoked when an item in the
     * RecyclerView has been clicked and held.
     */
    public interface OnItemLongClickListener {
        /**
         * Callback method to be invoked when an item in the RecyclerView
         * has been clicked and held.
         *
         * @param view The view within the RecyclerView that was clicked
         * @param position The position of the view in the list
         * @return true if the callback consumed the long click, false otherwise
         */
        boolean onItemLongClick(View view, int position);
    }
}
