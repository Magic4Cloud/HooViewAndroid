/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.bean.pay;

import com.google.gson.annotations.SerializedName;

public class PayOrderEntity {
    // Weixin order info
    private String appid;
    private String mch_id;
    private String noncestr;
    @SerializedName("package")
    private String packageX;
    private String partnerid;
    private String prepay_id;
    private String prepayid;
    private String result_code;
    private String return_code;
    private String return_msg;
    private String sign;
    private String timestamp;
    private String trade_type;

    // Alipay order info
    private String _input_charset;
    private String app_id;
    private String body;
    private String notify_url;
    private String out_trade_no;
    private String partner;
    private String payment_type;
    private String seller_id;
    private String service;
    private String sign_type;
    private String subject;
    private String total_fee;

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public void setMch_id(String mch_id) {
        this.mch_id = mch_id;
    }

    public void setNoncestr(String noncestr) {
        this.noncestr = noncestr;
    }

    public void setPackageX(String packageX) {
        this.packageX = packageX;
    }

    public void setPartnerid(String partnerid) {
        this.partnerid = partnerid;
    }

    public void setPrepay_id(String prepay_id) {
        this.prepay_id = prepay_id;
    }

    public void setPrepayid(String prepayid) {
        this.prepayid = prepayid;
    }

    public void setResult_code(String result_code) {
        this.result_code = result_code;
    }

    public void setReturn_code(String return_code) {
        this.return_code = return_code;
    }

    public void setReturn_msg(String return_msg) {
        this.return_msg = return_msg;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setTrade_type(String trade_type) {
        this.trade_type = trade_type;
    }

    public String getAppid() {
        return appid;
    }

    public String getMch_id() {
        return mch_id;
    }

    public String getNoncestr() {
        return noncestr;
    }

    public String getPackageX() {
        return packageX;
    }

    public String getPartnerid() {
        return partnerid;
    }

    public String getPrepay_id() {
        return prepay_id;
    }

    public String getPrepayid() {
        return prepayid;
    }

    public String getResult_code() {
        return result_code;
    }

    public String getReturn_code() {
        return return_code;
    }

    public String getReturn_msg() {
        return return_msg;
    }

    public String getSign() {
        return sign;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getTrade_type() {
        return trade_type;
    }

    public String get_input_charset() {
        return _input_charset;
    }

    public void set_input_charset(String _input_charset) {
        this._input_charset = _input_charset;
    }

    public String getApp_id() {
        return app_id;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getNotify_url() {
        return notify_url;
    }

    public void setNotify_url(String notify_url) {
        this.notify_url = notify_url;
    }

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }

    public String getPartner() {
        return partner;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }

    public String getPayment_type() {
        return payment_type;
    }

    public void setPayment_type(String payment_type) {
        this.payment_type = payment_type;
    }

    public String getSeller_id() {
        return seller_id;
    }

    public void setSeller_id(String seller_id) {
        this.seller_id = seller_id;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getSign_type() {
        return sign_type;
    }

    public void setSign_type(String sign_type) {
        this.sign_type = sign_type;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTotal_fee() {
        return total_fee;
    }

    public void setTotal_fee(String total_fee) {
        this.total_fee = total_fee;
    }
}
