package com.easyvaas.elapp.ui.market;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.easyvaas.elapp.bean.user.GlobalStockStatus;
import com.easyvaas.elapp.db.Preferences;
import com.easyvaas.elapp.db.RealmHelper;
import com.hooview.app.R;
import com.easyvaas.elapp.bean.market.ExponentListModel;
import com.easyvaas.elapp.bean.market.ExponentModel;
import com.easyvaas.elapp.net.HooviewApiHelper;
import com.easyvaas.elapp.net.MyRequestCallBack;
import com.easyvaas.elapp.ui.base.BaseFragment;

/**
 * Created by guojun on 2016/12/31 18:16.
 */

public class GlobalContentListFragment extends BaseFragment {
    private List<ExponentModel> mGlobalStockLists = new ArrayList<ExponentModel>();
    private List<ExponentModel> mSelectGlobalStockLists = new ArrayList<ExponentModel>();
    private List<ExponentModel> mGlobalRecordList = new ArrayList<ExponentModel>();
    private MyAdapter mMyAdapter;
    private CallBack mCallBack;
    public static GlobalContentListFragment newInstance() {
        GlobalContentListFragment fragment = new GlobalContentListFragment();
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallBack = (CallBack) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getContext(), R.layout.fragment_market_global, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.rv_exponent);
        TextView tvPrompt = (TextView) view.findViewById(R.id.tv_prompt);
        tvPrompt.setText(R.string.globa_edit_prompt);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.shape_market_exponent_vertical_divider));
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(mMyAdapter = new MyAdapter(mGlobalStockLists));
        List<ExponentModel> globalRecordList = RealmHelper.getInstance().getGlobalRecordList();
        mGlobalRecordList.addAll(globalRecordList);
        loadGlobalStockData();
    }

    private void loadGlobalStockData() {
        HooviewApiHelper.getInstance().getGlobalStockList(new MyRequestCallBack<ExponentListModel>() {
            @Override
            public void onSuccess(ExponentListModel result) {
                List<ExponentModel> data = result.getData();
                for (ExponentModel ex : data) {
                    for (ExponentModel global : mGlobalRecordList) {
                        if (ex.getSymbol().equals(global.getSymbol())) {
                            ex.setSelect(true);
                        }
                    }
                }
                mGlobalStockLists.clear();
                mGlobalStockLists.addAll(data);

                mMyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String msg) {

            }
        });
    }


    private class MyAdapter extends RecyclerView.Adapter {
        List<ExponentModel> mGlobalStockLists;
        public MyAdapter(List<ExponentModel> globalStockLists){
            mGlobalStockLists = globalStockLists;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(View.inflate(getContext(), R.layout.item_global_content_select, null));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            if(mGlobalStockLists.get(position).getSymbol().equals(Preferences.getInstance(getActivity()).getString(mGlobalStockLists.get(position).getName()))){
                ((MyViewHolder)holder).checkBox.setSelected(true);
            }
            ((MyViewHolder)holder).checkBox.setChecked(mGlobalStockLists.get(position).isSelect());
            ((MyViewHolder)holder).checkBox.setText(mGlobalStockLists.get(position).getName());
            ((MyViewHolder)holder).checkBox.setOnCheckedChangeListener(
                    new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            if(b){
                                mCallBack.getSelectList(mGlobalStockLists.get(position));
                            }else{
                                mCallBack.removeSelectList(mGlobalStockLists.get(position));
                            }
                        }
                    });
        }

        @Override
        public int getItemCount() {
            return mGlobalStockLists.size();
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        public CheckBox checkBox;

        public MyViewHolder(View itemView) {
            super(itemView);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkbox);
        }
    }

    interface CallBack{
        void getSelectList(ExponentModel list);
        void removeSelectList(ExponentModel list);
    }
}
