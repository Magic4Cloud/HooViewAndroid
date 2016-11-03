package com.hooview.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.hooview.app.R;
import com.hooview.app.adapter.holder.HomeFooterViewHolder;
import com.hooview.app.adapter.holder.HomeHeaderViewHolder;
import com.hooview.app.adapter.holder.HomeTabViewHolder;
import com.hooview.app.bean.CarouselInfoEntityArray;
import com.hooview.app.bean.video.VideoEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Author:   yyl
 * Description:
 * CreateDate:     2016/10/20
 */
public class HomeTabListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //总的数据
    private List totalLists;

    //banner数据
    private CarouselInfoEntityArray bannerEntity;

    //视频列表数据
    private List<VideoEntity> mVideoLists = new ArrayList<>();

    private Context context;

    private LayoutInflater inflater;

    private static final int HEADER = 0;
    private static final int BODY = 1;
    private static final int FOOTER = 2;

    private boolean isLast;

    //构造方法传递数据
    public HomeTabListAdapter(Context context, List mLists) {
        this.totalLists = mLists;
        this.context = context;
        inflater = LayoutInflater.from(context);

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == HEADER) {
            return new HomeHeaderViewHolder(inflater.inflate(R.layout.layout_home_tab_banner, parent, false), context);
        } else if (viewType == BODY) {
            return new HomeTabViewHolder(inflater.inflate(R.layout.item_home_tab_left, parent, false), context);
        } else {
            return new HomeFooterViewHolder(inflater.inflate(R.layout.item_not_more, parent, false));
        }

    }

    @Override
    public int getItemViewType(int position) {

        if (!isLast) {
            if (position == 0) {
                return HEADER;
            } else {
                return BODY;
            }
        } else {
            if (position == 0) {
                return HEADER;
            } else if (position == totalLists.size() - 1) {
                return FOOTER;
            } else {
                return BODY;
            }
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof HomeHeaderViewHolder) {
            ((HomeHeaderViewHolder) holder).bindData((CarouselInfoEntityArray) totalLists.get(position));
        } else if (holder instanceof HomeTabViewHolder) {
            ((HomeTabViewHolder) holder).bindData((VideoEntity) totalLists.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return totalLists.size();
    }


    public void setLast(boolean last) {
        isLast = last;
    }
}
