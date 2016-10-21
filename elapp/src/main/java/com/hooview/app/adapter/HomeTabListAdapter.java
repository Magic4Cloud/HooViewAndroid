package com.hooview.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.hooview.app.R;
import com.hooview.app.adapter.holder.HomeTabViewHolder;
import com.hooview.app.bean.video.VideoEntity;

import java.util.List;

/**
 * Author:   yyl
 * Description:
 * CreateDate:     2016/10/20
 */
public class HomeTabListAdapter extends RecyclerView.Adapter<HomeTabViewHolder> {

    private List<VideoEntity> result;

    private Context context;

    private LayoutInflater inflater;

    //构造方法传递数据
    public HomeTabListAdapter(Context context, List<VideoEntity> mLists) {
        this.result = mLists;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public HomeTabViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HomeTabViewHolder(inflater.inflate(R.layout.item_home_tab_left, parent,false), context);
    }

    @Override
    public void onBindViewHolder(HomeTabViewHolder holder, int position) {
        holder.bindData(result.get(position));
    }

    @Override
    public int getItemCount() {
        return result.size();
    }
}
