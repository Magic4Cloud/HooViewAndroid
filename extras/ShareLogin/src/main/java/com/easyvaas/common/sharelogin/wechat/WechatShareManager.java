package com.easyvaas.common.sharelogin.wechat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.widget.Toast;

import com.easyvaas.common.sharelogin.R;
import com.easyvaas.common.sharelogin.data.ShareConstants;
import com.easyvaas.common.sharelogin.model.IShareManager;
import com.easyvaas.common.sharelogin.model.ShareContent;
import com.easyvaas.common.sharelogin.util.ShareUtil;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXMusicObject;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;

import java.io.ByteArrayOutputStream;

public class WechatShareManager extends WechatBaseManager implements IShareManager {
    /**
     * friends
     */
    public static final int WEIXIN_SHARE_TYPE_TALK = SendMessageToWX.Req.WXSceneSession;

    /**
     * friends TimeLine
     */
    public static final int WEIXIN_SHARE_TYPE_FRENDS = SendMessageToWX.Req.WXSceneTimeline;

    private static final int THUMB_SIZE = 150;

    public WechatShareManager(Context context) {
        super(context);
    }

    private void shareText(int shareType, ShareContent shareContent) {
        String text = shareContent.getContent();
        //初始化一个WXTextObject对象
        WXTextObject textObj = new WXTextObject();
        textObj.text = text;
        //用WXTextObject对象初始化一个WXMediaMessage对象
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = textObj;
        msg.description = text;
        //构造一个Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        //transaction字段用于唯一标识一个请求
        req.transaction = ShareUtil.buildTransaction("textshare");
        req.message = msg;
        //发送的目标场景， 可以选择发送到会话 WXSceneSession 或者朋友圈 WXSceneTimeline。 默认发送到会话。
        req.scene = shareType;
        mIWXAPI.sendReq(req);
    }

    private void sharePicture(int shareType, ShareContent shareContent) {
        Bitmap bmp = ShareUtil.extractThumbNail(shareContent.getImageUrl(), THUMB_SIZE, THUMB_SIZE, true);
        WXImageObject imageObject = new WXImageObject();
        imageObject.setImagePath(shareContent.getImageUrl());

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = imageObject;
        msg.description = shareContent.getTitle();
        msg.thumbData = bmpToByteArray(bmp, true);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = shareType;
        mIWXAPI.sendReq(req);

    }

    private void shareWebPage(int shareType, ShareContent shareContent) {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = shareContent.getURL();
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = shareContent.getTitle();
        msg.description = shareContent.getContent();
        Bitmap bmp;
        if (TextUtils.isEmpty(shareContent.getImageUrl())) {
            bmp = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.app_logo1);
        } else {
            bmp = ShareUtil.extractThumbNail(shareContent.getImageUrl(), THUMB_SIZE, THUMB_SIZE, true);
        }

        if (bmp == null) {
            Toast.makeText(mContext, mContext.getString(R.string.share_pic_empty),
                    Toast.LENGTH_SHORT).show();
        } else {
            msg.thumbData = ShareUtil.bmpToByteArray(bmp);
        }

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = ShareUtil.buildTransaction("webpage");
        req.message = msg;
        req.scene = shareType;
        mIWXAPI.sendReq(req);
    }

    private void shareMusic(int shareType, ShareContent shareContent) {
        WXMusicObject music = new WXMusicObject();
        //Str1+"#wechat_music_url="+str2 ;str1是网页地址，str2是音乐地址。

        music.musicUrl = shareContent.getURL() + "#wechat_music_url=" + shareContent.getMusicUrl();
        WXMediaMessage msg = new WXMediaMessage(music);
        msg.title = shareContent.getTitle();
        msg.description = shareContent.getContent();

        Bitmap thumb = ShareUtil.extractThumbNail(shareContent.getImageUrl(), THUMB_SIZE, THUMB_SIZE, true);

        if (thumb == null) {
            Toast.makeText(mContext, mContext.getString(R.string.share_pic_empty),
                    Toast.LENGTH_SHORT).show();
        } else {
            msg.thumbData = ShareUtil.bmpToByteArray(thumb);
        }

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = ShareUtil.buildTransaction("music");
        req.message = msg;
        req.scene = shareType;
        mIWXAPI.sendReq(req);
    }

    @Override
    public void share(ShareContent content, int shareType) {
        switch (content.getShareWay()) {
            case ShareConstants.SHARE_WAY_TEXT:
                shareText(shareType, content);
                break;
            case ShareConstants.SHARE_WAY_PIC:
                sharePicture(shareType, content);
                break;
            case ShareConstants.SHARE_WAY_WEBPAGE:
                shareWebPage(shareType, content);
                break;
            case ShareConstants.SHARE_WAY_MUSIC:
                shareMusic(shareType, content);
                break;
        }
    }

    public byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
