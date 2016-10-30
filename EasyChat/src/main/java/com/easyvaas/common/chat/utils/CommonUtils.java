package com.easyvaas.common.chat.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.exceptions.EaseMobException;

import com.easyvaas.common.chat.bean.ChatRedPackEntity;
import com.easyvaas.common.chat.db.ChatDBManager;
import com.easyvaas.common.chat.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

public class CommonUtils {
    private static final String TAG = CommonUtils.class.getSimpleName();

    /**
     * 检测网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetWorkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }

        return false;
    }

    /**
     * 检测Sdcard是否存在
     *
     * @return
     */
    public static boolean isExitsSdcard() {
        return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }

    /**
     * 根据消息内容和消息类型获取消息内容提示
     *
     * @param message
     * @param context
     * @return
     */
    public static String getMessageDigest(EMMessage message, Context context) {
        String digest = "";
        switch (message.getType()) {
            case LOCATION:
                if (message.direct == EMMessage.Direct.RECEIVE) {
                    //从sdk中提到了ui中，使用更简单不犯错的获取string方法
                    //              digest = EasyUtils.getAppResourceString(context, "location_recv");
                    digest = getString(context, R.string.chat_location_recv);
                    digest = String.format(digest, message.getFrom());
                    return digest;
                } else {
                    //              digest = EasyUtils.getAppResourceString(context, "location_prefix");
                    digest = getString(context, R.string.chat_location_prefix);
                }
                break;
            case IMAGE:
                if (isLivingImage(message)) {
                    digest = getString(context, R.string.chat_living);
                } else {
                    digest = getString(context, R.string.chat_picture);
                }
                break;
            case VOICE:
                digest = getString(context, R.string.chat_voice);
                break;
            case VIDEO:
                digest = getString(context, R.string.chat_video);
                break;
            case TXT:
                if (!message.getBooleanAttribute(ChatConstants.MESSAGE_ATTR_IS_VOICE_CALL, false)) {
                    TextMessageBody txtBody = (TextMessageBody) message.getBody();
                    digest = txtBody.getMessage();
                    if (digest.equals(context.getResources().getString(R.string.red_pack_tag))) {
                        digest = context.getResources().getString(R.string.red_pack);
                    }
                } else {
                    TextMessageBody txtBody = (TextMessageBody) message.getBody();
                    digest = getString(context, R.string.chat_voice_call) + txtBody.getMessage();
                }
                break;
            case FILE:
                digest = getString(context, R.string.chat_file);
                break;
            default:
                ChatLogger.e(TAG, "error, unknow type");
                return "";
        }

        return digest;
    }

    public static String getString(Context context, int resId) {
        return context.getResources().getString(resId);
    }

    public static String getTopActivity(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);

        if (runningTaskInfos != null)
            return runningTaskInfos.get(0).topActivity.getClassName();
        else
            return "";
    }

    public static boolean isLivingImage(EMMessage message) {
        try {
            if (!TextUtils.isEmpty(message.getStringAttribute(ChatConstants.EXTERNAL_EXTRA_KEY_VIDEO_ID))) {
                return true;
            }
        } catch (EaseMobException e) {
            ChatLogger.e(TAG, "handle image, get vid failed!", e);
        }
        return false;
    }

    public static boolean isAppRunningBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                return appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND;
            }
        }
        return false;
    }

    public static ChatRedPackEntity getRedPackEntity(EMMessage message) {
        ChatRedPackEntity redPackEntity = null;
        try {
            JSONObject redPack = message
                    .getJSONObjectAttribute(ChatConstants.MESSAGE_ATTR_RED_PACK_INFO);
            String redPackCode = redPack == null ? ""
                    : redPack.optString(ChatConstants.MESSAGE_ATTR_RED_PACK_CODE);
            if (redPack != null && !TextUtils.isEmpty(redPackCode)) {
                String redPackLogo = redPack.optString(ChatConstants.MESSAGE_ATTR_RED_PACK_LOGO, "");
                redPackLogo = TextUtils.isEmpty(redPackLogo)
                        ? ChatDBManager.getInstance().getAvatar(message.getFrom())
                        : redPackLogo;
                redPackEntity = new ChatRedPackEntity();
                redPackEntity.setCode(redPackCode);
                redPackEntity.setName(redPack.getString(ChatConstants.MESSAGE_ATTR_RED_PACK_NAME));
                redPackEntity.setCount(redPack.getInt(ChatConstants.MESSAGE_ATTR_RED_PACK_COUNT));
                redPackEntity.setLogo(redPackLogo);
                redPackEntity.setValue(redPack.getInt(ChatConstants.MESSAGE_ATTR_RED_PACK_VALUE));
                redPackEntity.setFrom(message.getFrom());
                redPackEntity.setMsgId(message.getMsgId());
            }
        } catch (EaseMobException | JSONException e) {
            ChatLogger.w(TAG, "Parse red pack message error!", e);
        }
        return redPackEntity;
    }

    public static String getRedPackCode(EMMessage message) {
        try {
            JSONObject redPack = message.getJSONObjectAttribute(ChatConstants.MESSAGE_ATTR_RED_PACK_INFO);
            return redPack == null ? "" : redPack.optString(ChatConstants.MESSAGE_ATTR_RED_PACK_CODE);
        } catch (EaseMobException e) {
            ChatLogger.w(TAG, "Parse red pack message error!", e);
        }
        return "";
    }

    public static String getVerName(Context c) {
        PackageManager pm = c.getPackageManager();
        PackageInfo pi = null;
        try {
            pi = pm.getPackageInfo(c.getPackageName(), PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            ChatLogger.e(TAG, "Error while collect package info", e);
            e.printStackTrace();
            return "error";
        }
        if (pi == null) {
            return "error1";
        }
        String versionName = pi.versionName;
        if (versionName == null) {
            return "not set";
        }
        return versionName;
    }

    public static boolean isFullScreen(Activity activity) {
        int flag = activity.getWindow().getAttributes().flags;
        return (flag & WindowManager.LayoutParams.FLAG_FULLSCREEN)
                == WindowManager.LayoutParams.FLAG_FULLSCREEN;
    }

    public static void showImage(String url, int loadingResId, ImageView target) {
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

    public static String getFormatDate(String dateTime, String formatStyle) {
        if (TextUtils.isEmpty(dateTime)) {
            return "";
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        ParsePosition pos = new ParsePosition(0);
        Date cTime = formatter.parse(dateTime, pos);
        if (cTime == null) {
            return "";
        }

        formatter = new SimpleDateFormat(formatStyle, Locale.CHINA);
        return formatter.format(cTime);
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
                ChatLogger.e(TAG, "getFileMD5.Exception : " + e.getMessage());
            } catch (IOException e) {
                ChatLogger.e(TAG, "getFileMD5.Exception : " + e.getMessage());
            } catch (OutOfMemoryError e) {
                ChatLogger.e(TAG, "getFileMD5.Exception : " + e.getMessage());
            } catch (Exception e) {
                ChatLogger.e(TAG, "getFileMD5.Exception : " + e.getMessage());
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        ChatLogger.e(TAG, "getFileMD5.Exception : " + e.getMessage());
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

}
