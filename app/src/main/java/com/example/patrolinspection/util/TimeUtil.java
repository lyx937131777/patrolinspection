package com.example.patrolinspection.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil
{
    //后台给的日期格式 转化为日期Date
    public static Date stringToDate(String s){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
//        DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
//        df2.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date date = null;
        try {
            date = df.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
    //字符串+格式 转化为日期Date
    public static Date stringToDate(String s, String formatString){
        DateFormat df = new SimpleDateFormat(formatString);
        Date date = null;
        try {
            date = df.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    //后台给的日期格式 转化为目标的日期格式字符串
    public static String timeStampToString(String s, String formatString){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        DateFormat df2 = new SimpleDateFormat(formatString);
//        df2.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date date = null;
        try {
            date = df.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return df2.format(date);
    }

    //日期转化为目标的日期格式字符串
    public static String dateToString(Date date, String formatString){
        DateFormat df2 = new SimpleDateFormat(formatString);
//        df2.setTimeZone(TimeZone.getTimeZone("GMT"));
        return df2.format(date);
    }

    //时分秒转化成时分的格式
    public static String hmsToHm(String s){
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        DateFormat df2 = new SimpleDateFormat("HH:mm");
        Date date = null;
        try {
            date = df.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return df2.format(date);
    }
}
