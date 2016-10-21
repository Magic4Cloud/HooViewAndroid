package com.hooview.app.adapter.holder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.bigkoo.convenientbanner.holder.Holder;
import com.hooview.app.R;
import com.hooview.app.bean.CarouselInfoEntity;
import com.hooview.app.utils.PicassoUtil;

/**
 * Author:   yyl
 * Description: 顶部轮播的Banner
 * CreateDate:     2016/10/19
 */
public class HomeHeaderSliderHolder implements Holder<CarouselInfoEntity> {

    private View view;
    private ImageView iv;

    @Override
    public View createView(Context context) {
        view = LayoutInflater.from(context).inflate(R.layout.layout_tab_home_banner, null, false);
        iv = (ImageView) view.findViewById(R.id.banner_iv);
        return view;
    }

    @Override
    public void UpdateUI(Context context, int position, CarouselInfoEntity data) {
        //更新轮播图片
        PicassoUtil.loadPlaceholder(context, data.getThumb(),R.drawable.rectangle_holder_view).into(iv);
    }
}
