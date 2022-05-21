package com.haipai.cabinet.ui.activity.get;


import android.os.Bundle;
import android.widget.TextView;

import com.haipai.cabinet.R;
import com.haipai.cabinet.entity.BatteryInfo;
import com.haipai.cabinet.manager.LocalDataManager;
import com.haipai.cabinet.manager.OrderManager;
import com.haipai.cabinet.manager.ReportManager;
import com.haipai.cabinet.ui.activity.BaseActivity;
import com.haipai.cabinet.ui.activity.ResultActivity;
import com.haipai.cabinet.util.CustomMethodUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;

public class GetOpenActivity extends BaseActivity {

    @BindView(R.id.tv_slot)
    TextView tvSlot;
    @BindView(R.id.tv_describe)
    TextView tvDescribe;
    @BindView(R.id.tv_end_time)
    TextView tvEndTime;
    private int endTime = 60;

    private int result = -1;
    private int STEP_CHECK_OUTBATTERY = 1; // 检测是否有电池阶段
    private int STEP_OPEN = 2; // 开门
    private int STEP_GET= 3; // 取电池
    private int curStep = STEP_CHECK_OUTBATTERY;

    public static final int FAILED = -1; //失败
    public static final int SUCCESS = 0; //成功
    public static final int NO_CHECK_BATTERY_ENOUGH = 6; //没有可换电池
    private int openTimes = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common);
    }

    @Override
    public void initUIView() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        onCheckOutBattey();
    }

    @Override
    public void passSecond() {
        super.passSecond();
        tvEndTime.setText("" + endTime);
        endTime--;
        if (endTime > 0){
            /*if (curStep == STEP_CHECK_OUTBATTERY){
                if(endTime % 3 == 1) {

                }
            }*/
            if (curStep == STEP_OPEN){
                if (result == SUCCESS){
                    if(endTime % 3 == 1) {
                        if(CustomMethodUtil.isOpen(LocalDataManager.mBatteryGet.getPort())){
                            //成功
                            tvSlot.setText("" + (LocalDataManager.mBatteryGet.getPort()+1));
                            tvDescribe.setText((LocalDataManager.mBatteryGet.getPort()+1) + "号仓已打开，请取出电池！");
                            speak((LocalDataManager.mBatteryGet.getPort()+1) + "号仓已打开，请取出电池！");
                            endTime = 3;
                            curStep = STEP_GET;
                            LocalDataManager.shouldEmptyPort = LocalDataManager.mBatteryGet.getPort();
                            ReportManager.boxOpenReport(LocalDataManager.mBatteryGet.getPort());

                            //取电成功
                            ReportManager.switchFinishReport(26,LocalDataManager.mBatteryGet.getPort(),null,null);
                        }else {
                            if(openTimes < 5){
                                CustomMethodUtil.open(LocalDataManager.mBatteryGet.getPort());
                                openTimes ++;
                            }else {
                                //仓门打不开
                                tvSlot.setText("失败" );
                                tvDescribe.setText((LocalDataManager.mBatteryGet.getPort()+1) + "号仓门打不开，请重新扫码取电！");
                                speak((LocalDataManager.mBatteryGet.getPort()+1) + "号仓门打不开，请重新扫码取电！");
                                result = FAILED;
                                endTime = 3;
                                CustomMethodUtil.setPortDisable(LocalDataManager.mBatteryGet.getPort(),true);
                                //满电仓门开启失败，终止流程
                                ReportManager.switchFinishReport(24,LocalDataManager.mBatteryGet.getPort(),null,null);
                            }
                        }
                    }
                } else {
                    if (result == NO_CHECK_BATTERY_ENOUGH){
                        tvSlot.setText("失败" );
                        tvDescribe.setText("没有可取电池！");
                        speak("没有可取电池！");
                    }
                    endTime = 3;
                    curStep = FAILED;
                }
            }
        }else if (endTime == 0){
            finish();
        }
    }
    private void onCheckOutBattey(){
        List<BatteryInfo> batteryInfos = LocalDataManager.getInstance().getBatteriesClone();
        CustomMethodUtil.sortBattery(batteryInfos);
        List<BatteryInfo> bestList = new ArrayList<>();
        //String Voltage = OrderManager.currentOrder.getVoltage(); //协议不确定

        int type = 0;//Voltage;todo
        for(BatteryInfo batteryInfo : batteryInfos){
            if(batteryInfo.getType() == type
                    /*&& batteryInfo.getPort() != LocalDataManager.openSlot1*/
                    && !CustomMethodUtil.isPortDisable(batteryInfo.getPort())
                    /*&& batteryInfo.isOutValid()*/){
                bestList.add(batteryInfo);
            }
        }
       /* boolean isExchangeOut = false;
        if(bestList.size() == 0 ){
            for(BatteryInfo batteryInfo : batteryInfos){
                if(batteryInfo.getType() == type
                        *//*&& batteryInfo.getPort() != LocalDataManager.openSlot1*//*
                        && !CustomMethodUtil.isPortDisable(batteryInfo.getPort())
                        *//*&& batteryInfo.isOutValid()*//*){
                    bestList.add(batteryInfo);
                }
            }
        }*/
        if(bestList.size()>0) {
            /*if (isExchangeOut){
                LocalDataManager.mBatteryGetOrBack = bestList.get(bestList.size()-1);
            }else {
                LocalDataManager.mBatteryGetOrBack = bestList.get(new Random().nextInt(bestList.size()));
            }*/
            LocalDataManager.mBatteryGet = bestList.get(bestList.size()-1);
            CustomMethodUtil.open(LocalDataManager.mBatteryGet.getPort());
            openTimes ++;
            tvSlot.setText("" + (LocalDataManager.mBatteryGet.getPort()+1));
            tvDescribe.setText("正在打开"+ (LocalDataManager.mBatteryGet.getPort()+1) + "号仓...");
            result = SUCCESS;
            curStep = STEP_OPEN;
        }else {
            curStep = STEP_OPEN;
            result = NO_CHECK_BATTERY_ENOUGH;

            //没有可取电池，用24代替
            ReportManager.switchFinishReport(24,-1,null,null);
        }
    }
}