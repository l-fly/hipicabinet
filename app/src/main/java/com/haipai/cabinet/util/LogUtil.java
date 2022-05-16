package com.haipai.cabinet.util;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;


/**
 * Log工具，类似android.util.Log。
 * tag自动产生，格式: customTagPrefix:className.methodName(L:lineNumber),
 * customTagPrefix为空时只输出：className.methodName(L:lineNumber)。
 * Author: wyouflf
 * Date: 13-7-24
 * Time: 下午12:23
 */
public class LogUtil {

    private LogUtil() {
    }

    private static String generateTag() {
        StackTraceElement caller = new Throwable().getStackTrace()[2];
        String callerClazzName = caller.getClassName();
        callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1);
        return callerClazzName;
    }

    private static String generatePrefix() {
        StackTraceElement caller = new Throwable().getStackTrace()[2];
        String prefix = "%s(L:%d)";
        String callerClazzName = caller.getClassName();
        callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1);
        prefix = String.format(prefix, caller.getMethodName(), caller.getLineNumber());
        return prefix;
    }

    public static void d() {
        String tag = generateTag();
        String prefix = generatePrefix();

        Log.d(tag, prefix);
    }

    public static void d(String content) {
        String tag = generateTag();
        String prefix = generatePrefix();

        Log.d(tag, prefix + " " + content);
    }

    public static void d(String content, Throwable tr) {
        String tag = generateTag();
        String prefix = generatePrefix();

        Log.d(tag, prefix + " " + content, tr);
    }

    public static void e(String content) {
        String tag = generateTag();
        String prefix = generatePrefix();

        Log.e(tag, prefix + " " + content);
    }

    public static void e(String content, Throwable tr) {
        String tag = generateTag();
        String prefix = generatePrefix();

        Log.e(tag, prefix + " " + content, tr);
    }

    public static void i(String content) {
        String tag = generateTag();
        String prefix = generatePrefix();

        Log.i(tag, prefix + " " + content);
    }

    public static void i(String content, Throwable tr) {
        String tag = generateTag();
        String prefix = generatePrefix();

        Log.i(tag, prefix + " " + content, tr);
    }

    public static void v(String content) {
        String tag = generateTag();
        String prefix = generatePrefix();

        Log.v(tag, prefix + " " + content);
    }

    public static void v(String content, Throwable tr) {
        String tag = generateTag();
        String prefix = generatePrefix();

        Log.v(tag, prefix + " " + content, tr);
    }

    public static void w(String content) {
        String tag = generateTag();
        String prefix = generatePrefix();

        Log.w(tag, prefix + " " + content);
    }

    public static void w(String content, Throwable tr) {
        String tag = generateTag();
        String prefix = generatePrefix();

        Log.w(tag, prefix + " " + content, tr);
    }

    public static void w(Throwable tr) {
        String tag = generateTag();

        Log.w(tag, tr);
    }


    public static void wtf(String content) {
        String tag = generateTag();
        String prefix = generatePrefix();

        Log.wtf(tag, prefix + " " + content);
    }

    public static void wtf(String content, Throwable tr) {
        String tag = generateTag();
        String prefix = generatePrefix();

        Log.wtf(tag, prefix + " " + content, tr);
    }

    public static void wtf(Throwable tr) {
        String tag = generateTag();

        Log.wtf(tag, tr);
    }

    /**
     * 写文件 20201021.txt
     *
     * @param content 文件内容
     */
    public static final String APP_FILE_PATH = Environment.getExternalStorageDirectory().getPath()
            + File.separator + "haipai"+ File.separator;//sdcard 路径
    public static void f(String content) {
        i(content);
        try {
            String directoryName = APP_FILE_PATH + "log";
            File directoryFile = new File(directoryName);
            if (!directoryFile.exists()) {
                directoryFile.mkdirs();
            }
            String logName = directoryName + File.separator + DateTimeUtils.getDateTimeString("yyyyMMdd", System.currentTimeMillis()) + ".txt";
            File logFile = new File(logName);
            if (logFile == null) {
                logFile.mkdir();
            }
            FileOutputStream stream = new FileOutputStream(logFile, true);
            String s = DateTimeUtils.getDateTimeString(null, System.currentTimeMillis()) + ": " + content + "\r\n";
            stream.write(s.getBytes());
            stream.flush();
            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 固件信息路径 /mnt/internal_sd/BatteryStation/deviceInfo/20201021.txt
     * 电池心跳路径 /mnt/internal_sd/BatteryStation/heartBeat/20201021.txt
     * 按照系统时间每天生成一个对应文件
     * @param dirName 文件夹
     * @param content 文件内容
     */
   /* public static void writeDeviceInfo(String dirName, String content) {
        try {
            String directoryName = MyConfiguration.APP_FILE_PATH + dirName;
            File directoryFile = new File(directoryName);
            if (!directoryFile.exists()) {
                directoryFile.mkdirs();
            }
            String logName = directoryName + File.separator + DateTimeUtils.getDateTimeString("yyyyMMdd", System.currentTimeMillis()) + ".txt";
            File logFile = new File(logName);
            if (logFile == null) {
                logFile.mkdir();
            }
            FileOutputStream stream = new FileOutputStream(logFile, true);
            String s = DateTimeUtils.getDateTimeString(null, System.currentTimeMillis()) + ": " + content + "\r\n";
            stream.write(s.getBytes());
            stream.flush();
            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/



}

