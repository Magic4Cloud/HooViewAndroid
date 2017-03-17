package com.easyvaas.elapp.adapter.item;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.easyvaas.common.adapter.AdapterItem;
import com.easyvaas.elapp.utils.Utils;
import com.hooview.app.R;
import com.easyvaas.elapp.bean.message.MessageGroupEntity;

/**
 * Created by guoliuya on 2017/2/25.
 */

public class MessageUnReadAdapterItem implements AdapterItem<MessageGroupEntity> {
    private Context mContext;
    private TextView mMsgTime;
    private TextView mMsgAdvise;
    private TextView mMsgContent;
    private ImageView mImageIcon;

    public MessageUnReadAdapterItem(Context context) {
        this.mContext = context;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_unread_message;
    }

    @Override
    public void onBindViews(View root) {
        mMsgTime = (TextView) root.findViewById(R.id.tv_unread_time);
        mMsgAdvise = (TextView) root.findViewById(R.id.tv_msg_advise);
        mMsgContent = (TextView) root.findViewById(R.id.tv_msg_content);
        mImageIcon = (ImageView) root.findViewById(R.id.iv_msg_icon);
    }

    @Override
    public void onSetViews() {

    }

    @Override
    public void onUpdateViews(MessageGroupEntity model, int position) {
        Utils.showImage(model.getIcon(),R.drawable.default_loading_bg,mImageIcon);
        mMsgTime.setText(model.getUpdate_time());
        mMsgAdvise.setText(model.getTitle());
        mMsgContent.setText(model.getLastest_content()!=null&&model.getLastest_content().getData()!=null?model.getLastest_content().getData().getText():"");
    }
}
