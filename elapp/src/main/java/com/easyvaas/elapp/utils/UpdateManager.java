/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hooview.app.R;
import com.easyvaas.elapp.bean.UpdateInfoEntity;
import com.easyvaas.elapp.db.Preferences;
import com.easyvaas.elapp.net.ApiHelper;
import com.easyvaas.elapp.net.MyRequestCallBack;
import com.easyvaas.elapp.net.RequestUtil;

public class UpdateManager {
    private static final String TAG = "UpdateManager";
    private static final int MSG_UPDATE_PROGRESS = 1;
    private static final int MSG_DOWNLOAD_COMPLETE = 2;
    private static final String SAVE_FILE_NAME = FileUtil.CACHE_DOWNLOAD_DIR + File.separator + "app.apk";

    private static UpdateManager mInstance;
    private static Context mContext;

    private Preferences mPref;
    private Dialog mDialog;
    private int mDownloadStatus;
    private int mProgress;
    private ProgressBar mProgressBar;
    private boolean mIsInterceptFlag;
    private boolean mIsCheckingUpdateInfo;
    private UpdateInfoEntity mUpdateInfo;
    private UpdateListener mUpdateListener;
    private UpdateDialogListener mUpdateDialogListener;

    private Handler mHandler;

    private static class MyHandler extends Handler {
        private SoftReference<UpdateManager> softReference;

        public MyHandler(UpdateManager updateManager) {
            softReference = new SoftReference<UpdateManager>(updateManager);
        }

        @Override
        public void handleMessage(Message msg) {
            UpdateManager updateManager = softReference.get();
            if (updateManager == null) {
                return;
            }
            switch (msg.what) {
                case MSG_UPDATE_PROGRESS:
                    updateManager.mProgressBar.setProgress(updateManager.mProgress);
                    break;
                case MSG_DOWNLOAD_COMPLETE:
                    updateManager.installApk();
                    break;
                default:
                    break;
            }
        }
    }

    private UpdateManager() {
        this.mHandler = new MyHandler(this);
        mPref = Preferences.getInstance(mContext);
    }

    public static UpdateManager getInstance(Context context) {
        mContext = context;
        if (mInstance == null) {
            mInstance = new UpdateManager();
        }
        return mInstance;
    }

    public void checkUpdateInfo(final UpdateListener updateListener,
            final UpdateDialogListener dialogListener) {
        if (mIsCheckingUpdateInfo || ChannelUtil.isGoogleChannel(mContext)) {
            return;
        }
        mIsCheckingUpdateInfo = true;
        ApiHelper.getInstance().checkUpdate(new MyRequestCallBack<UpdateInfoEntity>() {
                    @Override
                    public void onSuccess(UpdateInfoEntity result) {
                        if (result == null) {
                            return;
                        }
                        mUpdateInfo = result;
                        mUpdateListener = updateListener;
                        mUpdateDialogListener = dialogListener;
                        mPref.putBoolean(Preferences.KEY_IS_FORCE_UPDATE,
                                result.getForce() == UpdateInfoEntity.IS_FORCE_UPDATE);
                        if (UpdateInfoEntity.IS_UPDATE == result.getUpdate()) {
                            updateListener.onUpdateReturned(result);
                            showUpdateDialog(dialogListener);
                            mIsCheckingUpdateInfo = false;
                            mPref.putBoolean(Preferences.KEY_IS_HAVE_UPDATE, true);
                        }
                    }

                    @Override
                    public void onError(String errorInfo) {
                        super.onError(errorInfo);
                    }

                    @Override
                    public void onFailure(String msg) {
                        RequestUtil.handleRequestFailed(msg);
                    }
                });
    }

    public void checkUpdateAfterSplash() {
        final Activity activity = (Activity) mContext;

        checkUpdateInfo(new UpdateListener() {
            @Override
            public void onUpdateReturned(UpdateInfoEntity result) {
                switch (result.getUpdate()) {
                    case UpdateStatus.Yes: // has update
                        if (!activity.isFinishing()) {
                        }
                        break;
                    case UpdateStatus.No: // has no update
                        break;
                }
            }
        }, new UpdateDialogListener() {
            @Override
            public void onClick(int updateStatus) {
                switch (updateStatus) {
                    case UpdateStatus.Update:
                        break;
                    case UpdateStatus.Ignore:
                        break;
                    case UpdateStatus.NotNow:
                        break;
                }
            }
        });
    }

    public Dialog getUpdateDialog() {
        return mDialog;
    }

    private void showUpdateDialog(final UpdateDialogListener dialogListener) {
        View dialogView = LayoutInflater.from(mContext).inflate(R.layout.app_update_dialog, null);
        TextView appUpdateVersionNameTv = (TextView) dialogView.findViewById(R.id.app_update_version_name_tv);
        TextView appSizeTv = (TextView) dialogView.findViewById(R.id.app_size_tv);
        TextView appUpdateContent = (TextView) dialogView.findViewById(R.id.app_update_content_tv);
        if (mUpdateInfo != null) {
            appUpdateVersionNameTv.setText(
                    mContext.getString(R.string.app_new_version_name, mUpdateInfo.getUpdate_version()));
            appSizeTv.setText(mContext.getString(R.string.app_new_version_size,
                    (float) (mUpdateInfo.getTarget_size()) / 1024 / 1024));
            appUpdateContent.setText(mUpdateInfo.getUpdate_log());
        }
        Button appUpdateIdOk = (Button) dialogView.findViewById(R.id.app_update_id_ok);
        Button appUpdateIdCancel = (Button) dialogView.findViewById(R.id.app_update_id_cancel);
        mDialog = DialogUtil.getCustomDialog((Activity) mContext, dialogView, false, false, -1);
        mDialog.setOnKeyListener(null);
        appUpdateIdOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogListener != null) {
                    dialogListener.onClick(UpdateStatus.Update);
                }
                mDialog.dismiss();
                showDownloadDialog();
            }
        });
        if (!isForceUpdate()) {
            appUpdateIdCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialog.dismiss();
                    if (dialogListener != null) {
                        dialogListener.onClick(UpdateStatus.NotNow);
                    }
                }
            });
        } else {
            appUpdateIdCancel.setVisibility(View.GONE);
        }
        mDialog.show();
    }

    public void showDownloadDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.soft_update_tip);

        final LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.view_dowload_process, null);
        mProgressBar = (ProgressBar) v.findViewById(R.id.download_process);

        builder.setView(v);
        if (!isForceUpdate()) {
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    mIsInterceptFlag = true;
                    mDownloadStatus = DownloadStatus.Intercept;
                }
            });
        }
        if (mDownloadStatus == DownloadStatus.Complete) {
            mProgressBar.setProgress(mProgress);
            builder.setPositiveButton(R.string.install_software, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    installApk();
                }
            });

        } else {
            downloadApk();
        }
        mDialog = builder.create();
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();
    }

    private void downloadApk() {
        if (mUpdateInfo == null) {
            return;
        }
        Thread downLoadThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(mUpdateInfo.getUpdate_url());

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.connect();
                    int length = conn.getContentLength();
                    InputStream is = conn.getInputStream();

                    File apkFile = new File(SAVE_FILE_NAME);
                    if (!apkFile.getParentFile().exists()) {
                        apkFile.getParentFile().mkdir();
                    }
                    FileOutputStream fos = new FileOutputStream(apkFile);

                    int count = 0;
                    byte buf[] = new byte[1024];

                    do {
                        int numread = is.read(buf);
                        count += numread;
                        mProgress = (int) (((float) count / length) * 100);
                        mHandler.sendEmptyMessage(MSG_UPDATE_PROGRESS);
                        if (numread <= 0) {
                            mHandler.sendEmptyMessage(MSG_DOWNLOAD_COMPLETE);
                            break;
                        }
                        fos.write(buf, 0, numread);
                    } while (!mIsInterceptFlag);//点击取消就停止下载.

                    fos.close();
                    is.close();
                } catch (IOException e) {
                    Logger.w(TAG, "Download update apk failed !", e);
                }
            }
        });
        downLoadThread.start();
        mDownloadStatus = DownloadStatus.Downloading;
    }

    private void installApk() {
        File apkFile = new File(SAVE_FILE_NAME);
        if (!apkFile.exists() || mUpdateInfo == null
                || !Utils.checkMd5(apkFile, mUpdateInfo.getNew_md5())) {
            return;
        }
        mDownloadStatus = DownloadStatus.Complete;
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + apkFile.toString()),
                "application/vnd.android.package-archive");
        mContext.startActivity(i);
    }

    public boolean isForceUpdate() {
        return mUpdateInfo != null && mUpdateInfo.getForce() == UpdateInfoEntity.IS_FORCE_UPDATE;
    }

    public boolean isHaveUpdate() {
        return (mUpdateInfo != null && mUpdateInfo.getUpdate() == UpdateInfoEntity.IS_UPDATE)
                || mPref.getBoolean(Preferences.KEY_IS_HAVE_UPDATE, false);
    }

    public int getDownloadStatus() {
        return mDownloadStatus;
    }

    public static class UpdateStatus {
        public static final int No = 0;
        public static final int Yes = 1;
        public static final int NoneWifi = 2;
        public static final int Timeout = 3;
        public static final int Update = 4;
        public static final int NotNow = 5;
        public static final int Ignore = 6;
    }

    public static class DownloadStatus {
        public static final int Intercept = 1;
        public static final int Downloading = 2;
        public static final int Complete = 3;
    }

    public abstract static class UpdateListener{
        public abstract void onUpdateReturned(UpdateInfoEntity result);
    }

    public abstract static class UpdateDialogListener{
        public abstract void onClick(int updateStatus);
    }
}
