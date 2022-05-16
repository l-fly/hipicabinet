package com.haipai.cabinet.ui.activity;


import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.provider.Settings;
import com.haipai.cabinet.R;
import com.haipai.cabinet.manager.LocalDataManager;

import java.util.List;

import butterknife.OnClick;

public class MenuActivity extends BaseActivity {
    @OnClick(R.id.btn_back)
    public void onActionBack() {
        finish();
    }
    @OnClick(R.id.into_setting)
    public void onActionIntoSetting() {
        Intent intent = new Intent(Settings.ACTION_SETTINGS);
        startActivity(intent);
    }
    @OnClick(R.id.into_file)
    public void onActionIntoFile() {
        try {
            String pakName = "";
            List<PackageInfo> infos = getPackageManager().getInstalledPackages(0);
            for (int i = 0; i < infos.size(); i++) {
                PackageInfo info = infos.get(i);
                Intent intent = getPackageManager().getLaunchIntentForPackage(info.packageName);
                // LogUtil.d(info.packageName+" "+info.versionName+" "+(intent==null?"no":"yes"));
                if(info.packageName.equals("com.estrongs.android.pop")){
                    pakName = info.packageName;
                    break;
                }
            }
            pakName = "com.android.rk";
            Intent intent = getPackageManager().getLaunchIntentForPackage(pakName);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    @OnClick(R.id.into_open)
    public void onActionIntoOpen() {
        startActivity(new Intent(this, OpenActivity.class));
    }
    @OnClick(R.id.local_details)
    public void onActionIntoLocalDetails() {
        startActivity(new Intent(this, LocalDetailsActivity.class));
    }
    @OnClick(R.id.into_operation)
    public void onActionIntoOperation() {
        startActivity(new Intent(this, OperationActivity.class));
    }
    @OnClick(R.id.into_test_operation)
    public void onActionIntoTestOperation() {
        startActivity(new Intent(this, TestOperationActivity.class));
    }
    @OnClick(R.id.into_slot_manage)
    public void onActionIntoSeflInfo() {
        startActivity(new Intent(this, SlotManageActivity.class));
    }
    @OnClick(R.id.into_self_info)
    public void onActionIntoSlotManage() {
        startActivity(new Intent(this, SelfInfoActivity.class));
    }
    @OnClick(R.id.into_switch_ip)
    public void onActionSwitchIp() {
        startActivity(new Intent(this, SwitchIpActivity.class));
    }

    @OnClick(R.id.upgrade_apk)
    public void onActionUpgradeApk() {
        startActivity(new Intent(this, UpgradeApkActivity.class));
    }

    @OnClick(R.id.into_audio_set)
    public void onActionAudioSet() {
        startActivity(new Intent(this, AudioSetActivity.class));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        LocalDataManager.shouldEmptyPort = -1;
    }

    @Override
    public void initUIView() {

    }

    @Override
    public void setCurrent() {

    }

}