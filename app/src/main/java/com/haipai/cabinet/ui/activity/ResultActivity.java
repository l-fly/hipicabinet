package com.haipai.cabinet.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.haipai.cabinet.R;
import com.haipai.cabinet.manager.LocalDataManager;
import com.haipai.cabinet.manager.ReportManager;
import com.haipai.cabinet.util.CustomMethodUtil;
import com.haipai.cabinet.util.LogUtil;

import butterknife.BindView;

public class ResultActivity extends BaseActivity {
    @BindView(R.id.tv_slot)
    TextView tvSlot;
    @BindView(R.id.tv_describe)
    TextView tvDescribe;
    @BindView(R.id.tv_end_time)
    TextView tvEndTime;
    private int endTime = 60;
    public static final String KEY_RESULT = "RESULT";
    public static final int FAILED = -1; //失败
    public static final int SUCCESS = 0; //成功
    public static final int NO_EMPTY_SLOT = 1; //没有空仓
    public static final int NO_OPEN_SLOT = 2; //要进的电池仓门打不开
    public static final int NO_CLOSE_SLOT = 3; //仓门未关闭
    public static final int NO_CHECK_BATTERY = 4; //没有检测到电池
    public static final int CHECK_BATTERY_FAIL = 5; //体检失败
    public static final int NO_CHECK_BATTERY_ENOUGH = 6; //没有可换电池
    public static final int NO_OUT_OPEN_SLOT = 7; //要出的电池仓门打不开
    public static final int CHECK_BATTERY_NOTYOURS = 8; //体检失败
    private int result;
    private int openOutTimes = 0; //出电池的开门次数
    private int openInTimes = 0; //进电池开门次数
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        result = getIntent().getIntExtra(KEY_RESULT, -1);
        setContentView(R.layout.activity_common);

        LogUtil.i("###############  result " + result);
    }

    @Override
    public void initUIView() {
        if(result == SUCCESS){
            LocalDataManager.openSlot2 = LocalDataManager.mBattery2.getPort();
            tvSlot.setText("" + (LocalDataManager.openSlot2+1));
            tvDescribe.setText("正在打开" +(LocalDataManager.openSlot2+1)+"号仓...");
            CustomMethodUtil.open(LocalDataManager.openSlot2);
        }
        else if(result == NO_EMPTY_SLOT){
            tvSlot.setText("换电失败");
            tvDescribe.setText("没有找到空仓");
            speak("换电失败，请重新扫码换电");
            endTime = 5 ;
            //没有找到空仓，协议里没有此类型，用23替代
            ReportManager.switchFinishReport(23,LocalDataManager.openSlot1,null,null);
        }
        else if(result == NO_OPEN_SLOT){
            tvSlot.setText("换电失败");
            tvDescribe.setText((LocalDataManager.openSlot1+1)+"号仓门打不开");
            speak("换电失败，请重新扫码换电");
            endTime = 5 ;

            //空仓柜门未打开，终止流程
            ReportManager.switchFinishReport(20,LocalDataManager.openSlot1,null,null);
        }
        else if(result == NO_CLOSE_SLOT){
            tvSlot.setText("换电失败");
            tvDescribe.setText((LocalDataManager.openSlot1+1)+"号仓门未关闭");
            speak("换电失败，请重新扫码换电");
            endTime = 5 ;

            //用户没有放入电池，终止流程
            ReportManager.switchFinishReport(21,LocalDataManager.openSlot1,null,null);
        }
        else if(result == NO_CHECK_BATTERY){
            tvSlot.setText("换电失败");
            tvDescribe.setText("没有检测到电池,请检查电池是否插好");
            speak("没有检测到电池,请检查电池是否插好");
            endTime = 5 ;

            //用户没有放入电池，终止流程
            ReportManager.switchFinishReport(21,LocalDataManager.openSlot1,null,null);
        }
        else if(result == CHECK_BATTERY_NOTYOURS){
            tvSlot.setText("换电失败");
            tvDescribe.setText("电池不属于您，请联系客服");
            speak("电池不属于您，请联系客服");
            endTime = 5 ;

            //用户与放入的电池不匹配，终止流程
            ReportManager.switchFinishReport(22,LocalDataManager.openSlot1,null,null);
        }
        else if(result == CHECK_BATTERY_FAIL){
            tvSlot.setText("换电失败");
            tvDescribe.setText("体检失败，请联系客服");
            speak("体检失败，请联系客服");
            endTime = 5 ;

            //体检失败，终止流程
            ReportManager.switchFinishReport(23,LocalDataManager.openSlot1,null,null);
        }
        else if(result == NO_CHECK_BATTERY_ENOUGH){
            tvSlot.setText("换电失败");
            tvDescribe.setText("没有可换电池，请去其他换电柜换电");
            speak("没有可换电池，请去其他换电柜换电");
            endTime = 5 ;

            //没有可换电池，协议里没有此类型，用23替代
            ReportManager.switchFinishReport(23,LocalDataManager.openSlot1,null,null);
        }
    }

    @Override
    public void passSecond() {
        super.passSecond();
        tvEndTime.setText("" + endTime);
        endTime--;
        if (endTime > 0) {
           if (result == SUCCESS){
               if(endTime % 3 == 1) {
                   if(CustomMethodUtil.isOpen(LocalDataManager.openSlot2)){
                      //换电成功
                       tvDescribe.setText((LocalDataManager.openSlot2 + 1)+"号仓门已打开，请取出电池并关闭仓门！");
                       speak((LocalDataManager.openSlot2 + 1)+"号仓门已打开，请取出电池并关闭仓门！");
                       LocalDataManager.shouldEmptyPort = LocalDataManager.openSlot2;
                       endTime = 5;

                       ReportManager.boxOpenReport(LocalDataManager.openSlot2);
                       //用户取出电池,流程正常结束
                       ReportManager.switchFinishReport(26,LocalDataManager.openSlot1,LocalDataManager.mBattery1,LocalDataManager.mBattery2);
                   }else {
                       if(openOutTimes < 5){
                           CustomMethodUtil.open(LocalDataManager.openSlot2);
                           openOutTimes ++;
                       }else {
                           //仓门打不开，退回原来的电池，换电失败
                           result = NO_OUT_OPEN_SLOT;
                           CustomMethodUtil.open(LocalDataManager.openSlot1);
                           openInTimes ++;

                           CustomMethodUtil.setPortDisable(LocalDataManager.openSlot2,true);
                           tvSlot.setText("换电失败");
                           tvDescribe.setText((LocalDataManager.openSlot2 + 1)+"号仓打不开，请取回原来的电池");
                           speak((LocalDataManager.openSlot2 + 1)+"号仓打不开，请取回原来的电池");
                           endTime = 5;

                           //满电仓门开启失败，终止流程
                           ReportManager.switchFinishReport(24,LocalDataManager.openSlot1,null,null);
                       }
                   }
               }
           }
           else if (result == NO_OUT_OPEN_SLOT){
               if(endTime % 3 == 1) {
                   if(CustomMethodUtil.isOpen(LocalDataManager.openSlot1)){
                       tvSlot.setText("换电失败");
                       tvDescribe.setText((LocalDataManager.openSlot1 + 1)+"号仓已打开，请取回原来的电池");
                       speak((LocalDataManager.openSlot1 + 1)+"号仓已打开，请取回原来的电池");
                       endTime = 5;
                       ReportManager.boxOpenReport(LocalDataManager.openSlot1);
                   }else {
                       if(openInTimes < 5){
                           CustomMethodUtil.open(LocalDataManager.openSlot1);
                           openInTimes ++;
                       }else {
                           result = FAILED;
                           endTime = 1;
                           //吞电池了
                           LogUtil.f("吞电池了");
                       }
                   }
               }
           }
           else if (result == NO_CHECK_BATTERY
                    || result == CHECK_BATTERY_FAIL
                    || result == NO_CHECK_BATTERY_ENOUGH){
               if(endTime % 3 == 1) {
                   if(CustomMethodUtil.isOpen(LocalDataManager.openSlot1)){
                       tvSlot.setText("换电失败");
                       tvDescribe.setText((LocalDataManager.openSlot1 + 1)+"号仓已打开，请取回原来的电池");
                       speak((LocalDataManager.openSlot1 + 1)+"号仓已打开，请取回原来的电池");
                       endTime = 5;

                       ReportManager.boxOpenReport(LocalDataManager.openSlot1);
                   }else {
                       if(openInTimes < 5){
                           CustomMethodUtil.open(LocalDataManager.openSlot1);
                           openInTimes ++;
                       }else {
                           result = FAILED;
                           endTime = 1;
                           //吞电池了
                           LogUtil.f("吞电池了");
                       }
                   }
               }
           }

        }else if (endTime == 0){
            finish();
        }
    }
}