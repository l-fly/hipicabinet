package com.haipai.cabinet.ui.activity;



import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.haipai.cabinet.R;
import com.haipai.cabinet.manager.UpgradeManager;
import com.haipai.cabinet.util.LogUtil;

import butterknife.BindView;
import butterknife.OnClick;

public class UpgradeApkActivity extends BaseActivity {
    @OnClick(R.id.btn_back)
    public void onActionBack() {
        finish();
    }

    @BindView(R.id.tv_self_ver)
    TextView tvSelfVer;

    @BindView(R.id.tv_local_ver)
    TextView tvLocalVer;

    @OnClick(R.id.btn_refresh)
    public void onActionRefresh() {
        UpgradeManager.getInstance().getApkVersion(this);
        tvSelfVer.setText("已安装版本：" + UpgradeManager.getInstance().apkVerInstalled);
        tvLocalVer.setText("可升级版本：" + UpgradeManager.getInstance().apkVerInFile);
    }

    @OnClick(R.id.btn_upgrade)
    public void onActionUpgrade() {
        if(UpgradeManager.getInstance().isAvilibleApk(this,"com.haipai.appupgradehelper")){
            if(UpgradeManager.getInstance().apkVerInFile > UpgradeManager.getInstance().apkVerInstalled){
                UpgradeManager.getInstance().upgradeApk(this);
               /* Intent intent = new Intent();
                intent.setAction("com.haipai.cabinet.update");
                intent.putExtra("file",UpgradeManager.getInstance().getLatestApkPath(this));
                sendBroadcast(intent);*/
            }else {
                showToast("暂无升级");
            }
        }else {
            showToast("没有安装升级软件");
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upgrade_apk);
    }

    @Override
    public void initUIView() {
        tvSelfVer.setText("已安装版本：" + UpgradeManager.getInstance().apkVerInstalled);
        tvLocalVer.setText("可升级版本：" + UpgradeManager.getInstance().apkVerInFile);
    }
}