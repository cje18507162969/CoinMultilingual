package com.coin.market.util;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by LiYang on 2018/10/25.
 */

public class DateUtils {


    public static String getTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// HH:mm:ss
        //获取当前时间
        Date date = new Date(System.currentTimeMillis());
        return simpleDateFormat.format(date);
    }

    /**
     *   Date转成时间格式的String
     */
    public static String getTime(Date date) {//可根据需要自行截取数据显示
        Log.d("getTime()", "choice date millis: " + date.getTime());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        return format.format(date);
    }

    /**
     *   String 类型日期   转格式 yyyy-MM-dd HH:mm:ss - yyyy-MM-dd
     */
    public static String StringToDate(String time) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date;
        date = format.parse(time);
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        String s = format1.format(date);
        return s;
    }


    /**
     *   算秒数
     */
    public static long dayDiff3(String date1,String format) {
        SimpleDateFormat formater = new SimpleDateFormat(format);
        long diff = 0l;
        try {
            diff = formater.parse(date1).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return diff;
    }

    /**
     * @param date1  字符串日期1
     * @param date2  字符串日期2
     * @param format 日期格式化方式  format="yyyy-MM-dd"
     * @return
     * @descript:计算两个字符串日期相差的时间 （秒数）
     */
    public static long dayDiff2(String date1, String date2, String format) {
        SimpleDateFormat formater = new SimpleDateFormat(format);
        long diff = 0l;
        try {
            long d1 = formater.parse(date1).getTime();
            long d2 = formater.parse(date2).getTime();
            diff = (d1 - d2) / (1000);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return diff;
    }

    /*
    *计算time2减去time1的差值 差值只设置 几天 几个小时 或 几分钟
    * 根据差值返回多长之间前或多长时间后
    * */
    public static String getDistanceTime(long  time1,long time2 ) {
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;
        long diff ;
        String flag;
        if(time1<time2) {
            diff = time2 - time1;
            flag="";
        } else {
            diff = time1 - time2;
            flag="";
        }
        day = diff / (24 * 60 * 60 * 1000);
        hour = (diff / (60 * 60 * 1000) - day * 24);
        min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);

        sec = (diff/1000-day*24*60*60-hour*60*60-min*60);
//        if(day!=0)return day+""+flag;
//        if(hour!=0)return hour+""+flag;
//        if(min!=0)return min+""+flag;

        if(day!=0)return (10-day)+"天";
        if(hour!=0)return "9天";
        if(min!=0)return min+""+flag;

        return "";
    }

    // 时间戳 转换 String  年月日时分秒  yyyy-MM-ddHH:mm
    public static String longtoString(long s) {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(s);
        return formatter.format(calendar.getTime());
    }

    // 时间戳 转换 String  年月日时分秒  yyyy-MM-ddHH:mm
    public static String longToString(long s) {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(s);
        return formatter.format(calendar.getTime());
    }

    // 判断是否为时间戳  如果不是时间戳那么直接显示   如果为时间戳转换为 String的 yyyy-MM-dd 时间显示
    public static String longToStringX(long s) {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(s);
        return formatter.format(calendar.getTime());
    }

    public static String getDateMore(String str, String str2, int day){
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = null;
        int days = 0;
        int rDate;
        try {
            date1 = (Date) sd.parse(str2);
            Date date2 = (Date) sd.parse(str);
            days = (int) ((date1.getTime() - date2.getTime()) / (1000*3600*24));

        } catch (ParseException e) {
            e.printStackTrace();
        }
        rDate = 10-days;
        rDate = rDate-day;
        if (rDate==10){
            return "9天";
        } else if (rDate<10 && rDate>=0){
            return (rDate)+"天";
        }else {
            return "";
        }
    }

    public static String getDateMore2(String str, String str2){
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = null;
        int days = 0;
        int rDate;
        try {
            date1 = (Date) sd.parse(str2);
            Date date2 = (Date) sd.parse(str);
            days = (int) ((date1.getTime() - date2.getTime()) / (1000*3600*24));

        } catch (ParseException e) {
            e.printStackTrace();
        }
        rDate = 10-days;
        if (rDate==10){
            return "9天";
        } else if (rDate<10 && rDate>=0){
            return (rDate)+"天";
        }else {
            return "";
        }
    }

//    public static String compareDate(String date1, String date2){
//        DateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
//        try {
//            Date d1 = dateFormat.parse(date1);
//            Date d2 = dateFormat.parse(date2);
//            if(d1.equals(d2)){
//                System.out.println(date1+"="+date2);
//            }else if(d1.before(d2)){
//                System.out.println(date1+"在"+date2+"之前");
//            }else if(d1.after(d2)){
//                System.out.println(date1+"在"+date2+"之后");
//            }
//        } catch (ParseException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }

    /**
     *   对比两个时间戳 相差多少秒
     */
    public static long getDistanceTimeSS(long time1,long time2 ) {
        long sec = 0;
        long diff ;
        if(time1<time2) {
            diff = time2 - time1;
        } else {
            diff = time1 - time2;
        }
        sec = (diff/1000);
        if (sec>200){
            sec = 0;
        }
        //Log.e("cjh>>>Time：","diff:"+ diff +  "sec:" + sec);
        return sec;
    }


    /**
     * @descript:计算两个字符串日期相差的天数
     * @param date1 字符串日期1
     * @param date2 字符串日期2
     * @param format 日期格式化方式  format="yyyy-MM-dd"
     * @return
     */
    public static long dayDiff(String date1, String date2,String format) {
        SimpleDateFormat formater = new SimpleDateFormat(format);
        long diff=0l;
        try {
            long d1 = formater.parse(date1).getTime();
            long d2 = formater.parse(date2).getTime();
            //diff=(Math.abs(d1-d2) / (1000 * 60 * 60 * 24));
            diff=(d1-d2)/(1000 * 60 * 60 * 24);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return diff;
    }

}
