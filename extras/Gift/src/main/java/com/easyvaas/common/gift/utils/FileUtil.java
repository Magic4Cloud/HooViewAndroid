package com.easyvaas.common.gift.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

class FileUtil {
    private static final String TAG = "FileUtil";

    private static String getCacheDir(Context context, String type) {
        File file = new File(context.getExternalFilesDir("Gift") + File.separator + type);
        if (!file.exists()) {
            file.mkdirs();
            File noMediaFile = new File(file + ".nomedia");
            if (!noMediaFile.exists()) {
                try {
                    noMediaFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return file.getAbsolutePath();
    }

    public static String getLikeIconDir(Context context) {
        return getCacheDir(context, "Like");
    }

    public static String getCacheAnimDir(Context context) {
        return getCacheDir(context, "Anim");
    }

    private static boolean isFileIsExist(String filePath) {
        if (filePath == null || filePath.length() < 1) {
            GiftLogger.d(TAG, "param invalid, filePath: " + filePath);
            return false;
        }

        return new File(filePath).exists();
    }

    private static InputStream readFile(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            GiftLogger.d(TAG, "Invalid param. filePath is empty.");
            return null;
        }

        InputStream is = null;

        try {
            if (isFileIsExist(filePath)) {
                File f = new File(filePath);
                is = new FileInputStream(f);
            } else {
                return null;
            }
        } catch (Exception ex) {
            GiftLogger.w(TAG, "Exception, ex: ", ex);
            return null;
        }
        return is;
    }

    public static String readFileToString(String filePath) {
        return inputStream2String(readFile(filePath));
    }

    private static  String inputStream2String(InputStream is) {
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        StringBuilder buffer = new StringBuilder();
        String line = "";
        try {
            while ((line = in.readLine()) != null){
                buffer.append(line);
            }
        } catch (IOException e) {
            GiftLogger.w(TAG, "inputStream2String() exception ", e);
        }
        return buffer.toString();
    }
}
