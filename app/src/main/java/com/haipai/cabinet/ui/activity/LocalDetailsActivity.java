package com.haipai.cabinet.ui.activity;


import android.os.Bundle;
import android.widget.TextView;

import com.haipai.cabinet.MyApplication;
import com.haipai.cabinet.R;

import butterknife.BindView;
import butterknife.OnClick;

public class LocalDetailsActivity extends BaseActivity {
    @OnClick(R.id.btn_back)
    public void onActionFinish() {
        finish();
    }
    @BindView(R.id.tv_info)
    TextView tvIofo;
    int timeTick = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_details);
    }

    @Override
    public void initUIView() {

    }

    @Override
    public void passSecond() {
        super.passSecond();
        timeTick ++;
        if(timeTick % 3 ==1){
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(((MyApplication)getApplication()).getRealTimeInfo());
            tvIofo.setText(stringBuffer.toString());
        }
    }
}