package com.easyvaas.common.sharelogin;

public class ShareBlock {

    private static ShareBlock mInstance;

    private ShareBlock() {
    }

    public static ShareBlock getInstance() {
        if (mInstance == null) {
            mInstance = new ShareBlock();
        }
        return mInstance;
    }

    private String mWechatAppId = "wx89d62b0eec9c1d9c";
    private String mWeiboAppId  = "3385195966";
    private String mQQAppId = "1105885323";
    private String mWechatSecret= "ffb4c8f18145bbf1d19ddd99bef72749";

    /**
     * init all config
     */
    public void initShare(String wechatAppId, String weiboAppId, String qqAppId,String wechatSecret){
        mWechatAppId = wechatAppId;
        mWeiboAppId = weiboAppId;
        mQQAppId = qqAppId;
        mWechatSecret = wechatSecret;
    }

    /**
     * init wechat config
     */
    public void initWechat(String wechatAppId,String wechatSecret){
        mWechatAppId = wechatAppId;
        mWechatSecret = wechatSecret;
    }

    /**
     * init weibo config
     */
    public void initWeibo(String weiboAppId){
        mWeiboAppId = weiboAppId;
    }

    /**
     * init QQ config
     */
    public void initQQ(String qqAppId){
        mQQAppId = qqAppId;
    }

    public String getWechatAppId() {
        return mWechatAppId;
    }

    public String getWeiboAppId() {
        return mWeiboAppId;
    }

    public String getQQAppId() {
        return mQQAppId;
    }

    public String getWechatSecret() {
        return mWechatSecret;
    }
}
