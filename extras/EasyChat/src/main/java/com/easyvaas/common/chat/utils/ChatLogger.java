package com.easyvaas.common.chat.utils;

import android.util.Log;

import com.easyvaas.common.chat.BuildConfig;

public class ChatLogger {

    private static final String APPNAME = "ev-chat-";
    private static boolean LOGV_ON = BuildConfig.DEBUG;
    private static boolean LOGD_ON = BuildConfig.DEBUG;
    private static boolean LOGW_ON = true;
    private static boolean LOGE_ON = true;

    public static void d(String tag, String msg) {
        if (LOGD_ON) {
            tag = APPNAME + tag;
            Log.d(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (LOGW_ON) {
            tag = APPNAME + tag;
            Log.w(tag, msg);
        }
    }

    public static void w(String tag, String msg, Throwable tr) {
        if (LOGW_ON) {
            tag = APPNAME + tag;
            Log.w(tag, msg, tr);
        }
    }

    public static void e(String tag, String msg, Throwable tr) {
        if (LOGE_ON) {
            tag = APPNAME + tag;
            Log.e(tag, msg, tr);
        }
    }

    public static void e(String tag, String msg) {
        if (LOGE_ON) {
            tag = APPNAME + tag;
            Log.e(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (LOGV_ON) {
            tag = APPNAME + tag;
            Log.i(tag, msg);
        }
    }

    public static void v(String tag, String msg) {
        if (LOGV_ON) {
            tag = APPNAME + tag;
            Log.v(tag, msg);
        }
    }

    public static void v(Class<?> c, String msg) {
        if (LOGV_ON) {
            String tag = APPNAME + c.getSimpleName();
            Log.v(tag, msg);
        }
    }

    public static void d(Class<?> c, String msg) {
        if (LOGD_ON) {
            String tag = APPNAME + c.getSimpleName();
            Log.d(tag, msg);
        }
    }

    public static void i(Class<?> c, String msg) {
        if (LOGD_ON) {
            String tag = APPNAME + c.getSimpleName();
            Log.d(tag, msg);
        }
    }

    public static void w(Class<?> c, String msg) {
        if (LOGW_ON) {
            String tag = APPNAME + c.getSimpleName();
            Log.w(tag, msg);
        }
    }

    public static void w(Class<?> c, String msg, Throwable tr) {
        if (LOGW_ON) {
            String tag = APPNAME + c.getSimpleName();
            Log.w(tag, msg, tr);
        }
    }

    public static void e(Class<?> c, String msg) {
        if (LOGE_ON) {
            String tag = APPNAME + c.getSimpleName();
            Log.e(tag, msg);
        }
    }

    public static void e(Class<?> c, String msg, Throwable tr) {
        if (LOGE_ON) {
            String tag = APPNAME + c.getSimpleName();
            Log.e(tag, msg, tr);
        }
    }
}
