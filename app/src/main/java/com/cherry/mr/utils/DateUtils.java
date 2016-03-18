package com.cherry.mr.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import net.xici.newapp.R;
import net.xici.newapp.app.XiciApp;


import android.text.TextUtils;
import android.util.Log;


public class DateUtils {

    public static SimpleDateFormat FORMAT_DEFAULT_ALL = new SimpleDateFormat("yyyy年MM月dd日 hh:mm:ss", Locale.CHINA);
    public static SimpleDateFormat FORMAT_MM_SS = new SimpleDateFormat("MM月dd日 hh:mm:ss", Locale.CHINA);
    /**
     * format:yyyy年MM月dd日 hh:mm
     */
    public static SimpleDateFormat FORMAT_DEFAULT = new SimpleDateFormat("yyyy年MM月dd日 hh:mm", Locale.CHINA);
    /**
     * format:yyyy年MM月
     */
    public static SimpleDateFormat FORMAT_YY_MM = new SimpleDateFormat("yyyy年MM月", Locale.CHINA);

    public static SimpleDateFormat FORMAT_SEND_SMS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static SimpleDateFormat FORMAT_FEED = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static SimpleDateFormat FORMAT_FEED2 = new SimpleDateFormat("yyyy-MM-dd");

    public static String nowToString(SimpleDateFormat format) {
        if (format == null) {
            return null;
        }
        return format.format(new Date());
    }

    public static String nowToStringYYMM() {
        return nowToString(FORMAT_YY_MM);
    }

    public static String getSmsMonthRequestFormat(Calendar c) {
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        month++;
        return String.format("%d%02d", year, month);
    }

    public static String getSmsMonthShowFormat(Calendar c) {
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        month++;
        return String.format("%d - %d月", year, month);
    }

    public static String getRelativeDate(String str) {
        if (TextUtils.isEmpty(str))
            return "";
        try {
            Date date = FORMAT_FEED.parse(str);
            return getRelativeDate(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (str.endsWith(".0")) {
            return str.substring(0, str.length() - 2);
        }
        return str;
    }

    public static String getRelativeDate(Date date) {
        Date now = new Date();
        XiciApp app = XiciApp.getInstance();
        String sec = app.getString(R.string.created_at_beautify_sec);
        String min = app.getString(R.string.created_at_beautify_min);
        String hour = app.getString(R.string.created_at_beautify_hour);
        String day = app.getString(R.string.created_at_beautify_day);
        String suffix = app.getString(R.string.created_at_beautify_suffix);

        // seconds 
        long diff = (now.getTime() - date.getTime()) / 1000;

        if (diff < 0) {
            diff = 0;
        }

        if (diff < 60)
            return diff + sec + suffix;

        // minutes
        diff /= 60;
        if (diff < 60)
            return diff + min + suffix;

        // hours
        diff /= 60;
        if (diff < 24)
            return diff + hour + suffix;

        // days
        diff /= 24;
        if (diff < 7)
            return diff + day + suffix;

        diff /= 365;
        if (diff < 1)
            return FORMAT_MM_SS.format(date);

        return FORMAT_DEFAULT_ALL.format(date);
    }

    public static boolean timeEanble(String starttime, String endtime) {
        Date now = new Date();
        try {
            Date start = FORMAT_FEED.parse(starttime);
            if (start.after(now)) {
                return false;
            }

            Date end = FORMAT_FEED.parse(endtime);
            if (end.before(now)) {
                return false;
            }
            return true;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 时间比对
     *
     * @param DATE1
     * @param DATE2
     * @return
     */
    public static int compare_date(String DATE1, String DATE2) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date dt1 = df.parse(DATE1);
            Date dt2 = df.parse(DATE2);

            //dt1 在dt2后
            if (dt1.getTime() > dt2.getTime()) {
//                System.out.println("dt1 在dt2前");
                return 1;
            }
            //dt1在dt2之前
            else if (dt1.getTime() < dt2.getTime()) {
//                System.out.println("dt1在dt2后");
                return -1;
            } else {
                return 0;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return 0;
    }


    /**
     * 比对剩余时间
     *
     * @param DATE1
     * @param DATE2
     * @return
     */
    public static long[] returnLimtTime(String DATE1, String DATE2) {
        long[] time = null;
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date now = df.parse(DATE1);
            Date date = df.parse(DATE2);
            long l = now.getTime() - date.getTime();
            long day = l / (24 * 60 * 60 * 1000);
            long hour = (l / (60 * 60 * 1000) - day * 24);
            long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
            long s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
            time = new long[]{day, hour, min, s};

            Log.d("lyao", "" + day + "天" + hour + "小时" + min + "分" + s + "秒");
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return time;
    }


    public static String getNowTime() {
        return FORMAT_FEED.format(new Date());
    }


    /**
     * 是否是当天
     *
     * @param time
     * @return
     */
    public static boolean isToday(String time) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            Date dt1 = df.parse(time);
            Date dt2 = df.parse(getNowTime());


            //今天
            if (dt1.getTime() == dt2.getTime()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return false;
    }

    /**
     * 格式化时间同成圈活动中使用
     * 当天的显示时分
     * 当年的显示 月日时分
     * 其他全部显示
     *
     * @param time
     * @return
     */

    public static String formatDateTime_CityActivity(String time) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            Date dt1 = df.parse(time);
            Date dt2 = df.parse(getNowTime());


            //今天
            if (dt1.getTime() == dt2.getTime()) {

                Date dt3 = df2.parse(time);
                time = (new SimpleDateFormat("HH:mm")).format(dt3);
//                return time.split(" ")[1];
                return time;

            } else {
                //传入时间
                String year = time.split(" ")[0].substring(0, 4);
                String currentyear = getNowTime().split(" ")[0].substring(0, 4);

                //如果是今年 显示月份加时间
                if (TextUtils.equals(year, currentyear)) {
//                time = time.substring(5, time.length());
                    Date dt3 = df2.parse(time);
                    time = (new SimpleDateFormat("MM月dd日")).format(dt3);
                }


                return time;
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return time;


    }


    public static String formatDateTime(String time) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            Date dt1 = df.parse(time);
            Date dt2 = df.parse(getNowTime());


            //今天
            if (dt1.getTime() == dt2.getTime()) {

                Date dt3 = df2.parse(time);
                time = (new SimpleDateFormat("HH:mm")).format(dt3);
//                return time.split(" ")[1];
                return time;

            } else {
                //传入时间
                String year = time.split(" ")[0].substring(0, 4);
                String currentyear = getNowTime().split(" ")[0].substring(0, 4);

                //如果是今年 显示月份加时间
                if (TextUtils.equals(year, currentyear)) {
//                time = time.substring(5, time.length());
                    Date dt3 = df2.parse(time);
                    time = (new SimpleDateFormat("MM-dd HH:mm")).format(dt3);
                }


                return time;
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return time;


    }

    /**
     * 专门显示活动贴中的活动
     *
     * @param time
     * @return
     */
    public static String formatDateTime_FloorActivity(String time) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            Date dt1 = df.parse(time);
            Date dt2 = df.parse(getNowTime());


            //传入时间
            String year = time.split(" ")[0].substring(0, 4);
            String currentyear = getNowTime().split(" ")[0].substring(0, 4);

            //如果是今年 显示月份加时间
            if (TextUtils.equals(year, currentyear)) {
//                time = time.substring(5, time.length());
                Date dt3 = df2.parse(time);
                time = (new SimpleDateFormat("MM-dd HH:mm")).format(dt3);
            }


            return time;


        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return time;
    }

    /**
     * 专门为了显示活动创建页面的日期
     *
     * @param year
     * @param monthOfYear
     * @param dayOfMonth
     * @param dayOfWeek
     * @return
     */
    public static String formateDateForShow(int year, int monthOfYear, int dayOfMonth, int dayOfWeek) {
        String dateString = "";

        if (year % 100 < 10)
            dateString += "0" + year % 100;
        else
            dateString += year % 100;

        if (monthOfYear < 10)
            dateString += "/0" + monthOfYear;
        else
            dateString += "/" + monthOfYear;

        if (dayOfMonth < 10)
            dateString += "/0" + dayOfMonth;
        else
            dateString += "/" + dayOfMonth;

        switch (dayOfWeek) {
            case 1:
                dateString += "周日";
                break;
            case 2:
                dateString += "周一";
                break;
            case 3:
                dateString += "周二";
                break;
            case 4:
                dateString += "周三";
                break;
            case 5:
                dateString += "周四";
                break;
            case 6:
                dateString += "周五";
                break;
            case 7:
                dateString += "周六";
                break;
        }

        return dateString;
    }

    /**
     * 专门为了显示活动创建页面的时间
     *
     * @param hourOfDay
     * @param minute
     * @return
     */
    public static String formateTimeForShow(int hourOfDay, int minute) {
        String timeString = "";
        if (hourOfDay >= 12) {
            hourOfDay = hourOfDay % 12;
            timeString += "下午";
            if (hourOfDay < 10)
                timeString += "0" + hourOfDay;
            else
                timeString += hourOfDay;
        } else {
            timeString += "上午";
            if (hourOfDay < 10)
                timeString += "0" + hourOfDay;
            else
                timeString += hourOfDay;
        }

        if (minute < 10)
            timeString += ":0" + minute;
        else
            timeString += ":" + minute;

        return timeString + ":00";
    }


    /**
     * cms时间显示规则
     *
     * @param time
     * @return
     */
    public static String formatDateTime_CmsTime(String time) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            Date dt1 = df.parse(time);
            Date dt2 = df.parse(getNowTime());
            //今天
            if (dt1.getTime() == dt2.getTime()) {
                time = getRelativeDate(time);
                return time;
            } else {
                //传入时间
                String year = time.split(" ")[0].substring(0, 4);
                String currentyear = getNowTime().split(" ")[0].substring(0, 4);
                //如果是今年 显示月份加时间
                if (TextUtils.equals(year, currentyear)) {
//                time = time.substring(5, time.length());
                    Date dt3 = df2.parse(time);
                    time = (new SimpleDateFormat("MM月dd日 HH:mm")).format(dt3);
                }
                else
                {
                    time = (new SimpleDateFormat("yyyy年MM月dd日")).format(dt1);
                }
                return time;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return time;
    }

    /**
     * 加入西祠 时间显示规则
     *
     * @param time
     * @return
     */
    public static String formatDateTime_JoinXici(String time) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date dt1 = df.parse(time);
            time = (1900 + dt1.getYear()) + "年" + (dt1.getMonth() + 1) + "月加入西祠";
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return time;
    }


    /**
     * 给服务器的日期格式化
     *
     * @param year
     * @param monthOfYear
     * @param dayOfMonth
     * @return
     */
    public static String formatDateForPutActivity(int year, int monthOfYear, int dayOfMonth) {
        String dateString = year + "";
        if (monthOfYear < 10)
            dateString += "-0" + monthOfYear;
        else
            dateString += "-" + monthOfYear;
        if (dayOfMonth < 10)
            dateString += "-0" + dayOfMonth;
        else
            dateString += "-" + dayOfMonth;
        return dateString;
    }

    /**
     * 给服务器的时间格式化
     *
     * @param hourOfDay
     * @param minute
     * @return
     */
    public static String formatTimeForPutActivity(int hourOfDay, int minute) {
        String timeString = "";

        if (hourOfDay < 10)
            timeString += "0" + hourOfDay;
        else
            timeString += hourOfDay;

        if (minute < 10)
            timeString += ":0" + minute;
        else
            timeString += ":" + minute;

        timeString += ":00";

        return timeString;
    }



}
