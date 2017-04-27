/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.utils;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.UUID;

public class FileUtil {
    private static final String TAG = FileUtil.class.getSimpleName();
    
    public static final String LOGO_FILE_NAME = "hooview_logo.png";
    public static final String WATERMARK_FILE_NAME = "watermark.png";
    
    public static final String CACHE_DIR_OLD = Environment
            .getExternalStorageDirectory().getAbsolutePath() + File.separator + "elapp";

    public static String CACHE_DIR;
    public static String CACHE_DOWNLOAD_DIR;
    public static String CACHE_SPLASH_DIR;
    public static String CACHE_SHARE_DIR;
    public static String CACHE_IMAGE_DIR;

    public static void checkCacheDir(Context ctx) {
        CACHE_DIR = ctx.getExternalFilesDir(null).getAbsolutePath();
        CACHE_DOWNLOAD_DIR = ctx.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        CACHE_SPLASH_DIR = ctx.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath();
        CACHE_SHARE_DIR = ctx.getExternalFilesDir("Share").getAbsolutePath();
        CACHE_IMAGE_DIR = ctx.getExternalFilesDir("Image").getAbsolutePath();

        File noMediaFile = new File(CACHE_IMAGE_DIR + File.separator + ".nomedia");
        if (!noMediaFile.exists()) {
            try {
                noMediaFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        deleteDir(new File(CACHE_DIR_OLD));
    }

    public static boolean createDirectory(String filePath) {
        if (null == filePath) {
            return false;
        }

        if (CACHE_IMAGE_DIR.equals(filePath)) {
            File noMediaFile = new File(CACHE_IMAGE_DIR + File.separator +".nomedia");
            if (!noMediaFile.exists()) {
                try {
                    noMediaFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        File file = new File(filePath);

        return file.exists() || file.mkdirs();
    }

    public static void deleteDir(File file) {
        if (file.exists()) {
            File[] fileList = file.listFiles();
            for (File f : fileList) {
                if (f.isDirectory()) {
                    deleteDir(f);
                } else {
                    f.delete();
                }
            }
            file.delete();
        }
    }

    public static void checkDirExist(String path) {
        File file = new File(path);
        if(!file.exists()){
            file.mkdir();
        }
    }

    public static boolean copyAssetData(Context context, String assetsFileName, String targetPath) {
        try {
            InputStream inputStream = context.getAssets().open(assetsFileName);
            FileOutputStream output = new FileOutputStream(targetPath + File.separator + assetsFileName);
            byte[] buf = new byte[10240];
            int count = 0;
            while ((count = inputStream.read(buf)) > 0) {
                output.write(buf, 0, count);
            }
            output.close();
            inputStream.close();
        } catch (IOException e) {

            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static void copyAssetsFiles(Context context, String oldPath, String newPath) {
        try {
            String fileNames[] = context.getAssets().list(oldPath);
            if (fileNames.length > 0) {
                File file = new File(newPath);
                file.mkdirs();
                for (String fileName : fileNames) {
                    copyAssetsFiles(context, oldPath + "/" + fileName, newPath + "/" + fileName);
                }
            } else {
                InputStream is = context.getAssets().open(oldPath);
                FileOutputStream fos = new FileOutputStream(new File(newPath));
                byte[] buffer = new byte[1024];
                int byteCount = 0;
                while ((byteCount = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, byteCount);
                }
                fos.flush();
                is.close();
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getFileName(String filePath) {
        if (!TextUtils.isEmpty(filePath)) {
            return filePath.substring(filePath.lastIndexOf("/") + 1);
        }
        return filePath;
    }

    public static boolean isFileIsExist(String filePath) {
        if (filePath == null || filePath.length() < 1) {
            Logger.d(TAG, "param invalid, filePath: " + filePath);
            return false;
        }

        File f = new File(filePath);

        return f.exists();
    }

    public static String getFileNameBody(String filePath) {
        if (!TextUtils.isEmpty(filePath)) {
            String fullFileName = getFileName(filePath);
            int dotPosition = fullFileName.lastIndexOf("");
            if (dotPosition > 0) {
                return fullFileName.substring(0, dotPosition);
            } else {
                return fullFileName;
            }
        }

        return "";
    }

    public static InputStream readFile(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            Logger.d(TAG, "Invalid param. filePath is empty.");
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
            Logger.w(TAG, "Exception, ex: ", ex);
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
            Logger.w(TAG, "inputStream2String() exception ", e);
        }
        return buffer.toString();
    }

    public static String getRandomImageFileName() {
        return UUID.randomUUID().toString() + "_" + System.currentTimeMillis() + ".jpg";
    }

    public static String getSimpleFolderSize(String path) {
        double size = getSize(new File(path));
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        String sizeStr = "0 KB";
        if (size > 1024 * 1024) {
            sizeStr = decimalFormat.format(size / 1024 / 1024) + " MB";
        } else if (size > 1024) {
            sizeStr = decimalFormat.format(size / 1024) + " KB";
        }
        return sizeStr;
    }

    private static double getSize(File file) {
        if (file.exists()) {
            if (!file.isFile()) {
                File[] fl = file.listFiles();
                double ss = 0;
                for (File f : fl)
                    ss += getSize(f);
                return ss;
            } else {
                return file.length();
            }
        } else {
            return 0.0;
        }
    }
}
