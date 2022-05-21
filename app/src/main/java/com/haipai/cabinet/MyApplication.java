package com.haipai.cabinet;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.WindowManager;

import com.haipai.cabinet.entity.BatteryInfo;
import com.haipai.cabinet.manager.LocalDataManager;
import com.haipai.cabinet.manager.ReportManager;
import com.haipai.cabinet.manager.TcpManager;
import com.haipai.cabinet.manager.UpgradeManager;
import com.haipai.cabinet.manager.WIFIManager;
import com.haipai.cabinet.serialUtil.SerialPortUtil;
import com.haipai.cabinet.util.CustomCrashHandler;
import com.haipai.cabinet.util.CustomMethodUtil;
import com.haipai.cabinet.util.DateTimeUtils;
import com.haipai.cabinet.util.LogUtil;
import com.haipai.cabinet.util.PreferencesUtil;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MyApplication extends Application {
    public static int SCREEN_TYPE = -1;
    public static final int SCREEN_TYPE_MAICHONG_7CUN = 1;    //脉冲7寸屏
    public static final int SCREEN_TYPE_DERRUI_7CUN = 2;    //德睿7寸屏

    public interface SecondCheck{
        void onPassSecond();
    }
    private static final int HANDLER_TIME_LOOP = 1000;   //时间轮询

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {

        @Override
        public void dispatchMessage(Message msg) {
            if(msg.what == HANDLER_TIME_LOOP) {
                if (hasMessages(HANDLER_TIME_LOOP)) {
                    removeMessages(HANDLER_TIME_LOOP);
                }
               // LogUtil.i("TIME_LOOP" + "  application " );
                synchronized (mSecondCheckList){
                    for(SecondCheck check:mSecondCheckList){
                        try{
                            check.onPassSecond();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
                try{
                    if(timer != null && (CustomMethodUtil.elapsedRealtime() - lastTimeTaskRun > 10000 || CustomMethodUtil.elapsedRealtime()  < lastTimeTaskRun-5000)){
                        if(timerTask!=null){
                            timerTask.cancel();
                            timerTask = null;
                        }
                        timer.cancel();
                        timer = null;
                    }
                    if(timer==null){
                        timer = new Timer("mainTimer");
                        timerTask = new MyTimerTask();
                        timer.schedule(timerTask,0,1000);
                    }
                }catch (Exception e){
                    LogUtil.d(e.getMessage());
                }
                sendEmptyMessageDelayed(HANDLER_TIME_LOOP,1000);
            }
        }
    };
    private long lastTimeTaskRun = 0;
    private Timer timer = null;
    private MyTimerTask timerTask = null;
    private int timerTick = 0;

    class MyTimerTask extends TimerTask {
        public void run() {
            long now = CustomMethodUtil.elapsedRealtime();
            lastTimeTaskRun = now;
            timerTick ++;
            if(timerTick > 14400){
                timerTick = 0;
            }

            LocalDataManager.getInstance().onPassSecond();
            TcpManager.getInstance().onPassSecond();

            LogUtil.i("#######initStatus  "  + LocalDataManager.initStatus);
            if (timerTick % 5 == 0) {
                if(LocalDataManager.initStatus == 1){
                    ReportManager.login();
                }
            }
            if(timerTick % 60 ==0){
                if(LocalDataManager.initStatus ==2){
                    ReportManager.messageAllReport();
                }

            }



        }
    }
    List<SecondCheck> mSecondCheckList = new ArrayList<>();

    public void setSecondCheck(SecondCheck check){
        synchronized (mSecondCheckList){
            if(!mSecondCheckList.contains(check)){
                mSecondCheckList.add(check);
            }
        }
    }

    public void removeSecondCheck(SecondCheck check){
        synchronized (mSecondCheckList){
            if(mSecondCheckList.contains(check)){
                mSecondCheckList.remove(check);
            }
        }
    }
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        CustomCrashHandler mCustomCrashHandler = CustomCrashHandler.getInstance();
        mCustomCrashHandler.setCustomCrashHanler(mContext);
        // 延迟5秒启动时间轮训
        if(!mHandler.hasMessages(HANDLER_TIME_LOOP)) {
            mHandler.sendEmptyMessageDelayed(HANDLER_TIME_LOOP, 5000);
        }
        mTextToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    LogUtil.d("tts init success");
                    //设置朗读语言
                    int supported = mTextToSpeech.setLanguage(Locale.CHINESE);
                    if ((supported != TextToSpeech.LANG_AVAILABLE) && (supported != TextToSpeech.LANG_COUNTRY_AVAILABLE)) {
                        LogUtil.d("tts not support " + supported);
                        mTextToSpeech = null;
                    }
                } else {
                    LogUtil.d("tts init fail");
                    mTextToSpeech = null;
                }

            }
        });
        String[] strings = new String[]{
                MyConfiguration.UPDATE_APK_PATH,
                MyConfiguration.UPDATE_CTRL_PATH,
                MyConfiguration.UPDATE_PMS_PATH,
                MyConfiguration.UPDATE_CHARGE_PATH,
        };
        for(int i = 0; i < strings.length;i++) {
            try {
                File dir = new File(strings[i]);
                if(!dir.exists()){
                    dir.mkdirs();
                }
            } catch (Exception e) {
            }
        }
        UpgradeManager.getInstance().getApkVersion(this);
        checkDisableSlot();
        checkICCID();

        WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        LogUtil.d("MODEL:"+ Build.MODEL+", BRAND:"+Build.BRAND+", DEVICE:"+Build.DEVICE+", MANUFACTURER:"+Build.MANUFACTURER+", PRODUCT:"+Build.PRODUCT+", width="+width+", height="+height);

        String serialPath ;
        if(Build.MODEL.equalsIgnoreCase("MC-Android") && Build.BRAND.equalsIgnoreCase("Android")
                && Build.DEVICE.equalsIgnoreCase("rk3288_box") && Build.MANUFACTURER.equalsIgnoreCase("rockchip") ){
            SCREEN_TYPE = SCREEN_TYPE_MAICHONG_7CUN;
            serialPath = "/dev/ttyS3";

        }else if(Build.MODEL.equalsIgnoreCase("DRCC_T10") && Build.BRAND.equalsIgnoreCase("Allwinner")
                && Build.DEVICE.equalsIgnoreCase("a40-p1") && Build.MANUFACTURER.equalsIgnoreCase("Allwinner")){
            SCREEN_TYPE = SCREEN_TYPE_DERRUI_7CUN;
            serialPath = "/dev/ttyS2";
        }else {
            serialPath = "/dev/ttyS3";
        }
        int baudrate = 115200;

        SerialPortUtil.getInstance().onCreate(serialPath,baudrate);

        TcpManager.getInstance().connect();


    }

    private void checkDisableSlot(){
        for (int i = 0;i<LocalDataManager.MAX_STATUS_NUM;i++){
            LocalDataManager.disableSlot[i] = PreferencesUtil.getInstance(this).getSlotDisable(i);
        }
    }
    public void checkICCID(){
        try {
            TelephonyManager tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            if(tel.getSimSerialNumber()!=null){
                LocalDataManager.ccid = tel.getSimSerialNumber();
            }
            if(tel.getDeviceId()!=null){
                LocalDataManager.imei = tel.getDeviceId();
            }
            if(tel.getSubscriberId()!=null){
                LocalDataManager.imsi = tel.getSubscriberId();
            }
            LogUtil.d("sim ccid=" + tel.getSimSerialNumber() + ", imei=" + tel.getDeviceId() + ", imsi=" + tel.getSubscriberId());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if(mHandler.hasMessages(HANDLER_TIME_LOOP)){
            mHandler.removeMessages(HANDLER_TIME_LOOP);
        }
        if(mTextToSpeech!=null){
            mTextToSpeech.shutdown();
            mTextToSpeech = null;
        }
    }
    TextToSpeech mTextToSpeech = null;
    public boolean speak(String msg) {
        if (mTextToSpeech != null) {
            LogUtil.d("tts speak " + msg);
            mTextToSpeech.speak(msg, TextToSpeech.QUEUE_FLUSH, null, null);
            return true;
        }
        return false;
    }
    public String getRealTimeInfo() {//本机信息
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("now:").append(DateTimeUtils.getDateTimeString(null,System.currentTimeMillis())).append("\n");
        stringBuffer.append("APK version").append(":").append(UpgradeManager.getInstance().apkVerInstalled).append("\n");
        stringBuffer.append("APK auto-upgrade :").append(UpgradeManager.getInstance().isAvilibleApk(this,"com.haipai.appupgradehelper")).append("\n");
        stringBuffer.append("DevId").append(":").append(LocalDataManager.devId).append("\n");
        stringBuffer.append("emptySlotNum").append(":").append(LocalDataManager.getEmptyNum()).append("\n");

        stringBuffer.append("doorStatus").append(":");
        for (int i=0;i<LocalDataManager.slotNum;i++){
            stringBuffer.append(i+1).append("-").append(CustomMethodUtil.isOpen(i)?"open; " :"close; ");
        }
        stringBuffer.append("\n");
        stringBuffer.append("\n");
        stringBuffer.append("\nNormal:\n");
        List<BatteryInfo> list = LocalDataManager.getInstance().getBatteriesClone();
        if(list!=null && list.size()!=0) {
            synchronized (list) {
                for (BatteryInfo info : list) {
                    stringBuffer.append(batteryDescribe(info));
                }
            }
        }
        stringBuffer.append("\n");
        stringBuffer.append("\nExtra(Slot is Open or Disable):\n");
        List<BatteryInfo> extralist = LocalDataManager.getInstance().getExtraBatteriesClone();
        if(extralist!=null && extralist.size()!=0) {
            synchronized (extralist) {
                for (BatteryInfo info : extralist) {
                    stringBuffer.append(batteryDescribe(info));
                }
            }
        }
        stringBuffer.append("\n");

        return stringBuffer.toString();
    }
    private String batteryDescribe(BatteryInfo info){
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer
                .append("port").append(":").append(info.getPort()+1)
               /* .append("id").append(":").append(info.getpId())*/
                .append(", sn:").append(info.getSn())
                .append(", fault:").append(info.getFault())
                .append(", current").append(":").append(info.getCurrent())
                .append(", voltage").append(":").append(info.getVoltage())
                .append(", Soc").append(":").append(info.getSoc())
                .append(", Cycle").append(":").append(info.getCycle())
                .append(", residualmAh").append(":").append(info.getResidualmAh());
        stringBuffer.append("\n");
        return stringBuffer.toString();
    }
    private static Context mContext;
    public static Context getContext() {
        return mContext;
    }
}
