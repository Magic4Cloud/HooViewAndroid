package com.hooview.app.adapter.holder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.hooview.app.R;
import com.hooview.app.bean.video.VideoEntity;
import com.hooview.app.utils.PicassoUtil;
import com.hooview.app.utils.Utils;
import com.hooview.app.view.RoundRectangleImageView;

/**
 * Author:   yyl
 * Description:
 * CreateDate:     2016/10/20
 */
public class HomeTabViewHolder extends RecyclerView.ViewHolder {

    private Context context;

    private TextView tvTitle;
    private TextView tvName;
    private TextView tvTime;
    private RoundRectangleImageView rectangleView;
    private ImageButton ivPlay;

    private ImageView ivHeader;

    public HomeTabViewHolder(View itemView, Context context) {
        super(itemView);
        this.context = context;
        tvTime = (TextView) itemView.findViewById(R.id.tv_time);
        tvName = (TextView) itemView.findViewById(R.id.tv_name);
        tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
        ivPlay = (ImageButton) itemView.findViewById(R.id.iv_play);
        rectangleView = (RoundRectangleImageView) itemView.findViewById(R.id.rectangle_view);
        ivHeader = (ImageView) itemView.findViewById(R.id.iv_header);

        //动态设置封面的高度
        //RelativeLayout.LayoutParams layoutParams = rectangleView.
        //layoutParams.width = ScreenUtil.getLiveInUgcItemWidth(context, recyclerViewWidth);
//        layoutParams.width = (int) ViewUtil.getScreenWidth(context);
//        layoutParams.height = layoutParams.width;
//        itemView.setLayoutParams(layoutParams);
    }

    //绑定数据
    public void bindData(final VideoEntity entity) {
        if (entity.getTitle() != null) {
            tvTitle.setText(entity.getTitle());
        }
        if (entity.getNickname() != null) {
            tvName.setText(entity.getNickname());
        }
        if (entity.getLive_start_time() != null) {
            tvTime.setText(entity.getLive_start_time());
        }
        if (entity.getThumb() != null) {
            PicassoUtil.loadPlaceholder(context, entity.getThumb(), R.drawable.square_holder_view).into(rectangleView);
        }

        if (entity.getLogourl() != null) {
            PicassoUtil.loadPlaceholder(context, entity.getLogourl(), R.drawable.rotundity_holder_view).into(ivHeader);
        }

        ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击跳转页面,跳转到开始播放直播
                //loadThumb(entity.getThumb(), entity.getLiving() == VideoEntity.IS_LIVING);
                Utils.watchVideo(context, entity);
            }
        });


    }
}
