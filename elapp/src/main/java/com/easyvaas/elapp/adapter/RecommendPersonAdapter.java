package com.easyvaas.elapp.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.easyvaas.common.widget.MyUserPhoto;
import com.easyvaas.elapp.bean.news.TopRatedModel.RecommendBean;
import com.easyvaas.elapp.utils.UserUtil;
import com.hooview.app.R;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Date   2017/4/11
 * Editor  Misuzu
 */

public class RecommendPersonAdapter extends RecyclerView.Adapter {

    List<RecommendBean> datas;
    Context mContext;

    public RecommendPersonAdapter(Context context, List<RecommendBean> datas) {
        this.datas = datas;
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecommendViewholder(LayoutInflater.from(mContext).inflate(R.layout.item_person, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        RecommendViewholder mRecommendViewholder = (RecommendViewholder) holder;
        RecommendBean data = datas.get(position);
        mRecommendViewholder.mItemPersonFans.setText(data.getFellow());
        mRecommendViewholder.mItemPersonName.setText(data.getNickname());
        UserUtil.showUserPhoto(mContext,data.getAvatar(),mRecommendViewholder.mItemPersonAvator);
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public class RecommendViewholder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_person_avator)
        MyUserPhoto mItemPersonAvator;
        @BindView(R.id.item_person_name)
        TextView mItemPersonName;
        @BindView(R.id.item_person_fans)
        TextView mItemPersonFans;
        @BindView(R.id.item_card_layout)
        CardView mItemCardLayout;

        public RecommendViewholder(View itemView) {
            super(itemView);
        }

        @OnClick(R.id.item_card_layout)
        public void onViewClicked() {
            // Aya : 2017/4/11 跳转用户界面疑问
        }
    }
}