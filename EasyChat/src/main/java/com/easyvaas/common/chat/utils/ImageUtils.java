package com.easyvaas.common.chat.utils;

import com.easemob.util.PathUtil;

public class ImageUtils {
    private static final String TAG = ImageUtils.class.getSimpleName();

    public static String getThumbnailImagePath(String thumbRemoteUrl) {
        String thumbImageName = thumbRemoteUrl
                .substring(thumbRemoteUrl.lastIndexOf("/") + 1, thumbRemoteUrl.length());
        String path = PathUtil.getInstance().getImagePath() + "/" + "th" + thumbImageName;
        ChatLogger.d(TAG, "thum image path:" + path);
        return path;
    }

}
