/*
 *    Copyright (C) 2015 Haruki Hasegawa
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.easyvaas.elapp.adapter.recycler;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.easyvaas.common.advancedRecyclerView.draggable.DraggableItemAdapter;
import com.easyvaas.common.advancedRecyclerView.draggable.DraggableItemConstants;
import com.easyvaas.common.advancedRecyclerView.draggable.ItemDraggableRange;
import com.easyvaas.common.advancedRecyclerView.draggable.RecyclerViewDragDropManager;
import com.easyvaas.common.advancedRecyclerView.utils.AbstractDraggableItemViewHolder;
import com.easyvaas.elapp.bean.market.ExponentModel;
import com.easyvaas.elapp.ui.market.GlobalContentEditActivity;
import com.easyvaas.elapp.utils.Logger;
import com.easyvaas.elapp.utils.Utils;
import com.hooview.app.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class GlobalMarketExponentAdapter
        extends RecyclerView.Adapter<GlobalMarketExponentAdapter.MyViewHolder>
        implements DraggableItemAdapter<GlobalMarketExponentAdapter.MyViewHolder> {
    private static final String TAG = "MyDraggableItemAdapter";
    private int mItemMoveMode = RecyclerViewDragDropManager.ITEM_MOVE_MODE_DEFAULT;
    private List<ExponentModel> mExponentLists = new ArrayList<ExponentModel>();
    public int ITEM_TYPE_NORMAL = 1;
    public int ITEM_TYPE_FOOTER = 2;
    private Context mContext;

    // NOTE: Make accessible with short name
    private interface Draggable extends DraggableItemConstants {
    }

    public static class MyViewHolder extends AbstractDraggableItemViewHolder {
        TextView mTvName;
        TextView mTvNumber;
        TextView mTvPercent;
        CardView mCardView;
        Button mBtnEdit;
        boolean isFooter;

        MyViewHolder(View v, boolean isFooter) {
            super(v);
            this.isFooter = isFooter;
            if (isFooter) {
                mBtnEdit = (Button) v.findViewById(R.id.btn_edit);
            } else {
                mTvName = (TextView) v.findViewById(R.id.tv_name);
                mTvNumber = (TextView) v.findViewById(R.id.tv_number);
                mTvPercent = (TextView) v.findViewById(R.id.tv_percent);
                mCardView = (CardView) v;
            }
        }
    }

    public GlobalMarketExponentAdapter(Context context) {
        // DraggableItemAdapter requires stable ID, and also
        // have to implement the getItemId() method appropriately.
        setHasStableIds(true);
        this.mContext = context;
    }

    public void setExponentListModel(List<ExponentModel> listModel) {
        this.mExponentLists = listModel;
        notifyDataSetChanged();
    }

    public void setItemMoveMode(int itemMoveMode) {
        mItemMoveMode = itemMoveMode;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return ITEM_TYPE_FOOTER;
        } else {
            return ITEM_TYPE_NORMAL;
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_NORMAL) {
            final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            final View v = inflater.inflate(R.layout.view_exponent_cell, parent, false);
            return new MyViewHolder(v, false);
        } else {
            final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            final View v = inflater.inflate(R.layout.view_global_exponent_footer, parent, false);
            return new MyViewHolder(v, true);
        }
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        // set text
        if (holder.isFooter) {
            holder.mBtnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GlobalContentEditActivity.start(mContext, mContext.getResources().getString(R.string.navigation_title_market));
                }
            });
        } else {
            if (mExponentLists != null && mExponentLists.size() > 0) {
                Context ctx = holder.mTvName.getContext();
                final ExponentModel model = mExponentLists.get(position);

                String number = new DecimalFormat(".##").format(model.getClose());
                String percentStr;
                if (model.getChange() > 0) {
                    percentStr = ctx.getString(R.string.exponent_percent_up);
                    percentStr = String.format(percentStr,
                            (model.getClose() - model.getPreclose()), model.getChange());
                } else {
                    percentStr = ctx.getString(R.string.exponent_percent);
                    percentStr = String.format(percentStr,
                            (model.getClose() - model.getPreclose()), model.getChange());
                }
                Logger.d(TAG, "onBindViewHolder: " + model.getChange() + "   percentStr" + percentStr);
                holder.mTvName.setText(model.getName());
                holder.mTvNumber.setText(number);
                holder.mTvPercent.setText(percentStr);
                holder.mCardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Utils.showStockDetail(holder.mCardView.getContext(), model.getName(), model.getSymbol(), false);
                    }
                });

                if (percentStr.startsWith("-")) {
                    holder.mCardView.setCardBackgroundColor(ctx.getResources()
                            .getColor(R.color.exponent_down));
                } else {
                    holder.mCardView.setCardBackgroundColor(ctx.getResources()
                            .getColor(R.color.exponent_up));
                }
            }
        }

        // set background resource (target view ID: container)
        final int dragState = holder.getDragStateFlags();

//        if (((dragState & Draggable.STATE_FLAG_IS_UPDATED) != 0)) {
//            int bgResId;
//
//            if ((dragState & Draggable.STATE_FLAG_IS_ACTIVE) != 0) {
//                bgResId = R.drawable.bg_item_dragging_active_state;
//
//                // need to clear drawable state here to get correct appearance of the dragging item.
//                DrawableUtils.clearState(holder.mContainer.getForeground());
//            } else if ((dragState & Draggable.STATE_FLAG_DRAGGING) != 0) {
//                bgResId = R.drawable.bg_item_dragging_state;
//            } else {
//                bgResId = R.drawable.bg_item_normal_state;
//            }
//
//            holder.mContainer.setBackgroundResource(bgResId);
//        }
    }


    @Override
    public int getItemCount() {
        return mExponentLists == null ? 0 : mExponentLists.size() + 1;
    }

    @Override
    public void onMoveItem(int fromPosition, int toPosition) {
        Log.d(TAG, "onMoveItem(fromPosition = " + fromPosition + ", toPosition = " + toPosition + ")");

        if (fromPosition == toPosition) {
            return;
        }

        if (mItemMoveMode == RecyclerViewDragDropManager.ITEM_MOVE_MODE_DEFAULT) {
//            mProvider.moveItem(fromPosition, toPosition);
            ExponentModel remove = mExponentLists.remove(fromPosition);
            mExponentLists.add(toPosition, remove);
            notifyItemMoved(fromPosition, toPosition);
        } else {
//            mProvider.swapItem(fromPosition, toPosition);
            notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCheckCanStartDrag(MyViewHolder holder, int position, int x, int y) {
        return true;
    }

    @Override
    public ItemDraggableRange onGetItemDraggableRange(MyViewHolder holder, int position) {
        // no drag-sortable range specified
        return null;
    }

    @Override
    public boolean onCheckCanDrop(int draggingPosition, int dropPosition) {
        return true;
    }
}
