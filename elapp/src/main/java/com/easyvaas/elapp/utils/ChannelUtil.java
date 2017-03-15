/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import android.content.Context;
import android.text.TextUtils;

import com.hooview.app.BuildConfig;
import com.easyvaas.elapp.db.Preferences;

public class ChannelUtil {
    private static final String CHANNEL_FILE_KEY_START = "channel_";
    private static final String CHANNEL_PLACEHOLDER = "rel";
    private static final String DEFAULT_CHANNEL_NAME = "site";

    public static void initChannelFromApk(Context context) {
        String channelName = "";
        String channelId = "";

        String sourceDir = context.getApplicationInfo().sourceDir;
        String filePathKey = "META-INF/" + CHANNEL_FILE_KEY_START;
        ZipFile zipfile = null;
        ZipEntry channelFileEntry = null;

        try {
            zipfile = new ZipFile(sourceDir);
            Enumeration entries = zipfile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = ((ZipEntry) entries.nextElement());
                String entryName = entry.getName();
                if (entryName.startsWith(filePathKey)) {
                    channelFileEntry = entry;
                    break;
                }
            }
            if (channelFileEntry == null) {
                return;
            }

            BufferedReader br = new BufferedReader(
                    new InputStreamReader(zipfile.getInputStream(channelFileEntry)));
            channelName = br.readLine();
            channelId = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (zipfile != null) {
                try {
                    zipfile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (!TextUtils.isEmpty(channelName) && !TextUtils.isEmpty(channelId)) {
            Preferences.getInstance(context).putString(Preferences.KEY_CHANNEL_NAME, channelName);
            Preferences.getInstance(context).putString(Preferences.KEY_CHANNEL_ID, channelId);
        }
    }

    public static boolean isGoogleChannel(Context context) {
        return "google".equalsIgnoreCase(getChannelName(context));
    }

    public static String getChannelName(Context context) {
        if (BuildConfig.FLAVOR.equalsIgnoreCase(CHANNEL_PLACEHOLDER)) {
            return Preferences.getInstance(context)
                    .getString(Preferences.KEY_CHANNEL_NAME, DEFAULT_CHANNEL_NAME);
        } else {
            return BuildConfig.FLAVOR;
        }
    }

    public static String getAppKey(Context context) {
        if (BuildConfig.DEBUG) {
            return Constants.UMENG_APP_KEY;
        }
        return Preferences.getInstance(context)
                .getString(Preferences.KEY_CHANNEL_ID, Constants.UMENG_APP_KEY);
    }
}
