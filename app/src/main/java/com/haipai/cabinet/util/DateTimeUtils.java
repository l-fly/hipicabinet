package com.haipai.cabinet.util;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Ashion on 2016/6/17.
 */
public class DateTimeUtils {

    public static String defaultFormat = "yyyy-MM-dd HH:mm:ss";
    public static String dateFormat = "yyyy/MM/dd";
    public static String timeFormat = "HH:mm";

    private static SimpleDateFormat getSimpleDateFormat(final String format) {
        final SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf;
    }

    public static String getDateTimeString(String format, long timeInMs) {
        if (format == null){
            format = defaultFormat;
        }
        return getSimpleDateFormat(format).format(timeInMs);
    }

    public static String getDateString(long timeInMs) {
        return getSimpleDateFormat(dateFormat).format(timeInMs);
    }

    public static String getTimeString(long timeInMs) {
        return getSimpleDateFormat(timeFormat).format(timeInMs);
    }

    public static String formatDate(int year, int month, int day){
        String monthStr = String.valueOf(month);
        if (month < 10){
            monthStr = "0"+ monthStr;
        }
        String dayStr = String.valueOf(day);
        if (day < 10){
            dayStr = "0"+ dayStr;
        }
        return String.valueOf(year) +"-"+ monthStr + "-" + dayStr;
    }

    public static String secondToTimeString(int second, int divider){
        StringBuffer result = new StringBuffer();
        int h = second/3600;
        int m = (second - h * 3600)/60;
        int s = (second - h * 3600)%60;
        if (h > 0){
            /*if (h<10) {
                result.append("0");
            }*/
            result.append(h);
            if (divider == 1) {
                result.append("h");
            }
        }
        if (m > 0){
            if (divider == 0 && h > 0){
                result.append(":");
            }
            /*if (m < 10){
                result.append("0");
            }*/
            result.append(m);
            if (divider == 1) {
                result.append("m");
            }
        }
        if (s > 0){
            if (divider == 0){
                if (h == 0 && m == 0){
                    result.append("0");
                }
                result.append(":");
            }
            if (s < 10) {
                result.append("0");
            }
            result.append(s);
            if (divider == 1) {
                result.append("s");
            }
        }
        return result.toString();
    }

    public static String secondToShortTime(int second){
        StringBuffer result = new StringBuffer();
        int h = second/3600;
        int m = (second - h * 3600)/60;
        int s = (second - h * 3600)%60;

        if (h<10) {
            result.append("0");
        }
        result.append(h);
        result.append(":");
        if (m > 0){
            if (m < 10){
                result.append("0");
            }
            result.append(m);
        }
        return result.toString();
    }

    public static String secondToTimeString(int second){
        StringBuffer result = new StringBuffer();
        DecimalFormat decimalFormat=new DecimalFormat(".0");
        int h = second/3600;
        int m = (second - h * 3600)/60;
        int s = (second - h * 3600)%60;
        if (h > 0){
            if (m == 0){
                result.append(h);
            }else {
                float min = (m/ 60.0f);
                result.append(decimalFormat.format(h + min));
            }
            result.append("h");
        }else if (m > 0){
            if (s == 0){
                result.append(m);
            }else {
                float sec = (s/ 60.0f);
                result.append(decimalFormat.format(m + sec));
            }
            result.append("m");
        }else {
            float sec = (s/ 60.0f);
            result.append(decimalFormat.format(sec));
            result.append("m");
        }

        return result.toString();
    }



    // strTime????????????String???????????????
    // formatType????????????
    // strTime??????????????????formatType???????????????????????????
    public static long stringToLong(String strTime, String formatType){
        Date date = null; // String????????????date??????
        try {
            date = stringToDate(strTime, formatType);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date == null) {
            return 0;
        } else {
            long currentTime = dateToLong(date); // date????????????long??????
            return currentTime;
        }
    }

    // strTime????????????string??????????????????formatType??????????????????yyyy-MM-dd HH:mm:ss//yyyy???MM???dd???
    // HH???mm???ss??????
    // strTime???????????????????????????formatType?????????????????????
    public static Date stringToDate(String strTime, String formatType)
            throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(formatType);
        Date date = null;
        date = formatter.parse(strTime);
        return date;
    }

    // date????????????date???????????????
    public static long dateToLong(Date date) {
        return date.getTime();
    }





    public static String createGmtOffsetString() {
        long cur = System.currentTimeMillis();
        SimpleDateFormat formatter=new SimpleDateFormat("HH");
        Date curDate=new Date(cur);//????????????????????????????????
        String str = formatter.format(curDate);
        int hourStd = (int)((cur % (3600 * 1000 * 24))/(3600 * 1000));   //??????????????????
        int hourLoc = Integer.parseInt(str);       //????????????????????????
        int delta = hourLoc - hourStd;   //????????????
        if(delta < -12){
            delta += 24;
        }else if(delta > 12){
            delta -= 24;
        }
        return Integer.toString(delta);
//        TimeZone tz = TimeZone.getDefault();
//        int offsetMillis = tz.getRawOffset();
//        int offsetMinutes = offsetMillis / 60000;
//        char sign;
//        StringBuilder builder = new StringBuilder(9);
//        if (offsetMinutes < 0) {
//            sign = '-';
//            offsetMinutes = -offsetMinutes;
//            builder.append(sign);
//        }
//        builder.append(offsetMinutes / 60);
//
//        return builder.toString();
    }


//    public static void main(String[] args){
//        /*String dateStr = "2016-10-19";
//        long result = stringToLong(dateStr, dateFormat);*/
//        String result = createGmtOffsetString();
//        int zoneInt = Integer.parseInt(result);
//        System.out.println("xxx="+zoneInt);
//
//    }
    public static boolean isInChina(){
        TimeZone timeZone = TimeZone.getDefault();
        String timeZoneID = timeZone.getID();
        if (timeZoneID != null && (timeZoneID.toLowerCase().contains("shanghai"))||
                (timeZoneID.toLowerCase().contains("beijing"))){
            return true;
        }else{
            return false;
        }
    }

}
