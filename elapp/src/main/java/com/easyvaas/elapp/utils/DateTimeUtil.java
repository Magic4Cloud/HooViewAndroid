/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;

import com.hooview.app.R;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

public class DateTimeUtil {
    public static final String TAG = "DateTimeUtil";
    private static StringBuilder sFormatBuilder = new StringBuilder();
    private static Formatter sFormatter = new Formatter(sFormatBuilder, Locale.getDefault());
    private static SimpleDateFormat sServerDateFormatter;
    private static final Object[] sTimeArgs = new Object[5];
    public static int[] constellationEdgeDay = new int[]{20, 19, 21, 21, 21, 22, 23, 23, 23, 23, 22, 22};

    public static String getDurationTime(Context context, long startLongTime, long endLongTime) {
        long sec = (endLongTime - startLongTime) / 1000;
        String durationformat = context.getString(
                sec < 3600 ? R.string.duration_format_short : R.string.duration_format_long);

        /* Provide multiple arguments so the format can be changed easily
         * by modifying the xml.
         */
        sFormatBuilder.setLength(0);

        final Object[] timeArgs = sTimeArgs;
        timeArgs[0] = sec / 3600;
        timeArgs[1] = sec / 60;
        timeArgs[2] = (sec / 60) % 60;
        timeArgs[3] = sec;
        timeArgs[4] = sec % 60;

        return sFormatter.format(durationformat, timeArgs).toString();
    }

    public static String getDurationTime(Context context, long duration) {
        long sec = duration / 1000;
        String durationformat = context.getString(
                sec < 3600 ? R.string.duration_format_short : R.string.duration_format_long);

        /* Provide multiple arguments so the format can be changed easily
         * by modifying the xml.
         */
        sFormatBuilder.setLength(0);

        final Object[] timeArgs = sTimeArgs;
        timeArgs[0] = sec / 3600;
        timeArgs[1] = sec / 60;
        timeArgs[2] = (sec / 60) % 60;
        timeArgs[3] = sec;
        timeArgs[4] = sec % 60;

        return sFormatter.format(durationformat, timeArgs).toString();
    }

    /**
     * 视频时长
     *
     * @param context  Context
     * @param duration long 秒
     * @return
     */
    public static String getTimeDurationCn(Context context, long duration) {
        String format = context.getString(duration < 3600 ? R.string.duration_format_short_cn : R.string.duration_format_long_cn);

        /* Provide multiple arguments so the format can be changed easily
         * by modifying the xml.
         */
        sFormatBuilder.setLength(0);
        final Object[] timeArgs = sTimeArgs;
        timeArgs[0] = duration / 3600;
        timeArgs[1] = duration / 60;
        timeArgs[2] = (duration / 60) % 60;
        timeArgs[3] = duration;
        timeArgs[4] = duration % 60;
        return sFormatter.format(format, timeArgs).toString();
    }

    @SuppressLint("StringFormatMatches")
    public static String getSimpleTime(Context context, long startTimeSpan) {
        String result = "";
        startTimeSpan = startTimeSpan / 1000;
        if (startTimeSpan > (60 * 60 * 24 * 365)) {
            result = context.getString(R.string.before_year, startTimeSpan / (60 * 60 * 24 * 365));
        } else if (startTimeSpan > (60 * 60 * 24 * 30)) {
            result = context.getString(R.string.before_month, startTimeSpan / (60 * 60 * 24 * 30));
        } else if (startTimeSpan > (60 * 60 * 24 * 7)) {
            result = context.getString(R.string.before_week, startTimeSpan / (60 * 60 * 24 * 7));
        } else if (startTimeSpan > (60 * 60 * 24)) {
            result = context.getString(R.string.before_day, startTimeSpan / (60 * 60 * 24));
        } else if (startTimeSpan > (60 * 60)) {
            result = context.getString(R.string.before_hour, startTimeSpan / (60 * 60));
        } else if (startTimeSpan > 60) {
            result = context.getString(R.string.before_minute, startTimeSpan / 60);
        } else {
            result = context.getString(R.string.just_now);
        }

        return result;
    }

    // 2015-06-15 10:36:05
    public static String getShortTime(Context context, String time) {
        if (TextUtils.isEmpty(time)) {
            return "";
        }

        String result = "";
        if (sServerDateFormatter == null) {
            sServerDateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        }
        time = time.trim();
        ParsePosition pos = new ParsePosition(0);
        Date cTime = sServerDateFormatter.parse(time, pos);
        long duration = System.currentTimeMillis() - cTime.getTime();
        long MILLIS_ONE_MONTH = 30 * 24 * 3600 * 1000L;
        long MILLIS_ONE_WEEK = 7 * 24 * 3600 * 1000;
        long MILLIS_ONE_DAY = 24 * 3600 * 1000;
        long MILLIS_ONE_HOUR = 3600 * 1000;

        if (duration < MILLIS_ONE_HOUR) {
            long MILLIS_ONE_MINUTE = 60 * 1000;
            result = context.getString(R.string.before_minute, duration / MILLIS_ONE_MINUTE);
        } else if (duration < MILLIS_ONE_DAY) {
            result = context.getString(R.string.before_hour, duration / MILLIS_ONE_HOUR);
        } else if (duration < MILLIS_ONE_WEEK) {
            result = context.getString(R.string.before_day, duration / MILLIS_ONE_DAY);
        } else if (duration < MILLIS_ONE_MONTH) {
            result = context.getString(R.string.before_week, duration / MILLIS_ONE_WEEK);
        } else {
            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd HH:mm", Locale.getDefault());
            result = formatter.format(cTime);
        }

        return result;
    }

    public static String getNewsTime(Context context, String time) {
        if (TextUtils.isEmpty(time)) {
            return "";
        }

        String result = "";
        if (sServerDateFormatter == null) {
            sServerDateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        }
        time = time.trim();
        ParsePosition pos = new ParsePosition(0);
        Date cTime = sServerDateFormatter.parse(time, pos);
        long duration = 0;
        if (cTime != null) {
            duration = System.currentTimeMillis() - cTime.getTime();
        }
        long MILLIS_ONE_MONTH = 30 * 24 * 3600 * 1000L;
        long MILLIS_ONE_WEEK = 7 * 24 * 3600 * 1000;
        long MILLIS_ONE_DAY = 24 * 3600 * 1000;
        long MILLIS_ONE_HOUR = 3600 * 1000;

        SimpleDateFormat format = new SimpleDateFormat("  HH:mm", Locale.getDefault());
        if (duration < MILLIS_ONE_DAY) {
            result = "今天" + format.format(cTime);
        } else if (duration > MILLIS_ONE_DAY && duration < MILLIS_ONE_DAY * 2) {
            result = "昨天" + format.format(cTime);
        } else {
            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd HH:mm", Locale.getDefault());
            result = formatter.format(cTime);
        }
        return result;
    }

    /**
     * 06/06 12:30，未考虑年份
     */
    public static String getTimeVideo(String time) {
        if (TextUtils.isEmpty(time)) {
            return "";
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date dateOri = format.parse(time.trim(), new ParsePosition(0));
        if (dateOri == null) {
            return "";
        }
        String result = "";
        SimpleDateFormat formatNear = new SimpleDateFormat(" HH:mm", Locale.getDefault());
        SimpleDateFormat formatFar = new SimpleDateFormat("MM/dd HH:mm", Locale.getDefault());
        // 48小时之内
        if (System.currentTimeMillis() - dateOri.getTime() < 2 * 24 * 3600 * 1000) {
            // 目标时间
            Calendar calendarOri = Calendar.getInstance();
            calendarOri.setTime(dateOri);
            int dayOri = calendarOri.get(Calendar.DAY_OF_MONTH);
            Logger.d("xmzd", "day original: " + dayOri);
            // 当前时间
            Calendar calendarNow = Calendar.getInstance();
            calendarNow.setTime(new Date());
            int dayNow = calendarNow.get(Calendar.DAY_OF_MONTH);
            Logger.d("xmzd", "day now: " + dayNow);
            if (dayNow == dayOri) {
                result = "今天" + formatNear.format(dateOri);
            } else if ((dayNow == dayOri + 1) || (dayNow == 1 && (dayOri == 28 || dayOri == 29 || dayOri == 30 || dayOri == 31))) {
                result = "昨天" + formatNear.format(dateOri);
            } else {
                result = formatFar.format(dateOri);
            }
        } else {
            result = formatFar.format(dateOri);
        }
        return result;
    }

    // 2015-06-15 10:36:05
    public static String getSimpleTime(Context context, String time) {
        if (TextUtils.isEmpty(time)) {
            return "";
        }

        String result = "";
        if (sServerDateFormatter == null) {
            sServerDateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        }
        time = time.trim();
        ParsePosition pos = new ParsePosition(0);
        Date cTime = sServerDateFormatter.parse(time, pos);
        long duration = System.currentTimeMillis() - cTime.getTime();
        long MILLIS_ONE_MONTH = 30 * 24 * 3600 * 1000L;
        long MILLIS_ONE_WEEK = 7 * 24 * 3600 * 1000;
        long MILLIS_ONE_DAY = 24 * 3600 * 1000;
        long MILLIS_ONE_HOUR = 3600 * 1000;

        /*if (duration < MILLIS_ONE_HOUR) {
            long MILLIS_ONE_MINUTE = 60 * 1000;
            result = context.getString(R.string.before_minute, duration / MILLIS_ONE_MINUTE);
        } else if (duration < MILLIS_ONE_DAY) {
            result = context.getString(R.string.before_hour, duration / MILLIS_ONE_HOUR);
        } else if (duration < MILLIS_ONE_WEEK) {
            result = context.getString(R.string.before_day, duration / MILLIS_ONE_DAY);
        } else if (duration < MILLIS_ONE_MONTH) {
            result = context.getString(R.string.before_week, duration / MILLIS_ONE_WEEK);
        } else {*/
        if (isToday(cTime)) {
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm", Locale.getDefault());
            result = context.getString(R.string.today_time, formatter.format(cTime));
        } else {
            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd HH:mm", Locale.getDefault());
            result = formatter.format(cTime);
        }

        return result;
    }

    /**
     * 11月12日
     */
    public static String getCheatsTime(Context context, String time) {
        if (TextUtils.isEmpty(time)) {
            return "";
        }
        String result = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.getDefault());
        time = time.trim();
        ParsePosition pos = new ParsePosition(0);
        Date cTime = format.parse(time, pos);
        SimpleDateFormat formatter = new SimpleDateFormat("MM月dd日", Locale.getDefault());
        result = formatter.format(cTime);
        return result;
    }

    private static boolean isToday(Date date) {
        Calendar current = Calendar.getInstance();
        Calendar today = Calendar.getInstance();    //今天

        today.set(Calendar.YEAR, current.get(Calendar.YEAR));
        today.set(Calendar.MONTH, current.get(Calendar.MONTH));
        today.set(Calendar.DAY_OF_MONTH, current.get(Calendar.DAY_OF_MONTH));
        //  Calendar.HOUR——12小时制的小时数 Calendar.HOUR_OF_DAY——24小时制的小时数
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);

        current.setTime(date);
        return current.after(today);
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

    public static long formatServerDate(String dataTime) {
        if (TextUtils.isEmpty(dataTime)) {
            return 0;
        } else {
            return formatServerTime(dataTime).getTime();
        }
    }

    /**
     * @param dateTime style "yyyy-MM-dd HH:mm:ss"
     */
    private static Date formatServerTime(String dateTime) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        ParsePosition pos = new ParsePosition(0);
        return formatter.parse(dateTime, pos);
    }

    private static Date formatBirthday(String dateTime) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        ParsePosition pos = new ParsePosition(0);
        return formatter.parse(dateTime, pos);
    }

    public static String formatToServerDate(long millSeconds) {
        Date date = new Date(millSeconds);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return formatter.format(date);
    }

    public static String getCountdownTime(Context ctx, long startTimeSpan) {
        String result = "";

        long daySecond = 60 * 60 * 24;
        long hourSecond = 60 * 60;
        startTimeSpan = -startTimeSpan;
        if (startTimeSpan < 30) {
            result = ctx.getString(R.string.video_schedule_will_start_live);
        } else if (startTimeSpan <= 60) {
            result = ctx.getString(R.string.countdown_second, startTimeSpan);
            result = ctx.getString(R.string.video_schedule_countdown, result);
        } else {
            if (startTimeSpan > daySecond) {
                result = ctx.getString(R.string.countdown_day, startTimeSpan / daySecond);
                startTimeSpan = startTimeSpan % daySecond;
            }
            if (startTimeSpan > hourSecond) {
                result = result + ctx.getString(R.string.countdown_hour, startTimeSpan / hourSecond);
                startTimeSpan = startTimeSpan % hourSecond;
            }
            if (startTimeSpan > 60) {
                result = result + ctx.getString(R.string.countdown_minute, startTimeSpan / 60);
            }
            result = ctx.getString(R.string.video_schedule_countdown, result);
        }

        return result;
    }

    public static String getSimpleCountdownTime(Context ctx, long millSeconds) {
        String result = "";
        if (millSeconds > 24 * 3600 * 1000) {
            long day = millSeconds / (24 * 3600 * 1000);
            result = ctx.getString(R.string.video_schedule_last_day, day);
        } else if (millSeconds > 3600 * 1000) {
            long hour = millSeconds / (3600 * 1000);
            result = ctx.getString(R.string.video_schedule_last_hour, hour);
        } else if (millSeconds > 60 * 1000) {
            long min = millSeconds / (60 * 1000);
            result = ctx.getString(R.string.video_schedule_last_min, min);
        }
        return result;
    }

    public static boolean isCurrentDateInSpan(String startDateStr, String stopDateStr) {
        boolean flag = false;
        if (TextUtils.isEmpty(startDateStr) || TextUtils.isEmpty(stopDateStr)) {
            return false;
        }
        Date nowDate = new java.util.Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        Date startDate;
        Date stopDate;
        try {
            startDate = simpleDateFormat.parse(startDateStr);
            stopDate = simpleDateFormat.parse(stopDateStr);
            flag = (startDate.before(nowDate) && stopDate.after(nowDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return flag;
    }

    public static int daysBetween(String startDate, String stopDate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar cal = Calendar.getInstance();
        cal.setTime(sdf.parse(startDate));
        long time1 = cal.getTimeInMillis();
        cal.setTime(sdf.parse(stopDate));
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);
        return Integer.parseInt(String.valueOf(between_days));
    }

    /**
     * @param birthday style "yyyy-MM-dd"
     */
    public static int getAge(String birthday) {
        Date birthDay = formatBirthday(birthday);
        Calendar cal = Calendar.getInstance();

        if (birthDay == null || cal.before(birthDay)) {
            Logger.w(TAG, "The birthDay is before Now.It's unbelievable!");
            return 0;
        }
        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH);
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);
        cal.setTime(birthDay);

        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH);
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);

        int age = yearNow - yearBirth;

        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth) {
                if (dayOfMonthNow < dayOfMonthBirth) age--;
            } else {
                age--;
            }
        }
        return age;
    }

    private static int[] getYearMonthDay(String birthday) {
        Date date = formatBirthday(birthday);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return new int[]{
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.DAY_OF_MONTH)
        };
    }

    private final static int[] CONSTELLATION_DAY_ARR = new int[]{
            20, 19, 21, 20, 21, 22, 23, 23, 23, 24, 23, 22
    };
    private final static int[] CONSTELLATION_RES_ARR = new int[]{
            R.string.constellation_aquarius, R.string.constellation_pisces, R.string.constellation_aries,
            R.string.constellation_taurus, R.string.constellation_gemini, R.string.constellation_cancer,
            R.string.constellation_leo, R.string.constellation_virgo, R.string.constellation_libra,
            R.string.constellation_scorpio, R.string.constellation_sagittarius, R.string.constellation_capricorn
    };

    public static String getConstellation(Context context, String birthday) {
        int data[] = getYearMonthDay(birthday);
        if (data != null && data.length == 3) {
            return getConstellation(context, data[1], data[2]);
        }
        return "";
    }

    public static String getConstellation(Context context, int month, int day) {
        int resId = CONSTELLATION_RES_ARR[CONSTELLATION_RES_ARR.length - 1];
        month = month - 1;
        if (day < CONSTELLATION_DAY_ARR[month]) {
            month = month - 1;
        }
        if (month >= 0) {
            resId = CONSTELLATION_RES_ARR[month];
        }
        return context.getString(resId);
    }


    // 2015-06-15 10:36:05
    public static String getLastestNewsTime(Context context, String time) {
        if (TextUtils.isEmpty(time)) {
            return "";
        }

        String result = "";
        if (sServerDateFormatter == null) {
            sServerDateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        }
        time = time.trim();
        ParsePosition pos = new ParsePosition(0);
        Date cTime = sServerDateFormatter.parse(time, pos);
        long duration = System.currentTimeMillis() - cTime.getTime();
        long MILLIS_ONE_MONTH = 30 * 24 * 3600 * 1000L;
        long MILLIS_ONE_WEEK = 7 * 24 * 3600 * 1000;
        long MILLIS_ONE_DAY = 24 * 3600 * 1000;
        long MILLIS_ONE_HOUR = 3600 * 1000;
        if (duration < MILLIS_ONE_HOUR) {
            long MILLIS_ONE_MINUTE = 60 * 1000;
            result = context.getString(R.string.before_minute, duration / MILLIS_ONE_MINUTE);
        } else if (duration < MILLIS_ONE_DAY) {
            result = context.getString(R.string.before_hour, duration / MILLIS_ONE_HOUR);
        } else {
            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd HH:mm", Locale.getDefault());
            result = formatter.format(cTime);
        }

        return result;
    }

    public static String getLiveTime(Context context, String time) {
        long timee = formatServerDate(time);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM.dd", Locale.getDefault());
        return simpleDateFormat.format(new Date(timee));
    }

    public static String getImageTextLiveTime(Context context, long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return simpleDateFormat.format(new Date(time));
    }

    public static String formatDate(Context context, long time, String pattern) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
        return simpleDateFormat.format(new Date(time));
    }
}
