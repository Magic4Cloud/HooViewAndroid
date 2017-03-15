package com.easyvaas.common.gift.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class GiftDB {
    private static final String PREF_NAME = "gift.local.dbfile";

    public static final String KEY_PARAM_GOODS_JSON = "key_param_goods_json";
    public static final String KEY_PARAM_ASSET_E_COIN_ACCOUNT = "key_param_asset_e_coin_account";

    private static GiftDB mGiftDB;

    private SharedPreferences mSettings;

    private GiftDB(Context context) {
        mSettings = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static GiftDB getInstance(Context context) {
        if (mGiftDB == null) {
            mGiftDB = new GiftDB(context.getApplicationContext());
        }
        return mGiftDB;
    }

    public int getInt(String name, int def) {
        return mSettings.getInt(name, def);
    }

    public void putInt(String name, int value) {
        SharedPreferences.Editor edit = mSettings.edit();
        edit.putInt(name, value);
        edit.apply();
    }

    public String getString(String key) {
        return mSettings.getString(key, "");
    }

    public String getString(String key, String defaultValue) {
        return mSettings.getString(key, defaultValue);
    }

    public void putString(String key, String value) {
        SharedPreferences.Editor edit = mSettings.edit();
        edit.putString(key, value);
        edit.apply();
    }

    public long getLong(String key, long defaultValue) {
        return mSettings.getLong(key, defaultValue);
    }

    public void putLong(String key, long value) {
        SharedPreferences.Editor edit = mSettings.edit();
        edit.putLong(key, value);
        edit.apply();
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return mSettings.getBoolean(key, defaultValue);
    }

    public void putBoolean(String key, boolean bool) {
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putBoolean(key, bool);
        editor.apply();
    }

    public void remove(String key) {
        SharedPreferences.Editor editor = mSettings.edit();
        editor.remove(key);
        editor.apply();
    }

    public void clear() {
        SharedPreferences.Editor editor = mSettings.edit();
        editor.clear();
        editor.apply();
    }
}

