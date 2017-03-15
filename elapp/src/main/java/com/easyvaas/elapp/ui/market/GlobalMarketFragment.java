package com.easyvaas.elapp.ui.market;

import android.graphics.drawable.NinePatchDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.easyvaas.common.advancedRecyclerView.animator.DraggableItemAnimator;
import com.easyvaas.common.advancedRecyclerView.animator.GeneralItemAnimator;
import com.easyvaas.common.advancedRecyclerView.decoration.ItemShadowDecorator;
import com.easyvaas.common.advancedRecyclerView.footDragGrid.FooterGridLayoutManager;
import com.easyvaas.common.advancedRecyclerView.footDragGrid.FooterViewRecyclerViewDragDropManager;
import com.easyvaas.elapp.adapter.recycler.GlobalMarketExponentAdapter;
import com.easyvaas.elapp.bean.market.ExponentListModel;
import com.easyvaas.elapp.bean.market.ExponentModel;
import com.easyvaas.elapp.bean.user.GlobalStockStatus;
import com.easyvaas.elapp.db.Preferences;
import com.easyvaas.elapp.db.RealmHelper;
import com.easyvaas.elapp.event.MarketRefreshEvent;
import com.easyvaas.elapp.net.HooviewApiHelper;
import com.easyvaas.elapp.net.MyRequestCallBack;
import com.easyvaas.elapp.ui.base.BaseListFragment;
import com.hooview.app.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class GlobalMarketFragment extends BaseListFragment {
    //// TODO: 2.1 正式版得继承BaseListFragment
    private List<ExponentModel> mExponentLists  = new ArrayList<ExponentModel>();
    private Button mBtnEdit;
    private FooterGridLayoutManager mLayoutManager;
    private RecyclerView.Adapter mWrappedAdapter;
    private FooterViewRecyclerViewDragDropManager mRecyclerViewDragDropManager;
    private GlobalMarketExponentAdapter mAdapter;
    private boolean mCanRefresh;

    public static GlobalMarketFragment newInstance() {
        Bundle args = new Bundle();
        GlobalMarketFragment fragment = new GlobalMarketFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    //    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.empty_feature_no_open, container, false);
//    }

    @Override
    public void iniView(View view) {
        Preferences.getInstance(getActivity()).clear();
        mBtnEdit = (Button) view.findViewById(R.id.btn_edit);
        view.findViewById(R.id.tv_prompt).setVisibility(View.VISIBLE);
        mLayoutManager = new FooterGridLayoutManager(getContext(), 3, GridLayoutManager.VERTICAL, false);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL);
        dividerItemDecoration
                .setDrawable(getResources().getDrawable(R.drawable.shape_market_exponent_vertical_divider));
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        // drag & drop manager
        mRecyclerViewDragDropManager = new FooterViewRecyclerViewDragDropManager();
        mRecyclerViewDragDropManager.setDraggingItemShadowDrawable(
                (NinePatchDrawable) ContextCompat.getDrawable(getContext(), R.drawable.material_shadow_z3));
        // Start dragging after long press
        mRecyclerViewDragDropManager.setInitiateOnLongPress(true);
        mRecyclerViewDragDropManager.setInitiateOnMove(false);
        mRecyclerViewDragDropManager.setLongPressTimeout(750);
        // setup dragging item effects (NOTE: DraggableItemAnimator is required)
        mRecyclerViewDragDropManager.setDragStartItemAnimationDuration(250);
        mRecyclerViewDragDropManager.setDraggingItemAlpha(0.8f);
        mRecyclerViewDragDropManager.setDraggingItemScale(1.3f);
        mRecyclerViewDragDropManager.setDraggingItemRotation(15.0f);
        mAdapter = new GlobalMarketExponentAdapter(getContext());

        mWrappedAdapter = mRecyclerViewDragDropManager
                .createWrappedAdapter(mAdapter);      // wrap for dragging

        GeneralItemAnimator animator = new DraggableItemAnimator(); // DraggableItemAnimator is required to make item animations properly.

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mWrappedAdapter);  // requires *wrapped* adapter
        mRecyclerView.setItemAnimator(animator);
        mRecyclerView.addItemDecoration(new ItemShadowDecorator(
                (NinePatchDrawable) ContextCompat.getDrawable(getContext(), R.drawable.material_shadow_z3)));
        mRecyclerViewDragDropManager.attachRecyclerView(mRecyclerView);

        loadData();
    }

    @Override
    public void onRefresh() {
        super.onRefresh();
        if (!mCanRefresh) {
            loadData();
        }else{
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    private void loadData() {
        if(isHasGlobalDb()){
            mExponentLists.clear();
            List<ExponentModel> globalRecordList = RealmHelper.getInstance().getGlobalRecordList();
            mExponentLists.addAll(globalRecordList);
            mAdapter.setExponentListModel(mExponentLists);
            mSwipeRefreshLayout.setRefreshing(false);
        }else{
            HooviewApiHelper.getInstance().getGlobalStockList(new MyRequestCallBack<ExponentListModel>() {
                @Override
                public void onSuccess(ExponentListModel result) {
                    if (result != null) {
                        GlobalStockStatus bean = new GlobalStockStatus();
                        mExponentLists.clear();
                        keepNineMember(result.getData());
                        insertGlobalStatus(bean);
                        mAdapter.setExponentListModel(mExponentLists);
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }

                @Override
                public void onFailure(String msg) {
                    mSwipeRefreshLayout.setRefreshing(false);
                    showEmptyView(getString(R.string.has_no_data));
                }

                @Override
                public void onError(String errorInfo) {
                    super.onError(errorInfo);
                    showEmptyView(getString(R.string.has_no_data));
                }
            });
        }
    }

    private void keepNineMember(List<ExponentModel> exponentLists) {

        for (int i = 0; i < exponentLists.size(); i++) {
            if (i < 9) {
                mExponentLists.add(exponentLists.get(i));
            }
        }

    }

    private boolean isHasGlobalDb() {
        try {
            List<ExponentModel> globalRecordList = RealmHelper.getInstance().getGlobalRecordList();
            if (globalRecordList != null && globalRecordList.size() > 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(List<ExponentModel> event) {
        mCanRefresh = true;
        GlobalStockStatus bean = new GlobalStockStatus();
        mExponentLists.clear();
        List<ExponentModel> globalRecordList = RealmHelper.getInstance().getGlobalRecordList();
        mExponentLists.addAll(globalRecordList);
//        if(event!=null&&event.size()>0&&event.get(0).isAddOrDelete()){
//            for(ExponentModel ex:event){
//                if(ex.isAddOrDelete()){
//                    ex.setAddOrDelete(false);
//                    ex.setSelect(false);
//                    //                for(ExponentModel ex2:mExponentLists){
//                    for(int i = 0;i < mExponentLists.size();i++){
//                        if(ex.getSymbol().equals(mExponentLists.get(i).getSymbol())){
//                            mExponentLists.remove(i);
//                            bean.setSlect(false);
//                            deleteGlobalStatus(bean,mExponentLists.get(i));
//                        }
//                    }
//                }else{
//                    mExponentLists.addAll(event);
//                    bean.setSlect(true);
//                    insertGlobalStatus(bean);
//                }
//                ex.setAddOrDelete(false);
//                ex.setSelect(false);
////                for(ExponentModel ex2:mExponentLists){
//                    for(int i = 0;i < mExponentLists.size();i++){
//                        if(ex.getSymbol().equals(mExponentLists.get(i).getSymbol())){
//                            mExponentLists.remove(i);
//                            deleteGlobalStatus(bean,mExponentLists.get(i));
//                        }
//                    }
//                   if(ex.getSymbol().equals(ex2.getSymbol())){
//                       mExponentLists.remove(ex2);
//                       deleteGlobalStatus(bean,ex2);
//                   }
//                }
//            }
//        }else{
//            mExponentLists.addAll(event);
//            insertGlobalStatus(bean);
//        }
        mAdapter.setExponentListModel(mExponentLists);
    }

    private void insertGlobalStatus(GlobalStockStatus bean) {
        for (ExponentModel exponentModel : mExponentLists) {
            exponentModel.setId(exponentModel.getSymbol());
            if (!RealmHelper.getInstance()
                    .queryGlobalRecordId(exponentModel.getId())) {
                RealmHelper.getInstance().insertGlobalRecord(exponentModel);
            }
        }
    }

    private void deleteGlobalStatus(GlobalStockStatus bean,ExponentModel exponentModel){
            bean.setId(exponentModel.getCode() == null ? exponentModel.getSymbol() : exponentModel.getCode());
            if (!TextUtils.isEmpty(
                    exponentModel.getCode() == null ? exponentModel.getSymbol() : exponentModel.getCode())
                    && RealmHelper.getInstance()
                    .queryGlobalRecordId(bean.getId())) {
                RealmHelper.getInstance().deleteGlobalRecord(bean.getId());
            }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MarketRefreshEvent event) {
        if(!mCanRefresh){
            mSwipeRefreshLayout.setRefreshing(true);
            loadData();
        }else{
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

}
