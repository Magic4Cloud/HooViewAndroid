/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.DrawableRes;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.easyvaas.common.bottomsheet.BottomSheet;
import com.hooview.app.app.EVApplication;
import com.hooview.app.bean.video.VideoEntity;
import com.hooview.app.db.Preferences;
import com.hooview.app.live.activity.PlayerActivity;
import com.hooview.app.live.activity.SetPasswordActivity;
import com.hooview.app.net.ApiConstant;
import com.hooview.app.net.RequestHelper;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utils {
    private static final String TAG = Utils.class.getSimpleName();
    private static final String IMAGE_TYPE = "image/*";

    public static String getDeviceId(Context context) {
        final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm == null) {
            return "";
        }
        return "" + tm.getDeviceId();
    }

    public static String getMD5(String val) throws NoSuchAlgorithmException {
        char hexDigits[] = {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f'
        };
        byte[] strTemp = val.getBytes();
        MessageDigest mdTemp = MessageDigest.getInstance("MD5");
        mdTemp.update(strTemp);
        byte[] md = mdTemp.digest();
        int j = md.length;
        char str[] = new char[j * 2];
        int k = 0;
        for (int i = 0; i < j; i++) {
            byte b = md[i];
            str[k++] = hexDigits[b >> 4 & 0xf];
            str[k++] = hexDigits[b & 0xf];
        }

        return new String(str);
    }

    public static String getMD5(File file) {
        MessageDigest mMessageDigest = null;
        try {
            mMessageDigest = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mMessageDigest != null) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
                byte[] buffer = new byte[8192];
                int length;
                while ((length = fis.read(buffer)) != -1) {
                    mMessageDigest.update(buffer, 0, length);
                }
                return bytesToHexString(mMessageDigest.digest());
            } catch (FileNotFoundException e) {
                Logger.e(TAG, "getFileMD5.Exception : " + e.getMessage());
            } catch (IOException e) {
                Logger.e(TAG, "getFileMD5.Exception : " + e.getMessage());
            } catch (OutOfMemoryError e) {
                Logger.e(TAG, "getFileMD5.Exception : " + e.getMessage());
            } catch (Exception e) {
                Logger.e(TAG, "getFileMD5.Exception : " + e.getMessage());
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        Logger.e(TAG, "getFileMD5.Exception : " + e.getMessage());
                    }
                }
            }
        }
        return null;
    }

    public static String bytesToHexString(byte[] bytes) {
        if (bytes != null) {
            StringBuffer stringBuffer = new StringBuffer();
            for (int i = 0; i < bytes.length; i++) {
                int v = bytes[i] & 0xFF;
                String hv = Integer.toHexString(v);
                if (hv.length() < 2) {
                    stringBuffer.append(0);
                }
                stringBuffer.append(hv);
            }
            return stringBuffer.toString();
        }
        return null;
    }

    public static boolean checkMd5(File loadFile, String strMd5) {
        if (loadFile != null && !TextUtils.isEmpty(strMd5)) {
            return strMd5.equals(getMD5(loadFile));
        }
        return false;
    }

    public static boolean isFullScreen(Activity activity) {
        int flag = activity.getWindow().getAttributes().flags;
        return (flag & WindowManager.LayoutParams.FLAG_FULLSCREEN)
                == WindowManager.LayoutParams.FLAG_FULLSCREEN;
    }

    public static void setTextLeftDrawable(Context context, TextView view, int drawableId) {
        Drawable drawable = context.getResources().getDrawable(drawableId);
        if (drawable != null) {
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        }
        view.setCompoundDrawables(drawable, null, null, null);
    }

    public static void setTextTopDrawable(Context context, TextView view, int drawableId) {
        Drawable drawable = context.getResources().getDrawable(drawableId);
        if (drawable != null) {
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        }
        view.setCompoundDrawables(null, drawable, null, null);
    }

    public static void showImage(Context context, String url, @DrawableRes int loadingResId, Target target) {
        showImage(context, url, loadingResId, (Object) target);
    }

    public static void showImage(@DrawableRes int drawableId, ImageView target) {
        Picasso.with(target.getContext()).load(drawableId).into(target);
    }

    public static void showImage(String url, @DrawableRes int loadingResId, ImageView target) {
        showImage(target.getContext(), url, loadingResId, target);
    }

    private static void showImage(Context context, String url, @DrawableRes int loadingResId, Object target) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        RequestCreator requestCreator = null;
        if (url.startsWith("/")) {
            File file = new File(url);
            if (file.exists()) {
                requestCreator = Picasso.with(context).load(file);
            } else if (target instanceof ImageView && loadingResId > 0) {
                ((ImageView) target).setImageResource(loadingResId);
            }
        } else {
            requestCreator = Picasso.with(context).load(url);
        }
        if (requestCreator != null) {
            if (loadingResId > 0) {
                requestCreator.error(loadingResId).placeholder(loadingResId);
            }
            if (target instanceof Target) {
                requestCreator.into((Target) target);
            } else {
                requestCreator.fit().centerCrop();
                requestCreator.into((ImageView) target);
            }
        }
    }

    public static void cachedImageAsync(Context context, String url, String saveDir) {
        String fileName = url;
        try {
            fileName = Utils.getMD5(url);
        } catch (NoSuchAlgorithmException e) {
            Logger.w(TAG, "MD5 url failed", e);
        }
        if (!new File(saveDir + File.separator + fileName).exists()) {
            RequestHelper.getInstance(context).downloadFile(url, saveDir, null);
        }
    }

    public static BottomSheet getSetThumbBottomPanel(final Activity activity,
            final String imageFileName, final int requestCodeCamera, final int requestCodeImage) {
        return new BottomSheet.Builder(activity).sheet(com.hooview.app.R.menu.select_thumb)
                .listener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case com.hooview.app.R.id.menu_select_thumb_by_camera:
                                Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                if (Environment.getExternalStorageState()
                                        .equals(Environment.MEDIA_MOUNTED)) {
                                    intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(
                                            new File(Environment.getExternalStorageDirectory(),
                                                    imageFileName)));
                                }
                                activity.startActivityForResult(intentFromCapture, requestCodeCamera);
                                break;
                            case com.hooview.app.R.id.menu_select_thumb_by_gallery:
                                if (openPhotosNormal(activity, requestCodeImage)
                                        || openPhotosBrowser(activity, requestCodeImage)) {
                                    // Have open select photo app, do nothing
                                } else {
                                    SingleToast.show(activity, com.hooview.app.R.string.msg_no_photos_browser_tip);
                                }
                                break;
                            case com.hooview.app.R.id.menu_cancel:
                                dialog.dismiss();
                                break;
                        }
                    }
                }).build();
    }

    private static boolean openPhotosNormal(Activity activity, int actResultCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_TYPE);
        try {
            activity.startActivityForResult(intent, actResultCode);
            return true;
        } catch (ActivityNotFoundException ex) {
            Logger.w(TAG, "openPhotosNormal failed!", ex);
        }
        return false;
    }

    private static boolean openPhotosNormal(Fragment activity, int actResultCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_TYPE);
        try {
            activity.startActivityForResult(intent, actResultCode);
            return true;
        } catch (ActivityNotFoundException ex) {
            Logger.w(TAG, "openPhotosNormal failed!", ex);
        }
        return false;
    }

    private static boolean openPhotosBrowser(Activity activity, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(IMAGE_TYPE);
        Intent wrapperIntent = Intent.createChooser(intent, null);
        try {
            activity.startActivityForResult(wrapperIntent, requestCode);
            return true;
        } catch (ActivityNotFoundException ex) {
            Logger.e(TAG, "openPhotosBrowser failed!", ex);
        }
        return false;
    }

    private static boolean openPhotosBrowser(Fragment activity, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(IMAGE_TYPE);
        Intent wrapperIntent = Intent.createChooser(intent, null);
        try {
            activity.startActivityForResult(wrapperIntent, requestCode);
            return true;
        } catch (ActivityNotFoundException ex) {
            Logger.e(TAG, "openPhotosBrowser failed!", ex);
        }
        return false;
    }

    public static File startPhotoZoom(Activity activity, Uri uri, int width, int height, int requestCode) {
        if (uri != null) {
            File sdcardTempPic = new File(FileUtil.CACHE_IMAGE_DIR,
                    "/tmp_pic_" + System.currentTimeMillis() + ".jpg");
            Intent intent = new Intent("com.android.camera.action.CROP");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                String url = getRealFilePath(activity, uri);
                intent.setDataAndType(Uri.fromFile(new File(url)), "image/*");
            } else {
                intent.setDataAndType(uri, "image/*");
            }
            intent.putExtra("crop", "true");
            intent.putExtra("aspectX", width);
            intent.putExtra("aspectY", height);
            intent.putExtra("outputX", width);
            intent.putExtra("outputY", height);
            intent.putExtra("scale", true);
            intent.putExtra("scaleUpIfNeeded", true);
            //intent.putExtra("return-data", true);
            intent.putExtra("return-data", false);
            intent.putExtra("output", Uri.fromFile(sdcardTempPic));
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            activity.startActivityForResult(intent, requestCode);

            return sdcardTempPic;
        } else {
            Logger.w(TAG, "The uri is not exist.");
        }
        return null;
    }

    public static String getRealFilePath(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        if (null == uri) {
            return "";
        }
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null) {
            data = uri.getPath();
        }
        if (isKitKat) {
            return getPicturePathKitKat(context, uri);
        } else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver()
                    .query(uri, new String[] { MediaStore.Images.ImageColumns.DATA }, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static String getPicturePathKitKat(final Context context, final Uri uri) {
        // DocumentProvider
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return "";
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    private static String getDataColumn(Context context, Uri uri, String selection,
            String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    private static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static String downloadWatermarkImage(String imageUrl) {
        FileUtil.checkDirExist(FileUtil.CACHE_IMAGE_DIR);
        String filePath = FileUtil.CACHE_IMAGE_DIR + "/"
                + imageUrl.substring(imageUrl.lastIndexOf("/") + 1) + "_";

        return getLocalImagePath(imageUrl, Bitmap.CompressFormat.PNG, filePath);
    }

    public static String getCacheServerLikeImage(String imageUrl) {
        if (imageUrl == null) {
            return null;
        }
        String filePath = FileUtil.CACHE_IMAGE_DIR + "/server_like_"
                + imageUrl.substring(imageUrl.lastIndexOf("/") + 1) + "_";
        if (new File(filePath).exists()) {
            return new File(filePath).getPath();
        }
        return null;
    }

    public static String getLocalImagePath(String imageUrl) {
        String path = FileUtil.CACHE_IMAGE_DIR + "/tmp_pic_" + System.currentTimeMillis() + "jpg_";
        return getLocalImagePath(imageUrl, Bitmap.CompressFormat.JPEG, path);
    }

    public static String getLocalImagePath(String imageUrl, Bitmap.CompressFormat formatType,
            String saveFileName) {
        Bitmap bitmap;
        if (imageUrl.startsWith("http")) {
            try {
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(input);
            } catch (IOException e) {
                return null;
            }
        } else {
            bitmap = BitmapFactory.decodeFile(imageUrl);
        }
        File imageFile = new File(saveFileName);
        BufferedOutputStream bos = null;
        if (bitmap == null) {
            return "";
        }
        try {
            bos = new BufferedOutputStream(new FileOutputStream(imageFile));
            bitmap.compress(formatType, 80, bos);
            bos.flush();
            bos.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return imageFile.getAbsolutePath();
    }

    public static boolean saveBitmap(Bitmap bitmap, String localPath) {
        if (bitmap == null) {
            return false;
        }
        File pictureFile = new File(localPath);
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
            return true;
        } catch (FileNotFoundException e) {
            Logger.e(TAG, "File not found! ", e);
        } catch (IOException e) {
            Logger.e(TAG, "File accessing error !", e);
        }
        return false;
    }

    public static void updateCountryCode(Context context, TextView tvCountryName, TextView tvCountryCode) {
        String countryName = Preferences.getInstance(context).getString(Preferences.KEY_COUNTRY_NAME,
                context.getResources().getString(com.hooview.app.R.string.default_country_name));
        String countryCode = Preferences.getInstance(context).getString(Preferences.KEY_COUNTRY_CODE,
                context.getResources().getString(com.hooview.app.R.string.default_country_code_number));
        tvCountryName.setText(countryName);
        tvCountryCode.setText(countryCode);
        String code = countryCode.replace("+", "");
        ApiConstant.VALUE_COUNTRY_CODE = code + "_";
    }

    public static boolean isGpsEnable(Context context) {
        boolean isGpsEnable = false;
        LocationManager mLocationManager = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        try {
            isGpsEnable = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                    || mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception exception) {
            Logger.e(TAG, "gps permission Denial! ", exception);
        }

        return isGpsEnable;
    }

    private static final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Preferences.getInstance(EVApplication.getApp()).putString(Preferences.KEY_CACHE_LOCATION,
                    location.getLatitude() + "," + location.getLongitude());
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    public static Location getLocation(Context context) {
        final int MIN_TIME_BW_UPDATES = 60000;
        final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 1.0f;
        Location location = null;
        LocationManager locationManager = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        try {
            // getting GPS status
            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            // getting network status
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES,
                            mLocationListener, context.getMainLooper());
                    Logger.d(TAG, "Network Enabled");
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled && location == null) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES,
                            mLocationListener, context.getMainLooper());
                    Logger.d(TAG, "GPS Enabled");
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }
            }
        } catch (Exception e) {
            Logger.e(TAG, "Get location failed! ", e);
        }

        return location;
    }

    public static void removeLocationListener(Context context) {
        LocationManager mLocationManager = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        mLocationManager.removeUpdates(mLocationListener);
    }

    public static void watchVideo(Context context, String vid) {
        watchVideo(context, vid, "");
    }

    public static void watchVideo(Context context, String vid, String password) {
        if (TextUtils.isEmpty(vid)) {
            return;
        }
        VideoEntity videoEntity = new VideoEntity();
        videoEntity.setVid(vid);
        if (!TextUtils.isEmpty(password)) {
            videoEntity.setPassword(password);
        }
        watchVideo(context, videoEntity);
    }

    public static void watchVideo(Context context, VideoEntity video) {
        Intent intent = new Intent();
        intent.putExtra(Constants.EXTRA_KEY_VIDEO_ID, video.getVid());
        intent.putExtra(SetPasswordActivity.EXTRA_KEY_PASSWORD, video.getPassword());
        intent.putExtra(Constants.EXTRA_KEY_VIDEO_IS_LIVE, video.getLiving() == VideoEntity.IS_LIVING);
        intent.putExtra(Constants.EXTRA_KEY_USER_IM_ID, video.getName());

        if (video.getPermission() == ApiConstant.VALUE_LIVE_PERMISSION_PASSWORD
                && !Preferences.getInstance(context).getUserNumber().equals(video.getName())) {
            intent.setClass(context, SetPasswordActivity.class);
        } else {
            intent.setClass(context, PlayerActivity.class);
            context.sendBroadcast(new Intent(Constants.ACTION_CLOSE_CURRENT_VIDEO_PLAYER));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }


}
