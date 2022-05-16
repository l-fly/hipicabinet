package com.haipai.cabinet.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.SystemClock;

import com.haipai.cabinet.MyApplication;
import com.haipai.cabinet.entity.BatteryInfo;
import com.haipai.cabinet.manager.LocalDataManager;
import com.haipai.cabinet.manager.SerialManager;
import com.haipai.cabinet.ui.activity.MainActivity;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CustomMethodUtil {


    private static long startTimeMillis = 1650470400000L;//初始时间20220421

    /**
     * 获取系统开机时间，用来处理时间差
     * @return
     */
    public static long elapsedRealtime(){
        return SystemClock.elapsedRealtime() + startTimeMillis;
    }

    public static boolean isPortEmpty(int slot){
        return LocalDataManager.getInstance().isPortEmpty(slot);
    }
    public static void open(int port){
        //todo
        byte [] send = new byte[2];
        send[0] = (byte) 1;
        send[1] = (byte) 0;
        SerialManager.getInstance().send16(port +4,100,send);
    }
    public static boolean isOpen(int slot){
        return !LocalDataManager.getInstance().isPortClose(slot);
    }
    public static boolean isPortDisable(int slot){
        return LocalDataManager.isPortDisable(slot);
    }
    public static void setPortDisable(int port, boolean isDisable){
        LocalDataManager.setPortDisable(port,isDisable);
    }

    /**
     * 找空仓
     * @param start
     * @return
     */
    public static int getEnableEmptyPort(int start){
        int slot = -1;
        for (int i = start;i<LocalDataManager.slotNum;i++){
            if(LocalDataManager.getInstance().isPortEmpty(i) && !isPortDisable(i)){
                slot = i;
                break;
            }
        }
        return slot;
    }
    public static void sortBattery(List<BatteryInfo> tempList){
        synchronized (tempList) {
            Collections.sort(tempList, new Comparator<BatteryInfo>() {
                @Override
                public int compare(BatteryInfo o1, BatteryInfo o2) {
                    return o2.getSoc() - o1.getSoc();
                }
            });
        }
    }
    public static void restartApp(){
        try{
            LogUtil.f("restartApp");
            Thread.sleep(200);
            Intent intent = new Intent(MyApplication.getContext(), MainActivity.class);
            PendingIntent restartIntent = PendingIntent.getActivity(
                    MyApplication.getContext(), 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            //退出程序
            AlarmManager mgr = (AlarmManager)MyApplication.getContext().getSystemService(Context.ALARM_SERVICE);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 2000, restartIntent); // 1秒钟后重启应用   
            MyApplication.getContext().startActivity(new Intent(MyApplication.getContext(), MainActivity.class));
        }catch (Exception e){}
        android.os.Process.killProcess(android.os.Process.myPid());
    }
    public static void setAudioSet(int value){
        AudioManager audiomanage = (AudioManager) MyApplication.getContext().getSystemService(Context.AUDIO_SERVICE);
        audiomanage.setStreamVolume(AudioManager.STREAM_MUSIC, value, 0);
    }
    public static int getAudioSet(){
        AudioManager audiomanage = (AudioManager) MyApplication.getContext().getSystemService(Context.AUDIO_SERVICE);
        return audiomanage.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

}
