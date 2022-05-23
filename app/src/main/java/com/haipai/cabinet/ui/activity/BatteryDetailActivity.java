package com.haipai.cabinet.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.haipai.cabinet.R;
import com.haipai.cabinet.entity.BatteryInfo;
import com.haipai.cabinet.manager.LocalDataManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;

public class BatteryDetailActivity extends BaseActivity {
    @OnClick(R.id.btn_back)
    public void onActionBack() {
        finish();
    }
    private List<TextView> listTvType = new ArrayList<>();
    private int[] listTvTypeIds = new int[]{R.id.tv_type_1,
            R.id.tv_type_2,R.id.tv_type_3,R.id.tv_type_4,
            R.id.tv_type_5,R.id.tv_type_6,R.id.tv_type_7,
            R.id.tv_type_8,R.id.tv_type_9,R.id.tv_type_10,
            R.id.tv_type_11,R.id.tv_type_12,
    };
    private List<ProgressBar> listProgressBar = new ArrayList<>();
    private int[] listPbIds = new int[]{R.id.pb_1,
            R.id.pb_2,R.id.pb_3,R.id.pb_4,
            R.id.pb_5,R.id.pb_6,R.id.pb_7,
            R.id.pb_8,R.id.pb_9,R.id.pb_10,
            R.id.pb_11,R.id.pb_12,
    };
    private List<TextView> listTvValue = new ArrayList<>();
    private int[] listValueIds = new int[]{R.id.tv_value_1,
            R.id.tv_value_2,R.id.tv_value_3,R.id.tv_value_4,
            R.id.tv_value_5,R.id.tv_value_6,R.id.tv_value_7,
            R.id.tv_value_8,R.id.tv_value_9,R.id.tv_value_10,
            R.id.tv_value_11,R.id.tv_value_12,
    };
    private List<TextView> listTvDescribe = new ArrayList<>();
    private int[] listDescribeIds = new int[]{R.id.tv_describe_1,
            R.id.tv_describe_2,R.id.tv_describe_3,R.id.tv_describe_4,
            R.id.tv_describe_5,R.id.tv_describe_6,R.id.tv_describe_7,
            R.id.tv_describe_8,R.id.tv_describe_9,R.id.tv_describe_10,
            R.id.tv_describe_11,R.id.tv_describe_12,
    };
    private List<TextView> listTvSlolt = new ArrayList<>();
    private int[] listSloltIds = new int[]{R.id.tv_slolt_1,
            R.id.tv_slolt_2,R.id.tv_slolt_3,R.id.tv_slolt_4,
            R.id.tv_slolt_5,R.id.tv_slolt_6,R.id.tv_slolt_7,
            R.id.tv_slolt_8,R.id.tv_slolt_9,R.id.tv_slolt_10,
            R.id.tv_slolt_11,R.id.tv_slolt_12,
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battery_detail);
        setStayDuration(30*1000);
    }

    @Override
    public void initUIView() {
        for(int i = 0;i<12;i++){
            TextView textType = (TextView) findViewById(listTvTypeIds[i]);
            listTvType.add(textType);
            ProgressBar progressBar = (ProgressBar) findViewById(listPbIds[i]);
            listProgressBar.add(progressBar);
            TextView textValue = (TextView) findViewById(listValueIds[i]);
            listTvValue.add(textValue);
            TextView textDescribe = (TextView) findViewById(listDescribeIds[i]);
            listTvDescribe.add(textDescribe);
            TextView textSlolt = (TextView) findViewById(listSloltIds[i]);
            listTvSlolt.add(textSlolt);
        }
        for(int i = 0;i<LocalDataManager.slotNum;i++){
            if(i<listTvSlolt.size()){
                if (LocalDataManager.isPortDisable(i)){
                    listTvSlolt.get(i).setText("禁仓");
                }
            }
        }

        List<BatteryInfo> batteryInfos = LocalDataManager.getInstance().getBatteriesClone();
        for(BatteryInfo batteryInfo : batteryInfos){
            if(batteryInfo.getPort() < listProgressBar.size()){
                int type = batteryInfo.getType();
                String typeName = "";
                switch (type){
                    case 0:
                        typeName = "48V";
                        break;
                    case 1:
                        typeName = "60V";
                        break;
                    case 2:
                        typeName = "72V";
                        break;
                }
                listTvType.get(batteryInfo.getPort()).setText(typeName);
                listTvType.get(batteryInfo.getPort()).setVisibility(View.VISIBLE);

                listProgressBar.get(batteryInfo.getPort()).setProgress(batteryInfo.getSoc());
                listProgressBar.get(batteryInfo.getPort()).setVisibility(View.VISIBLE);

                listTvValue.get(batteryInfo.getPort()).setText( (batteryInfo.getSoc()>100?100:batteryInfo.getSoc()) + "%");
                listTvValue.get(batteryInfo.getPort()).setVisibility(View.VISIBLE);

                if (batteryInfo.isOutValid()){
                    listTvDescribe.get(batteryInfo.getPort()).setText("可换电");
                }else {
                    listTvDescribe.get(batteryInfo.getPort()).setText("正在充电...");
                }
                listTvDescribe.get(batteryInfo.getPort()).setVisibility(View.VISIBLE);

                listTvSlolt.get(batteryInfo.getPort()).setVisibility(View.GONE);
            }
        }
    }
}