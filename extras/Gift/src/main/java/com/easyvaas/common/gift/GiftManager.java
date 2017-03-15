package com.easyvaas.common.gift;

import java.io.FileNotFoundException;
import java.util.List;

import android.content.Context;
import android.view.ViewGroup;

import rx.functions.Action1;

import com.easyvaas.common.gift.action.NotificationAction;
import com.easyvaas.common.gift.animator.GiftAnimator;
import com.easyvaas.common.gift.bean.GiftEntity;
import com.easyvaas.common.gift.bean.GoodsEntity;
import com.easyvaas.common.gift.bean.GoodsEntityArray;
import com.easyvaas.common.gift.utils.GiftDB;
import com.easyvaas.common.gift.utils.GiftUtility;

public class GiftManager {
    private GiftAnimator mGiftAnimator;

    public GiftManager(ViewGroup host, Action1<NotificationAction> onNotificationSend) {
        mGiftAnimator = new GiftAnimator(host, onNotificationSend, "");
    }

    public void showRemoteAnim(List<GiftEntity> entities) {
        try {
            mGiftAnimator.showRemoteAnim(entities);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void showLocalAlignmentsEndAnim(GiftEntity entity) {
        try {
            mGiftAnimator.showLocalAlignmentsEndAnim(entity);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void showLocalAnim(GiftEntity entity) {
        try {
            mGiftAnimator.showLocalAnim(entity);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void setECoinCount(Context context, long eCoinCount) {
        GiftDB.getInstance(context).putLong(GiftDB.KEY_PARAM_ASSET_E_COIN_ACCOUNT, eCoinCount);
    }

    public static void setGoodsList(Context context, GoodsEntityArray goodsEntityArray) {
        if (goodsEntityArray.getGoodslist() == null || goodsEntityArray.getGoodslist().size() == 0) {
            GiftUtility.removeGiftCache(context);
        } else {
            List<GoodsEntity> objects = goodsEntityArray.getGoodslist();
            GiftUtility.loadGift(context, objects);
        }
    }
}
