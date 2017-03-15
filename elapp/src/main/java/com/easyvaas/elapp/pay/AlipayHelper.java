/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.pay;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.hooview.app.R;
import com.easyvaas.elapp.bean.pay.PayOrderEntity;
import com.easyvaas.elapp.ui.pay.CashInActivity;
import com.easyvaas.elapp.utils.DialogUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class AlipayHelper {
    private static final String TAG = "AlipayHelper";
    // 商户PID
    public String PARTNER = "";
    // 商户收款账号
    public String SELLER = "";
    // 商户私钥，pkcs8格式
    public String RSA_PRIVATE = "";
    // 支付宝公钥
    public static final String RSA_PUBLIC = "";

    public static final int MSG_SDK_PAY_FLAG = 100;

    private Activity mActivity;

    private Handler mHandler;

    public AlipayHelper(Activity activity, CashInActivity.MyHandler handler) {
        this.mActivity = activity;
        this.mHandler = handler;
    }

    /**
     * call alipay sdk pay. 调用SDK支付
     */
    public void aliPay(PayOrderEntity orderEntity) {
        PARTNER = orderEntity.getPartner();
        SELLER = orderEntity.getSeller_id();
        RSA_PRIVATE = orderEntity.getSign();
        if (TextUtils.isEmpty(PARTNER) || TextUtils.isEmpty(RSA_PRIVATE) || TextUtils.isEmpty(SELLER)) {
            DialogUtil.getOneButtonDialog(mActivity, R.string.msg_pay_failed, true, true, null).show();
            return;
        }
        String orderInfo = getOrderInfo(orderEntity);

        /**
         * 特别注意，这里的签名逻辑需要放在服务端，切勿将私钥泄露在代码中！
         */
        String sign = orderEntity.getSign();
        //                sign(orderInfo);
        try {
            /**
             * 仅需对sign 做URL编码
             */
            sign = URLEncoder.encode(sign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        /**
         * 完整的符合支付宝参数规范的订单信息
         */
        final String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + getSignType();

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(mActivity);
                // 调用支付接口，获取支付结果
                String result = alipay.pay(payInfo, true);

                Message msg = new Message();
                msg.what = MSG_SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    /**
     * get the sdk version. 获取SDK版本号
     */
    public void getSDKVersion() {
        PayTask payTask = new PayTask(mActivity);
        String version = payTask.getVersion();
        Toast.makeText(mActivity, version, Toast.LENGTH_SHORT).show();
    }

    /**
     * create the order info. 创建订单信息
     */
    private String getOrderInfo(PayOrderEntity orderEntity) {

        // 参数编码， 固定值
        String orderInfo = "_input_charset=" + "\"" + orderEntity.get_input_charset() + "\"";

        orderInfo += "&app_id=" + "\"" + orderEntity.getApp_id() + "\"";

        orderInfo += "&body=" + "\"" + orderEntity.getBody() + "\"";

        orderInfo += "&notify_url=" + "\"" + orderEntity.getNotify_url() + "\"";

        orderInfo += "&out_trade_no=" + "\"" + orderEntity.getOut_trade_no() + "\"";
        // 签约合作者身份ID
        orderInfo += "&partner=" + "\"" + PARTNER + "\"";

        orderInfo += "&payment_type=" + "\"" + orderEntity.getPayment_type() + "\"";

        // 签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

        orderInfo += "&service=" + "\"" + orderEntity.getService() + "\"";

        // 商户网站唯一订单号

        // 商品名称
        orderInfo += "&subject=" + "\"" + orderEntity.getSubject() + "\"";

        // 商品详情

        // 商品金额
        orderInfo += "&total_fee=" + "\"" + orderEntity.getTotal_fee() + "\"";

        // 服务器异步通知页面路径

        // 服务接口名称， 固定值

        // 支付类型， 固定值

        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        //        orderInfo += "&it_b_pay=\"30m\"";

        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
        //        orderInfo += "&return_url=\"m.alipay.com\"";

        // 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
        // orderInfo += "&paymethod=\"expressGateway\"";

        return orderInfo;
    }

    /**
     * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
     */
    private String getOutTradeNo() {
        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss", Locale.getDefault());
        Date date = new Date();
        String key = format.format(date);

        Random r = new Random();
        key = key + r.nextInt();
        key = key.substring(0, 15);
        return key;
    }

    /**
     * sign the order info. 对订单信息进行签名
     *
     * @param content 待签名订单信息
     */
    private String sign(String content) {
        return SignUtils.sign(content, RSA_PRIVATE);
    }

    /**
     * get the sign type we use. 获取签名方式
     */
    private String getSignType() {
        return "sign_type=\"RSA\"";
    }
}
