package com.hooview.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.hooview.app.R;
import com.hooview.app.adapter.holder.HomeHeaderViewHolder;
import com.hooview.app.adapter.holder.HomeTabViewHolder;
import com.hooview.app.bean.CarouselInfoEntityArray;
import com.hooview.app.bean.video.VideoEntity;

import java.util.List;

/**
 * Author:   yyl
 * Description:
 * CreateDate:     2016/10/20
 */
public class HomeTabListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<VideoEntity> result;
    private List<CarouselInfoEntityArray> bannerEntity;

    private Context context;

    private LayoutInflater inflater;

    private static final int HEADER = 0;
    private static final int BODY = 1;

    //构造方法传递数据
    public HomeTabListAdapter(Context context, List<VideoEntity> mLists, List<CarouselInfoEntityArray> bannerEntity) {
        this.result = mLists;
        this.bannerEntity = bannerEntity;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == HEADER) {
            return new HomeHeaderViewHolder(inflater.inflate(R.layout.layout_home_tab_banner, parent,false),context);
        } else {
            return new HomeTabViewHolder(inflater.inflate(R.layout.item_home_tab_left, parent,false), context);
        }

    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0) {
            return HEADER;
        } else {
            return BODY;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(position == 0) {
            ((HomeHeaderViewHolder)holder).bindData(bannerEntity.get(0));
        } else {
            ((HomeTabViewHolder)holder).bindData(result.get(position - 1));
        }

    }

    @Override
    public int getItemCount() {
        return result.size() + 1;
    }
}
