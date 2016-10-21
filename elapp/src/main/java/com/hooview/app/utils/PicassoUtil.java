package com.hooview.app.utils;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.text.TextUtils;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

public class PicassoUtil {

    public static RequestCreator load(Context context, String url) {
        if (TextUtils.isEmpty(url))
            url = null;
        return Picasso.with(context)
                .load(url)
                .tag(context);
    }

    public static RequestCreator load(Context context, int resId) {
        return Picasso.with(context)
                .load(resId)
                .tag(context);
    }

    public static RequestCreator load(Context context, Uri uri) {
        return Picasso.with(context)
                .load(uri)
                .tag(context);
    }

    public static RequestCreator loadHandleError(Context context, String url, @DrawableRes int resId) {
        if (TextUtils.isEmpty(url))
            url = null;
        return Picasso.with(context)
                .load(url)
                .error(resId)
                .tag(context);
    }


    public static RequestCreator loadPlaceholder(Context context, String url, @DrawableRes int resId) {
        if (TextUtils.isEmpty(url))
            url = null;
        return Picasso.with(context)
                .load(url)
                .error(resId)
                .placeholder(resId)
                .tag(context);
    }

    public static RequestCreator loadPlaceholder(Context context, @DrawableRes int targetResId, @DrawableRes int resId) {
        return Picasso.with(context)
                .load(targetResId)
                .error(resId)
                .placeholder(resId)
                .tag(context);
    }

    public static RequestCreator loadHandleError(Context context, int resId, @DrawableRes int errorResId) {
        return Picasso.with(context)
                .load(resId)
                .error(errorResId)
                .tag(context);
    }
}
