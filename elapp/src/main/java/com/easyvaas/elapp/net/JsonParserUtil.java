/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.net;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.easyvaas.elapp.utils.Logger;

public class JsonParserUtil {
    private static final String TAG = JsonParserUtil.class.getSimpleName();

    private static JSONObject checkResponse(String jsonString) {
        if (TextUtils.isEmpty(jsonString)) {
            return null;
        }
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonString);
        } catch (JSONException e) {
            Logger.w(TAG, "checkResponse wrong ", e);
        }
        return jsonObject;
    }

    public static JSONObject getJsonObject(String jsonString) {
        if (TextUtils.isEmpty(jsonString)) {
            return null;
        }
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonString);
        } catch (JSONException e) {
            Logger.w(TAG, "checkResponse wrong ", e);
        }
        return jsonObject;
    }

    public static boolean isResponseOk(JSONObject jsonObject) {
        if (jsonObject != null) {
            String status = jsonObject.optString(ApiConstant.KEY_RET_VAL);
            if (TextUtils.isEmpty(status)) {
                status = jsonObject.optString(ApiConstant.KEY_RET_VAL_SHORT);
            }
            if (jsonObject.has("created_at")) {
                return true;
            } else if ("ok".equalsIgnoreCase(status)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isResponseOk(String jsonString) {
        JSONObject jsonObject = checkResponse(jsonString);
        return isResponseOk(jsonObject);
    }

    public static String getResultInfo(JSONObject jsonObject) {
        if (jsonObject == null || !isResponseOk(jsonObject)) {
            return "";
        }
        String info = "";
        info = jsonObject.optString(ApiConstant.KEY_RET_INFO);
        if (TextUtils.isEmpty(info)) {
            info = jsonObject.optString(ApiConstant.KEY_RET_INFO_SHORT);
        }
        if (info.matches("\\s*\\{\\s*\\}\\s*")) {
            return "";
        }
        return info;
    }

    public static String getResultValue(JSONObject jsonObject) {
        if (jsonObject == null) {
            return "";
        }
        String info = "";
        info = jsonObject.optString(ApiConstant.KEY_RET_VAL);
        if (TextUtils.isEmpty(info)) {
            info = jsonObject.optString(ApiConstant.KEY_RET_VAL_SHORT);
        }
        return info;
    }

    public static String getErrorInfo(JSONObject jsonObject) {
        if (jsonObject == null) {
            return "";
        }
        String info = "";
        info = jsonObject.optString(ApiConstant.KEY_RET_ERR);
        if (TextUtils.isEmpty(info)) {
            info = jsonObject.optString(ApiConstant.KEY_RET_ERR_SHORT);
        }
        return info;
    }

    public static String getString(String json, String key) {
        return getString(getJsonObject(json), key);
    }

    public static String getString(JSONObject jsonObject, String key) {
        if (jsonObject == null) {
            return "";
        }
        String info = jsonObject.optString(key, "");
        if (info.matches("\\s*\\{\\s*\\}\\s*")) {
            return "";
        }
        return info;
    }

    public static int getInt(String json, String key) {
        return getInt(getJsonObject(json), key);
    }

    public static int getInt(JSONObject jsonObject, String key) {
        if (jsonObject == null) {
            return -1;
        }
        return jsonObject.optInt(key, -1);
    }

    public static boolean getBoolean(String json, String key) {
        return getBoolean(getJsonObject(json), key);
    }

    public static boolean getBoolean(JSONObject jsonObject, String key) {
        if (jsonObject == null) {
            return false;
        }
        return jsonObject.optBoolean(key, false);
    }

    public static List toJavaBeans(Object javabean, JSONObject jsonObject, String keyJsonArray) {
        return toJavaBeans(javabean, jsonObject, keyJsonArray, false);
    }

    public static List toJavaBeans(Object javabean, JSONObject jsonObject,
            String keyJsonArray, boolean isReverse) {
        List list = new ArrayList();
        if (jsonObject == null) {
            return list;
        }
        JSONArray jsonArray = null;
        try {
            jsonArray = jsonObject.getJSONArray(keyJsonArray);
        } catch (JSONException e) {
            Logger.w(TAG, "toJavaBeans get json array failed, key: " + keyJsonArray, e);
            return list;
        }
        Logger.d(TAG, "JsonArray length : " + jsonArray.length());
        int size = jsonArray.length();
        if (size > 0) {
            try {
                if (isReverse) {
                    list.add(toJavaBean(javabean, jsonArray.getJSONObject(size - 1)));
                    for (int i = size - 2; i > -1; i--) {
                        Object bean = Class.forName(javabean.getClass().getName()).newInstance();
                        list.add(toJavaBean(bean, jsonArray.getJSONObject(i)));
                    }
                } else {
                    list.add(toJavaBean(javabean, jsonArray.getJSONObject(0)));
                    for (int i = 1; i < size; i++) {
                        Object bean = Class.forName(javabean.getClass().getName()).newInstance();
                        list.add(toJavaBean(bean, jsonArray.getJSONObject(i)));
                    }
                }
            } catch (JSONException e) {
                Logger.w(TAG, "toJavaBeans failed !!!", e);
            } catch (ClassNotFoundException e) {
                Logger.w(TAG, "toJavaBeans failed !!!", e);
            } catch (InstantiationException e) {
                Logger.w(TAG, "toJavaBeans failed !!!", e);
            } catch (IllegalAccessException e) {
                Logger.w(TAG, "toJavaBeans failed !!!", e);
            }
        }
        return list;
    }

    public static Object toJavaBean(Object javabean, JSONObject jsonObject) {
        return toJavaBean(javabean, toMap(jsonObject));
    }

    private static Object toJavaBean(Object javabean, Map<String, String> data) {
        Field[] fields = javabean.getClass().getFields();
        for (Field field : fields) {
            // ChatLogger.d(TAG, "field: " + field.getName() + " modifier : " + field.getModifiers());
            if (Modifier.isFinal(field.getModifiers()) || !Modifier.isPublic(field.getModifiers())) {
                continue;
            }
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            String value = data.get(field.getName());
            // ChatLogger.d(TAG, "toJavaBean(), key : " + field.getName() + " value: " + value);
            if (value == null || value.isEmpty()) {
                continue;
            }
            try {
                if (field.getType().equals(int.class)) {
                    field.set(javabean, Integer.parseInt(value));
                } else if (field.getType().equals(String.class)) {
                    field.set(javabean, value);
                } else if (field.getType().equals(double.class)) {
                    field.set(javabean, Double.parseDouble(value));
                } else if (field.getType().equals(long.class)) {
                    field.set(javabean, Long.parseLong(value));
                }
            } catch (IllegalAccessException e) {
                Logger.w(TAG, "toJavaBean() failed. ", e);
            }
        }

        return javabean;
    }

    public static Object toJavaBeanBySetMethod(Object javabean, JSONObject jsonObject) {
        Map<String, String> data = toMap(jsonObject);
        Method[] methods = javabean.getClass().getDeclaredMethods();
        for (Method method : methods) {
            try {
                if (method.getName().startsWith("set")) {

                    String field = method.getName();
                    field = field.substring(field.indexOf("set") + 3);
                    field = field.toLowerCase().charAt(0) + field.substring(1);
                    String value = data.get(field);
                    if (value == null || value.isEmpty()) {
                        continue;
                    }
                    if (method.getReturnType().equals(int.class)) {
                        method.invoke(javabean, Integer.parseInt(value));
                    } else if (method.getReturnType().equals(String.class)) {
                        method.invoke(javabean, String.valueOf(value));
                    } else if (method.getReturnType().equals(double.class)) {
                        method.invoke(javabean, Double.parseDouble(value));
                    } else if (method.getReturnType().equals(long.class)) {
                        method.invoke(javabean, Long.parseLong(value));
                    }
                }
            } catch (IllegalAccessException e) {
                Logger.w(TAG, "toJavaBeanBySetMethod() failed. ", e);
            } catch (InvocationTargetException e) {
                Logger.w(TAG, "toJavaBeanBySetMethod() failed. ", e);
            }
        }

        return javabean;
    }

    public static Map<String, String> toMap(JSONObject jsonObject) {
        Logger.d(TAG, "toMap(), json: " + jsonObject);
        Map<String, String> result = new HashMap<String, String>();
        Iterator iterator = jsonObject.keys();
        String key, value;
        try {
            while (iterator.hasNext()) {
                key = (String) iterator.next();
                value = jsonObject.getString(key);
                result.put(key, value);
                // ChatLogger.d(TAG, "key : " + key + " \n value: " + value);
            }
        } catch (JSONException e) {
            Logger.w(TAG, "String json toMap() failed. ", e);
        }
        return result;
    }

    public static String getRetval(String str){
        try {
            JSONObject object = new JSONObject(str);
           return object.getString("retval");
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

    public static String getAnimName(String jsonStr) {
        try {
            JSONObject object = new JSONObject(jsonStr);
            return object.getString("NameType");
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }
}
