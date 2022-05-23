package com.haipai.cabinet.ui.activity.inquiry;


import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.haipai.cabinet.R;
import com.haipai.cabinet.entity.BatteryInfo;
import com.haipai.cabinet.manager.LocalDataManager;
import com.haipai.cabinet.manager.ReportManager;
import com.haipai.cabinet.ui.activity.BaseActivity;
import com.haipai.cabinet.ui.activity.ResultActivity;
import com.haipai.cabinet.util.CustomMethodUtil;
import com.haipai.cabinet.util.LogUtil;

import java.util.List;

import butterknife.BindView;

public class InquiryWaitBatteryActivity extends BaseActivity {
    @BindView(R.id.tv_slot)
    TextView tvSlot;
    @BindView(R.id.tv_describe)
    TextView tvDescribe;
    @BindView(R.id.tv_end_time)
    TextView tvEndTime;

    private int endTime = 60;
    private int openTimes = 0; //开门次数
    int result = -1; //1没有空仓，2仓门打不开 ,3仓门未关闭
    private static final int OPEN_PROCESS = 1; //开门阶段
    private static final int CLOSE_PROCESS = 2;  //关门阶段
    private static final int STEP_FAIL = 3;  //阶段失败，进入下个判断
    private int curStep = OPEN_PROCESS;

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
        boolean needFindRealEmpty = true;
        if (LocalDataManager.shouldEmptyPort !=-1
                &&CustomMethodUtil.isPortEmpty(LocalDataManager.shouldEmptyPort)
                &&!CustomMethodUtil.isPortDisable(LocalDataManager.shouldEmptyPort)){
            LocalDataManager.openSlot1 = LocalDataManager.shouldEmptyPort;
            needFindRealEmpty = false;
        }
        while(needFindRealEmpty){
            if (LocalDataManager.openSlot1 == -1) {
                LocalDataManager.openSlot1 = CustomMethodUtil.getEnableEmptyPort((int) (System.currentTimeMillis() % LocalDataManager.slotNum));
            }
            if (LocalDataManager.openSlot1 == -1) {   //随机没找到，从最前面找起
                LocalDataManager.openSlot1 = CustomMethodUtil.getEnableEmptyPort(0);
            }
            needFindRealEmpty = false;  //完成了查找
            if(LocalDataManager.openSlot1!=-1){
                List<BatteryInfo> heartBatteries = LocalDataManager.getInstance().extraBatteries;
                for(int i = 0; i < heartBatteries.size();i++){
                    if(heartBatteries.get(i).getPort()==LocalDataManager.openSlot1){
                        needFindRealEmpty = true;
                        LocalDataManager.openSlot1 = -1;
                        LogUtil.f("Wait 查找到空仓"+(LocalDataManager.openSlot1+1)+"但是在心跳里这个仓有电池！！！");
                    }
                }
            }
        }
        LogUtil.f("Wait 查找空仓完成，空仓是"+(LocalDataManager.openSlot1==-1?"没有":(LocalDataManager.openSlot1+1)));
        if (LocalDataManager.openSlot1 != -1) {
            CustomMethodUtil.open(LocalDataManager.openSlot1);
            tvSlot.setText("" + (LocalDataManager.openSlot1 +1));
            tvDescribe.setText("正在打开" + (LocalDataManager.openSlot1 +1) + "号仓门...");
           // speak("正在打开" + (LocalDataManager.openSlot1 +1) + "号仓门...");
            openTimes ++;
            curStep = OPEN_PROCESS;
        }else{
            //没有足够的空仓
            result = ResultActivity.NO_EMPTY_SLOT;
            endTime = 3;
            tvSlot.setText("");
            tvDescribe.setText("没有找到空仓");
            speak("没有找到空仓");
        }
    }

    @Override
    public void passSecond() {
        super.passSecond();
        tvEndTime.setText("" + endTime);
        endTime--;
        if (endTime > 0){
            //LogUtil.i("#####curStep  " + curStep);
            if (curStep == OPEN_PROCESS){
                if(endTime % 2 == 1) {
                    if(CustomMethodUtil.isOpen(LocalDataManager.openSlot1)){
                        tvSlot.setText("" + (LocalDataManager.openSlot1 +1));
                        tvDescribe.setText((LocalDataManager.openSlot1 +1) + "号仓门已打开，请放入电池，插好电池并关闭仓门。");
                        speak((LocalDataManager.openSlot1 +1) + "号仓门已打开，请放入电池，插好电池并关闭仓门。");

                        ReportManager.boxOpenReport(LocalDataManager.openSlot1);
                        curStep = CLOSE_PROCESS;
                        if(endTime < 12){
                            //如果时间不够，加点时间
                            endTime = 12;
                        }
                    }else {
                        if(openTimes < 5 ){
                            CustomMethodUtil.open(LocalDataManager.openSlot1);
                            openTimes ++;
                        }else {
                            //仓门5次打不开
                            curStep = STEP_FAIL;
                            result = ResultActivity.NO_OPEN_SLOT;
                            endTime = 3;
                            tvSlot.setText("" +(LocalDataManager.openSlot1+1));
                            tvDescribe.setText((LocalDataManager.openSlot1+1)+"号仓门打不开");
                            speak((LocalDataManager.openSlot1+1)+"号仓门打不开");
                            //仓门打不开禁仓
                            CustomMethodUtil.setPortDisable(LocalDataManager.openSlot1,true);
                        }
                    }
                }
            }else if(curStep == CLOSE_PROCESS){

               // if(endTime % 3 == 1) {
                   // LogUtil.i("#####curStep  " + CustomMethodUtil.isOpen(LocalDataManager.openSlot1));
                    if(!CustomMethodUtil.isOpen(LocalDataManager.openSlot1)){
                        result = 0;
                        endTime = -1;
                        startActivity(new Intent(InquiryWaitBatteryActivity.this, InquiryCheckingActivity.class));
                        finish();
                    }else {
                        result = ResultActivity.NO_CLOSE_SLOT;
                    }
               // }
            }
        }else if(endTime ==0){
            if(result == 0){
                startActivity(new Intent(InquiryWaitBatteryActivity.this, InquiryCheckingActivity.class));
                finish();
            }else{
                Intent intent = new Intent(this, ResultActivity.class);
                intent.putExtra(ResultActivity.KEY_RESULT, result);
                startActivity(intent);
                finish();
            }
        }

    }
}