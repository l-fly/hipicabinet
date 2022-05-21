package com.haipai.cabinet.ui.activity;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.haipai.cabinet.MyApplication;
import com.haipai.cabinet.R;
import com.haipai.cabinet.manager.LocalDataManager;
import com.haipai.cabinet.manager.OrderManager;
import com.haipai.cabinet.manager.ReportManager;
import com.haipai.cabinet.manager.SerialManager;
import com.haipai.cabinet.manager.TcpManager;
import com.haipai.cabinet.manager.WIFIManager;
import com.haipai.cabinet.ui.activity.back.BackWaitBatteryActivity;
import com.haipai.cabinet.ui.activity.get.GetOpenActivity;
import com.haipai.cabinet.util.CustomMethodUtil;
import com.haipai.cabinet.util.LogUtil;
import com.haipai.cabinet.util.PreferencesUtil;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (OrderManager.currentOrder != null && OrderManager.currentOrder.getValue().equals("01")){
                        startActivity(new Intent(MainActivity.this, WaitBatteryActivity.class));
                    }else {
                        LogUtil.f("MainActivity 换电订单丢失");
                    }
                    break;
                case 2:
                    startActivity(new Intent(MainActivity.this, GetOpenActivity.class));
                    break;
                case 3:
                    startActivity(new Intent(MainActivity.this, BackWaitBatteryActivity.class));
                    break;
            }

        }
    };
    OrderManager.IOrderListener orderListener = new OrderManager.IOrderListener() {

        @Override
        public void onReceive(String type) {
            switch (type){
                case "01": //换电
                    mHandler.sendEmptyMessage(1);
                    break;
                case "02"://还电池
                    //mHandler.sendEmptyMessage(2);
                    break;
                case "03"://取电池
                   // mHandler.sendEmptyMessage(3);
                    break;
                case "11"://首放 取
                    mHandler.sendEmptyMessage(2);
                    break;
                case "12"://退还电池
                    mHandler.sendEmptyMessage(3);
                    break;
            }
        }
    };
    @BindView(R.id.tv_exchange_num_48)
    TextView tvExchangeNum48;
    @BindView(R.id.tv_exchange_num_60)
    TextView tvExchangeNum60;
    @BindView(R.id.tv_exchange_num_72)
    TextView tvExchangeNum72;

    @BindView(R.id.tv_get_num_48)
    TextView tvGetNum48;
    @BindView(R.id.tv_get_num_60)
    TextView tvGetNum60;
    @BindView(R.id.tv_get_num_72)
    TextView tvGetNum72;

    @BindView(R.id.tv_return_num)
    TextView tvReturnNum;

    @BindView(R.id.tv_empty_num)
    TextView tvEmptyNum;

    @BindView(R.id.iv_qr_code)
    ImageView ivQrCode;

    @BindView(R.id.tv_code)
    TextView tvCode;

    @OnClick({R.id.tv_return_num})
    public void actionTest(){
        //startActivity(new Intent(MainActivity.this, BackWaitBatteryActivity.class));
        //LocalDataManager.devId = "";
        ReportManager.login();

    }

    @OnClick({R.id.iv_qr_code})
    public void actionTest2(){

        //ReportManager.messageAllReport();
        startActivity(new Intent(MainActivity.this, MenuActivity.class));
    }
    private long lastLoginClick = 0;
    private int hiddenTotal = 0;
    @OnClick({R.id.view_login})
    public void actionLogin(){
        long now = CustomMethodUtil.elapsedRealtime();
        if(now - lastLoginClick < 500){
            hiddenTotal++;
            if(hiddenTotal > 5){
                startActivity(new Intent(this, MenuActivity.class));
                hiddenTotal = 0;
            }
        }else{
            hiddenTotal = 0;
        }
        lastLoginClick = now;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WIFIManager.getMobileNetworkSignal(this);

        LogUtil.i("#####    protected void onCreate() ");
    }
    private long onResumeTime;
    @Override
    protected void onResume() {
        super.onResume();
        onResumeTime = CustomMethodUtil.elapsedRealtime();
        LocalDataManager.initOrderStatus();
        OrderManager.getInstance().setOrderListener(orderListener);

        LogUtil.i("#####    protected void onResume() ");

    }

    @Override
    protected void onStop() {
        super.onStop();
        OrderManager.getInstance().removeOrderListener(orderListener);
    }

    @Override
    public void initUIView() {
        Typeface newTypeface = Typeface.createFromAsset(getAssets(), "fonts/haettenschweiler.ttf");
        tvExchangeNum48.setTypeface(newTypeface);
        tvExchangeNum60.setTypeface(newTypeface);
        tvExchangeNum72.setTypeface(newTypeface);

        tvGetNum48.setTypeface(newTypeface);
        tvGetNum60.setTypeface(newTypeface);
        tvGetNum72.setTypeface(newTypeface);

        tvReturnNum.setTypeface(newTypeface);

        tvEmptyNum.setTypeface(newTypeface);
    }

    @Override
    public void setCurrent() {
        LocalDataManager.currentActivity = LocalDataManager.MAIN_ACTIVITY;
    }
    private int timerTick = 0;
    @Override
    public void passSecond() {
        super.passSecond();
        timerTick ++;
        if(timerTick > 14400){
            timerTick = 0;
        }
        if (timerTick %3 == 1){
            if (LocalDataManager.shouldEmptyPort != -1){
                if (!CustomMethodUtil.isPortEmpty(LocalDataManager.shouldEmptyPort)
                        && !CustomMethodUtil.isOpen(LocalDataManager.shouldEmptyPort)){
                    long now = CustomMethodUtil.elapsedRealtime();
                    if(now - onResumeTime < 300000){
                        if (LocalDataManager.shouldOpenTimes < 3){
                            CustomMethodUtil.open(LocalDataManager.shouldEmptyPort);
                            LocalDataManager.shouldOpenTimes ++;
                        }else {
                            LocalDataManager.shouldEmptyPort = -1;
                        }
                    }else {
                        LocalDataManager.shouldEmptyPort = -1;
                    }
                }
            }
        }


        int exchangeNum48 = LocalDataManager.getLogicValidBatteryNum(0);
        tvExchangeNum48.setText(exchangeNum48 < 10? "0" + exchangeNum48 : "" + exchangeNum48);
        int exchangeNum60 = LocalDataManager.getLogicValidBatteryNum(1);
        tvExchangeNum60.setText(exchangeNum60 < 10? "0" + exchangeNum60 : "" + exchangeNum60);
        int exchangeNum72 = LocalDataManager.getLogicValidBatteryNum(2);
        tvExchangeNum72.setText(exchangeNum72 < 10? "0" + exchangeNum72 : "" + exchangeNum72);


        int getNum48 = LocalDataManager.getLogicGetBatteryNum(0);
        tvGetNum48.setText(getNum48 < 10? "0" + getNum48 : "" + getNum48);
        int getNum60 = LocalDataManager.getLogicGetBatteryNum(1);
        tvGetNum60.setText(getNum60 < 10? "0" + getNum60 : "" + getNum60);
        int getNum72 = LocalDataManager.getLogicGetBatteryNum(2);
        tvGetNum72.setText(getNum72 < 10? "0" + getNum72 : "" + getNum72);

        int returnNum = LocalDataManager.getLogicReturnBatteryNum();
        tvReturnNum.setText(returnNum < 10? "0" + returnNum : "" + returnNum);

        int emptyNum = LocalDataManager.getEmptyNum();
        tvEmptyNum.setText(emptyNum < 10? "0" + emptyNum : "" + emptyNum);

        if (needDraw){
            drawQRCode();
        }
    }
    boolean needDraw = true;
    private void drawQRCode(){
        if (LocalDataManager.devId.isEmpty()){
            return ;
        }
        try {
            int ip = PreferencesUtil.getInstance().getSwitchIp();
            String msg;
            switch (ip){
                case 1:
                    msg = "";
                    break;
                case 2:
                    msg = "";
                    break;
                case 11:
                    msg = "https://pnenergy.net/miniapp/wx46ccbcae91738bdd/?";
                    break;
                case 21:
                    msg = "https://iot.jiabaida.com/wxb0a0ffa3955420bc/?devId=";
                    break;
                default:
                    msg = "";
                break;
            }
            msg = msg + LocalDataManager.devId;
           // msg = msg +"CHZD12GDTY200329110";
            tvCode.setText("终端号：" + LocalDataManager.devId);
           // tvCode.setText("终端号：CHZD12GDTY200329110" );
            BitMatrix matrix = new MultiFormatWriter().encode(msg, BarcodeFormat.QR_CODE, 300, 300);
            matrix = updateBit(matrix,18);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(matrix);
            ivQrCode.setImageBitmap(bitmap);
            needDraw = false;
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    private BitMatrix updateBit(BitMatrix matrix, int margin){
        int tempM = margin*2;
        int[] rec = matrix.getEnclosingRectangle();
        int resWidth = rec[2] + tempM;
        int resHeight = rec[3] + tempM;
        BitMatrix resMatrix = new BitMatrix(resWidth, resHeight);
        resMatrix.clear();
        for(int i= margin; i < resWidth- margin; i++){
            for(int j=margin; j < resHeight-margin; j++){
                if(matrix.get(i-margin + rec[0], j-margin + rec[1])){
                    resMatrix.set(i,j);
                }
            }
        }
        return resMatrix;
    }
}