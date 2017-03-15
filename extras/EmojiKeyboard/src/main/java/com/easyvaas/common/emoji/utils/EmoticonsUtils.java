package com.easyvaas.common.emoji.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.os.Environment;

import com.easyvaas.common.emoji.bean.EmoticonBean;
import com.easyvaas.common.emoji.bean.EmoticonSetBean;
import com.easyvaas.common.emoji.db.DBHelper;

public class EmoticonsUtils {
    /**
     * 保存于内存中的表情HashMap
     */
    public static HashMap<String, String> emojiMap = new HashMap<String, String>();
    private static String TAG = "EmoticonsUtils";

    /**
     * 初始化表情数据库
     *
     * @param context
     * @param forceInit
     */
    public static void initEmoticonsDB(final Context context, boolean forceInit) {
        if (forceInit) {
            context.deleteDatabase(DBHelper.DATABASE_NAME);
            Utils.setIsInitDb(context, false);
        }
        if (!Utils.isInitDb(context)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    DBHelper dbHelper = DBHelper.getInstance(context);
                    /**
                     * FROM DRAWABLE
                     */
                    ArrayList<EmoticonBean> emojiArray = ParseDatas(EmoticonBean.FACE_TYPE_NOMAL, context);
                    EmoticonSetBean emojiEmoticonSetBean = new EmoticonSetBean("emoji", 3, 7);
                    emojiEmoticonSetBean.setIconUri("drawable://icon_emoji");
                    emojiEmoticonSetBean.setItemPadding(20);
                    emojiEmoticonSetBean.setVerticalSpacing(10);
                    emojiEmoticonSetBean.setShowDelBtn(true);
                    emojiEmoticonSetBean.setEmoticonList(emojiArray);
                    dbHelper.insertEmoticonSet(emojiEmoticonSetBean);

                    /**
                     * FROM FILE
                     */
                    String filePath = Environment.getExternalStorageDirectory() + "/wxemoticons";
                    try {
                        FileUtils.unzip(context.getAssets().open("wxemoticons.zip"), filePath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    XmlUtil xmlUtil = new XmlUtil(context);
                    EmoticonSetBean bean = xmlUtil.ParserXml(xmlUtil.getXmlFromSD(filePath + "/wxemoticons.xml"));
                    bean.setItemPadding(20);
                    bean.setVerticalSpacing(5);
                    bean.setIconUri("file://" + filePath + "/icon_030_cover.png");
                    dbHelper.insertEmoticonSet(bean);

                    dbHelper.cleanup();
                    Utils.setIsInitDb(context, true);
                }
            }).start();
        }else{
            ParseData(context);
        }
    }

    public static EmoticonsKeyboardBuilder getSimpleBuilder(Context context) {
        DBHelper dbHelper = DBHelper.getInstance(context);
        ArrayList<EmoticonSetBean> mEmoticonSetBeanList = dbHelper.queryEmoticonSet("emoji", "");
        dbHelper.cleanup();
        return new EmoticonsKeyboardBuilder.Builder()
                .setEmoticonSetBeanList(mEmoticonSetBeanList)
                .build();
    }

    public static ArrayList<EmoticonBean> ParseDatas(long eventType, Context context) {
        ArrayList<EmoticonBean> emojis = new ArrayList<EmoticonBean>();
        String fileName = null;
        String content = null;
        try {
            InputStream in = context.getResources().getAssets().open("emoji");
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));

            String str = "";
            while ((str = br.readLine()) != null) {
                String[] strings = str.split(",");
                fileName = strings[0];
                content = strings[1];
                String uri = strings[0] .substring(0, strings[0].lastIndexOf("."));
                emojiMap.put(content, uri);
                EmoticonBean bean = new EmoticonBean(eventType, fileName, content);
                emojis.add(bean);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return emojis;
    }


    public static void ParseData(Context context) {
        String content = "";
        try {
            InputStream in = context.getResources().getAssets().open("emoji");
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));

            String str = "";
            while ((str = br.readLine()) != null) {
                String[] strings = str.split(",");
                content = strings[1];
                String fileName = strings[0] .substring(0, strings[0].lastIndexOf("."));
                emojiMap.put(content, fileName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
