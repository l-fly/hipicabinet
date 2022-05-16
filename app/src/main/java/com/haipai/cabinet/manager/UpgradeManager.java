package com.haipai.cabinet.manager;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

import com.haipai.cabinet.MyConfiguration;
import com.haipai.cabinet.util.LogUtil;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;

public class UpgradeManager {
    private static final boolean IS_UPDATE_LOG = true;

    public static int update_status = 0;    //0表示没在升级，1表示APK升级,2表示CTRL升级，3表示PMS升级,4表示CHARGE升级

    public int apkVerInstalled = -1;     //已安装版本
    public int apkVerInFile = -1;        //本地文件最新版本
    public String apkVerName = "";
    public String lastApkPath = "";
    private static UpgradeManager instance;
    private UpgradeManager() {

    }
    public static UpgradeManager getInstance() {
        if (instance == null) {
            instance = new UpgradeManager();
        }
        return instance;
    }
    public void upgradeApk(Context context) {
        if(apkVerInFile > apkVerInstalled && !lastApkPath.isEmpty()){
            update_status = 1;
            Intent intent = new Intent();
            intent.setAction("com.haipai.cabinet.update");
            intent.putExtra("file",lastApkPath);
            context.sendBroadcast(intent);
        }
    }
    public boolean isAvilibleApk(Context context, String packageName ) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        for ( int i = 0; i < pinfo.size(); i++ ) {
            if(pinfo.get(i).packageName.equalsIgnoreCase(packageName))
                return true;
        }
        return false;
    }
    public void getApkVersion(Context context){
        if(apkVerInstalled <= 0){
            try{
                PackageManager manager = context.getPackageManager();
                PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
                apkVerInstalled = info.versionCode;
                apkVerName = info.versionName;
                if(IS_UPDATE_LOG)
                    LogUtil.d("apk update apkVerInstall = "+apkVerInstalled);
            }catch (Exception e){}
        }

        String[] fileList = (new File(MyConfiguration.UPDATE_APK_PATH)).list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return (name.endsWith(".apk"));
            }
        });
        if(fileList!=null && fileList.length > 0){
            if(IS_UPDATE_LOG) {
                LogUtil.d("apk update apkFile has " + fileList.length);
            }
            PackageManager manager = context.getPackageManager();
            int maxIndex = 0;
            int maxVer = 0;
            try {
                maxVer = manager.getPackageArchiveInfo(MyConfiguration.UPDATE_APK_PATH+fileList[0], PackageManager.GET_ACTIVITIES).versionCode;
                if(IS_UPDATE_LOG){
                    LogUtil.d("apk update file "+MyConfiguration.UPDATE_APK_PATH+fileList[0]+" code = "+maxVer);
                }
            }catch (Exception e){
                if(IS_UPDATE_LOG){
                    LogUtil.d("apk update file "+MyConfiguration.UPDATE_APK_PATH+fileList[0]+" parse fail: "+e.getMessage());
                }
            }

            int verCode;
            for(int i = 1; i < fileList.length;i++){
                try {
                    verCode = manager.getPackageArchiveInfo(MyConfiguration.UPDATE_APK_PATH+fileList[i], PackageManager.GET_ACTIVITIES).versionCode;
                    if(IS_UPDATE_LOG){
                        LogUtil.d("apk update file "+MyConfiguration.UPDATE_APK_PATH+fileList[i]+" code = "+verCode);
                    }
                    if(verCode > maxVer){
                        File f = new File(MyConfiguration.UPDATE_APK_PATH+fileList[maxIndex]);
                        f.delete();
                        maxVer = verCode;
                        maxIndex = i;
                    }else{
                        File f = new File(MyConfiguration.UPDATE_APK_PATH+fileList[i]);
                        f.delete();
                    }
                }catch (Exception e){
                    if(IS_UPDATE_LOG){
                        LogUtil.d("apk update file "+ MyConfiguration.UPDATE_APK_PATH+fileList[i] + "operate fail: "+e.getMessage());
                    }
                }

            }
            apkVerInFile = maxVer;
            lastApkPath = MyConfiguration.UPDATE_APK_PATH + fileList[maxIndex];

            if(IS_UPDATE_LOG)
                LogUtil.d("apk update apkVerInFile = "+apkVerInFile);
        }

    }

    public void downloadFile(Context context, String url, String savePath){
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setDestinationUri(Uri.fromFile(new File(savePath)));

        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }
}
