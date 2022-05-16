package com.haipai.cabinet.ui.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.haipai.cabinet.R;
import com.haipai.cabinet.manager.SerialManager;
import com.haipai.cabinet.manager.LocalDataManager;
import com.haipai.cabinet.util.NumberBytes;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

public class OperationActivity extends BaseActivity {
    @BindView(R.id.et_slave_id)
    EditText mEtSlaveId;
    @BindView(R.id.rg_func)
    RadioGroup mRgFunc;
    @BindView(R.id.et_offset)
    EditText mEtOffset;
    @BindView(R.id.et_amount)
    EditText mEtAmount;
    @BindView(R.id.tv_console)
    TextView mTvConsole;
    @BindView(R.id.area_amount)
    LinearLayout mAreaAmount;
    @BindView(R.id.area_value)
    LinearLayout mAreaValue;
    @BindView(R.id.et_multi_value)
    EditText mEtMultiValue;
    SerialManager.IOnReceiveSerialDataListener serialDataListener = new SerialManager.IOnReceiveSerialDataListener() {
        @Override
        public void onReceive(byte cmd) {

        }

        @Override
        public void onReceive(byte devAddr ,byte cmd, byte[] data) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mOffset == devAddr && data.length == mAmount*2){
                        appendText(cmd + "读取：" + NumberBytes.getHexString(data) + "\n");
                    }
                }
            });

        }

        @Override
        public void onError(byte[] err) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    appendText("读取错误：" + NumberBytes.getHexString(err) + "\n");
                }
            });
        }
    };
    @Override
    protected void onStart() {
        super.onStart();
        SerialManager.getInstance().setSerialDataListener(serialDataListener);
    }
    private void appendText(String text) {
        mTvConsole.append(text);
    }
    @Override
    protected void onStop() {
        super.onStop();
        SerialManager.getInstance().removeSerialDataListener(serialDataListener);
    }
    //R.id.bt_ccu_part1,R.id.bt_ccu_part2,R.id.bt_ccu_part3,R.id.bt_ccu_part4,R.id.bt_pms_part1,
    @OnClick({R.id.btn_back,R.id.btn_send,R.id.btn_clear_record,R.id.bt_goto_info})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_back:
                finish();
                break;
            case R.id.bt_goto_info:
                startActivity(new Intent(this, SelfInfoActivity.class));
                break;
            case R.id.btn_send:
                if (cmd == 0x03){
                    if (checkSlave() && checkOffset() && checkAmount()) {
                        SerialManager.getInstance().send03(mSalveId,mOffset,mAmount);
                    }
                }else if(cmd == 0x04){
                    if (checkSlave() && checkOffset() && checkAmount()) {
                        SerialManager.getInstance().send04(mSalveId,mOffset,mAmount);
                    }
                }else if(cmd == 0x10){
                    if (checkSlave() && checkOffset() && checkRegValues()) {
                        SerialManager.getInstance().send16(mSalveId,mOffset,mRegValues);
                    }
                }


                break;
            case R.id.btn_clear_record:
               // speak("清除记录");
                mTvConsole.setText("");
                break;
          /*  case R.id.bt_ccu_part1:
               // speak("hello world");
                LocalDataManager.getInstance().getCcuDataPartOne();
                break;
            case R.id.bt_ccu_part2:
                LocalDataManager.getInstance().getCcuDataPartTow();
                break;
            case R.id.bt_ccu_part3:
                LocalDataManager.getInstance().getCcuDataPartThree();
                break;
            case R.id.bt_ccu_part4:
                LocalDataManager.getInstance().getCcuDataPartFour();
                break;
            case R.id.bt_pms_part1:
                LocalDataManager.getInstance().getPmsDataPartOne(0);
                break;*/
        }
    }
    private int mSalveId;
    private int mAmount;
    int mOffset;
    int cmd = 0x03;
    private byte[] mRegValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operation);
    }

    @Override
    public void initUIView() {
        mRgFunc.check(R.id.rb_func03);
        mRgFunc.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.rb_func03:
                        cmd = 0x03;
                        mAreaAmount.setVisibility(View.VISIBLE);
                        mAreaValue.setVisibility(View.GONE);
                        break;
                    case R.id.rb_func04:
                        cmd = 0x04;
                        mAreaAmount.setVisibility(View.VISIBLE);
                        mAreaValue.setVisibility(View.GONE);
                        break;
                    case R.id.rb_func16:
                        cmd = 0x10;
                        mAreaAmount.setVisibility(View.GONE);
                        mAreaValue.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });
        setStayDuration(10000000);
    }

    @Override
    public void setCurrent() {

    }
    /**
     * 检查设备地址
     *
     * @return
     */
    private boolean checkSlave() {

        // 设备地址
        mSalveId = Integer.MIN_VALUE;
        try {
            mSalveId = Integer.parseInt(mEtSlaveId.getText().toString().trim());
        } catch (NumberFormatException e) {
            //e.printStackTrace();
        }

        if (mSalveId == Integer.MIN_VALUE) {
            showToast("无效设备地址");
            return false;
        }
        return true;
    }

    /**
     * 检查数据地址
     *
     * @return
     */
    private boolean checkOffset() {

        // 数据地址
        mOffset = Integer.MIN_VALUE;
        try {
            mOffset = Integer.parseInt(mEtOffset.getText().toString().trim());
        } catch (NumberFormatException e) {
            //e.printStackTrace();
        }

        if (mOffset == Integer.MIN_VALUE) {
            showToast("无效地址");
            return false;
        }
        return true;
    }

    /**
     * 检查数量
     */
    private boolean checkAmount() {

        // 寄存器/线圈数量
        mAmount = Integer.MIN_VALUE;
        try {
            int value = Integer.parseInt(mEtAmount.getText().toString().trim());
            if (value >= 1 && value <= 255) {
                mAmount = value;
            }
        } catch (NumberFormatException e) {
            //e.printStackTrace();
        }

        if (mAmount == Integer.MIN_VALUE) {
            showToast("无效数量");
            return false;
        }
        return true;
    }

    /**
     * 检查多个线圈输出值
     *
     * @return
     */
    private boolean checkRegValues() {

        mRegValues = null;
        try {
            String str = mEtMultiValue.getText().toString().trim();
            String[] split = str.split(",");
            ArrayList<Integer> result = new ArrayList<>();
            for (String s : split) {
                result.add(Integer.parseInt(s.trim(), 16));
            }
            byte[] values = new byte[result.size()];
            for (int i = 0; i < values.length; i++) {
                int v = result.get(i);
                if (v >= 0 && v <= 0xffff) {
                    values[i] = (byte) v;
                } else {
                    throw new RuntimeException();
                }
            }
            if (values.length % 2 == 0) {
                mRegValues = values;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (mRegValues == null) {
            showToast("无效输出值");
            return false;
        }
        return true;
    }

}