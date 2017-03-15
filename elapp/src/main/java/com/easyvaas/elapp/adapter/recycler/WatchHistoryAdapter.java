package com.easyvaas.elapp.adapter.recycler;

import java.util.List;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.TabHost;

import com.easyvaas.common.adapter.AdapterItem;
import com.easyvaas.common.adapter.CommonRcvAdapter;
import com.easyvaas.elapp.adapter.item.ReadHistoryAdapterItem;
import com.easyvaas.elapp.adapter.item.WatchHistoryAdapterItem;
import com.easyvaas.elapp.bean.message.MessageGroupEntity;
import com.easyvaas.elapp.bean.user.Record;

/**
 * Created by guoliuya on 2017/2/27.
 */

public class WatchHistoryAdapter extends CommonRcvAdapter<Object> {
    private Context mContext;
    private boolean mIsRead;

    public WatchHistoryAdapter(List<Object> data,Context context,boolean isRead) {
        super(data);
        this.mContext = context;
        this.mIsRead = isRead;
    }

    @NonNull
    @Override
    public AdapterItem getItemView(Object type) {
        if(!mIsRead){
            return new WatchHistoryAdapterItem(mContext);
        }else{
            return new ReadHistoryAdapterItem(mContext);
        }
    }

    @Override
    public Object getItemViewType(Object o) {
        return super.getItemViewType(o);
    }
}
