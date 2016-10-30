package com.easyvaas.common.chat.activity;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.easemob.util.ImageUtils;

import com.easyvaas.common.chat.base.BaseActivity;
import com.easyvaas.common.chat.task.LoadLocalBigImgTask;
import com.easyvaas.common.chat.utils.ChatLogger;
import com.easyvaas.common.chat.utils.ImageCache;
import com.easyvaas.common.chat.view.photoview.PhotoView;
import com.easyvaas.common.chat.view.photoview.PhotoViewAttacher;
import com.easyvaas.common.chat.R;

public class ShowBigImageActivity extends BaseActivity {
    private static final String TAG = ShowBigImageActivity.class.getSimpleName();
    public static final String LOCAL_IMAGE_URL = "localImageUrl";
    public static final String REMOTE_IMAGE_URL = "remoteImageUrl";

    private ProgressDialog mProgressDialog;
    private PhotoView mPhotoView;
    private ProgressBar mLoadLocalPb;

    private int mDefaultDrawableRes;
    private String mLocalFilePath;
    private String mRemotePath;
    private boolean mIsDownloaded;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_big_image);

        mPhotoView = (PhotoView) findViewById(R.id.image);
        mPhotoView.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                finish();
            }
        });
        mLoadLocalPb = (ProgressBar) findViewById(R.id.pb_load_local);

        mDefaultDrawableRes = getIntent().getIntExtra("default_image", R.drawable.default_image);
        Uri uri = getIntent().getParcelableExtra(LOCAL_IMAGE_URL);
        String secret = getIntent().getStringExtra("secret");
        mRemotePath = getIntent().getStringExtra(REMOTE_IMAGE_URL);

        if (uri != null && new File(uri.getPath()).exists()) {
            ChatLogger.d(TAG, "show big image file exists. directly show it");
            Bitmap mBitmap = ImageCache.getInstance().get(uri.getPath());
            if (mBitmap == null) {
                LoadLocalBigImgTask task = new LoadLocalBigImgTask(this, uri.getPath(), mPhotoView,
                        mLoadLocalPb,
                        ImageUtils.SCALE_IMAGE_WIDTH,
                        ImageUtils.SCALE_IMAGE_HEIGHT);
                if (android.os.Build.VERSION.SDK_INT > 10) {
                    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    task.execute();
                }
            } else {
                showPhoto(mBitmap);
            }
        } else if (!TextUtils.isEmpty(mRemotePath)) {
            Bitmap bitmap = ImageCache.getInstance().get(mRemotePath);
            if (bitmap == null) {
                ChatLogger.d(TAG, "download remote mPhotoView");
                Map<String, String> maps = new HashMap<String, String>();
                if (!TextUtils.isEmpty(secret)) {
                    maps.put("share-secret", secret);
                }
                mLoadLocalPb.setVisibility(View.VISIBLE);
                MyAsyncTask asyncTask = new MyAsyncTask();
                asyncTask.execute();
            } else {
                showPhoto(bitmap);
            }
        } else {
            showDefaultPhoto();
        }
    }

    private void showDefaultPhoto() {
        showPhoto(null);
    }

    private void showPhoto(Bitmap bitmap) {
        if (bitmap == null) {
            mPhotoView.setImageResource(mDefaultDrawableRes);
            return;
        }
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        int screenWidth = outMetrics.widthPixels;
        int screenHeight = outMetrics.heightPixels;
        if (bitmap.getWidth() < screenWidth || bitmap.getHeight() < screenHeight) {
            mPhotoView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        } else {
            mPhotoView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        }
        mPhotoView.setImageBitmap(bitmap);
    }

    /*public String getLocalFilePath(String remoteUrl) {
        String localPath;
        if (remoteUrl.contains("/")) {
            localPath = PathUtil.getInstance().getImagePath().getAbsolutePath() + "/"
                    + remoteUrl.substring(remoteUrl.lastIndexOf("/") + 1);
        } else {
            localPath = PathUtil.getInstance().getImagePath().getAbsolutePath() + "/" + remoteUrl;
        }
        return localPath;
    }

    private void downloadImage(final String remoteFilePath, final Map<String, String> headers) {
        String str1 = getResources().getString(R.string.Download_the_pictures);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage(str1);
        mProgressDialog.show();
        mLocalFilePath = getLocalFilePath(remoteFilePath);

        final EMCallBack callback = new EMCallBack() {
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DisplayMetrics metrics = new DisplayMetrics();
                        getWindowManager().getDefaultDisplay().getMetrics(metrics);
                        int screenWidth = metrics.widthPixels;
                        int screenHeight = metrics.heightPixels;

                        mBitmap = ImageUtils.decodeScaleImage(mLocalFilePath, screenWidth, screenHeight);
                        if (mBitmap == null) {
                            mPhotoView.setImageResource(mDefaultDrawableRes);
                        } else {
                            mPhotoView.setImageBitmap(mBitmap);
                            ImageCache.getInstance().put(mLocalFilePath, mBitmap);
                            mIsDownloaded = true;
                        }
                        if (mProgressDialog != null) {
                            mProgressDialog.dismiss();
                        }
                    }
                });
            }

            public void onError(int error, String msg) {
                ChatLogger.e(TAG, "offline file transfer error:" + msg);
                File file = new File(mLocalFilePath);
                if (file.exists() && file.isFile()) {
                    file.delete();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressDialog.dismiss();
                        mPhotoView.setImageResource(mDefaultDrawableRes);
                    }
                });
            }

            public void onProgress(final int progress, String status) {
                ChatLogger.d(TAG, "Progress: " + progress);
                final String str2 = getResources().getString(R.string.Download_the_pictures_new);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressDialog.setMessage(str2 + progress + "%");
                    }
                });
            }
        };
        EMChatManager.getInstance().downloadFile(remoteFilePath, mLocalFilePath, headers, callback);
    }*/

    @Override
    public void onBackPressed() {
        if (mIsDownloaded) {
            setResult(RESULT_OK);
        }
        finish();
    }

    private class MyAsyncTask extends AsyncTask<Void, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(Void... voids) {
            if (!mRemotePath.startsWith("http")) {
                return BitmapFactory.decodeFile(mRemotePath);
            }
            Bitmap bitmap = null;
            try {
                URL url = new URL(mRemotePath);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5 * 1000);
                if (conn.getResponseCode() == 200) {
                    InputStream inputStream = conn.getInputStream();
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    if (bitmap != null) {
                        ImageCache.getInstance().put(mRemotePath, bitmap);
                        mIsDownloaded = true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            mLoadLocalPb.setVisibility(View.GONE);
            showPhoto(bitmap);
        }
    }
}
