package com.easyvaas.elapp.adapter;


import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.easyvaas.common.adapter.AdapterItem;
import com.easyvaas.common.adapter.CommonRcvAdapter;
import com.easyvaas.elapp.adapter.item.ImageTextNormalMessageItem;
import com.easyvaas.elapp.adapter.item.ImageTextReplyMessageItem;
import com.easyvaas.elapp.chat.model.EMMessageWrapper;
import com.easyvaas.elapp.utils.Logger;

import java.util.List;

public class ImageTextLiveChatAdapter extends CommonRcvAdapter<EMMessageWrapper> {
    private static final String TAG = "ImageTextLiveChatAdapte";
    private boolean isAnchor;
    private Context mContext;

    public ImageTextLiveChatAdapter(boolean isAnchor, Context context, List<EMMessageWrapper> list) {
        super(list);
        this.isAnchor = isAnchor;
        mContext = context;
    }

    @NonNull
    @Override
    public AdapterItem<EMMessageWrapper> getItemView(Object type) {
        EMMessageWrapper wrapper = (EMMessageWrapper) type;
        Logger.d(TAG, "getItemView: " + wrapper.nickname);
        if (TextUtils.isEmpty(wrapper.replyNickname)) {
            return new ImageTextNormalMessageItem(mContext);
        } else {
            return new ImageTextReplyMessageItem();
        }
    }

    @Override
    public Object getItemViewType(EMMessageWrapper emMessageWrapper) {
        return emMessageWrapper;
    }
}
