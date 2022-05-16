package com.haipai.cabinet.broadcast;

import android.app.ActivityManager;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;


import com.haipai.cabinet.ui.activity.MainActivity;
import com.haipai.cabinet.util.LogUtil;

import java.util.List;

public class StartReceiver extends BroadcastReceiver {
    private final String startAction = Intent.ACTION_BOOT_COMPLETED;
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(startAction)){
            String mainStr="com.haipai.cabinet.activity.MainActivity";
            if(!isForeground(context, mainStr)) {
                Intent myIntent = new Intent(context, MainActivity.class);
                myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(myIntent);
            }
        }
    }
    public static boolean isForeground(Context context, String className) {
        if (context == null || TextUtils.isEmpty(className)) {
            return false;
        }

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
            if (className.equals(cpn.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
