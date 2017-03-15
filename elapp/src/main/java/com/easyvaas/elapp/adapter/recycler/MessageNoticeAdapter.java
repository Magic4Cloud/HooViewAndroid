package com.easyvaas.elapp.adapter.recycler;

import java.util.List;

import android.content.Context;
import android.support.annotation.NonNull;

import com.easyvaas.common.adapter.AdapterItem;
import com.easyvaas.common.adapter.CommonRcvAdapter;
import com.easyvaas.elapp.adapter.item.MessageUnReadAdapterItem;
import com.easyvaas.elapp.bean.message.MessageGroupEntity;

/**
 * Created by guoliuya on 2017/2/25.
 */

public class MessageNoticeAdapter extends CommonRcvAdapter<MessageGroupEntity>{
    private Context mContext;

    public MessageNoticeAdapter(List<MessageGroupEntity> data,Context context) {
        super(data);
        this.mContext = context;
    }

    @NonNull
    @Override public AdapterItem getItemView(Object type) {
        return new MessageUnReadAdapterItem(mContext);
    }
}
