package com.easyvaas.elapp.adapter.item;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.easyvaas.common.adapter.AdapterItem;
import com.easyvaas.common.widget.RoundImageView;
import com.hooview.app.R;
import com.easyvaas.elapp.bean.video.LiveCommentModel;
import com.easyvaas.elapp.utils.Utils;

public class LiveCommentAdapterItem implements AdapterItem<LiveCommentModel.PostsEntity> {
    private RoundImageView mRivHeader;
    private TextView mTvName;
    private TextView mTvComment;


    private Context mContext;

    public LiveCommentAdapterItem(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_live_comment;
    }

    @Override
    public void onBindViews(View root) {
        mRivHeader = (RoundImageView) root.findViewById(R.id.riv_header);
        mTvName = (TextView) root.findViewById(R.id.tv_name);
        mTvComment = (TextView) root.findViewById(R.id.tv_comment);
    }

    @Override
    public void onSetViews() {

    }

    @Override
    public void onUpdateViews(final LiveCommentModel.PostsEntity model, int position) {
        if (model != null) {
            mTvComment.setText(model.getContent());
                mTvName.setText(model.getUser_name());
                Utils.showImage(model.getUser_avatar(), R.drawable.account_bitmap_user, mRivHeader);

        }

    }
}
