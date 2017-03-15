package com.easyvaas.elapp.adapter.item;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.easyvaas.common.adapter.AdapterItem;
import com.hooview.app.R;
import com.easyvaas.elapp.bean.user.ReadRecord;
import com.easyvaas.elapp.bean.user.Record;
import com.easyvaas.elapp.ui.live.PlayerActivity;
import com.easyvaas.elapp.utils.DateTimeUtil;
import com.easyvaas.elapp.utils.StringUtil;
import com.easyvaas.elapp.utils.Utils;
import com.squareup.picasso.Picasso;

/**
 * Created by guoliuya on 2017/2/27.
 */

public class ReadHistoryAdapterItem implements AdapterItem<ReadRecord> {
    private Context mContext;
    ImageView ivThumbnail;
    TextView tvTitle;
    TextView tvTime;
    TextView tvWatchCount;

    public ReadHistoryAdapterItem(Context context) {
        this.mContext = context;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_common_news;
    }

    @Override
    public void onBindViews(View root) {
        ivThumbnail = (ImageView) root.findViewById(R.id.iv_thumbnail);
        tvTitle = (TextView) root.findViewById(R.id.tv_title);
        tvTime = (TextView) root.findViewById(R.id.tv_time);
        tvWatchCount = (TextView) root.findViewById(R.id.tv_watch_count);
    }

    @Override
    public void onSetViews() {

    }

    @Override
    public void onUpdateViews(final ReadRecord model, int position) {
        if (model != null) {
            //              Utils.showImage(newsModel.getCover(), R.drawable.account_bitmap_list, ivThumbnail);
            Picasso.with(mContext).load(model.getPic()).error(R.drawable.account_bitmap_list).into(ivThumbnail);
            tvTitle.setText(model.getTitle());
            tvTime.setText(model.getTime());
            tvWatchCount.setText(mContext.getString(R.string.news_watch_count,
                    StringUtil.formatThousand(model.getCount())));
            ivThumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.showNewsDetail(mContext, model.getTitle(), model.getId() + "");
                }
            });
        }
    }
}

