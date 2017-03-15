package com.easyvaas.common.gift.view;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.easyvaas.common.gift.bean.GiftEntity;
import com.easyvaas.common.gift.bean.GoodsEntity;
import com.easyvaas.common.gift.utils.GiftDB;
import com.easyvaas.common.gift.utils.GiftUtility;

import com.easyvaas.common.gift.R;

public class GiftPagerView extends FrameLayout {
    public static final int TAB_CATEGORY_GIFT = 2;

    public static final int MSG_SEND_BURST_GIFT = 10;
    public static final int MSG_BURST_COUNT_DOWN = 11;
    public static final int TIME_BURST_GIFT_COUNT_DOWN = 2000;
    public static final int COUNT_BURST_END = -1;

    private GiftPagerAdapter mCurrentGiftAdapter;
    private GiftPagerAdapter mGiftPagerAdapter;

    private Context mContext;
    private OnGiftSendCallBack mOnGiftSendCallBack;
    private ViewPager mGiftContentViewPager;
    private View mTabGiftTv;
    private TextView mECoinAccountTv;
    private View mCashInBtn;
    private Button mSendGiftBtn;
    private PageIndicateView mPageIndicateView;
    private GiftDB mPref;

    private GoodsEntity mSelectGift;
    private ImageView tabGiftLine;

    private List<GoodsEntity> mGiftDataList;
    private List<GoodsEntity> mExpressDataList;
    private List<GiftEntity> mBurstGiftEntities;
    private List<GoodsEntity> mRedPackList;
    private int mCurrentTabType;
    private int mECoinGiftCount = 0;
    private int mSendGiftCount = 0;
    private String mNickName;
    private String mLogoUrl;
    private MyHandler mHandler;

    private static class MyHandler extends Handler {
        private SoftReference<GiftPagerView> softReference;

        public MyHandler(GiftPagerView giftPagerView) {
            softReference = new SoftReference<GiftPagerView>(giftPagerView);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            GiftPagerView giftPagerView = softReference.get();

            switch (msg.what) {
                case MSG_SEND_BURST_GIFT:
                    if (giftPagerView.mBurstGiftEntities != null
                            && giftPagerView.mBurstGiftEntities.size() > 0
                            && giftPagerView.mSendGiftCount == 0) {
                        GiftEntity entity = giftPagerView.mBurstGiftEntities.get(0);
                        if (entity != null) {
                            giftPagerView.mOnGiftSendCallBack.sendGift(entity);
                        }
                        giftPagerView.mBurstGiftEntities.remove(0);
                        sendEmptyMessageDelayed(MSG_SEND_BURST_GIFT, 50);
                    }
                    break;
                case MSG_BURST_COUNT_DOWN:
                    giftPagerView.saveBurstGift();
                    break;
            }

        }
    }

    private GiftGridAdapter.OnGiftViewClickCallBack mOnGiftViewClickCallBack
            = new GiftGridAdapter.OnGiftViewClickCallBack() {
        @Override
        public void onGiftViewItemClick(GoodsEntity data) {
            data.setChecked(true);
            if (mSelectGift != null) {
                mSelectGift.setChecked(false);
            }
            saveBurstGift();
            mSelectGift = data;
            mSendGiftBtn.setSelected(true);
            mSendGiftBtn.setEnabled(true);
        }
    };

    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.tab_gift_tv) {
                onTabChanged(TAB_CATEGORY_GIFT);
            } else if (v.getId() == R.id.send_gift_btn) {
                if (!isAssetEnough()) {
                    return;
                }
                initBurstGift();
            } else if (v.getId() == R.id.cash_in_tv||v.getId()==R.id.e_coin_account_tv) {
                mOnGiftSendCallBack.jumpCashInActivity();
                mOnGiftSendCallBack.onUpdateView();
            }
        }
    };

    public GiftPagerView(Context context) {
        super(context);
        init(context);
    }

    public GiftPagerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GiftPagerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        View rootView = LayoutInflater.from(context).inflate(R.layout.view_gift_expression_page, null);
        mHandler = new MyHandler(this);
        mGiftContentViewPager = (ViewPager) rootView.findViewById(R.id.gift_content_view_pager);
        mGiftContentViewPager.addOnPageChangeListener(new ViewPagerOnPageChangeListener());
        mTabGiftTv = rootView.findViewById(R.id.tab_gift_tv);
        mTabGiftTv.setOnClickListener(mOnClickListener);
        mSendGiftBtn = (Button) rootView.findViewById(R.id.send_gift_btn);
        tabGiftLine = (ImageView) rootView.findViewById(R.id.tab_gift_line);
        tabGiftLine.setSelected(true);
        mSendGiftBtn.setOnClickListener(mOnClickListener);
        mCashInBtn = rootView.findViewById(R.id.cash_in_tv);
        mCashInBtn.setOnClickListener(mOnClickListener);
        mECoinAccountTv = (TextView) rootView.findViewById(R.id.e_coin_account_tv);
        mPageIndicateView = (PageIndicateView) rootView.findViewById(R.id.page_indicate_view);

        mPref = GiftDB.getInstance(context);
        updateAssetInfo();
        initData();

        if (mGiftDataList.size() == 0) {
            mTabGiftTv.setVisibility(View.GONE);
        } else if (mECoinGiftCount == 0) {
            mECoinAccountTv.setVisibility(View.GONE);
        }

        onTabChanged(TAB_CATEGORY_GIFT);
        addView(rootView);
    }

    public void setSelf(String nickName, String logoUrl) {
        this.mNickName = nickName;
        this.mLogoUrl = logoUrl;
    }

    public void setOnViewClickListener(OnGiftSendCallBack listener) {
        this.mOnGiftSendCallBack = listener;
    }

    private void onTabChanged(int type) {
        switch (type) {
            case TAB_CATEGORY_GIFT:
                if (mCurrentTabType == TAB_CATEGORY_GIFT) {
                    return;
                }
                mCurrentTabType = TAB_CATEGORY_GIFT;
                if (mGiftPagerAdapter == null) {
                    mGiftPagerAdapter = new GiftPagerAdapter(mContext);
                    mGiftPagerAdapter.setOnGridItemClickListener(mOnGiftViewClickCallBack);
                }
                mGiftPagerAdapter.setData(mGiftDataList);
                mCurrentGiftAdapter = mGiftPagerAdapter;
                mTabGiftTv.setSelected(true);
                if (mECoinGiftCount > 0) {
                    mECoinAccountTv.setVisibility(VISIBLE);
                    mCashInBtn.setVisibility(View.VISIBLE);
                }
                break;
        }
        mGiftContentViewPager.setAdapter(mCurrentGiftAdapter);
        mPageIndicateView.setPageCount(mCurrentGiftAdapter.getTotalPage());
        mPageIndicateView.setCurrentPageIndex(mCurrentGiftAdapter.getCurrentPageIndex());
        mGiftContentViewPager.setCurrentItem(mCurrentGiftAdapter.getCurrentPageIndex());
        if (mSelectGift != null) {
            if (type != mSelectGift.getType()) {
                mSendGiftBtn.setSelected(false);
                mSendGiftBtn.setEnabled(false);
            } else {
                mSendGiftBtn.setSelected(true);
                mSendGiftBtn.setEnabled(true);
            }

        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    private class ViewPagerOnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageSelected(int pageNumber) {
            mCurrentGiftAdapter.setCurrentPageIndex(pageNumber);
            mPageIndicateView.setCurrentPageIndex(mCurrentGiftAdapter.getCurrentPageIndex());
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }

    private void initData() {
        mExpressDataList = new ArrayList<>();
        mGiftDataList = new ArrayList<>();
        mBurstGiftEntities = new ArrayList<>();
        mRedPackList = new ArrayList<>();
        List<GoodsEntity> objects = GiftUtility.getAllCachedGoods(mContext);
        if (objects == null || objects.size() == 0) {
            return;
        }
        for (GoodsEntity goodsEntity : objects) {
            if (goodsEntity.getType() == GoodsEntity.TYPE_EMOJI) {
                mExpressDataList.add(goodsEntity);
            } else if (goodsEntity.getType() == GoodsEntity.TYPE_BALERY) {
                // TODO Do nothing
            } else if (goodsEntity.getType() == GoodsEntity.TYPE_GIFT) {
                if (goodsEntity.getAnitype() == GoodsEntity.ANI_TYPE_RED_PACK) {
                    mRedPackList.add(goodsEntity);
                }
                mGiftDataList.add(goodsEntity);
                if (goodsEntity.getCosttype() == GoodsEntity.COST_TYPE_E_COIN) {
                    mECoinGiftCount++;
                }
            }
        }
    }

    public void exceptRedPackGift() {
        mGiftDataList.removeAll(mRedPackList);
        onTabChanged(TAB_CATEGORY_GIFT);
    }
    public void updateAssetInfo() {
        mECoinAccountTv.setText(mContext.getString(R.string.e_coin_count,
                mPref.getLong(GiftDB.KEY_PARAM_ASSET_E_COIN_ACCOUNT, 0))+" >");
    }

    public void checkBurst() {
        if (!isAssetEnough()) {
            return;
        }
        mHandler.removeMessages(MSG_BURST_COUNT_DOWN);
        mHandler.sendEmptyMessageDelayed(MSG_BURST_COUNT_DOWN, TIME_BURST_GIFT_COUNT_DOWN);
        mSendGiftCount++;
        if (mBurstGiftEntities.size() > 0) {
            mOnGiftSendCallBack
                    .onBurstCountChanged(mSendGiftCount,
                            mBurstGiftEntities.get(mBurstGiftEntities.size() - 1));
        }
    }

    private boolean isAssetEnough() {
        if (mSelectGift == null) {
            return false;
        }
        if (mSelectGift.getCosttype() == GoodsEntity.COST_TYPE_E_COIN) {
            long eCoin = mPref.getLong(GiftDB.KEY_PARAM_ASSET_E_COIN_ACCOUNT, 0);
            if (mSelectGift.getCost() > eCoin) {
                Toast.makeText(getContext(), R.string.msg_gift_buy_failed_e_coin_not_enough,
                        Toast.LENGTH_SHORT).show();
                mOnGiftSendCallBack.onECoinNoEnough();
                return false;
            }
            eCoin -= mSelectGift.getCost();
            mPref.putLong(GiftDB.KEY_PARAM_ASSET_E_COIN_ACCOUNT, eCoin);
            mOnGiftSendCallBack.onECoinChanged(eCoin);
        }
        updateAssetInfo();
        return true;
    }

    private void saveBurstGift() {
        if (mBurstGiftEntities.size() > 0) {
            mBurstGiftEntities.get(mBurstGiftEntities.size() - 1).setGiftCount(mSendGiftCount);
            mOnGiftSendCallBack.onBurstCountChanged(COUNT_BURST_END,
                    mBurstGiftEntities.get(mBurstGiftEntities.size() - 1));
            mHandler.sendEmptyMessage(MSG_SEND_BURST_GIFT);
        }
        mSendGiftCount = 0;
    }

    private void initBurstGift() {
        mSendGiftCount = 1;
        GiftEntity giftEntity = new GiftEntity();
        giftEntity.setGiftType(mCurrentTabType);
        giftEntity.setGiftCount(mSendGiftCount);
        giftEntity.setGiftId(mSelectGift.getId());
        giftEntity.setGiftPicUrl(mSelectGift.getPic());
        giftEntity.setGiftAniUrl(mSelectGift.getAni());
        giftEntity.setGiftName(mSelectGift.getName());
        giftEntity.setNickname(mNickName);
        giftEntity.setUserLogo(mLogoUrl);
        giftEntity.setAnimationType(mSelectGift.getAnitype());
        mBurstGiftEntities.add(giftEntity);
        mHandler.removeMessages(MSG_BURST_COUNT_DOWN);
        mOnGiftSendCallBack.onUpdateView();
        if (mSelectGift.getAnitype() == GoodsEntity.ANI_TYPE_RED_PACK) {
            mHandler.sendEmptyMessage(MSG_BURST_COUNT_DOWN);
            return;
        }
        mOnGiftSendCallBack.sendBurstGift(giftEntity);
        mHandler.sendEmptyMessageDelayed(MSG_BURST_COUNT_DOWN, TIME_BURST_GIFT_COUNT_DOWN);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    public interface OnGiftSendCallBack {
        void sendGift(GiftEntity data);

        void sendBurstGift(GiftEntity entity);

        void onBurstCountChanged(int burstCount, GiftEntity data);

        void onUpdateView();

        void jumpCashInActivity();

        void onECoinChanged(long eCoin);

        void onECoinNoEnough();
    }
}
