package com.easyvaas.elapp.helper;


import android.content.Context;

import com.easyvaas.elapp.net.ApiConstant;
import com.easyvaas.elapp.net.ApiHelper;
import com.easyvaas.elapp.net.MyRequestCallBack;

public class CollectHelper {
    private static CollectHelper COLLECTHELPER;
    private Context mContext;

    private CollectHelper(Context context) {
        mContext = context;
    }

    public static synchronized CollectHelper getInstance(Context context) {
        if (COLLECTHELPER == null) {
            COLLECTHELPER = new CollectHelper(context);
        }
        return COLLECTHELPER;
    }

    //// TODO: 2017/2/13
    public boolean isStockCollected(String code) {
        return false;
    }

    //// TODO: 2017/2/13
    public boolean isNewsCollected(String code) {
        return false;
    }


    public static void collect(final Context context, String code, String type, String action, final MyRequestCallBack<String> callBack) {
        ApiHelper.getInstance().collect(code, type, action, new MyRequestCallBack<String>() {
            @Override
            public void onSuccess(String result) {
                if (callBack != null) {
                    callBack.onSuccess(result);
                }
            }

            @Override
            public void onFailure(String msg) {
                if (callBack != null) {
                    callBack.onFailure(msg);
                }
            }

            @Override
            public void onError(String errorInfo) {
                super.onError(errorInfo);
                if (callBack != null) {
                    callBack.onFailure(errorInfo);
                }
            }
        });
    }

    public static void collectNews(Context context, String code) {
        collect(context, code, ApiConstant.COLLECT_TYPE_NEWS, ApiConstant.COLLECT_ACTION_ADD, new MyRequestCallBack<String>() {
            @Override
            public void onSuccess(String result) {

            }

            @Override
            public void onFailure(String msg) {

            }
        });
    }

    public static void collectStock(Context context, String code, MyRequestCallBack<String> callBack) {
        collect(context, code, ApiConstant.COLLECT_TYPE_STOCK, ApiConstant.COLLECT_ACTION_ADD, callBack);
    }

}
