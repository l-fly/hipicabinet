package com.haipai.cabinet.ui.activity;



import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;

import com.haipai.cabinet.R;
import com.haipai.cabinet.util.CustomMethodUtil;
import com.haipai.cabinet.util.LogUtil;
import com.haipai.cabinet.util.PreferencesUtil;

import butterknife.BindView;
import butterknife.OnClick;

public class SwitchIpActivity extends BaseActivity {
    private int inType;
    private int outType;
    @OnClick(R.id.btn_back)
    public void onActionBack() {
        if (inType == outType){
            finish();
        }else {
            PreferencesUtil.getInstance().setSwitchIp(outType);
            CustomMethodUtil.restartApp();
        }
    }

    @BindView(R.id.rg_func)
    RadioGroup mRgFunc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch_ip);
    }

    @Override
    public void initUIView() {
        int ip = PreferencesUtil.getInstance().getSwitchIp();
        LogUtil.i("########  " + ip);
        switch (ip){
            case 1:
                mRgFunc.check(R.id.rb_ip_1);
                inType = 1;
                break;
            case 2:
                mRgFunc.check(R.id.rb_ip_2);
                inType = 2;
                break;
            case 11:
                mRgFunc.check(R.id.rb_ip_11);
                inType = 11;
                break;
            case 21:
                mRgFunc.check(R.id.rb_ip_21);
                inType = 21;
                break;
        }
        outType = inType;
        mRgFunc.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                switch (id){
                    case R.id.rb_ip_1:
                        outType = 1;
                        break;
                    case R.id.rb_ip_2:
                        outType = 2;
                        break;
                    case R.id.rb_ip_11:
                        outType = 11;
                        break;
                    case R.id.rb_ip_21:
                        outType = 21;
                        break;
                }
            }
        });
    }
}