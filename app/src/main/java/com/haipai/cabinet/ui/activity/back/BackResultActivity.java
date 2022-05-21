package com.haipai.cabinet.ui.activity.back;


import android.os.Bundle;
import android.widget.TextView;

import com.haipai.cabinet.R;
import com.haipai.cabinet.manager.LocalDataManager;
import com.haipai.cabinet.manager.ReportManager;
import com.haipai.cabinet.ui.activity.BaseActivity;
import com.haipai.cabinet.util.CustomMethodUtil;
import com.haipai.cabinet.util.LogUtil;

import butterknife.BindView;

public class BackResultActivity extends BaseActivity {
    @BindView(R.id.tv_slot)
    TextView tvSlot;
    @BindView(R.id.tv_describe)
    TextView tvDescribe;
    @BindView(R.id.tv_end_time)
    TextView tvEndTime;
    private int endTime = 5;
    public static final String KEY_RESULT = "RESULT";
    public static final int SUCCESS = 0; //成功
    public static final int NO_EMPTY_SLOT = 1; //没有空仓
    public static final int NO_OPEN_SLOT = 2; //要进的电池仓门打不开
    public static final int NO_CLOSE_SLOT = 3; //仓门未关闭
    public static final int NO_CHECK_BATTERY = 4; //没有检测到电池
    public static final int CHECK_BATTERY_FAIL = 5; //体检失败
    public static final int CHECK_BATTERY_NOTYOURS = 8; //体检失败
    private int result;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        result = getIntent().getIntExtra(KEY_RESULT, -1);
        setContentView(R.layout.activity_common);
    }

    @Override
    public void initUIView() {
        if(result == SUCCESS){
            tvSlot.setText("" + (LocalDataManager.openSlot1+1));
            tvDescribe.setText("还电池成功");
            speak("还电池成功");
            endTime = 5 ;
            LocalDataManager.shouldEmptyPort = -1;
            ReportManager.switchFinishReport(26,LocalDataManager.openSlot1,null,null);
        }
        else if(result == NO_EMPTY_SLOT){
            tvSlot.setText("还电池失败");
            tvDescribe.setText("没有找到空仓");
            speak("还电池失败，请重新扫码还电池");
            endTime = 5 ;
            LocalDataManager.shouldEmptyPort = -1;
            //体检失败，协议里没有此类型，用23替代
            ReportManager.switchFinishReport(23,LocalDataManager.openSlot1,null,null);
        }
        else if(result == NO_OPEN_SLOT){
            tvSlot.setText("还电池失败");
            tvDescribe.setText((LocalDataManager.openSlot1+1)+"号仓门打不开");
            speak("还电池失败，请重新扫码还电池");
            endTime = 5 ;
            LocalDataManager.shouldEmptyPort = -1;
            //空仓柜门未打开，终止流程
            ReportManager.switchFinishReport(20,LocalDataManager.openSlot1,null,null);
        }
        else if(result == NO_CLOSE_SLOT){
            tvSlot.setText("还电池失败");
            tvDescribe.setText((LocalDataManager.openSlot1+1)+"号仓门未关闭");
            speak("还电池失败，请重新扫码还电池");
            endTime = 5 ;
            CustomMethodUtil.open(LocalDataManager.openSlot1);
            LocalDataManager.shouldEmptyPort = LocalDataManager.openSlot1;
            //空仓柜门未打开，终止流程
            ReportManager.switchFinishReport(21,LocalDataManager.openSlot1,null,null);
        }
        else if(result == NO_CHECK_BATTERY){
            tvSlot.setText("还电池失败");
            tvDescribe.setText("没有检测到电池,请检查电池是否插好");
            speak("没有检测到电池,请检查电池是否插好");
            endTime = 5 ;
            CustomMethodUtil.open(LocalDataManager.openSlot1);
            LocalDataManager.shouldEmptyPort = LocalDataManager.openSlot1;
            //没有检测到电池，协议里没有此类型，用23替代
            ReportManager.switchFinishReport(23,LocalDataManager.openSlot1,null,null);
        }
        else if(result == CHECK_BATTERY_FAIL){
            tvSlot.setText("还电池失败");
            tvDescribe.setText("体检失败，请联系客服");
            speak("体检失败，请联系客服");
            endTime = 5 ;
            CustomMethodUtil.open(LocalDataManager.openSlot1);
            LocalDataManager.shouldEmptyPort = LocalDataManager.openSlot1;
            //体检失败，协议里没有此类型，用23替代
            ReportManager.switchFinishReport(23,LocalDataManager.openSlot1,null,null);
        }
        else if(result == CHECK_BATTERY_NOTYOURS){
            tvSlot.setText("还电池失败");
            tvDescribe.setText("电池不属于您，请联系客服");
            speak("电池不属于您，请联系客服");
            endTime = 5 ;
            CustomMethodUtil.open(LocalDataManager.openSlot1);
            LocalDataManager.shouldEmptyPort = LocalDataManager.openSlot1;
            //用户与放入的电池不匹配，终止流程
            ReportManager.switchFinishReport(22,LocalDataManager.openSlot1,null,null);
        }
    }

    @Override
    public void passSecond() {
        super.passSecond();
        tvEndTime.setText("" + endTime);
        endTime--;
        if (endTime == 0){
            finish();
        }
    }
}