package com.haipai.cabinet.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferencesUtil {
    private static final String KEY_SLOT_DISABLE = "KEY_SLOT_DISABLE";
    private static final String KEY_SWITCH_IP = "KEY_SWITCH_IP";
    private static final String KEY_SERVER_IP = "KEY_SERVER_IP";
    private static final String KEY_SERVER_PORT = "KEY_SERVER_PORT";
    private static PreferencesUtil mSharedPreferencesManager;
    SharedPreferences prefs;
    SharedPreferences.Editor prefsEditor;
    private PreferencesUtil(Context context) {
        this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
        this.prefsEditor = prefs.edit();
    }
    public static PreferencesUtil getInstance(Context context) {
        if (mSharedPreferencesManager == null) {
            synchronized (PreferencesUtil.class) {
                if (mSharedPreferencesManager == null) {
                    mSharedPreferencesManager = new PreferencesUtil(context);
                }
            }
        }
        return mSharedPreferencesManager;
    }
    public static PreferencesUtil getInstance() {
        return mSharedPreferencesManager;
    }
    public void setSlotDisable(int position,boolean value){
        prefsEditor.putBoolean(KEY_SLOT_DISABLE + position,value).apply();
    }
    public boolean getSlotDisable(int position){
        return prefs.getBoolean(KEY_SLOT_DISABLE + position,false);
    }

    /**
     * 设置ip , 1-海湃测试环境，2-海湃正式环境
     *         11-湃能
     *         21-佳佰达
     *         500-远程下发的地址，此时需要从preferences中获取
     * @param ipType
     */
    public void setSwitchIp(int ipType){
        prefsEditor.putInt(KEY_SWITCH_IP,ipType).apply();
    }
    public int getSwitchIp(){
        return prefs.getInt(KEY_SWITCH_IP ,1);
    }

    /**
     * 保存远程下发的ip
     * @param ip
     */
    public void setServerIp(String ip){
        prefsEditor.putString(KEY_SERVER_IP,ip).apply();
    }

    public String getServerIp(){
        return prefs.getString(KEY_SERVER_IP,"192.168.0.22");
    }

    /**
     * 保存远程下发的端口
     * @param port
     */
    public void setServerPort(int port){
        prefsEditor.putInt(KEY_SERVER_PORT,port).apply();
    }

    public int getServerPort(){
        return prefs.getInt(KEY_SERVER_PORT,9988);
    }
}
