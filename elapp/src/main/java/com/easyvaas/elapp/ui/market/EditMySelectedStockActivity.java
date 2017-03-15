package com.easyvaas.elapp.ui.market;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easyvaas.common.advancedRecyclerView.draggable.DraggableItemAdapter;
import com.easyvaas.common.advancedRecyclerView.draggable.ItemDraggableRange;
import com.easyvaas.common.advancedRecyclerView.draggable.RecyclerViewDragDropManager;
import com.easyvaas.common.advancedRecyclerView.utils.AbstractDraggableItemViewHolder;
import com.hooview.app.R;
import com.easyvaas.elapp.bean.market.StockListModel;
import com.easyvaas.elapp.bean.user.CollectListModel;
import com.easyvaas.elapp.net.ApiHelper;
import com.easyvaas.elapp.net.HooviewApiHelper;
import com.easyvaas.elapp.net.MyRequestCallBack;
import com.easyvaas.elapp.ui.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import org.greenrobot.eventbus.EventBus;

public class EditMySelectedStockActivity extends BaseActivity implements View.OnClickListener {
    private RecyclerView mRecyclerView;
    private RelativeLayout mRlEmpty;
    private TextView mTvTitle;
    private TextView mTvComplete;
    private StockListModel mModel;
    private List<StockListModel.StockModel> mFinalItems = new ArrayList<StockListModel.StockModel>();

    public static void start(Context context, StockListModel model) {
        Intent starter = new Intent(context, EditMySelectedStockActivity.class);
        if (model != null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("stock", model);
            starter.putExtras(bundle);
        }
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_stock_edit);
        initView();
    }

    private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView);
        mRlEmpty = (RelativeLayout) findViewById(R.id.rl_empty);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mTvComplete = (TextView) findViewById(R.id.tv_complete);
        findViewById(R.id.iv_back).setOnClickListener(this);
        mTvComplete.setOnClickListener(this);
        mTvComplete.setVisibility(View.VISIBLE);
        mTvTitle.setText(R.string.title_edit_stock);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            mModel = (StockListModel) bundle.getSerializable("stock");
            initAdapter(mModel);
        } else {
            getSelectStr();
        }
    }

    private void getSelectStr() {
        ApiHelper.getInstance().getCollectList("4", new MyRequestCallBack<CollectListModel>() {
            @Override
            public void onSuccess(CollectListModel result) {
                getStockListInfo(result != null ? result.getCollectlist() : "");
            }

            @Override
            public void onFailure(String msg) {
            }

            @Override
            public void onError(String errorInfo) {
                super.onError(errorInfo);
            }
        });
    }

    private void getStockListInfo(String listStr) {
        HooviewApiHelper.getInstance().getSelectStockList(listStr, new MyRequestCallBack<StockListModel>() {
            @Override
            public void onSuccess(StockListModel result) {
                if (result != null) {
                    initAdapter(result);
                }
            }

            @Override
            public void onFailure(String msg) {
            }
        });
    }

    private void initAdapter(StockListModel model) {
        RecyclerViewDragDropManager dragMgr = new RecyclerViewDragDropManager();

        dragMgr.setInitiateOnMove(false);
        dragMgr.setInitiateOnLongPress(true);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(EditMySelectedStockActivity.this));
        mRecyclerView.setAdapter(dragMgr.createWrappedAdapter(new MyAdapter(model)));

        dragMgr.attachRecyclerView(mRecyclerView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_complete:
                finish();
                EventBus.getDefault().post(mFinalItems);
                break;
        }
    }

    static class MyItem {
        public final long id;
        public final String text;

        public MyItem(long id, String text) {
            this.id = id;
            this.text = text;
        }
    }

    class MyAdapter extends RecyclerView.Adapter<MyViewHolder> implements DraggableItemAdapter<MyViewHolder> {
        List<StockListModel.StockModel> mItems;

        public MyAdapter(StockListModel mModel) {
            setHasStableIds(true); // this is required for D&D feature.

            if (mModel != null) {
                mItems = new ArrayList<StockListModel.StockModel>();
                mItems.clear();
                mItems.addAll(mModel.getData());
            }
        }

        @Override
        public long getItemId(int position) {
            return mItems.get(position)
                    .hashCode(); // need to return stable (= not change even after reordered) value
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_my_stock_edit, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            StockListModel.StockModel stockModel = mItems.get(position);
            holder.mTvStockName.setText(stockModel.getName());
            holder.mTvStockNumber.setText(stockModel.getSymbol());
            holder.mTvPercent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mItems.remove(position);
                    notifyDataSetChanged();
                    setFinalItems(mItems);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        @Override
        public void onMoveItem(int fromPosition, int toPosition) {
            StockListModel.StockModel remove = mItems.remove(fromPosition);
            mItems.add(toPosition, remove);
            notifyItemMoved(fromPosition, toPosition);
            setFinalItems(mItems);
        }

        @Override
        public boolean onCheckCanStartDrag(MyViewHolder holder, int position, int x, int y) {
            return true;
        }

        @Override
        public ItemDraggableRange onGetItemDraggableRange(MyViewHolder holder, int position) {
            return null;
        }

        @Override
        public boolean onCheckCanDrop(int draggingPosition, int dropPosition) {
            return true;
        }
    }

    static class MyViewHolder extends AbstractDraggableItemViewHolder {
        public TextView mTvStockName;
        public TextView mTvStockNumber;
        public ImageView mTvPercent;

        public MyViewHolder(View itemView) {
            super(itemView);
            mTvStockName = (TextView) itemView.findViewById(R.id.tv_stock_name);
            mTvStockNumber = (TextView) itemView.findViewById(R.id.tv_stock_number);
            mTvPercent = (ImageView) itemView.findViewById(R.id.tv_del);
        }
    }

    private void setFinalItems(List<StockListModel.StockModel> finalItems) {

        mFinalItems.clear();
        mFinalItems.addAll(finalItems);

    }
}
