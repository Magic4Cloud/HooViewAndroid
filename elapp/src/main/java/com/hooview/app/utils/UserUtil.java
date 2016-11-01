/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.easyvaas.common.chat.ChatManager;
import com.google.gson.Gson;
import com.hooview.app.activity.user.FriendsUserInfoActivity;
import com.hooview.app.app.EVApplication;
import com.hooview.app.bean.user.User;
import com.hooview.app.db.Preferences;
import com.hooview.app.net.ApiUtil;
import com.squareup.picasso.Picasso;

import java.io.File;

public class UserUtil {
    public static final String BIRTHDAY_EMPTY = "0000-00-00";
    public static final String DEFAULT_BIRTHDAY = "1990-06-15";

    public static void setGender(TextView textView, String gender) {
        setGender(textView, gender, "");
    }

    public static void setGender(TextView textView, String gender, String birthday) {
        int age = TextUtils.isEmpty(birthday) ? 0 : DateTimeUtil.getAge(birthday);
        Drawable drawable = null;
        if (User.GENDER_MALE.equals(gender)) {
            drawable = textView.getContext().getResources().getDrawable(com.hooview.app.R.drawable.personal_icon_boy);
            textView.setBackgroundResource(com.hooview.app.R.drawable.shape_boy_bg);
            textView.setTextColor(textView.getContext().getResources().getColor(com.hooview.app.R.color.btn_color_main));
        } else {
            drawable = textView.getContext().getResources()
                    .getDrawable(com.hooview.app.R.drawable.personal_icon_girl);
            textView.setBackgroundResource(com.hooview.app.R.drawable.shape_girl_bg);
            textView.setTextColor(textView.getContext().getResources().getColor(com.hooview.app.R.color.btn_color_three_level));
        }
        if (drawable == null) {
            textView.setVisibility(View.GONE);
        } else {
            if (age > 0) {
                textView.setText(age + "");
            }
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            textView.setCompoundDrawables(drawable, null, null, null);
        }

    }

    public static void setConstellation(TextView textView, String birthday) {
        String constellation = TextUtils.isEmpty(birthday) ?
                "" :
                DateTimeUtil.getConstellation(textView.getContext(), birthday);
        if (TextUtils.isEmpty(constellation)) {
            textView.setVisibility(View.GONE);
        } else {
            textView.setText(constellation);
            textView.setVisibility(View.VISIBLE);
        }
    }

    public static void showUserInfo(Context context, String userId) {
        if (Preferences.getInstance(context).getUserNumber().equals(userId)) {
            context.sendBroadcast(new Intent(Constants.ACTION_GO_HOME_MINE));
        } else if (!TextUtils.isEmpty(userId)) {
            Intent intent = new Intent(context, FriendsUserInfoActivity.class);
            intent.putExtra(Constants.EXTRA_KEY_USER_ID, userId);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    public static void showUserInfoByImUser(Context context, String imUserId) {
        if (imUserId.equals(EVApplication.getUser().getImuser())) {
            context.sendBroadcast(new Intent(Constants.ACTION_GO_HOME_MINE));
        } else {
            Intent intent = new Intent(context, FriendsUserInfoActivity.class);
            intent.putExtra(Constants.EXTRA_KEY_USER_IM_ID, imUserId);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    public static void showUserPhoto(Context context, String url, ImageView userPhoto) {
        if (TextUtils.isEmpty(url)) {
            userPhoto.setImageResource(com.hooview.app.R.drawable.somebody);
        } else if (url.startsWith("http")) {
            Utils.showImage(url, com.hooview.app.R.drawable.somebody, userPhoto);
        } else if (url.startsWith("/")) {
            File file = new File(url);
            showUserPhoto(context, file, userPhoto);
        }
    }

    private static void showUserPhoto(Context context, File file, ImageView imageView) {
        if (file == null || !file.exists()) {
            imageView.setImageResource(com.hooview.app.R.drawable.somebody);
            return;
        }
        Picasso.with(context).load(file).fit().centerCrop()
                .error(com.hooview.app.R.drawable.somebody).placeholder(com.hooview.app.R.drawable.somebody)
                .into(imageView);
    }

    public static void handleAfterLoginBySession(Context context) {
        String userJson = Preferences.getInstance(context)
                .getString(Preferences.KEY_CACHED_USER_INFO_JSON);
        User user = null;
        if (!TextUtils.isEmpty(userJson)) {
            user = new Gson().fromJson(userJson, User.class);
        }
        handleAfterLogin(context, user, "LoginBySession");
    }

//    public static void handleAfterLoginBySession(Context context) {
//        handleAfterLogin(context, null, "LoginBySession");
//    }

    public static void handleAfterLogin(Context context, User user, String action) {
        if (user != null) {
            EVApplication.setUser(user);
            Preferences.getInstance(context).storageUserInfo(user);
            ChatManager.getInstance().userLogin(user.getImuser(), user.getImpwd());
        }

        ApiUtil.checkServerParam(context);
        ApiUtil.getAssetInfo(context);
    }
}
