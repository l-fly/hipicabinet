package com.haipai.cabinet.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.haipai.cabinet.R;
import com.haipai.cabinet.manager.LocalDataManager;
import com.haipai.cabinet.model.entity.PmsEntity;
import com.haipai.cabinet.util.LogUtil;

import butterknife.BindView;
import butterknife.OnClick;

public class SelfInfoActivity extends BaseActivity {
    @BindView(R.id.port)
    TextView tvPort;
    @BindView(R.id.info)
    TextView tvInfo;
    @BindView(R.id.add)
    Button btAdd;
    @BindView(R.id.reduce)
    Button btReduce;

    int selectPort = 0;
    private String mDateType="";
    public static final String GET_DATA_CCU = "get_data_ccu";
    public static final String GET_DATA_PMS = "get_data_pms";
    @OnClick({R.id.btn_back,R.id.get_ccu,R.id.get_pms,R.id.add,R.id.reduce})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.get_ccu:
                mDateType = GET_DATA_CCU;
                tvInfo.setText(LocalDataManager.getInstance().cabinet.getCcu().getDescribe());
                break;
            case R.id.get_pms:
                mDateType = GET_DATA_PMS;
                PmsEntity pmsEntity = LocalDataManager.getInstance().cabinet.getPmsList().get(selectPort);
                tvInfo.setText(pmsEntity.getDescribe());
                break;
            case R.id.add:
                if(selectPort < 12){
                    selectPort++;
                    updateShowPort();
                }
                break;
            case R.id.reduce:
                if(selectPort>0){
                    selectPort--;
                    updateShowPort();
                }
                break;
        }
    }
    private void updateShowPort(){
        tvPort.setText(""+(selectPort+1));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_info);
    }

    @Override
    public void initUIView() {
        updateShowPort();
    }
    int refreshVal = 3;
    int timeTick = 5;
    @Override
    public void passSecond() {
        super.passSecond();
/*
        timeTick++;
        if(timeTick > refreshVal) {
            timeTick = 0;
            switch (mDateType) {
                case GET_DATA_CCU: {
                    tvInfo.setText(LocalDataManager.getInstance().cabinet.getCcu().getDescribe());
                }
                break;
                case GET_DATA_PMS: {
                    PmsEntity pmsEntity = LocalDataManager.getInstance().cabinet.getPmsList().get(selectPort);
                    tvInfo.setText(pmsEntity.getDescribe());
                }
                break;

            }

        }*/
    }

    @Override
    public void setCurrent() {

    }
}