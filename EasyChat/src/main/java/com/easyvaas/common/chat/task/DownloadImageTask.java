package com.easyvaas.common.chat.task;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.easemob.chat.EMMessage;

import com.easyvaas.common.chat.utils.ChatLogger;

public class DownloadImageTask extends AsyncTask<EMMessage, Integer, Bitmap> {
    private DownloadFileCallback callback;

    public DownloadImageTask(String remoteDir, DownloadFileCallback callback) {
        this.callback = callback;
        String remoteDir1 = remoteDir;
    }

    @Override
    protected Bitmap doInBackground(EMMessage... params) {
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        callback.afterDownload(result);
    }

    @Override
    protected void onPreExecute() {
        callback.beforeDownload();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        callback.downloadProgress(values[0]);
    }

    public interface DownloadFileCallback {
        void beforeDownload();

        void downloadProgress(int progress);

        void afterDownload(Bitmap bitmap);
    }

    public static String getThumbnailImagePath(String imagePath) {
        String path = imagePath.substring(0, imagePath.lastIndexOf("/") + 1);
        path += "th" + imagePath.substring(imagePath.lastIndexOf("/") + 1, imagePath.length());
        ChatLogger.d("msg", "original image path:" + imagePath);
        ChatLogger.d("msg", "thum image path:" + path);
        return path;
    }
}
