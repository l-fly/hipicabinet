package com.haipai.cabinet;

import android.os.Environment;

import java.io.File;

public class MyConfiguration {


    public static final String APP_FILE_PATH = Environment.getExternalStorageDirectory().getPath()
            + File.separator + "haipai"+ File.separator;//sdcard 路径
    public static final String UPDATE_FILE_PATH = MyConfiguration.APP_FILE_PATH + "update" + File.separator;

    public static final String UPDATE_APK_PATH = UPDATE_FILE_PATH + "apk"+File.separator;

    public static final String UPDATE_CTRL_PATH = UPDATE_FILE_PATH + "ctrl" + File.separator;

    public static final String UPDATE_PMS_PATH = UPDATE_FILE_PATH + "pms" + File.separator;

    public static final String UPDATE_CHARGE_PATH = UPDATE_FILE_PATH + "charge" + File.separator;


}
