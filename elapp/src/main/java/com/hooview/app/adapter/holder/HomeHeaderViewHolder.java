package com.hooview.app.adapter.holder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.hooview.app.R;
import com.hooview.app.activity.WebViewActivity;
import com.hooview.app.bean.CarouselInfoEntity;
import com.hooview.app.bean.CarouselInfoEntityArray;
import com.hooview.app.utils.Constants;
import com.hooview.app.utils.ViewUtil;

/**
 * Author:   yyl
 * Description:
 * CreateDate:     2016/10/26
 */
public class HomeHeaderViewHolder extends RecyclerView.ViewHolder {

    private Context context;

    //轮播图
    private ConvenientBanner convenientBanner;
    private LinearLayout ll_container;
    private ImageView iv_selected;

    //距离
    private int mPointDis;

    //banner数量
    private int bannerCount;


    private static final String EXTRA_TYPE = "extra_type";
    private static final String EXTRA_KEY = "extra_key";

    private boolean isDrawPoint = false;

    public HomeHeaderViewHolder(View itemView,Context context) {
        super(itemView);
        this.context = context;
        convenientBanner = (ConvenientBanner) itemView.findViewById(R.id.banner);
        ll_container = (LinearLayout) itemView.findViewById(R.id.ll_container);
        iv_selected = (ImageView) itemView.findViewById(R.id.iv_selected_point);
    }

    //绑定数据
    public void bindData(final CarouselInfoEntityArray carouselInfoEntity){
        if(carouselInfoEntity != null) {
            if(isDrawPoint) {
                return;
            }
            addPoint(carouselInfoEntity.getCount());
            bannerCount = carouselInfoEntity.getCount();
            convenientBanner.setPages(new CBViewHolderCreator<HomeHeaderSliderHolder>() {
                @Override
                public HomeHeaderSliderHolder createHolder() {
                    return new HomeHeaderSliderHolder();
                }
            }, carouselInfoEntity.getObjects())    //设置需要切换的View
                    .setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {

                            Bundle bundle = new Bundle();
                            //int type = bundle.getInt(EXTRA_TYPE, -1);
                            String keyValue = bundle.getString(EXTRA_KEY);
                            Intent intent = null;
                            int type = carouselInfoEntity.getObjects().get(position).getContent().getType();
                            String title = carouselInfoEntity.getObjects().get(position).getContent().getData().getTitle();
                            String url = carouselInfoEntity.getObjects().get(position).getContent().getData().getWeb_url();
                            if (type == CarouselInfoEntity.TYPE_WEB) {
                                intent = new Intent(context, WebViewActivity.class);
                                intent.putExtra(WebViewActivity.EXTRA_KEY_TYPE, WebViewActivity.TYPE_ACTIVITY);
                                intent.putExtra(WebViewActivity.EXTRA_KEY_URL, url);
                                intent.putExtra(Constants.EXTRA_KEY_TITLE, title);
                            }
                            if (intent != null) {
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                            }
                        }
                    });
            if (carouselInfoEntity.getCount() < 2) {
                convenientBanner.setCanLoop(false);
                convenientBanner.setPointViewVisible(false);
            } else {
                convenientBanner.setPointViewVisible(true)    //设置指示器是否可见
                        .startTurning(5000);
            }

            convenientBanner.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


                }

                @Override
                public void onPageSelected(int position) {
                    int currentPosition = position % bannerCount;

                    int leftMargin = currentPosition
                            * mPointDis;
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) iv_selected
                            .getLayoutParams();
                    params.leftMargin = leftMargin;// 修改左边距

                    // 重新设置布局参数
                    iv_selected.setLayoutParams(params);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }
    }

    //添加圆点指示器
    private void addPoint(int count) {
        // 初始化小圆点
        for (int i = 0; i < count; i++) {
            isDrawPoint = true;
            ImageView point = new ImageView(context);
            point.setImageResource(R.drawable.shape_main_point_default);// 设置图片(shape形状)

            // 初始化布局参数, 宽高包裹内容,父控件是谁,就是谁声明的布局参数
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);

            if (i > 0) {
                // 从第二个点开始设置左边距
                params.leftMargin = (int) ViewUtil.dp2Px(context, 10);
            }

            point.setLayoutParams(params);// 设置布局参数

            ll_container.addView(point);// 给容器添加圆点
        }
        iv_selected.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {
                        // 移除监听,避免重复回调
                        iv_selected.getViewTreeObserver()
                                .removeGlobalOnLayoutListener(this);
                        // ivRedPoint.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        // layout方法执行结束的回调
                        mPointDis = ll_container.getChildAt(1).getLeft()
                                - ll_container.getChildAt(0).getLeft() - (int) ViewUtil.dp2Px(context, 2);
                    }
                });

    }

}
