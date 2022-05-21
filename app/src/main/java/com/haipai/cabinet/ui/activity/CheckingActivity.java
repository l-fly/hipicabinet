package com.haipai.cabinet.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.haipai.cabinet.R;
import com.haipai.cabinet.entity.BatteryInfo;
import com.haipai.cabinet.manager.LocalDataManager;
import com.haipai.cabinet.manager.OrderManager;
import com.haipai.cabinet.manager.ReportManager;
import com.haipai.cabinet.util.CustomMethodUtil;
import com.haipai.cabinet.util.LogUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;

public class CheckingActivity extends BaseActivity {
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
        //speak("正在检测电池...");

        if(!CustomMethodUtil.isPortEmpty(LocalDataManager.openSlot1)){
            List<BatteryInfo> batteryInfos = LocalDataManager.getInstance().getBatteriesClone();
            synchronized (batteryInfos){
                for(BatteryInfo batteryInfo : batteryInfos){
                    if(batteryInfo.getPort() == LocalDataManager.openSlot1){
                        LocalDataManager.mBattery1 = batteryInfo;
                        break;
                    }
                }
            }
            curStep = STEP_CHECK_BATTERYOK;
            onCheckInBattery();
            LogUtil.i("#####curStep wait isPortEmpty not ");
        }
    }

    @Override
    public void passSecond() {
        super.passSecond();
        tvEndTime.setText("" + endTime);
        endTime--;
        if (endTime > 0) {
            LogUtil.i("#####curStep wait  " + curStep);
            if (curStep == STEP_CHECK_HASBATTERY){
                //if(endTime % 3 == 1) {
                    if(checkHasBatteryTimes < 6){
                        LogUtil.i("##########curStep wait checkHasBatteryTimes " + checkHasBatteryTimes);
                        if(!CustomMethodUtil.isPortEmpty(LocalDataManager.openSlot1)){
                            List<BatteryInfo> batteryInfos = LocalDataManager.getInstance().getBatteriesClone();
                            synchronized (batteryInfos){
                                for(BatteryInfo batteryInfo : batteryInfos){
                                    if(batteryInfo.getPort() == LocalDataManager.openSlot1){
                                        LocalDataManager.mBattery1 = batteryInfo;
                                        break;
                                    }
                                }
                            }
                            curStep = STEP_CHECK_BATTERYOK;
                            onCheckInBattery();
                            LogUtil.i("#####curStep wait isPortEmpty not ");
                        }else {
                            LogUtil.i("#####curStep wait isPortEmpty ");
                            checkHasBatteryTimes ++ ;
                        }
                    }else {
                        // 4次没检测到电池，认为没放入电池
                        curStep = STEP_FAIL;
                        result = ResultActivity.NO_CHECK_BATTERY;
                        endTime = 1;
                    }
               // }
            }
            if (curStep == STEP_CHECK_BATTERYOK){
                LogUtil.i("#########");
            }
        }else if(endTime == 0){
            Intent intent = new Intent(this, ResultActivity.class);
            intent.putExtra(ResultActivity.KEY_RESULT, result);
            startActivity(intent);
            finish();
        }
    }
    private void onCheckInBattery(){
        LogUtil.i("#####curStep wait onCheckInBattery ");
        BatteryInfo battery = LocalDataManager.mBattery1;
        if(battery != null){
            if (battery.getSn().equals(OrderManager.currentOrder.getBatteryId())){
                if(battery.isInValid()){
                    //体检成功
                    onCheckOutBattey();
                }else {
                    //体检失败
                    result = ResultActivity.CHECK_BATTERY_FAIL;
                    endTime = 1;
                }
            }else {
                //电池不属于用户
                result = ResultActivity.CHECK_BATTERY_NOTYOURS;
                endTime = 1;
            }

        }else {
            result = ResultActivity.NO_CHECK_BATTERY;
            endTime = 1;
        }
    }
    private void onCheckOutBattey(){
        List<BatteryInfo> batteryInfos = LocalDataManager.getInstance().getBatteriesClone();
        CustomMethodUtil.sortBattery(batteryInfos);
        List<BatteryInfo> bestList = new ArrayList<>();
        //int Voltage = OrderManager.currentOrder.getVoltage(); 协议不确定
        int type = LocalDataManager.mBattery1.getType();
        for(BatteryInfo batteryInfo : batteryInfos){
            if(batteryInfo.getType() == type
                    && batteryInfo.getPort() != LocalDataManager.openSlot1
                    && !CustomMethodUtil.isPortDisable(batteryInfo.getPort())
                    && batteryInfo.isOutValid()){
                bestList.add(batteryInfo);
            }
        }
        boolean isExchangeOut = false;
        if(bestList.size() == 0 ){
            for(BatteryInfo batteryInfo : batteryInfos){
                if(batteryInfo.getType() == type
                        && batteryInfo.getPort() != LocalDataManager.openSlot1
                        && !CustomMethodUtil.isPortDisable(batteryInfo.getPort())
                        && batteryInfo.isExchangeOutValid()){
                    bestList.add(batteryInfo);
                }
            }
        }
        if(bestList.size()>0) {
            if (isExchangeOut){
                LocalDataManager.mBattery2 = bestList.get(bestList.size()-1);
            }else {
                LocalDataManager.mBattery2 = bestList.get(new Random().nextInt(bestList.size()));
            }
            result = ResultActivity.SUCCESS;
            endTime = 1;
        }else {
            result = ResultActivity.NO_CHECK_BATTERY_ENOUGH;
            endTime = 1;
        }
    }

}