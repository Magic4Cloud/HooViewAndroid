package com.easyvaas.common.gift.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.ImageView;

import com.easyvaas.common.gift.R;
import com.easyvaas.common.gift.bean.GoodsEntity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class GiftUtility {
    private static final String TAG = "GiftUtility";
    public static String CACHE_ANIM_DIR;

    private static void unZipFolder(String zipFileString, String outPathString) throws Exception {
        ZipInputStream inZip = new ZipInputStream(new FileInputStream(zipFileString));
        ZipEntry zipEntry;
        String szName = "";
        while ((zipEntry = inZip.getNextEntry()) != null) {
            szName = zipEntry.getName();
            if (zipEntry.isDirectory()) {
                // get the folder name of the widget
                szName = szName.substring(0, szName.length() - 1);
                File folder = new File(outPathString + File.separator + szName);
                folder.mkdirs();
            } else {

                File file = new File(outPathString + File.separator + szName);
                file.createNewFile();
                // get the output stream of the file
                FileOutputStream out = new FileOutputStream(file);
                int len;
                byte[] buffer = new byte[1024];
                // read (len) bytes into buffer
                while ((len = inZip.read(buffer)) != -1) {
                    // write (len) byte from buffer at the position 0
                    out.write(buffer, 0, len);
                    out.flush();
                }
                out.close();
            }
        }
        inZip.close();
    }

    private static void loadGoodsImage(Context context, String url) {
        if (context == null) return;
        Picasso.with(context).load(url).fetch();
    }

    public static String downloadServerLikeImage(Context context, String imageUrl) {
        String filePath = FileUtil.getLikeIconDir(context) + "/server_like_"
                + imageUrl.substring(imageUrl.lastIndexOf("/") + 1) + "_";
        if (new File(filePath).exists()) {
            return filePath;
        } else {
            return getLocalImagePath(imageUrl, Bitmap.CompressFormat.PNG, filePath);
        }
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
        BufferedOutputStream bos;
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

    public static float dp2Px(Context context, int dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return dpValue * scale + 0.5f;
    }

    public static float getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    public static float getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    public static String getGiftIdDir(int giftId) {
        return CACHE_ANIM_DIR + File.separator + giftId;
    }

    public static void loadGift(final Context context, List<GoodsEntity> goodsEntityList) {
        for (int i = 0, n = goodsEntityList.size(); i < n; i++) {
            final String imageAniUrl = goodsEntityList.get(i).getAni();
            final String imagePicUrl = goodsEntityList.get(i).getPic();
            if (goodsEntityList.get(i).getType() == GoodsEntity.TYPE_EMOJI) {
                new Thread() {
                    @Override
                    public void run() {
                        downloadServerLikeImage(context, imageAniUrl);
                    }
                }.start();
            } else {
                if (goodsEntityList.get(i).getAnitype() == GoodsEntity.ANI_TYPE_ZIP) {
                    loadGoodsZip(context, goodsEntityList.get(i).getId(), imageAniUrl);
                } else {
                    loadGoodsImage(context, imageAniUrl);
                }
                loadGoodsImage(context, imagePicUrl);
            }
            String json = new Gson().toJson(goodsEntityList);
            GiftDB.getInstance(context).putString(GiftDB.KEY_PARAM_GOODS_JSON, json);
        }
    }

    private static void loadGoodsZip(Context context, long giftId, final String imageAniUrl) {
        CACHE_ANIM_DIR = FileUtil.getCacheAnimDir(context);
        final String downloadPath = CACHE_ANIM_DIR + File.separator + "temp";
        final String unzipDir = getGiftIdDir((int) giftId);
        String fileName = imageAniUrl;
        try {
            fileName = getMD5(imageAniUrl);
        } catch (NoSuchAlgorithmException e) {
            GiftLogger.w(TAG, "get md5 failed", e);
        }

        final String finalFileName = fileName;
        new Thread() {
            public void run() {
                downloadFromUrl(imageAniUrl, finalFileName, downloadPath, new DownloadListener() {
                    @Override
                    public void onSuccess(final String filePath) {
                        new Thread() {
                            public void run() {
                                try {
                                    unZipFolder(filePath, unzipDir);
                                    new File(downloadPath).delete();
                                } catch (Exception e) {
                                    GiftLogger.e(TAG, "Unzip animation package failed ! file: "
                                            + filePath, e);
                                }
                            }
                        }.start();
                    }

                    @Override
                    public void onFailed(String msg) {
                        GiftLogger.w(TAG, "Download anim package failed ! : " + msg);
                    }
                });
            }
        }.start();
    }

    public static List<GoodsEntity> getAllCachedGoods(Context context) {
        GiftDB pref = GiftDB.getInstance(context);
        String json = pref.getString(GiftDB.KEY_PARAM_GOODS_JSON);
        List<GoodsEntity> list = new Gson().fromJson(json,
                new TypeToken<List<GoodsEntity>>() {
                }.getType());
        if (list == null) {
            GiftLogger.d("Utils", "getAllCachedGoods() list is null");
            list = new ArrayList<>();
        }
        return list;
    }

    public static void removeGiftCache(Context context) {
        GiftDB pref = GiftDB.getInstance(context);
        pref.remove(GiftDB.KEY_PARAM_GOODS_JSON);
    }

    public static void setGoodsCacheImage(String url, ImageView imageView) {
        showImage(url, R.drawable.account_bitmap_list, imageView);
    }

    private static void showImage(String url, int loadingResId, ImageView target) {
        showImage(target.getContext(), url, loadingResId, target);
    }

    private static void showImage(Context context, String url, int loadingResId, Object target) {
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

    public static void showUserPhoto(Context context, String url, ImageView userPhoto) {
        if (TextUtils.isEmpty(url)) {
            userPhoto.setImageResource(R.drawable.somebody);
        } else if (url.startsWith("http")) {
            showImage(url, R.drawable.somebody, userPhoto);
        } else if (url.startsWith("/")) {
            File file = new File(url);
            if (!file.exists()) {
                userPhoto.setImageResource(R.drawable.somebody);
            } else {
                Picasso.with(context).load(file).fit().centerCrop()
                        .error(R.drawable.somebody).placeholder(R.drawable.somebody)
                        .into(userPhoto);
            }
        }
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
        for (byte b : md) {
            str[k++] = hexDigits[b >> 4 & 0xf];
            str[k++] = hexDigits[b & 0xf];
        }

        return new String(str);
    }

    private static void downloadFromUrl(String urlStr, String fileName, String savePath,
            DownloadListener listener) {
        URL url = null;
        try {
            url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(3 * 1000);
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

            InputStream inputStream = conn.getInputStream();
            byte[] getData = readInputStream(inputStream);

            File saveDir = new File(savePath);
            if (!saveDir.exists()) {
                saveDir.mkdir();
            }
            File file = new File(saveDir + File.separator + fileName);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(getData);
            fos.close();
            if (inputStream != null) {
                inputStream.close();
            }
            listener.onSuccess(savePath + File.separator + fileName);
        } catch (IOException e) {
            listener.onFailed(e.getMessage());
        }
    }

    private static byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }

    public static String getAnimName(String jsonFile) {
        String jsonStr = FileUtil.readFileToString(jsonFile);
        try {
            JSONObject object = new JSONObject(jsonStr);
            return object.getString("NameType");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    interface DownloadListener {
        void onSuccess(String filePath);

        void onFailed(String msg);
    }
}

