package com.haipai.cabinet.manager;

import com.haipai.cabinet.entity.AllPropertiesRequest;
import com.haipai.cabinet.entity.BaseResponse;
import com.haipai.cabinet.entity.BatteryInfo;
import com.haipai.cabinet.entity.LoginRequest;
import com.haipai.cabinet.entity.WarningRequest;
import com.haipai.cabinet.model.entity.PmsEntity;
import com.haipai.cabinet.tcp.ServerProtocolDefine;
import com.haipai.cabinet.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class ReportManager {

    public static void login(){
        LogUtil.i("TcpManager mina  login");
        LoginRequest request = new LoginRequest();
        request.setMsgType(110);
        request.setDevId("CHZD12GDTY200329110");
        request.setCabSta("1");
        request.setProtocolVersion("V2");
        request.setCcid(LocalDataManager.ccid);
        request.setImei(LocalDataManager.imei);
        request.setImsi(LocalDataManager.imsi);
        request.setDevType(2);
        request.setTxnNo(System.currentTimeMillis());
        TcpManager.getInstance().send(ServerProtocolDefine.makeDataBytes(request));
    }

    public static void baseResponse(int msgType,int result,String txnNo){
        BaseResponse baseRespone = new BaseResponse();
        baseRespone.setDevId(LocalDataManager.devId);
        baseRespone.setMsgType(msgType);
        baseRespone.setResult(result);
        baseRespone.setTxnNo(txnNo);
        TcpManager.getInstance().send(ServerProtocolDefine.makeDataBytes(baseRespone));
    }
    public static void boxOpenReport(int port){
        WarningRequest request = new WarningRequest();
        request.setMsgType(410);
        request.setDevId(LocalDataManager.devId);
        request.setTxnNo("" +System.currentTimeMillis());
        WarningRequest.AlarmListBean bean = new WarningRequest.AlarmListBean();
        bean.setId("boxFault");
        bean.setAlarmDesc("05opens"+ (port+1));
        bean.setAlarmTime("" + System.currentTimeMillis());

        TcpManager.getInstance().send(ServerProtocolDefine.makeDataBytes(request));
    }
    public static void switchFinishReport(int alarmDesc,int doorPort,
                                           BatteryInfo emptBat,
                                           BatteryInfo fullBat){
        WarningRequest request = new WarningRequest();
        request.setMsgType(410);
        request.setDevId(LocalDataManager.devId);

        WarningRequest.AlarmListBean bean = new WarningRequest.AlarmListBean();
        bean.setId("switchFinish");
        if(LocalDataManager.mBatteryGetOrBack != null){
            bean.setBatteryId(LocalDataManager.mBatteryGetOrBack.getpId());
        }
        String txnNo ;
        if(OrderManager.currentOrder!=null){
            bean.setUserId(OrderManager.currentOrder.getUserId());
            txnNo = OrderManager.currentOrder.getTxnNo();
            request.setTxnNo(txnNo);
        }else {
            txnNo = "" + System.currentTimeMillis();
            request.setTxnNo(txnNo);
        }
        bean.setAlarmDesc("" + alarmDesc);
        bean.setDoorId(doorPort+1);
        bean.setAlarmTime("" + System.currentTimeMillis());
        if (emptBat != null){
            bean.setEmptDoorID(emptBat.getPort()+1);
            bean.setEmptBatteryId(emptBat.getpId());
            bean.setEmptBatsoc(emptBat.getSoc());
        }
        if (fullBat != null){
            bean.setFullDoorID(fullBat.getPort()+1);
            bean.setFullBatteryId(fullBat.getpId());
            bean.setFullBatsoc(fullBat.getSoc());
        }
        bean.setAlarmFlag(0);
        TcpManager.getInstance().send(ServerProtocolDefine.makeDataBytes(request),""+ txnNo);
    }
    public static void messageAllReport(){
        AllPropertiesRequest request = new AllPropertiesRequest();
        request.setMsgType(310);
        request.setIsFull(1);
        request.setDevId(LocalDataManager.devId);
        request.setTxnNo(System.currentTimeMillis());

        List<AllPropertiesRequest.CabListBean> cabList = new ArrayList<>();
        AllPropertiesRequest.CabListBean cabBean = new AllPropertiesRequest.CabListBean();
        cabBean.setdBM("" + LocalDataManager.dbm);
        //电表数据
        cabBean.setCabVol("" + LocalDataManager.getInstance().cabinet.getMeter().getVoltage());
        cabBean.setCabCur("" + LocalDataManager.getInstance().cabinet.getMeter().getCurrent());
        cabBean.setEmKwh("" + LocalDataManager.getInstance().cabinet.getMeter().getTotalWh());

        cabBean.setCabT("" + LocalDataManager.getInstance().cabinet.getCcu().getLcdTemp());
        cabBean.setBatNum("" + LocalDataManager.getBatteryTotal());
        cabBean.setBatFullA("" + LocalDataManager.getLogicValidBatteryNum(0));
        cabBean.setBatFullB("" + LocalDataManager.getLogicValidBatteryNum(1));
        cabBean.setBatFullC("" + LocalDataManager.getLogicValidBatteryNum(2));
        //协议没弄明白 todo
        List<String> cabFault = new ArrayList<>();
        cabFault.add("000");
        cabBean.setCabFault(cabFault);
        cabBean.setCabAlarm(cabFault);

        cabList.add(cabBean);

        List<AllPropertiesRequest.BoxListBean> boxList = new ArrayList<>();
        List<AllPropertiesRequest.BatListBean> batList = new ArrayList<>();
        for (int i = 0; i<LocalDataManager.slotNum; i++){
            if(LocalDataManager.getInstance().cabinet.getPmsList().size()>i){
                PmsEntity pmsEntity = LocalDataManager.getInstance().cabinet.getPmsList().get(i);
                AllPropertiesRequest.BoxListBean boxBean  = new AllPropertiesRequest.BoxListBean();
                boxBean.setDoorId("" + pmsEntity.getCabinID());
                boxBean.setBoxEnable("" + ((LocalDataManager.isPortDisable(pmsEntity.getPort())?1:0)));
                boxBean.setDoorSta("" + ((pmsEntity.getDevState()&1)==1?0:1));
                boxBean.setBoxChgSta("" + ((pmsEntity.getDevState()&2)==1?0:1));
                if(pmsEntity.hasBattery()){
                    AllPropertiesRequest.BatListBean batBean = new AllPropertiesRequest.BatListBean();
                    BatteryInfo battery = pmsEntity.getBatteryInfo();

                    boxBean.setBatteryId(battery.getpId());

                    batBean.setBatteryId(battery.getpId());
                    //batBean.setBmsT();
                    batBean.setSoc("" + battery.getSoc());
                    batBean.setChgCur("" + battery.getCurrent());
                    batBean.setBatVol("" + battery.getVoltage());
                    batBean.setBatCycle("" + battery.getCycle());
                    batList.add(batBean);
                }
                boxList.add(boxBean);
            }
        }
        TcpManager.getInstance().send(ServerProtocolDefine.makeDataBytes(request));
    }

}
