package com.haipai.cabinet.ui.activity.back;



import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.haipai.cabinet.R;
import com.haipai.cabinet.entity.BatteryInfo;
import com.haipai.cabinet.manager.LocalDataManager;
import com.haipai.cabinet.manager.OrderManager;
import com.haipai.cabinet.ui.activity.BaseActivity;
import com.haipai.cabinet.util.CustomMethodUtil;
import com.haipai.cabinet.util.LogUtil;

import java.util.List;

import butterknife.BindView;

public class BackCheckingActivity extends BaseActivity {
    @BindView(R.id.tv_slot)
    TextView tvSlot;
    @BindView(R.id.tv_describe)
    TextView tvDescribe;
    @BindView(R.id.tv_end_time)
    TextView tvEndTime;
    private int endTime = 60;
    private int STEP_CHECK_HASBATTERY = 1; // 检测是否有电池阶段
    private int STEP_CHECK_BATTERYOK = 2; // 检测电池是否合格阶段
    private int STEP_FAIL = 3; // 失败
    private int curStep = STEP_CHECK_HASBATTERY;
    private int checkHasBatteryTimes = 0;

    private int result = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common);
    }

    @Override
    public void initUIView() {
        tvSlot.setText("" +(LocalDataManager.openSlot1+1));
        tvDescribe.setText("正在检测电池...");
        speak("正在检测电池...");
    }

    @Override
    public void passSecond() {
        super.passSecond();
        tvEndTime.setText("" + endTime);
        endTime--;
        if (endTime > 0) {
            if (curStep == STEP_CHECK_HASBATTERY){
                //if(endTime % 3 == 1) {
                    if(checkHasBatteryTimes < 4){
                        if(!CustomMethodUtil.isPortEmpty(LocalDataManager.openSlot1)){
                            List<BatteryInfo> batteryInfos = LocalDataManager.getInstance().getBatteriesClone();
                            synchronized (batteryInfos){
                                for(BatteryInfo batteryInfo : batteryInfos){
                                    if(batteryInfo.getPort() == LocalDataManager.openSlot1){
                                        LocalDataManager.mBatteryBack = batteryInfo;
                                        break;
                                    }
                                }
                            }
                            curStep = STEP_CHECK_BATTERYOK;
                            onCheckInBattery();
                        }else {
                            checkHasBatteryTimes ++ ;
                        }
                    }else {
                        // 4次没检测到电池，认为没放入电池
                        curStep = STEP_FAIL;
                        result = BackResultActivity.NO_CHECK_BATTERY;
                        endTime = 1;
                    }
               // }
            }
            if (curStep == STEP_CHECK_BATTERYOK){
                LogUtil.i("#########");
            }
        }else if(endTime == 0){
            Intent intent = new Intent(this, BackResultActivity.class);
            intent.putExtra(BackResultActivity.KEY_RESULT, result);
            startActivity(intent);
            finish();
        }
    }
    private void onCheckInBattery(){
        BatteryInfo battery = LocalDataManager.mBatteryBack;
        if(battery != null){
            if (battery.getSn().equals(OrderManager.currentOrder.getBatteryId())){
                if(battery.isInValid()){
                    //体检成功
                    result = BackResultActivity.SUCCESS;
                    endTime = 1;
                }else {
                    //体检失败
                    result = BackResultActivity.CHECK_BATTERY_FAIL;
                    endTime = 1;
                }
            }else {
                result =  BackResultActivity.CHECK_BATTERY_NOTYOURS;
                endTime = 1;
            }

        }else {
            result = BackResultActivity.NO_CHECK_BATTERY;
            endTime = 1;
        }
    }
}