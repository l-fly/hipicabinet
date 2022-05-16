package com.haipai.cabinet.manager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.haipai.cabinet.MyApplication;
import com.haipai.cabinet.util.LogUtil;

public class WIFIManager {
    public static String TAG = "WIFIManager";
    private static Context context;
    public static TelephonyManager telephonyManager = null;
    public static int mRssi4G = 25;
    private static Handler handler = new Handler();

    public static void init(Context contex) {
        context = contex;
        telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        int simState = telephonyManager.getSimState();
        LogUtil.i("当前的SIM卡状态:" + simState);
        switch (simState) {
            case TelephonyManager.SIM_STATE_ABSENT://无sim卡
                handler.postDelayed(new WifiRunnble(), 10000);
                break;
            case TelephonyManager.SIM_STATE_NETWORK_LOCKED://无需NetWorkPIN解锁
                handler.postDelayed(new WifiRunnble(), 10000);
                break;
            case TelephonyManager.SIM_STATE_PIN_REQUIRED://需要PIN解锁
                handler.postDelayed(new WifiRunnble(), 10000);
                break;
            case TelephonyManager.SIM_STATE_PUK_REQUIRED://需要PUK解锁
                handler.postDelayed(new WifiRunnble(), 10000);
                break;
            case TelephonyManager.SIM_STATE_READY://良好
                getDataNetworkRssi();
                break;
            case TelephonyManager.SIM_STATE_UNKNOWN://未知状态
                handler.postDelayed(new WifiRunnble(), 10000);
                getWifiRssi();
                break;
        }
    }

    /**
     * 定时获取信号强度
     */
    static class WifiRunnble implements Runnable {
        @Override
        public void run() {
            getWifiRssi();
            handler.postDelayed(this, 20 * 1000);//10秒获取一次WIFI的信号强度
        }
    }

    /**
     * 获取数据网络信号强度
     */
    public static void getDataNetworkRssi() {
        if (telephonyManager != null) {
            @SuppressLint("MissingPermission")
            final int type = telephonyManager.getNetworkType();//获取网络类型联通3g,移动3g,电信3g...
            PhoneStateListener phoneStateListener = new PhoneStateListener() {
                @Override
                public void onSignalStrengthsChanged(SignalStrength signalStrength) {
                    super.onSignalStrengthsChanged(signalStrength);
                    String s = signalStrength.toString();//获取到的信号强度数据是以空格分开的
                    String[] split = s.split("\\s+");
                    int strength = 0;
                    int i=0;
                    for (String a : split){
                        i++;
                        if(i == 10){
                            //当获取到信号强度数据的第10个值的时候为信号强度值，自己可将每个值都打印出来看看
                            strength = Integer.parseInt(a);
                        }
                    }
                    mRssi4G = strength == mRssi4G ? mRssi4G : strength;
                    LogUtil.i("#####当前网络类型:" + type + ",信号强度:" + strength + "dBm");
                }
            };
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);//监听网络信号事件
        }
    }

    /**
     * 获取当前wifi信号强度(-50~0:信号较好,-70~-50:信号较差,-100~-50:信号最差)
     */

    public static void getWifiRssi() {
        @SuppressLint("WifiManagerLeak") WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        @SuppressLint("MissingPermission") WifiInfo info = wifiManager.getConnectionInfo();
        mRssi4G = info.getRssi();
        LogUtil.i( "#####wifi信号强度:" + mRssi4G);
    }

    public static boolean hasSimCard(Context context) {
        TelephonyManager telMgr = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
        int simState = telMgr.getSimState();
        boolean result = true;
        switch (simState) {
            case TelephonyManager.SIM_STATE_ABSENT:
            case TelephonyManager.SIM_STATE_UNKNOWN:
                result = false; // 没有SIM卡
                break;
        }
        return result;
    }

    public static void getMobileNetworkSignal(Context context) {
        if (!hasSimCard(context)) {
           // LogUtil.i("#### getMobileNetworkSignal: no sim card");
            return;
        }
        TelephonyManager mTelephonyManager = (TelephonyManager) MyApplication.getContext().getSystemService(Context.TELEPHONY_SERVICE);
        if (mTelephonyManager != null) {
            mTelephonyManager.listen(new PhoneStateListener() {

                @Override
                public void onSignalStrengthsChanged(SignalStrength signalStrength) {
                    super.onSignalStrengthsChanged(signalStrength);
                    int asu = signalStrength.getGsmSignalStrength();
                    int lastSignal = -113 + 2 * asu;
                    LocalDataManager.dbm = lastSignal;
                   /* if (lastSignal > 0) {
                        mobileNetworkSignal = lastSignal + "dBm";
                    }*/
                   // LogUtil.i("####  Current mobileNetworkSignal：" + lastSignal + " dBm");
                }
            }, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        }
    }
}
